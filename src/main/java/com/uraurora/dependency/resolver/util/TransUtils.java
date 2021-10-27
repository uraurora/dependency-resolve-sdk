package com.uraurora.dependency.resolver.util;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import static com.uraurora.dependency.resolver.util.Options.with;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 20:08
 * @description :
 */
public abstract class TransUtils {

    public static org.apache.maven.model.Dependency aether2Maven(org.eclipse.aether.graph.Dependency dependency) {
        return with(new org.apache.maven.model.Dependency(), d -> {
            d.setGroupId(dependency.getArtifact().getGroupId());
            d.setArtifactId(dependency.getArtifact().getArtifactId());
            d.setScope(dependency.getScope());
            d.setType(dependency.getArtifact().getExtension());
            if (dependency.isOptional()) {
                d.setOptional(dependency.getOptional());
            }
            d.setVersion(dependency.getArtifact().getVersion());
        });
    }

    public static org.eclipse.aether.graph.Dependency maven2aether(org.apache.maven.model.Dependency dependency) {
        Artifact artifact = new DefaultArtifact(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getType(),
                dependency.getVersion()
        );
        return new org.eclipse.aether.graph.Dependency(artifact, dependency.getScope());
    }
}
