package com.uraurora.dependency.resolver;

import com.uraurora.dependency.resolver.constants.FileConstants;
import com.uraurora.dependency.resolver.enums.MavenCommandEnum;
import com.uraurora.dependency.resolver.util.FileUtils;
import com.uraurora.dependency.resolver.util.ResolveUtils;
import com.uraurora.dependency.resolver.value.FileInfoContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;
import org.eclipse.aether.version.Version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.uraurora.dependency.resolver.util.Options.emptyString;
import static com.uraurora.dependency.resolver.util.Options.orDefault;
import static com.uraurora.dependency.resolver.util.ResolveUtils.newSession;
import static com.uraurora.dependency.resolver.util.ResolveUtils.newSystem;
import static com.uraurora.dependency.resolver.util.VersionUtils.SNAPSHOT_POM_REGEX;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 16:33
 * @description :
 */
public class RemoteArtifactResolver extends AbstractArtifactResolver
        implements IArtifactResolver, IDependencyCollector, IVersionCollector{

    private static final String LOCAL_CACHE_KEY_FILE = "cacheKey.txt";

    private final Path pomPath;

    public RemoteArtifactResolver(Artifact artifact, String targetPath, List<RemoteRepository> remoteRepositories, String mavenHome, String localCachePath) throws Exception {
        super(targetPath, remoteRepositories, mavenHome, localCachePath);
        this.artifact = artifactCheck(artifact);
        this.pomPath = ResolveUtils.download(this.artifact, path(), this.remoteRepositories);
        this.model = buildModel(pomPath);
    }

    @Override
    protected Path getPomPath() {
        return pomPath;
    }

    @Override
    protected boolean cacheAsideReadFlag(MavenCommandEnum commandEnum) throws IOException {
        final String fileName = commandEnum.getFileName();
        final Path dependencyFilePath = getMavenCommandFileRoot().resolve(fileName);
        if (!Files.exists(dependencyFilePath)) {
            return true;
        } else {
            // ??????????????????????????????????????????????????????????????????
            if (!artifact.isSnapshot()) {
                return false;
            } else {
                // SNAP-SHOT????????????????????????????????????????????????????????????????????????
                // ???????????????pom????????????????????????
                final Path parent = getPomPath().getParent();
                final String newValue = getTmpVersionFile(parent);

                if (StringUtils.isBlank(newValue)) {
                    return true;
                } else {
                    final String cacheValue = getCacheValue(getMavenCommandFileRoot());
                    if (StringUtils.isBlank(cacheValue)) {
                        return true;
                    } else {
                        return compareVersion(newValue, cacheValue) > 0;
                    }
                }
            }
        }
    }

    @Override
    protected void cacheAsideWrite() throws IOException {
        final Path parent = getPomPath().getParent();
        final String newValue = getTmpVersionFile(parent);

        if (StringUtils.isNotBlank(newValue)) {
            final Path resolve = getMavenCommandFileRoot().resolve(LOCAL_CACHE_KEY_FILE);
            Files.deleteIfExists(resolve);
            try (final BufferedWriter writer = Files.newBufferedWriter(resolve)) {
                writer.write(newValue);
            }
        }
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public List<Dependency> directDependencies() throws ArtifactDescriptorException {
        return null;
    }

    @Override
    public List<Dependency> dependenciesByJar() throws Exception {
        final DependencyNode root = dependenciesTreeByJar();
        final PreorderNodeListGenerator preorderGenerator = new PreorderNodeListGenerator();
        root.accept(preorderGenerator);
        final List<Dependency> dependencies = preorderGenerator.getDependencies(true);
        dependencies.remove(0);
        return dependencies;
    }

    @Override
    public DependencyNode dependenciesTreeByJar() throws Exception {
        final Path localRepoPath = Paths.get(orDefault(getLocalCachePath(), FileConstants.LOCAL_TEMP_PATH));
        final RepositorySystem system = newSystem();
        final RepositorySystemSession session = newSession(system, localRepoPath);

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(artifact, ""));
        collectRequest.setRepositories(getRemoteRepositories());
        CollectResult collectResult = system.collectDependencies(session, collectRequest);
        return collectResult.getRoot();
    }

    @Override
    public List<Version> findAvailableVersions() throws VersionRangeResolutionException {
        final VersionRangeResult versionRangeResult = ResolveUtils.getVersionRangeResult(artifact.getGroupId(), artifact.getArtifactId(), remoteRepositories);
        return versionRangeResult.getVersions();
    }

    @Override
    public Version findLatestVersion() throws VersionRangeResolutionException {
        final VersionRangeResult versionRangeResult = ResolveUtils.getVersionRangeResult(artifact.getGroupId(), artifact.getArtifactId(), remoteRepositories);
        return versionRangeResult.getHighestVersion();
    }


    //<editor-fold desc="static methods">

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @return ??????  fbi-migration-1.0.0-20210420.094010-6.pom
     */
    private static String getCacheValue(Path parent) throws IOException {
        final Path resolve = parent.resolve(LOCAL_CACHE_KEY_FILE);
        if (Files.exists(resolve)) {
            String res = emptyString;
            try (final BufferedReader reader = Files.newBufferedReader(resolve)) {
                res = reader.readLine();
            }
            return res;
        } else {
            return emptyString;
        }

    }

    /**
     * ??????????????????pom???????????????snapshot??????pom?????????
     *
     * @param parent ??????
     * @return ?????????
     * @throws IOException io??????
     */
    private static String getTmpVersionFile(Path parent) throws IOException {
        List<FileInfoContext> fileInfos = FileUtils.search(parent, SNAPSHOT_POM_REGEX, p -> true).stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(f -> getVersionNum(f.getDir().getFileName().toString()))))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fileInfos)) {
            return fileInfos.get(0).getDir().getFileName().toString();
        } else {
            return emptyString;
        }
    }

    /**
     * ??????snapshot??????????????????
     *
     * @param v1 v1
     * @param v2 v2
     * @return v1 - v2
     */
    private static int compareTimeStamp(String v1, String v2) {
        int l1 = v1.length(), l2 = v2.length(), i = 0;
        if (l1 != l2) {
            return 1;
        } else {
            while (i < l1 && v1.charAt(i) == v2.charAt(i)) {
                i++;
            }
            final String vb1 = v1.substring(i);
            final String vb2 = v2.substring(i);
            return vb1.compareTo(vb2);
        }
    }

    /**
     * ????????????????????????
     *
     * @param v1 v1
     * @param v2 v2
     * @return ??????v1.compareTo(v2)
     */
    private static int compareVersion(String v1, String v2) {
        try {
            final int i1 = getVersionNum(v1);
            final int i2 = getVersionNum(v2);
            return i1 - i2;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ????????? fbi-migration-1.0.0-20210427.070620-13.pom ??????????????????????????????????????????0
     *
     * @param s ?????????
     * @return ????????????
     */
    private static int getVersionNum(String s) {
        try {
            final int length = s.length();
            int index = length - 5;
            StringBuilder sb = new StringBuilder();
            while (index >= 0) {
                final char c = s.charAt(index);
                if (c != '-') {
                    sb.append(c);
                    index--;
                } else {
                    break;
                }
            }
            final String substring = sb.reverse().toString();
            return Integer.parseInt(substring);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    //</editor-fold>
}
