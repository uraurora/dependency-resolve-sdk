package com.uraurora.dependency.resolver;

import com.google.common.collect.Sets;
import com.uraurora.dependency.resolver.constants.FileConstants;
import com.uraurora.dependency.resolver.constants.MavenConstants;
import com.uraurora.dependency.resolver.enums.MavenCommandEnum;
import com.uraurora.dependency.resolver.util.Options;
import com.uraurora.dependency.resolver.util.ResolveUtils;
import com.uraurora.dependency.resolver.value.ArtifactDiff;
import com.uraurora.dependency.resolver.value.DependencyDiff;
import com.uraurora.dependency.resolver.value.DependencyTreeFile;
import com.uraurora.dependency.resolver.value.GroupArtifact;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.uraurora.dependency.resolver.constants.MavenConstants.DEPENDENCY_TREE_FILE_NAME;
import static com.uraurora.dependency.resolver.constants.MavenConstants.dependencyTreeCommand;
import static com.uraurora.dependency.resolver.util.Options.*;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 19:54
 * @description :
 */
public abstract class AbstractArtifactResolver {

    private static final String PACKING_TYPE_POM = "pom";

    public static final String PACKING_TYPE_JAR = "jar";

    /**
     * maven???????????????, optional
     */
    protected final String mavenHome;
    /**
     * pom????????????
     */
    protected final String targetPath;
    /**
     * maven????????????
     */
    protected final List<RemoteRepository> remoteRepositories;
    /**
     * ??????maven??????????????????????????????, optional
     */
    protected final String localCachePath;

    /**
     * pom???????????????????????????
     */
    protected Model model;
    /**
     * artifact
     */
    protected Artifact artifact;

    /**
     * maven reader
     */
    protected final MavenXpp3ReaderEx reader = new MavenXpp3ReaderEx();

    /**
     * maven writer
     */
    protected final MavenXpp3Writer writer = new MavenXpp3Writer();


    protected AbstractArtifactResolver(String targetPath, List<RemoteRepository> remoteRepositories, String mavenHome, String localCachePath) {
        this.targetPath = targetPath;
        this.remoteRepositories = remoteRepositories;
        this.mavenHome = mavenHome;
        this.localCachePath = localCachePath;
    }

    //<editor-fold desc="override interface methods">

    //<editor-fold desc="getter">
    public Artifact getArtifact() {
        return artifact;
    }

    /**
     * ?????????pom????????????
     *
     * @return ????????????
     */
    protected abstract Path getPomPath();

    public Path path() {
        return Paths.get(targetPath);
    }

    public Model model() {
        return model;
    }

    protected String getLocalCachePath() {
        return StringUtils.isBlank(localCachePath) ?
                FileConstants.LOCAL_TEMP_PATH : localCachePath;
    }

    protected List<RemoteRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    //</editor-fold>

    public List<Dependency> dependencies() throws Exception {
        final Path mavenPath = Paths.get(mavenHome);
        final Path tmpPath = getPomPath().getParent().resolve(MavenConstants.DEPENDENCY_LIST_FILE_NAME);
        final Path dependencyFilePath = getMavenCommandFileRoot().resolve(MavenConstants.DEPENDENCY_LIST_FILE_NAME);
        if (cacheAsideReadFlag(MavenCommandEnum.DEPENDENCY_LIST)) {
            ResolveUtils.executeMaven(getPomPath(), mavenPath, MavenConstants.dependencyListCommand());
            com.uraurora.dependency.resolver.util.FileUtils.moveAndReplace(tmpPath, dependencyFilePath);
            cacheAsideWrite();
        }

        final List<Dependency> res = listOf();
        try {
            res.addAll(ResolveUtils.parseList(dependencyFilePath));
        } catch (IOException e) {
            return res;
        } finally {
            if (isRemote() && artifact.isSnapshot()) {
                deleteDownloadFile();
            }
        }
        return res;
    }

