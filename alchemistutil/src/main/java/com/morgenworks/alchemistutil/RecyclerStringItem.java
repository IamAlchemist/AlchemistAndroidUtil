package com.morgenworks.alchemistutil;

/**
 * This is Created by wizard on 7/22/16.
 */
public class RecyclerStringItem implements RecyclerSimpleItem {
    private String content;

    public RecyclerStringItem(String content) {
        this.content = content;
    }

    @Override
    public String getText() {
        return content;
    }
}
