package com.uraurora.dependency.resolver.builder;

import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 20:18
 * @description :
 */
public class ArtifactResolverBuilder {

    private final List<RemoteRepository> remoteRepositories;

    private final String mavenHome;

    public ArtifactResolverBuilder(List<RemoteRepository> remoteRepositories, String mavenHome) {
        this.remoteRepositories = remoteRepositories;
        this.mavenHome = mavenHome;
    }
}
