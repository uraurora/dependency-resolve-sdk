package com.uraurora.dependency.resolver.enums;

import lombok.Getter;

import static com.uraurora.dependency.resolver.constants.MavenConstants.DEPENDENCY_LIST_FILE_NAME;
import static com.uraurora.dependency.resolver.constants.MavenConstants.DEPENDENCY_TREE_FILE_NAME;

@Getter
public enum MavenCommandEnum {
    /**
     * 依赖树
     */
    DEPENDENCY_TREE(DEPENDENCY_TREE_FILE_NAME),

    /**
     * 依赖列表
     */
    DEPENDENCY_LIST(DEPENDENCY_LIST_FILE_NAME)
    ;

    private final String fileName;

    MavenCommandEnum(String fileName) {
        this.fileName = fileName;
    }
}
