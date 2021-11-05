package com.uraurora.dependency.resolver.builder;

import com.uraurora.dependency.resolver.LocalArtifactResolver;
import org.eclipse.aether.repository.RemoteRepository;

import java.nio.file.Path;
import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-05 17:38
 * @description :
 */
public class LocalArtifactResolverBuilder extends ArtifactResolverBuilder {

    public LocalArtifactResolverBuilder withRemoteRepositories(List<RemoteRepository> remoteRepositories) {
        this.remoteRepositories = remoteRepositories;
        return this;
    }

    public LocalArtifactResolverBuilder withMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
        return this;
    }

    public void withLocalRepo(Path localRepo) {
        this.localRepo = localRepo;
    }

    public LocalArtifactResolver build() {
        return new LocalArtifactResolver();
    }

}
