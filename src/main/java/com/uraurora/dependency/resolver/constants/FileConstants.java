package com.uraurora.dependency.resolver.constants;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 19:37
 * @description : 文件相关的常量和方法
 */
public abstract class FileConstants {

    public static final String LOCAL_TEMP_PATH;

    static {
        final String tmp = System.getProperty("java.io.tmpdir");
        LOCAL_TEMP_PATH = (tmp.endsWith(File.separator)) ? tmp : tmp + File.separator;
    }


    public static final String ALL_PATTERN = ".+";

    public static final String TEXT_PATTERN = "(.+)\\.txt";

    public static final String PICTURE_PATTERN = "(.+)\\.(png|jpe?g|bmp|gif|ico|pcx|tif|raw|tga)";

    public static final String VIDEO_PATTERN = "(.+)\\.(mpe?g|avi|rm(vb)?|mov|wmv|asf|dat)";

    public static final String MUSIC_PATTERN = "(.+)\\.(mp3|wma|rm|wav|mid)";

    public static final String SCRIPT_PATTERN = "(.+)\\.sh";

    public static Path getTmpDirectoryPath(String directory, boolean forceClean) throws IOException {
        final Path path = Paths.get(LOCAL_TEMP_PATH).resolve(directory);
        FileUtils.forceMkdir(path.toFile());
        if (forceClean) {
            FileUtils.cleanDirectory(path.toFile());
        }
        return path;
    }

    public static Path getTmpDirectoryPath(String directory) throws IOException {
        return getTmpDirectoryPath(directory, false);
    }

    public static Path getTmpFilesPath() throws IOException {
        return getTmpDirectoryPath("tmpFiles");
    }

}
