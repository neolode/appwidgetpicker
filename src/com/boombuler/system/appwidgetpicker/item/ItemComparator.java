package com.boombuler.system.appwidgetpicker.item;

import java.util.Comparator;

public class ItemComparator implements Comparator<BaseItem> {
    @Override
    public int compare(BaseItem object1, BaseItem object2) {
        return object1.getName().compareToIgnoreCase(object2.getName());
    }
}
