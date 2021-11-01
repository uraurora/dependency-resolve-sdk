package com.uraurora.dependency.resolver.builder;

import com.uraurora.dependency.resolver.IArtifactResolver;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 20:18
 * @description :
 */
public class ArtifactResolverBuilder {

    /**
     * maven远程仓库
     */
    private List<RemoteRepository> remoteRepositories;

    /**
     * maven home，maven路径
     */
    private String mavenHome;

    private ArtifactResolverBuilder() {
    }

    public static ArtifactResolverBuilder builder() {
        return new ArtifactResolverBuilder();
    }

    public ArtifactResolverBuilder withRemoteRepositories(List<RemoteRepository> remoteRepositories) {
        this.remoteRepositories = remoteRepositories;
        return this;
    }

    public ArtifactResolverBuilder withMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
        return this;
    }

    public List<RemoteRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    public String getMavenHome() {
        return mavenHome;
    }

    public IArtifactResolver build() {
        return null;
    }
}
