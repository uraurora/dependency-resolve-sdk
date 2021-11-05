package com.uraurora.dependency.resolver.builder;

import org.eclipse.aether.repository.RemoteRepository;

import java.nio.file.Path;
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
    protected List<RemoteRepository> remoteRepositories;

    /**
     * maven home，maven路径
     */
    protected String mavenHome = System.getenv("MAVEN_HOME");


    /**
     * 本地maven仓库路径
     */
    protected Path localRepo;


    public ArtifactResolverBuilder() {

    }

    public static ArtifactResolverBuilder builder() {
        return new ArtifactResolverBuilder();
    }


    public LocalArtifactResolverBuilder withLocal() {
        return new LocalArtifactResolverBuilder();
    }

    public RemoteArtifactResolverBuilder withRemote() {
        return new RemoteArtifactResolverBuilder();
    }


    public Path getLocalRepo() {
        return localRepo;
    }

    public List<RemoteRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    public String getMavenHome() {
        return mavenHome;
    }
}
