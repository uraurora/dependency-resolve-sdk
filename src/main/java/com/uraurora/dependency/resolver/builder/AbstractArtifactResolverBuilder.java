package com.uraurora.dependency.resolver.builder;

import com.uraurora.dependency.resolver.AbstractArtifactResolver;
import com.uraurora.dependency.resolver.constants.FileConstants;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-26 17:13
 * @description :
 */
public abstract class AbstractArtifactResolverBuilder {

   protected String mavenHome = System.getenv("MAVEN_HOME");

   protected String localCachePath = FileConstants.LOCAL_TEMP_PATH;

   protected List<RemoteRepository> remoteRepositories;

    public AbstractArtifactResolverBuilder withMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
        return this;
    }

    public AbstractArtifactResolverBuilder withLocalCachePath(String localCachePath) {
        this.localCachePath = localCachePath;
        return this;
    }

    public AbstractArtifactResolverBuilder withRemoteRepositories(List<RemoteRepository> remoteRepositories) {
        this.remoteRepositories = remoteRepositories;
        return this;
    }

    public String getMavenHome() {
        return mavenHome;
    }

    public String getLocalCachePath() {
        return localCachePath;
    }

    public List<RemoteRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    /**
     * 构建解析器
     * @return 解析器实现类
     * @throws Exception e
     */
    public abstract AbstractArtifactResolver build() throws Exception;
}
