package com.uraurora.dependency.resolver.builder;

import org.eclipse.aether.repository.RemoteRepository;

import java.nio.file.Path;
import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-05 17:34
 * @description :
 */
public class RemoteArtifactResolverBuilder extends ArtifactResolverBuilder{


    public RemoteArtifactResolverBuilder withRemoteRepositories(List<RemoteRepository> remoteRepositories) {
        this.remoteRepositories = remoteRepositories;
        return this;
    }

    public RemoteArtifactResolverBuilder withMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
        return this;
    }

    public void withLocalRepo(Path localRepo) {
        this.localRepo = localRepo;
    }

}
