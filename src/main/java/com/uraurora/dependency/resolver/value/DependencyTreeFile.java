package com.uraurora.dependency.resolver.value;

import com.google.common.base.Splitter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.uraurora.dependency.resolver.util.Options.listOf;
import static com.uraurora.dependency.resolver.util.ResolveUtils.parseDepCoordinate;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-01 17:04
 * @description :
 */
public class DependencyTreeFile {

    private static final int BLANK = 3;

    private final String buffer;

    public DependencyTreeFile(Path file) throws IOException {
        this.buffer = FileUtils.readFileToString(file.toFile(), StandardCharsets.UTF_8);
    }

    public DependencyTreeFile(String str) {
        this.buffer = str;
    }

    public DependencyNode treeRoot() throws IOException {
        final List<String> lines = Splitter.on(System.lineSeparator()).splitToList(buffer);
        int depth = 0;
        DependencyNode root = parseNode(lines.get(0), depth);
        buildTree(root, lines, 0, depth);
        return root;
    }

    private static void buildTree(DependencyNode root, List<String> lines, int left, int depth) {
        // 找到同级的依赖创建子依赖列表
        List<InternalParam> params = findChildren(lines, left, depth + 1);
        // 与root关联,对每个子依赖建树
        root.setChildren(
                params.stream()
                        .map(InternalParam::getDependency)
                        .collect(Collectors.toList())
        );
        params.forEach(p -> buildTree(p.getDependency(), lines, p.getLeft(), depth + 1));
    }

    /**
     * 找到子依赖列表
     *
     * @param lines 文件的行数据列表
     * @param left  根依赖的行数
     * @param depth 树的深度，最初的根深度为9
     * @return 子依赖列表
     */
    private static List<InternalParam> findChildren(List<String> lines, int left, int depth) {
        List<InternalParam> res = listOf();

        String rootLine = lines.get(left);
        int rootIndex = index(rootLine);
        int i = left + 1;

        final int nextIndex = findNextIndex(lines, left, rootIndex);

        while (i < nextIndex) {
            String line = lines.get(i);
            int index = index(line);
            if (index == rootIndex + BLANK) {
                res.add(new InternalParam(
                        i,
                        nextIndex,
                        parseNode(line, depth)
                ));
            }
            i++;
        }

        return res;
    }

    /**
     * 找到当前行中+或\\的index
     *
     * @param line 字符串
     * @return index
     */
    private static int index(String line) {
        final int i = line.contains("+") ? line.indexOf("+-") : line.indexOf("\\-");
        return i == -1 ? -BLANK : i;
    }

    /**
     * 找到这个根依赖同一级的下一个依赖的index
     *
     * @param lines     列表
     * @param index     行数
     * @param charIndex charIndex
     * @return 下一个index作为行数right限制
     */
    private static int findNextIndex(List<String> lines, int index, int charIndex) {
        char c;
        int i = index;
        if (charIndex == -BLANK) {
            return lines.size();
        }
        try {
            do {
                i++;
                c = lines.get(i).charAt(charIndex);
            } while (c != '+' && c != '\\');
            return i;
        } catch (Exception e) {
            return lines.size();
        }
    }

    /**
     * 根据深度解析依赖节点
     *
     * @param coordinate 字符串
     * @param depth      树深度
     * @return 依赖节点
     */
    private static DependencyNode parseNode(String coordinate, int depth) {
        Dependency dependencyView;
        try {
            dependencyView = parseDepCoordinate(coordinate.substring(BLANK * depth));
        } catch (Exception ex) {
            throw new RuntimeException("error coordinate: " + coordinate, ex);
        }
        return new DefaultDependencyNode(dependencyView);
    }


    @Data
    @AllArgsConstructor
    private static class InternalParam {
        private int left;
        private int right;
        private DependencyNode dependency;

    }

}
