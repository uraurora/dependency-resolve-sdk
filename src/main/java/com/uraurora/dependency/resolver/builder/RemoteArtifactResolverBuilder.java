package com.uraurora.dependency.resolver.builder;

import com.uraurora.dependency.resolver.RemoteArtifactResolver;
import com.uraurora.dependency.resolver.constants.FileConstants;
import org.eclipse.aether.artifact.Artifact;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-26 17:17
 * @description :
 */
public class RemoteArtifactResolverBuilder extends AbstractArtifactResolverBuilder {

    private Artifact artifact;

    private String targetPath = FileConstants.LOCAL_TEMP_PATH;

    public RemoteArtifactResolverBuilder withArtifact(Artifact artifact) {
        this.artifact = artifact;
        return this;
    }

    public RemoteArtifactResolverBuilder withTargetPath(String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public RemoteArtifactResolver build() throws Exception {
        return new RemoteArtifactResolver(
                getArtifact(),
                getTargetPath(),
                getRemoteRepositories(),
                getMavenHome(),
                getLocalCachePath()
        );
    }
}
