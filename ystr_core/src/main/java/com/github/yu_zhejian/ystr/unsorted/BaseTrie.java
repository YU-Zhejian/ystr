package com.github.yu_zhejian.ystr.unsorted;

public abstract class BaseTrie implements TrieInterface {
    protected int treeHeight;
    protected int numWords;
    protected TrieNodeInterface root;

    @Override
    public int numNodes() {
        return root.numNodes();
    }

    @Override
    public int numWords() {
        return numWords;
    }

    @Override
    public int treeHeight() {
        return treeHeight;
    }
}
