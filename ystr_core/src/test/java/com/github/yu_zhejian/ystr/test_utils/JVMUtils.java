package com.github.yu_zhejian.ystr.test_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class JVMUtils {
    public static final Logger LH = LoggerFactory.getLogger(JVMUtils.class.getCanonicalName());

    private JVMUtils() {}

    public static void printInfo() {
        var systemProperties = System.getProperties();

        var osName = systemProperties.get("os.name");
        if (!Objects.equals(osName, "Linux")) {
            LH.warn("Detected operating system '{}', which is not Linux", osName);
        }

        var osArch = systemProperties.get("os.arch");
        if (!Objects.equals(osArch, "amd64")) {
            LH.warn("Detected operating system architecture {}, which is not amd64", osArch);
        }

        var osVer = systemProperties.get("os.version");
        LH.info("OS: '{}' arch. {} ver. '{}'", osName, osArch, osVer);
        LH.info(
                "Java: '{}' ver. '{}' (Spec. ver. {}) by '{}' with JAVAHOME='{}'",
                LogUtils.lazy(() -> systemProperties.get("java.runtime.name")),
                LogUtils.lazy(() -> systemProperties.get("java.version")),
                LogUtils.lazy(() -> systemProperties.get("java.specification.version")),
                LogUtils.lazy(() -> systemProperties.get("java.vendor")),
                LogUtils.lazy(() -> systemProperties.get("java.home")));
    }

    public static void printMemInfo() {
        var rt = Runtime.getRuntime();
        LH.info(
                "JVM Memory: Free/Total/Max {}/{}/{}",
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(rt.freeMemory())),
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(rt.totalMemory())),
                LogUtils.lazy(() -> FrontendUtils.toHumanReadable(rt.maxMemory())));
    }

    public static void main(String[] args) {
        printInfo();
        printMemInfo();
    }
}
