package com.uraurora.dependency.resolver.builder;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-26 17:11
 * @description :
 */
public class ArtifactResolverBuilder {

    public ArtifactResolverBuilder() {
    }

    public RemoteArtifactResolverBuilder withRemote() {
        return new RemoteArtifactResolverBuilder();
    }

    public LocalArtifactResolverBuilder withLocal() {
        return new LocalArtifactResolverBuilder();
    }
}
