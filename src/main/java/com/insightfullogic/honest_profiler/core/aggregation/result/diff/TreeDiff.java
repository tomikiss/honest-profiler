package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

/**
 * Diff which wraps provides the difference between two tree {@link Aggregation}s (containing {@link Node}s) as a tree
 * of {@link DiffNode}s, which each wrap and provide the difference between corresponding {@link Node}s.
 *
 * @param the type of the key
 */
public class TreeDiff extends AbstractDiff<Node, DiffNode, Tree>
{
    // Instance Properties

    private Map<String, DiffNode> data;

    // Instance Constructors

    /**
     * Empty constructor.
     */
    public TreeDiff()
    {
        data = new HashMap<>();
    }

    /**
     * Internal Copy constructor.
     *
     * @param entries the {@link List} of {@link DiffNode}s to be copied into this Diff
     */
    private TreeDiff(List<DiffNode> entries)
    {
        data = new HashMap<>();
        entries.forEach(entry -> data.put(entry.getKey(), entry));
    }

    /**
     * Sets a {@link Tree} as Base or New. Provided as convenience for {@link #setBase(Tree)} and {@link #setNew(Tree)},
     * to make it possible for the calling code to avoid the if-construction.
     *
     * @param aggregation the {@link Tree} to be set as Base or New in this Diff.
     * @param isBase a boolean indicating whether the {@link Tree} has to be set as Base.
     */
    public void set(Tree baseTree, Tree newTree)
    {
        super.setAggregations(baseTree, newTree);
        data.clear();

        baseTree.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode(node, null) : v.setBase(node)));

        newTree.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode(null, node) : v.setNew(node)));

    }

    /**
     * Returns the {@link DiffNode}s from this Diff.
     *
     * @return a {@link Collection} containing the {@link DiffNode}s from this Diff
     */
    public Collection<DiffNode> getData()
    {
        return data.values();
    }

    // AbstractDiff Implementation

    @Override
    public TreeDiff filter(FilterSpecification<DiffNode> filterSpec)
    {
        return new TreeDiff(
            getData().stream().map(node -> node.copyWithFilter(filterSpec.getFilter()))
                .filter(node -> node != null).collect(toList()));
    }
}
