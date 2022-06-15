package com.uraurora.dependency.resolver;

import com.uraurora.dependency.resolver.builder.ArtifactResolverBuilder;
import com.uraurora.dependency.resolver.visitor.DependencyFormatterVisitor;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;

import java.io.Console;
import java.util.List;

import static com.uraurora.dependency.resolver.util.Options.listOf;

public class RemoteArtifactResolverTest {

    // com.google.guava:guava:31.0.1-jre
    private static final Artifact artifact = new DefaultArtifact("com.sankuai.fbi:fbi-function-parent:1.1.9-SNAPSHOT");

    public static final List<RemoteRepository> REMOTE_REPOSITORIES = listOf(
            new RemoteRepository.Builder("meituan-dianping-snapshots", "default", "http://pixel.sankuai.com/repository/group-snapshots").build(),
            new RemoteRepository.Builder("meituan-dianping-releases", "default", "http://pixel.sankuai.com/repository/group-releases").build()
    );

    AbstractArtifactResolver resolver = new ArtifactResolverBuilder()
            .withRemote()
            .withArtifact(artifact)
            .withLocalCachePath(System.getenv("MAVEN_HOME"))
            .withMavenHome(System.getenv("MAVEN_HOME"))
            .withRemoteRepositories(REMOTE_REPOSITORIES)
            .build();

    public RemoteArtifactResolverTest() throws Exception {
    }

    @Test
    public void dependencies() throws Exception {
        final List<Dependency> dependencies = resolver.dependencies();
        dependencies.forEach(System.out::println);
    }

    @Test
    public void dependenciesTree() throws Exception {
        DependencyFormatterVisitor visitor = new DependencyFormatterVisitor();
        resolver.dependenciesTree().accept(visitor);
        System.out.println(visitor.getContent());
    }

    @Test
    public void directDependencies() throws Exception {
        final List<Dependency> dependencies = resolver.directDependencies();
        dependencies.forEach(System.out::println);
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