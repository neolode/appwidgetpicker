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

package com.boombuler.system.appwidgetpicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.boombuler.system.appwidgetpicker.item.BaseItem;
import com.boombuler.system.appwidgetpicker.item.GroupItem;

import java.util.ArrayList;


public class ItemAdapter extends BaseAdapter {
    protected Context fContext;
    protected LayoutInflater inflater;
    private ArrayList<? extends BaseItem> items;

    private class ViewHolder {
        private TextView nameView;
        private TextView countView;
        private ImageView imageView;
    }

    public ItemAdapter(Context context, ArrayList<? extends BaseItem> items) {
        this.items = items;
        fContext = context;
        inflater = (LayoutInflater)fContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        BaseItem o = getItem(position);
        boolean hideWidgetSize = true;
        int widgetCount = 0;
        if (o instanceof GroupItem) {
            widgetCount = ((GroupItem)o).getItems().size();
            hideWidgetSize = widgetCount == 1;
        }
        if (view == null) {
            view = inflater.inflate(R.layout.appwidgetpicker, null);
            vh = new ViewHolder();
            vh.nameView = (TextView)view.findViewById(R.id.name);
            vh.countView = (TextView)view.findViewById(R.id.count);
            vh.imageView = (ImageView)view.findViewById(R.id.icon);
            if (hideWidgetSize) {
                vh.countView.setVisibility(View.GONE);
                vh.nameView.setMaxLines(2);
            } else {
                vh.nameView.setMaxLines(1);
                vh.countView.setMaxLines(1);
            }
            view.setTag(vh);
        } else {
            vh = (ViewHolder)view.getTag();
        }

        vh.nameView.setText(o.getName());
        if (!hideWidgetSize) {
            vh.countView.setText(String.format(fContext.getString(R.string.widget_count), widgetCount));
        }
        vh.imageView.setImageDrawable(o.getImage());
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public BaseItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
