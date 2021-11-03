package com.uraurora.dependency.resolver.util;

import com.uraurora.dependency.resolver.value.GenericVersion;
import org.apache.maven.model.Dependency;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.version.Version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 20:15
 * @description :
 */
public abstract class VersionUtils {

    public static final String SNAPSHOT = "SNAPSHOT";

    public static final Pattern SNAPSHOT_TIMESTAMP = Pattern.compile("^(.*-)?([0-9]{8}\\.[0-9]{6}-[0-9]+)$");

    public static final String SNAPSHOT_POM_REGEX = "^(.*-)?([0-9]{8}\\.[0-9]{6}-[0-9]+)(.pom)+$";
    /**
     * 获取基础版本
     *
     * @param version version
     * @return 基础版本
     */
    public static String getBaseVersion(String version) {
        return toBaseVersion(version);
    }

    /**
     * 获取基础版本
     *
     * @param artifact artifact
     * @return 基础版本
     */
    public static String getBaseVersion(Artifact artifact) {
        return artifact.getBaseVersion();
    }

    /**
     * 获取基础版本
     *
     * @param artifact artifact
     * @return 基础版本
     */
    public static String getBaseVersion(org.apache.maven.artifact.Artifact artifact) {
        return toBaseVersion(artifact.getVersion());
    }

    /**
     * 是否是快照版版本
     *
     * @param version 版本
     * @return 是否是快照版
     */
    private static boolean isSnapshot(String version) {
        return version.endsWith(SNAPSHOT) || SNAPSHOT_TIMESTAMP.matcher(version).matches();
    }

    /**
     * 转换版本为基础版本，正式版版本即为基础版本，快照版会去除时间戳信息
     *
     * @param version maven version
     * @return base version
     */
    private static String toBaseVersion(String version) {
        String baseVersion;

        if (version == null) {
            baseVersion = version;
        } else if (version.startsWith("[") || version.startsWith("(")) {
            baseVersion = version;
        } else {
            Matcher m = SNAPSHOT_TIMESTAMP.matcher(version);
            if (m.matches()) {
                if (m.group(1) != null) {
                    baseVersion = m.group(1) + SNAPSHOT;
                } else {
                    baseVersion = SNAPSHOT;
                }
            } else {
                baseVersion = version;
            }
        }

        return baseVersion;
    }

    /**
     * 比较APP版本号的大小
     * <p>
     * 1、前者大则返回一个正数
     * 2、后者大返回一个负数
     * 3、相等则返回0
     *
     * @param version1 maven版本号
     * @param version2 maven版本号
     * @return int
     */
    public static int compareVersion(String version1, String version2) {
        GenericVersion genericVersion1 = new GenericVersion(version1);
        GenericVersion genericVersion2 = new GenericVersion(version2);
        return genericVersion1.compareTo(genericVersion2);
    }

    public static int compareVersion(Dependency dependency1, Dependency dependency2) {
        GenericVersion genericVersion1 = new GenericVersion(dependency1.getVersion());
        GenericVersion genericVersion2 = new GenericVersion(dependency2.getVersion());
        return genericVersion1.compareTo(genericVersion2);
    }

    public static int compareVersion(Artifact artifact1, Artifact artifact2) {
        Version v1 = new GenericVersion(artifact1.getVersion());
        Version v2 = new GenericVersion(artifact2.getVersion());
        return v1.compareTo(v2);
    }

    public static void main(String[] args) {
        Artifact artifact1 = new DefaultArtifact("com.sankuai.fbi:fbi-runtime:1.1.6-20211020.114554-4");
        Artifact artifact2 = new DefaultArtifact("com.sankuai.fbi:fbi-runtime:1.1.6-20211019.061902-2");
        System.out.println(compareVersion(artifact1, artifact2));
    }

}
