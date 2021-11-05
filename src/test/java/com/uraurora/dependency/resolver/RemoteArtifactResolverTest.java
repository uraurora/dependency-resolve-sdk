package com.uraurora.dependency.resolver;

import com.uraurora.dependency.resolver.builder.ArtifactResolverBuilder;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;

import java.util.List;

import static com.uraurora.dependency.resolver.util.Options.listOf;
import static org.junit.Assert.*;

public class RemoteArtifactResolverTest {

    private static final Artifact artifact = new DefaultArtifact("com.google.guava:guava:31.0.1-jre");

    public static final List<RemoteRepository> REMOTE_REPOSITORIES = listOf(
            new RemoteRepository.Builder("meituan-dianping-snapshots", "default", "http://pixel.sankuai.com/repository/group-snapshots").build(),
            new RemoteRepository.Builder("meituan-dianping-releases", "default", "http://pixel.sankuai.com/repository/group-releases").build()
    );

    RemoteArtifactResolver resolver = ArtifactResolverBuilder.builder()
            .withRemote()
            .withRemoteRepositories(REMOTE_REPOSITORIES)
            .build();



    @Test
    public void directDependencies() {

    }

    @Test
    public void dependenciesByJar() {
    }

    @Test
    public void dependenciesTreeByJar() {
    }

    @Test
    public void findAvailableVersions() {
    }

    @Test
    public void findLatestVersion() {
    }
}