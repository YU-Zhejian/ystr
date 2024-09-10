package com.github.yu_zhejian.ystr_demo.tinymap;

import org.jetbrains.annotations.NotNull;

import picocli.CommandLine;

import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "index",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "TODO")
public final class GenomeIndexerCmdRunner implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--fnaFilePath"},
            description = "Path to source reference genome file in FASTA format.",
            required = true)
    private String fnaFilePath;

    @CommandLine.Option(
            names = {"--idxDirPath"},
            description = "Path to the directory where constructed indices will be written.")
    private String idxDirPath;

    @Override
    public @NotNull Integer call() throws Exception {
        var conf = GenomeIndexerConfig.minimap2();
        if (idxDirPath == null) {
            idxDirPath = fnaFilePath + "tinymap-%s.idx.d".formatted(TinymapConstants.VERSION);
        }
        var gi = new GenomeIndexer(Path.of(fnaFilePath), Path.of(idxDirPath), conf, false);
        gi.index();
        return 0;
    }
}
