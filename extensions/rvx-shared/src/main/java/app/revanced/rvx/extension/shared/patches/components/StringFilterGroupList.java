package app.revanced.rvx.extension.shared.patches.components;

import app.revanced.rvx.extension.shared.utils.StringTrieSearch;

public final class StringFilterGroupList extends FilterGroupList<String, StringFilterGroup> {
    protected StringTrieSearch createSearchGraph() {
        return new StringTrieSearch();
    }
}
