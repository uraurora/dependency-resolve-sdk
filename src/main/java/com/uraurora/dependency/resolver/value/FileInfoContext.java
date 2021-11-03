package com.uraurora.dependency.resolver.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 16:57
 * @description :
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoContext {
    private Path dir;
    private BasicFileAttributes attributes;
}
