package com.github.yu_zhejian.ystr.trie;

/** The base class of trie nodes. */
public abstract class BaseTrieNode implements TrieNodeInterface {
    /** The value hold by the node */
    protected Object value = null;

    /** Whether a word ends here. This separates real word ends with intermediate nodes. */
    protected boolean isWordEnd = false;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public boolean isWordEnd() {
        return isWordEnd;
    }
}
