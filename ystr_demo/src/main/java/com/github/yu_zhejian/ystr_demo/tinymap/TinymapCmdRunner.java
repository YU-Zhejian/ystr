package com.github.yu_zhejian.ystr_demo.tinymap;

import picocli.CommandLine;

@CommandLine.Command(
        name = "tinymap",
        description = "A DNA/RNA to Genome aligner for Third-Generation Sequencing.",
        subcommands = {GenomeIndexerCmdRunner.class},
        version = TinymapConstants.VERSION,
        mixinStandardHelpOptions = true)
public final class TinymapCmdRunner {
    private TinymapCmdRunner() {}
}
