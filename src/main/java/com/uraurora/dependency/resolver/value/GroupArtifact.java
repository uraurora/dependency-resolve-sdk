package com.uraurora.dependency.resolver.value;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 17:25
 * @description :
 */
@Data
@AllArgsConstructor
public class GroupArtifact {
    private String groupId;

    private String artifactId;
}
