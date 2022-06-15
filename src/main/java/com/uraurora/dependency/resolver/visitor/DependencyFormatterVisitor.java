package com.uraurora.dependency.resolver.visitor;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author : gaoxiaodong04
 * @program : dependency-resolve-sdk
 * @date : 2021-11-26 17:41
 * @description :
 */
public class DependencyFormatterVisitor implements DependencyVisitor {
    private final List<ChildInfo> childInfos;

    private final StringBuilder sb;

    public DependencyFormatterVisitor() {
        this.childInfos = new ArrayList<>();
        this.sb = new StringBuilder();
    }

    public String getContent() {
        return sb.toString();
    }

    @Override
    public boolean visitEnter(DependencyNode node) {
        this.sb.append(this.formatIndentation()).append(this.formatNode(node)).append(System.lineSeparator());
        this.childInfos.add(new DependencyFormatterVisitor.ChildInfo(node.getChildren().size()));
        return true;
    }

    private String formatIndentation() {
        StringBuilder buffer = new StringBuilder(128);
        Iterator<ChildInfo> it = this.childInfos.iterator();

        while(it.hasNext()) {
            buffer.append(it.next().formatIndentation(!it.hasNext()));
        }

        return buffer.toString();
    }

    private String formatNode(DependencyNode node) {
        StringBuilder buffer = new StringBuilder(128);
        Artifact a = node.getArtifact();
        Dependency d = node.getDependency();
        buffer.append(a);
        if (d != null && d.getScope().length() > 0) {
            buffer.append(":").append(d.getScope());
            if (d.isOptional()) {
                buffer.append(" (optional)");
            }
        }

        String premanaged = DependencyManagerUtils.getPremanagedVersion(node);
        if (premanaged != null && !premanaged.equals(a.getBaseVersion())) {
            buffer.append(" (version managed from ").append(premanaged).append(")");
        }

        premanaged = DependencyManagerUtils.getPremanagedScope(node);
        if (premanaged != null && !premanaged.equals(d.getScope())) {
            buffer.append(" (scope managed from ").append(premanaged).append(")");
        }

        DependencyNode winner = (DependencyNode)node.getData().get("conflict.winner");
        if (winner != null && !ArtifactIdUtils.equalsId(a, winner.getArtifact())) {
            Artifact w = winner.getArtifact();
            buffer.append(" (conflicts with ");
            if (ArtifactIdUtils.toVersionlessId(a).equals(ArtifactIdUtils.toVersionlessId(w))) {
                buffer.append(w.getVersion());
            } else {
                buffer.append(w);
            }

            buffer.append(")");
        }

        return buffer.toString();
    }

    @Override
    public boolean visitLeave(DependencyNode node) {
        if (!this.childInfos.isEmpty()) {
            this.childInfos.remove(this.childInfos.size() - 1);
        }

        if (!this.childInfos.isEmpty()) {
            ++this.childInfos.get(this.childInfos.size() - 1).index;
        }

        return true;
    }

    private static class ChildInfo {
        final int count;
        int index;

        public ChildInfo(int count) {
            this.count = count;
        }

        public String formatIndentation(boolean end) {
            boolean last = this.index + 1 >= this.count;
            if (end) {
                return last ? "\\- " : "+- ";
            } else {
                return last ? "   " : "|  ";
            }
        }
    }


}

