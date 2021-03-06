package com.uraurora.dependency.resolver.util;

import com.uraurora.dependency.resolver.constants.FileConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.shared.invoker.*;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.version.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.uraurora.dependency.resolver.util.Options.*;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-10-27 19:54
 * @description :
 */
public abstract class ResolveUtils {

    private static final String STRING_NONE = "none";

    public static RepositorySystem newSystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        return map(locator, l -> {
            l.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
            l.addService(TransporterFactory.class, FileTransporterFactory.class);
            l.addService(TransporterFactory.class, HttpTransporterFactory.class);
            return l.getService(RepositorySystem.class);
        });
    }

    public static RepositorySystemSession newSession(RepositorySystem system, Path path) {
        return map(system, s -> {
            DefaultRepositorySystemSession res = MavenRepositorySystemUtils.newSession();
            LocalRepository localRepo = new LocalRepository(path.toFile());
            res.setLocalRepositoryManager(s.newLocalRepositoryManager(res, localRepo));
            return res;
        });
    }

    public static boolean isRemote(String groupId, String artifact, String version, List<RemoteRepository> remoteRepositories) {
        try {
            VersionRangeResult rangeResult = getVersionRangeResult(groupId, artifact, remoteRepositories);
            return rangeResult.getVersions().stream()
                    .map(Version::toString)
                    .collect(Collectors.toList())
                    .contains(version);
        } catch (VersionRangeResolutionException e) {
            return false;
        }
    }

    public static boolean isRemote(String groupId, String artifact, List<RemoteRepository> remoteRepositories) {
        try {
            VersionRangeResult rangeResult = getVersionRangeResult(groupId, artifact, remoteRepositories);
            return rangeResult.getVersions().size() > 0;
        } catch (VersionRangeResolutionException e) {
            return false;
        }
    }

    public static VersionRangeResult getVersionRangeResult(String groupId, String artifact, List<RemoteRepository> remoteRepositories) throws VersionRangeResolutionException {
        final RepositorySystem system = newSystem();
        final RepositorySystemSession session = newSession(system, Paths.get(FileConstants.LOCAL_TEMP_PATH));
        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setRepositories(remoteRepositories);
        rangeRequest.setArtifact(new DefaultArtifact(buildString(sb -> {
            sb.append(groupId).append(":")
                    .append(artifact).append(":")
                    .append("[,)");
        })));
        final VersionRangeResult result = system.resolveVersionRange(session, rangeRequest);
        return result;
    }


    /**
     * ???????????????????????????artifact??????pom???????????????
     *
     * @param artifact artifact
     * @param path     ?????????????????????
     * @return ??????maven pom???????????????
     * @throws ArtifactResolutionException ????????????
     * @throws IOException                 io??????
     */
    public static Path download(Artifact artifact, Path path, List<RemoteRepository> remoteRepositories) throws ArtifactResolutionException, IOException {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        RepositorySystem system = map(locator, l -> {
            l.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
            l.addService(TransporterFactory.class, FileTransporterFactory.class);
            l.addService(TransporterFactory.class, HttpTransporterFactory.class);
            return l.getService(RepositorySystem.class);
        });
        RepositorySystemSession session = map(system, s -> {
            DefaultRepositorySystemSession res = MavenRepositorySystemUtils.newSession();
            LocalRepository localRepo = new LocalRepository(path.toFile());
            res.setLocalRepositoryManager(s.newLocalRepositoryManager(res, localRepo));
            return res;
        });

        ArtifactRequest artifactRequest = with(
                new ArtifactRequest(artifact, remoteRepositories, null),
                request -> request.setArtifact(artifact)
        );
        ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

        File file = artifactResult.getArtifact().getFile();
        String workDirectory = file.getParentFile().getAbsolutePath();
        Path pomPath = Paths.get(workDirectory).resolve("download-pom.xml");
        Files.copy(file.toPath(), pomPath, StandardCopyOption.REPLACE_EXISTING);
        return pomPath;
    }

    /**
     * ????????????pom?????????????????????mvn??????
     *
     * @param pomPath   pom????????????
     * @param mavenHome maven???????????????
     * @param command   ????????????"dependency:list -DoutputFile=dependency.txt",????????????????????????List
     * @throws MavenInvocationException invoke??????
     * @throws CommandLineException     ???????????????
     */
    public static void executeMaven(Path pomPath, Path mavenHome, List<String> command) throws MavenInvocationException, CommandLineException {
        try {
            InvocationRequest invocationRequest = new DefaultInvocationRequest();
            // ??????????????????
            invocationRequest.setPomFile(pomPath.toFile());
            invocationRequest.setGoals(command);
            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(mavenHome.toFile());
            InvocationResult result = invoker.execute(invocationRequest);
            if (result.getExitCode() != 0) {
                if (result.getExecutionException() != null) {
                    throw result.getExecutionException();
                } else {
                    throw new RuntimeException("maven invoke exception, exitCode=" + result.getExitCode());
                }
            }
        } catch (MavenInvocationException | CommandLineException | RuntimeException e) {
            throw new RuntimeException("maven invoke exception, exception=" + e);
        }
    }

    public static List<Dependency> parseList(Path dependencyListPath) throws IOException {
        return Files.lines(dependencyListPath)
                .skip(2)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .map(ResolveUtils::parseListLine)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * ??????????????????????????????
     * @param coordinate ??????
     * @return ????????????
     */
    public static Dependency parseListLine(String coordinate) {
        Dependency dependencyView;
        try {
            if (coordinate.equalsIgnoreCase(STRING_NONE)) {
                return null;
            }
            dependencyView = parseDepCoordinate(coordinate);
        } catch (Exception ex) {
            throw new RuntimeException("error coordinate: " + coordinate, ex);
        }
        return dependencyView;
    }

    /**
     * ????????????????????????
     * @param coordinate ??????
     * @return ????????????
     */
    public static Dependency parseDepCoordinate(String coordinate) {
        final String[] split = coordinate.split(":");
        final int index;
        final String scope;
        if(split.length >= 5) {
            index = coordinate.lastIndexOf(":");
            final String[] depProps = coordinate.substring(index + 1).split(" ");
            scope = depProps[0];
        }else {
            index = coordinate.length();
            scope = "";
        }
        final String artifactCoordinate = coordinate.substring(0, index);
        Artifact artifact = new DefaultArtifact(artifactCoordinate);
        return new Dependency(
                artifact,
                scope,
                coordinate.contains("(optional)")
        );
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param dependencyFilePath ??????????????????
     * @return ?????????????????????
     * @throws IOException io??????
     */
    public static List<Dependency> parseTreeOutSide(Path dependencyFilePath) throws IOException {
        return Files.lines(dependencyFilePath)
                .skip(1)
                .map(StringUtils::trim)
                .filter(line -> line.startsWith("+-") || line.startsWith("\\-"))
                .map(line -> line.substring(3))
                .map(ResolveUtils::parseListLine)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * maven dependency??????????????????????????????????????????????????????
     *
     * @param dependency ??????
     * @return ???????????????
     */
    public static String serialize(org.apache.maven.model.Dependency dependency) {
        if (dependency == null) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(128);
        if (dependency.getGroupId() != null) {
            buffer.append(dependency.getGroupId());
        }
        if (dependency.getArtifactId() != null) {
            buffer.append(':').append(dependency.getArtifactId());
        }
        if (dependency.getVersion() != null) {
            buffer.append(':').append(dependency.getType());
        }
        if (StringUtils.isNotBlank(dependency.getClassifier()) && dependency.getClassifier().length() > 0) {
            buffer.append(':').append(dependency.getClassifier());
        }
        if (dependency.getVersion() != null) {
            buffer.append(':').append(dependency.getVersion());
        }
        if (dependency.getScope() != null) {
            buffer.append(':').append(dependency.getScope());
        }
        final boolean isOptional = StringUtils.isNotBlank(dependency.getOptional()) &&
                "true".equalsIgnoreCase(dependency.getOptional());
        if (isOptional) {
            buffer.append(" (")
                    .append("optional")
                    .append(")");
        }
        return buffer.toString();
    }

    public static org.apache.maven.model.Dependency deserialize(String str) {
        final org.eclipse.aether.graph.Dependency dependency = parseDepCoordinate(str);
        return TransUtils.aether2Maven(dependency);
    }


}
