package dev.tmm.chatmate.util;

import java.util.Arrays;
import java.util.Collection;

public class MultiPageList {
    private int pageSize;
    private String[] elements;

    public MultiPageList(int pageSize, Collection<String> elements) {
        this.pageSize = pageSize;
        this.elements = elements.toArray(new String[elements.size()]);
    }

    public MultiPageList(int pageSize, String... elements) {
        this.pageSize = pageSize;
        this.elements = elements;
    }

    public String[] getPage(int page) {
        return Arrays.copyOfRange(elements, Math.min(page * pageSize, elements.length), Math.min(page * pageSize + pageSize, elements.length));
    }
}