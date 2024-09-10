package com.github.yu_zhejian.ystr_demo;

import com.github.yu_zhejian.ystr.utils.JVMUtils;
import com.github.yu_zhejian.ystr.utils.LogUtils;
import com.github.yu_zhejian.ystr_demo.tinymap.TinymapCmdRunner;

import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "Main",
        mixinStandardHelpOptions = true,
        subcommands = {TinymapCmdRunner.class, VersionCmdRunner.class})
public final class Main implements Runnable {
    @Override
    public void run() {
        // Nop as all things are done by sub-commands
    }

    public static void main(String[] args) {
        var lh = LoggerFactory.getLogger(Main.class);
        lh.info("ystr_demo");
        lh.info("Args: {}", LogUtils.lazy(() -> String.join(" ", args)));
        System.exit(new CommandLine(new Main()).execute(args));
    }
}

@Command(name = "version", description = "Print nicely formatted version information.")
final class VersionCmdRunner implements Runnable {
    @Override
    public void run() {
        JVMUtils.printInfo();
        JVMUtils.printMemInfo();
    }
}
