package com.github.yu_zhejian.ystr.utils;

public final class JVMUtils {

    private JVMUtils() {}

    public static void printInfo() {
        var systemProperties = System.getProperties();

        var osName = systemProperties.get("os.name");

        var osArch = systemProperties.get("os.arch");

        var osVer = systemProperties.get("os.version");
        PyUtils.print("OS: name=", osName, "; arch=", osArch, "; ver=", osVer);

        PyUtils.print(
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

    public static void printMemInfo() {
        var rt = Runtime.getRuntime();
        PyUtils.print(
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
