package com.uraurora.dependency.resolver.builder;

import com.uraurora.dependency.resolver.LocalArtifactResolver;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-26 17:20
 * @description :
 */
public class LocalArtifactResolverBuilder extends AbstractArtifactResolverBuilder {

    private String pomPath;

    public String getPomPath() {
        return pomPath;
    }

    public LocalArtifactResolverBuilder withPomPath(String pomPath) {
        this.pomPath = pomPath;
        return this;
    }

    @Override
    public LocalArtifactResolver build() throws Exception {
        return new LocalArtifactResolver(
                getPomPath(),
                getRemoteRepositories(),
                getMavenHome(),
                getLocalCachePath()
        );
    }
}
