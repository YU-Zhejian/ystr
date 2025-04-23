package com.github.yu_zhejian.ystr_demo.tinymap;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.longs.LongBigArrayBigList;
import it.unimi.dsi.fastutil.longs.LongBigList;
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

/**
 * Data structure for genome index.
 *
 * <p>The current implementation loads everything in memory.
 *
 * @param config The configuration of the indexer.
 * @param fnaPath Path to the original FASTA file.
 * @param contigLens The length of each contig.
 * @param contigNames The names of each contig.
 * @param contigIndexPaths The paths to the index files. A contig may have multiple indices.
 */
public record GenomeIndex(
        GenomeIndexerConfig config,
        String fnaPath,
        LongBigList contigLens,
        BigList<String> contigNames,
        BigList<BigList<String>> contigIndexPaths) {

    /**
     * Number of contigs.
     *
     * @return As described.
     */
    public long numContigs() {
        return contigNames.size64();
    }

    public void toDisk(final @NotNull Path path) {
        var oConfig = new BaseConfiguration();
        oConfig.setProperty("numContigs", numContigs());

        for (long i = 0; i < numContigs(); i++) {
            oConfig.setProperty("contig.%d.contigLen".formatted(i), contigLens.getLong(i));
            oConfig.setProperty("contig.%d.contigName".formatted(i), contigNames.get(i));
            long numContigIndexPaths = contigIndexPaths.get(i).size64();
            oConfig.setProperty("contig.%d.numContigIndexPaths".formatted(i), numContigIndexPaths);
            for (long j = 0; j < numContigIndexPaths; j++) {
                oConfig.setProperty(
                        "contig.%d.contigIndexPaths.%d".formatted(i, j),
                        contigIndexPaths.get(i).get(j));
            }
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
                long numContigIndexPaths =
                        iConfig.getLong("contig.%d.numContigIndexPaths".formatted(i));
                var thisContigIndexPaths = new ObjectBigArrayBigList<String>();
                for (long j = 0; j < numContigIndexPaths; j++) {
                    thisContigIndexPaths.add(
                            iConfig.getString("contig.%d.contigIndexPaths.%d".formatted(i, j)));
                }
                contigIndexPaths.add(thisContigIndexPaths);
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
