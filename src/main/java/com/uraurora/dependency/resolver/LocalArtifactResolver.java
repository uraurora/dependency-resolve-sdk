package com.uraurora.dependency.resolver;

import com.uraurora.dependency.resolver.constants.FileConstants;
import com.uraurora.dependency.resolver.enums.MavenCommandEnum;
import com.uraurora.dependency.resolver.util.DateTimeUtils;
import com.uraurora.dependency.resolver.util.FileUtils;
import com.uraurora.dependency.resolver.util.TransUtils;
import com.uraurora.dependency.resolver.value.GenericVersion;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.uraurora.dependency.resolver.util.Options.buildString;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 16:33
 * @description :
 */
public class LocalArtifactResolver extends AbstractArtifactResolver
        implements IArtifactResolver, IDependencyCollector, IVersionCollector{



    protected LocalArtifactResolver(String targetPath, List<RemoteRepository> remoteRepositories, String mavenHome, String localCachePath) throws Exception {
        super(targetPath, remoteRepositories, mavenHome, localCachePath);
        checkFile(getPomPath());
        this.model = buildModel(targetPath);
        this.artifact = buildArtifact(model, Paths.get(this.targetPath));
        checkModel(model);
    }

    @Override
    protected Path getPomPath() {
        return null;
    }

    @Override
    public List<Dependency> directDependencies() throws ArtifactDescriptorException {
        return model().getDependencies().stream()
                .map(TransUtils::maven2aether)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dependency> dependenciesByJar() throws Exception {
        return null;
    }

    @Override
    public DependencyNode dependenciesTreeByJar() throws Exception {
        return null;
    }

    @Override
    public List<Version> findAvailableVersions() throws VersionRangeResolutionException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public Version findLatestVersion() throws VersionRangeResolutionException {
        return new GenericVersion(model().getVersion());
    }

    @Override
    protected boolean cacheAsideReadFlag(MavenCommandEnum commandEnum) throws IOException {
        final String fileName = commandEnum.getFileName();
        final Path dependencyFilePath = getMavenCommandFileRoot().resolve(fileName);
        if(!Files.exists(dependencyFilePath)){
            return true;
        }else{
            // 获取pom文件上次的修改时间
            final LocalDateTime pomDateTime = FileUtils.lastModifiedTime(getPomPath());
            // 获取命令文件的上次修改的时间
            final LocalDateTime mavenFileDateTime = FileUtils.lastModifiedTime(dependencyFilePath);
            return pomDateTime.compareTo(mavenFileDateTime) > 0;
        }
    }

    @Override
    protected void cacheAsideWrite() throws IOException {
        // local pom resolver无需写文件缓存
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    private void checkModel(Model model) throws IOException {
        final Path path = Paths.get(FileConstants.LOCAL_TEMP_PATH).resolve(DateTimeUtils.nowDateTime() + "-pom.xml");
        Model copy = model.clone();
        if(CollectionUtils.isNotEmpty(model.getModules())){
            copy.setModules(null);
        }
        writeModel(copy, path);
    }

    private static Artifact buildArtifact(Model model, Path pomPath){
        final String groupId = model.getGroupId();
        final String artifactId = model.getArtifactId();
        final String version = model.getVersion();
        final DefaultArtifact artifact = new DefaultArtifact(buildString(id -> {
            id.append((groupId == null) ? "[inherited]" : groupId);
            id.append(":");
            id.append(artifactId);
            id.append(":");
            id.append("pom");
            id.append(":");
            id.append((version == null) ? "[inherited]" : version);
        }));
        artifact.setFile(pomPath.toFile());
        return artifact;
    }
}
