package com.github.yu_zhejian.ystr.unsorted;

public abstract class BaseTrieNode implements TrieNodeInterface {
    protected Object value = null;

    /** Whether a word ends here. This separates real word ends with intermediate nodes. */
    protected boolean isWordEnd = false;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }
}
