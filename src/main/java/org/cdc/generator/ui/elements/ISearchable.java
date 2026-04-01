package org.cdc.generator.ui.elements;

import org.cdc.generator.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ISearchable {
    void doSearch(Map.Entry<String, String> search);

    CompletableFuture<Void> refreshTable();

    void showSearch(int index);

    default Component initSearchBar(ArrayList<Integer> lastSearchResult) {
        return Utils.initSearchComponent(lastSearchResult, this);
    }
}