    public DependencyNode dependenciesTree() throws Exception {
        final Path mavenPath = Paths.get(mavenHome);
        final Path tmpPath = getPomPath().getParent().resolve(DEPENDENCY_TREE_FILE_NAME);
        final Path dependencyFilePath = getMavenCommandFileRoot().resolve(DEPENDENCY_TREE_FILE_NAME);
        if (cacheAsideReadFlag(MavenCommandEnum.DEPENDENCY_TREE)) {
            ResolveUtils.executeMaven(getPomPath(), mavenPath, dependencyTreeCommand());
            com.uraurora.dependency.resolver.util.FileUtils.moveAndReplace(tmpPath, dependencyFilePath);
            cacheAsideWrite();
        }
        DependencyNode res;
        try {
            final DependencyTreeFile treeFile = new DependencyTreeFile(dependencyFilePath);
            res = treeFile.treeRoot();
        } catch (Exception e) {
            return new DefaultDependencyNode(artifact);
        } finally {
            if (isRemote() && artifact.isSnapshot()) {
                deleteDownloadFile();
            }
        }
        return res;
    }

    public List<Dependency> dependenciesExplicitly() throws Exception {
        final Path mavenPath = Paths.get(mavenHome);
        final Path tmpPath = getPomPath().getParent().resolve(MavenConstants.DEPENDENCY_TREE_FILE_NAME);
        final Path dependencyFilePath = getMavenCommandFileRoot().resolve(MavenConstants.DEPENDENCY_TREE_FILE_NAME);
        if (cacheAsideReadFlag(MavenCommandEnum.DEPENDENCY_TREE)) {
            ResolveUtils.executeMaven(getPomPath(), mavenPath, MavenConstants.dependencyTreeCommand());
            com.uraurora.dependency.resolver.util.FileUtils.moveAndReplace(tmpPath, dependencyFilePath);
            cacheAsideWrite();
        }
        final List<Dependency> res = listOf();
        try {
            res.addAll(ResolveUtils.parseTreeOutSide(dependencyFilePath));
        } catch (IOException e) {
            return res;
        } finally {
            if (isRemote() && artifact.isSnapshot()) {
                deleteDownloadFile();
            }
        }
        return res;
    }

    public abstract List<Dependency> directDependencies() throws ArtifactDescriptorException;

    public ArtifactDiff diff(IDependencyCollector other) throws Exception {
        final Set<Dependency> source = setOf(this.dependencies());
        final Set<Dependency> target = Options.setOf(other.dependencies());

        // ????????????????????????????????????????????????????????????
        return buildDependencyDiff(source, target);
    }

    public ArtifactDiff diffExplicitly(IDependencyCollector other) throws Exception {
        final Set<Dependency> source = setOf(this.dependenciesExplicitly());
        final Set<Dependency> target = Options.setOf(other.dependenciesExplicitly());
        return buildDependencyDiff(source, target);

    }

    public Set<Dependency> intersection(IDependencyCollector other) throws Exception {
        return Sets.intersection(new HashSet<>(this.dependencies()), new HashSet<>(other.dependencies()));
    }

    public Set<Dependency> union(IDependencyCollector other) throws Exception {
        return Sets.union(new HashSet<>(this.dependencies()), new HashSet<>(other.dependencies()));
    }


    public abstract List<Version> findAvailableVersions() throws VersionRangeResolutionException;

    public abstract Version findLatestVersion() throws VersionRangeResolutionException;
    //</editor-fold>

    /**
     * ???????????????????????????true????????????????????????????????????????????????
     *
     * @param commandEnum ????????????
     * @return ??????????????????????????????
     * @throws IOException io??????
     */
    protected abstract boolean cacheAsideReadFlag(MavenCommandEnum commandEnum) throws IOException;

    /**
     * ????????????????????????????????????
     *
     * @throws IOException io??????
     */
    protected abstract void cacheAsideWrite() throws IOException;

    /**
     * ?????????????????????
     *
     * @return ?????????????????????
     */
    protected abstract boolean isRemote();

