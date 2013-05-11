package com.boombuler.system.appwidgetpicker.item;

import android.graphics.drawable.Drawable;

public class BaseItem {
    protected String fName;
    protected Drawable fImage;

    public BaseItem(String name, Drawable image) {
        fName = name;
        fImage = image;
    }

    public String getName() {
        return fName;
    }

    public Drawable getImage() {
        return fImage;
    }
}
