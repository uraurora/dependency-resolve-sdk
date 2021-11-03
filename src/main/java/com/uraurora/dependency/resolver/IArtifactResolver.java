package com.uraurora.dependency.resolver;

import org.apache.maven.model.Model;
import org.eclipse.aether.artifact.Artifact;

import java.nio.file.Path;

/**
 * @author gaoxiaodong
 */
public interface IArtifactResolver {

    /**
     * 下载的文件根目录
     * @return 下载的文件目录
     */
    Path path();

    /**
     * 获取artifact信息
     * @return artifact
     */
    Artifact getArtifact();

    /**
     * 依赖模型
     * @return 依赖模型
     */
    Model model();

    /**
     * 是否是远程仓库包
     * @return 是否是远程仓库包
     */
    boolean isRemote();

    /**
     * 返回是否是snapshot包
     * @return 是否是snapshot包
     */
    default boolean isSnapshot(){
        return getArtifact().isSnapshot();
    }

}
