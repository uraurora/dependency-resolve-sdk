package com.uraurora.dependency.resolver;

import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.version.Version;

import java.util.List;

/**
 * @author gaoxiaodong
 */
public interface IVersionCollector {

    /**
     * 获取所有可用的版本
     * @return 版本列表
     * @throws VersionRangeResolutionException 版本限制异常
     */
    List<Version> findAvailableVersions() throws VersionRangeResolutionException;

    /**
     * 获取最新的版本
     * @return 获取最新的版本
     * @throws VersionRangeResolutionException 版本限制异常
     */
    Version findLatestVersion() throws VersionRangeResolutionException;

}
