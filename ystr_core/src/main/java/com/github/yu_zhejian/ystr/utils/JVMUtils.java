package com.github.yu_zhejian.ystr.utils;

/** Utilities for JVM. */
public final class JVMUtils {

    private JVMUtils() {}

    /** Print various JVM information to stdout. */
    public static void printInfo() {
        var systemProperties = System.getProperties();

        var osName = systemProperties.get("os.name");
        var osArch = systemProperties.get("os.arch");
        var osVer = systemProperties.get("os.version");
        var pp = new PyUtils.PrintParamsBuilder().setSep(new byte[0]).build();
        PyUtils.print(pp, "OS: name=", osName, "; arch=", osArch, "; ver=", osVer);
        PyUtils.print(
                pp,
                "Java: name=",
                systemProperties.get("java.runtime.name"),
                "; ver=",
                systemProperties.get("java.version"),
                "; spec=",
                systemProperties.get("java.specification.version"),
                "; vendor=",
                systemProperties.get("java.vendor"),
                "; JAVA_HOME=",
                systemProperties.get("java.home"));
    }

    /** Print various JVM memory information to stdout. */
    public static void printMemInfo() {
        var rt = Runtime.getRuntime();
        var pp = new PyUtils.PrintParamsBuilder().setSep(new byte[0]).build();
        PyUtils.print(
                pp,
                "JVM Memory: Free/Total/Max =",
                FrontendUtils.toHumanReadable(rt.freeMemory()),
                "/",
                FrontendUtils.toHumanReadable(rt.totalMemory()),
                "/",
                FrontendUtils.toHumanReadable(rt.maxMemory()));
    }

    public static void main(String[] args) {
        printInfo();
        printMemInfo();
    }
}
