package com.uraurora.dependency.resolver.util;

import com.uraurora.dependency.resolver.value.GenericVersion;
import org.apache.maven.model.Dependency;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 20:15
 * @description :
 */
public abstract class VersionUtils {

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

}
