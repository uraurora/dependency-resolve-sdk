package com.uraurora.dependency.resolver;

import com.uraurora.dependency.resolver.value.ArtifactDiff;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.resolution.ArtifactDescriptorException;

import java.util.List;
import java.util.Set;

/**
 * @author gaoxiaodong
 */
public interface IDependencyCollector {

    /**
     * 获取依赖列表
     * @return 依赖列表
     * @throws Exception 异常信息
     */
    List<Dependency> dependencies() throws Exception;

    /**
     * 获取最外层依赖列表
     * @return 最外层显式引入的依赖列表
     * @throws Exception 异常信息
     */
    List<Dependency> dependenciesExplicitly() throws Exception;

    /**
     * 获取传递依赖的依赖树信息
     * @return 返回传递依赖树的根节点，详细使用参考{@link DependencyNode#accept(DependencyVisitor)}
     * @throws Exception runtime异常
     */
    DependencyNode dependenciesTree() throws Exception;

    /**
     * 获取artifact直接引入的依赖
     * @return 直接依赖列表
     * @throws ArtifactDescriptorException 异常
     */
    List<Dependency> directDependencies() throws ArtifactDescriptorException;


    /**
     * 依赖交集
     * @param other 另一个resolver
     * @return 依赖交集
     * @throws Exception runtime异常
     */
    Set<Dependency> intersection(IDependencyCollector other) throws Exception;

    /**
     * 依赖并集
     * @param other 另一个resolver
     * @return 依赖并集
     * @throws Exception runtime异常
     */
    Set<Dependency> union(IDependencyCollector other) throws Exception;

    /**
     * 获取和另一个依赖管理文件的依赖区别
     * @param other see{@link IDependencyCollector}
     * @return see {@link ArtifactDiff} ,依赖区别列表
     * @throws Exception 异常
     */
    ArtifactDiff diff(IDependencyCollector other) throws Exception;

    /**
     * 获取和另一个依赖管理文件的依赖区别
     * @param other see{@link IDependencyCollector}
     * @return see {@link ArtifactDiff} ,依赖区别列表
     * @throws Exception 异常
     */
    ArtifactDiff diffExplicitly(IDependencyCollector other) throws Exception;

    /**
     * 根据下载jar信息来获取依赖列表，只有remote仓库中存在的artifact才可以调用此方法，参考{@link com.sankuai.fbi.dependency.sdk.dependency.impl.RemoteArtifactResolver}
     * @return 传递依赖列表
     * @throws Exception 异常信息
     */
    List<Dependency> dependenciesByJar() throws Exception;

    /**
     * 根据jar信息获取依赖树根节点，只有remote仓库中存在的artifact才可以调用此方法，参考{@link com.sankuai.fbi.dependency.sdk.dependency.impl.RemoteArtifactResolver}
     * @return 传递依赖根节点
     * @throws Exception 异常信息
     */
    DependencyNode dependenciesTreeByJar() throws Exception;


}
