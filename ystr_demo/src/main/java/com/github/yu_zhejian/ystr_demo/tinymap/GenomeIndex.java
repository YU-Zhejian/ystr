package com.github.yu_zhejian.ystr_demo.tinymap;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class GenomeIndex {
    private final GenomeIndexerConfig config;
    private final String fnaPath;
    private final LongBigArrayBigList contigLens;
    private final BigList<String> contigNames;
    private final BigList<BigList<String>> contigIndexPaths;

    public GenomeIndex(
            final GenomeIndexerConfig config,
            final String fnaPath,
            final LongBigArrayBigList contigLens,
            final BigList<String> contigNames,
            final BigList<BigList<String>> contigIndexPaths) {
        this.config = config;
        this.fnaPath = fnaPath;
        this.contigLens = contigLens;
        this.contigNames = contigNames;
        this.contigIndexPaths = contigIndexPaths;
    }

    public long numContigs() {
        return contigNames.size64();
    }

    public void toDisk(final @NotNull Path path) {
        var oConfig = new BaseConfiguration();
        oConfig.setProperty("numContigs", numContigs());

        for (long i = 0; i < numContigs(); i++) {
            oConfig.setProperty("contig.%d.contigLen".formatted(i), contigLens.getLong(i));
            oConfig.setProperty("contig.%d.contigName".formatted(i), contigNames.get(i));
            // FIXME: Errors here: List not saved as lists due to Apache Commons.
            // May change to //
            // https://mvnrepository.com/artifact/com.electronwill.night-config/toml
            // implementation 'com.electronwill.night-config:toml:3.8.1' instead.
            oConfig.setProperty("contig.%d.contigIndexPaths".formatted(i), contigIndexPaths.get(i));
        }
        oConfig.setProperty("config.kmerSize", config.kmerSize());
        oConfig.setProperty("config.numKmerPerMinimizer", config.numKmerPerMinimizer());
        oConfig.setProperty("config.ntShannonEntropyCutoff", config.ntShannonEntropyCutoff());
        oConfig.setProperty("fnaPath", fnaPath);

        var propertiesConf = new JSONConfiguration();
        ConfigurationUtils.copy(oConfig, propertiesConf);
        var handler = new FileHandler(propertiesConf);
        try {
            handler.save(path.toFile());
        } catch (ConfigurationException cex) {
            throw new IllegalArgumentException(cex);
        }
    }

    private static ConfigurationBuilder<FileBasedConfiguration> getBuilder(
            final @NotNull Path path) {
        var params = new Parameters();
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(JSONConfiguration.class)
                .configure(params.properties().setFileName(path.toString()));
    }

    @Contract("_ -> new")
    public static @NotNull GenomeIndex fromDisk(final @NotNull Path path) {
        var builder = getBuilder(path);
        try {
            var iConfig = builder.getConfiguration();
            var numContigs = iConfig.getLong("numContigs");

            final var contigLens = new LongBigArrayBigList(numContigs);
            final var contigNames = new ObjectBigArrayBigList<String>(numContigs);
            final var contigIndexPaths = new ObjectBigArrayBigList<BigList<String>>();

            for (long i = 0; i < numContigs; i++) {
                contigLens.add(iConfig.getLong("contig.%d.contigLen".formatted(i)));
                contigNames.add(iConfig.getString("contig.%d.contigName".formatted(i)));
                contigIndexPaths.add(new ObjectBigArrayBigList<>(
                        iConfig.getList(String.class, "contig.%d.contigIndexPaths")));
            }
            return new GenomeIndex(
                    new GenomeIndexerConfig(
                            iConfig.getInt("config.kmerSize"),
                            iConfig.getInt("config.numKmerPerMinimizer"),
                            iConfig.getDouble("config.ntShannonEntropyCutoff")),
                    iConfig.getString("fnaPath"),
                    contigLens,
                    contigNames,
                    contigIndexPaths);
        } catch (ConfigurationException cex) {
            throw new IllegalArgumentException(cex);
        }
    }
}
