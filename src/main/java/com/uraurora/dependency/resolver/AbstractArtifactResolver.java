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
     * maven根目录路径, optional
     */
    protected final String mavenHome;
    /**
     * pom文件路径
     */
    protected final Path targetPath;
    /**
     * maven远程仓库
     */
    protected final List<RemoteRepository> remoteRepositories;
    /**
     * 执行maven命令的本地缓存文件夹, optional
     */
    protected final String localCachePath;

    /**
     * pom文件的真实描述对象
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


    protected AbstractArtifactResolver(Path targetPath, List<RemoteRepository> remoteRepositories, String mavenHome, String localCachePath) {
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
     * 实际的pom文件路径
     *
     * @return 文件路径
     */
    protected abstract Path getPomPath();

    public Path path() {
        return targetPath;
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

    public ArtifactDiff diff(IDependencyCollector other) throws Exception {
        final Set<Dependency> source = setOf(this.dependencies());
        final Set<Dependency> target = Options.setOf(other.dependencies());

        // 不同版本包，指定是相同的包，不同的版本包
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

    //</editor-fold>

    /**
     * 缓存策略标志位，为true表示需要重新获取，否则缓存有数据
     *
     * @param commandEnum 命令枚举
     * @return 是否需要重新获取数据
     * @throws IOException io异常
     */
    protected abstract boolean cacheAsideReadFlag(MavenCommandEnum commandEnum) throws IOException;

    /**
     * 获取新的结果后需要写缓存
     *
     * @throws IOException io异常
     */
    protected abstract void cacheAsideWrite() throws IOException;

    /**
     * 是否是远程仓库
     *
     * @return 是否是远程仓库
     */
    protected abstract boolean isRemote();

    /**
     * maven命令执行文件存放的位置
     *
     * @return 文件路径
     * @throws IOException io异常
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
        // 不同版本包，指定是相同的包，不同的版本包
        final List<DependencyDiff> diff = listOf();

        //独立包，指的是标准包中不包含，业务包中包含的包
        final List<Dependency> independence = listOf(Sets.difference(source, target));

        // 增量包，指的是标准包中包含而业务包中不包含的包
        final List<Dependency> increment = listOf(Sets.difference(target, source));

        // 相同包，指的是artifact相同，版本也相同的包
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
     * 根据group 和 artifact组成唯一键的map
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
