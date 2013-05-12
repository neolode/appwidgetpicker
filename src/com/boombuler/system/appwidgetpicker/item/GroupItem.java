/*
 * Copyright (C) 2010 Florian Sundermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boombuler.system.appwidgetpicker.item;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;

public class GroupItem extends BaseItem {
    private final ArrayList<WidgetItem> fItems = new ArrayList<WidgetItem>();
    private String fPackageName;

    public GroupItem(String name, Drawable image) {
        super(name, image);
    }

    public ArrayList<WidgetItem> getItems() {
        return fItems;
    }

    public String getName() {
        if (fItems.size() == 1) {
            return fItems.get(0).getName();
        }
        return fName;
    }

    public Drawable getImage() {
        if (fItems.size() == 1) {
            return fItems.get(0).getImage();
        }
        return fImage;
    }

    public String getPackageName() {
        return fPackageName;
    }

    public void setPackageName(String aValue) {
        fPackageName = aValue;
    }

    public void sort() {
        Collections.sort(fItems, new ItemComparator());
    }
}