    /**
     * maven?????????????????????????????????
     *
     * @return ????????????
     * @throws IOException io??????
     */
    protected Path getMavenCommandFileRoot() throws IOException {
        final String groupId = getArtifact().getGroupId();
        final String artifactId = getArtifact().getArtifactId();
        final String version = getArtifact().getVersion();
        Path path = Paths.get(getLocalCachePath())
                .resolve(groupId)
                .resolve(artifactId)
                .resolve(version);
        FileUtils.forceMkdir(path.toFile());
        return path;
    }

    //<editor-fold desc="class methods">

    protected void writeModel(Model model, Path path) throws IOException {
        writer.write(Files.newBufferedWriter(path), model);
    }

    protected Model buildModel(Path pomPath) throws IOException, XmlPullParserException {
        return reader.read(
                Files.newBufferedReader(pomPath),
                true,
                with(new InputSource(), m -> {
                    m.setLocation(pomPath.toString());
                }));
    }

    private void deleteDownloadFile() throws IOException {
        final Path parent = getPomPath().getParent();
        FileUtils.forceDelete(parent.toFile());

    }
    //</editor-fold>

    //<editor-fold desc="static methods">


    protected static Artifact artifactCheck(Artifact artifact) {
        return new DefaultArtifact(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getClassifier(),
                PACKING_TYPE_POM,
                artifact.getVersion(),
                artifact.getProperties(),
                (File) null
        );
    }

    protected static ArtifactDiff buildDependencyDiff(Set<Dependency> source, Set<Dependency> target) {
        // ????????????????????????????????????????????????????????????
        final List<DependencyDiff> diff = listOf();

        //?????????????????????????????????????????????????????????????????????
        final List<Dependency> independence = listOf(Sets.difference(source, target));

        // ?????????????????????????????????????????????????????????????????????
        final List<Dependency> increment = listOf(Sets.difference(target, source));

        // ?????????????????????artifact??????????????????????????????
        final List<Dependency> same = listOf(Sets.intersection(source, target));

        Map<GroupArtifact, String> map1 = buildArtifactVersionMap(source), map2 = buildArtifactVersionMap(target);
        Map<GroupArtifact, Dependency> mapOfView = buildArtifactMap(source);
        map1.forEach((k, v) -> {
            if (map2.containsKey(k)) {
                final String view = map2.get(k);
                if (!v.equals(view)) {
                    diff.add(DependencyDiff.from(mapOfView.get(k), map2.get(k)));
                }
            }
        });
        List<Dependency> difference = diff.stream()
                .map(dif -> {
                    Artifact artifact = new DefaultArtifact(
                            dif.getGroupId(),
                            dif.getArtifactId(),
                            dif.getType(),
                            dif.getFromVersion()
                    );
                    return new Dependency(artifact, dif.getScope());
                })
                .collect(Collectors.toList());
        return with(new ArtifactDiff(diff, difference, independence, increment, same), a -> {
            a.setSource(source);
            a.setTarget(target);
        });
    }

    /**
     * ??????group ??? artifact??????????????????map
     *
     * @param dependencyViews views
     * @return map
     */
    private static Map<GroupArtifact, Dependency> buildArtifactMap(Collection<Dependency> dependencyViews) {
        return buildMap(m -> {
            for (Dependency view : dependencyViews) {
                m.put(new GroupArtifact(view.getArtifact().getGroupId(), view.getArtifact().getArtifactId()), view);
            }
        });
    }

    private static Map<GroupArtifact, String> buildArtifactVersionMap(Collection<Dependency> dependencyViews) {
        return buildMap(m -> {
            for (Dependency view : dependencyViews) {
                m.put(new GroupArtifact(view.getArtifact().getGroupId(), view.getArtifact().getArtifactId()), view.getArtifact().getVersion());
            }
        });
    }

    protected static void checkFile(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("the path=" + path + " does not exists! ");
        }
    }
    //</editor-fold>


}
