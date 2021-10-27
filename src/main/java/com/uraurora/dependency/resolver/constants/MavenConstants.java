package com.uraurora.dependency.resolver.constants;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 19:38
 * @description : maven相关的常量与方法
 */
public class MavenConstants {

    public static final String DEPENDENCY_LIST_FILE_NAME = "dependencyList.txt";

    public static final String DEPENDENCY_TREE_FILE_NAME = "dependencyTree.txt";

    public static final List<String> DEPENDENCY_LIST = Lists.newArrayList("dependency:list", "-DoutputFile=" + DEPENDENCY_LIST_FILE_NAME);

    public static final List<String> DEPENDENCY_TREE = Lists.newArrayList("dependency:tree", "-DoutputFile=" + DEPENDENCY_TREE_FILE_NAME);

    public static String dependencyPlugin(){
        return "dependency";
    }

    public static String dependencyPlugin(String version) {
        return "org.apache.maven.plugins:maven-dependency-plugin:" + version;
    }

    public static List<String> dependencyListCommand() {
        return Lists.newArrayList(dependencyPlugin() + ":list", "-DoutputFile=" + DEPENDENCY_LIST_FILE_NAME);
    }

    public static List<String> dependencyTreeCommand() {
        return Lists.newArrayList(dependencyPlugin() + ":tree", "-DoutputFile=" + DEPENDENCY_TREE_FILE_NAME);
    }

}
