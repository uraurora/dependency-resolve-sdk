package com.uraurora.dependency.resolver.value;

import lombok.Data;
import org.eclipse.aether.graph.Dependency;

import static com.uraurora.dependency.resolver.util.Options.with;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 16:42
 * @description :
 */
@Data
public class DependencyDiff {

    private String groupId;

    private String artifactId;

    private String type;

    private String scope;

    private String fromVersion;

    private String toVersion;

    public static DependencyDiff from(Dependency source, Dependency target){
        return with(new DependencyDiff(), diff->{
            diff.setGroupId(source.getArtifact().getGroupId());
            diff.setArtifactId(source.getArtifact().getArtifactId());
            diff.setScope(source.getScope());
            diff.setType(source.getArtifact().getExtension());
            diff.setFromVersion(source.getArtifact().getVersion());
            diff.setToVersion(target.getArtifact().getVersion());
        });
    }

    public static DependencyDiff from(Dependency source, String targetVersion){
        return with(new DependencyDiff(), diff->{
            diff.setGroupId(source.getArtifact().getGroupId());
            diff.setArtifactId(source.getArtifact().getArtifactId());
            diff.setScope(source.getScope());
            diff.setType(source.getArtifact().getExtension());
            diff.setFromVersion(source.getArtifact().getVersion());
            diff.setToVersion(targetVersion);
        });
    }

    @Override
    public String toString() {
        return groupId + ':' +
                artifactId + ':' +
                type + ':' +
                scope  +
                ", (version='" + fromVersion + "->" + toVersion + '\'' + ')';
    }

}
