package com.uraurora.dependency.resolver.value;

import lombok.Data;
import org.eclipse.aether.graph.Dependency;

import java.util.List;
import java.util.Set;

import static com.uraurora.dependency.resolver.util.Options.*;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 16:41
 * @description :
 */
@Data
public class ArtifactDiff {

    private Set<Dependency> source;

    private Set<Dependency> target;
    /**
     * 不同版本包，指定是相同的包，不同的版本包
     */
    private final List<DependencyDiff> diff;

    /**
     * 相同依赖但是版本不同，与diff内容相同，但是不包括变更到的版本信息
     */
    private final List<Dependency> difference;

    /**
     * 独立包，指的是标准包中不包含，业务包中包含的包
     */
    private final List<Dependency> independence;

    /**
     * 增量包，指的是标准包中包含而业务包中不包含的包
     */
    private final List<Dependency> increment;

    /**
     * 相同包，指的是artifact相同，版本也相同的包
     */
    private final List<Dependency> same;

    public static ArtifactDiff none(){
        return new ArtifactDiff(listOf(), listOf(), listOf(), listOf(), listOf());
    }

    public String diffOutput(){
        return buildString(sb->{
            sb.append("============================   版本变更的包  ============================").append(System.lineSeparator());
            for (DependencyDiff dependencyDiff : diff) {
                sb.append(dependencyDiff.toString()).append(System.lineSeparator());
            }
            sb.append(System.lineSeparator());
            sb.append("============================   版本不变的包  ===========================").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            for (Dependency dependency : same) {
                sb.append(dependency.toString()).append(System.lineSeparator());
            }
            for (Dependency dependency : independence) {
                sb.append(dependency.toString()).append(System.lineSeparator());
            }
        });
    }

}
