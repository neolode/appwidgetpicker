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
    protected Context context;
    protected LayoutInflater inflater;
    private ArrayList<? extends BaseItem> items;

    public ItemAdapter(Context context, ArrayList<? extends BaseItem> items) {
        this.items = items;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh;
        if (view == null) {
            view = inflater.inflate(R.layout.appwidgetpicker, null);
            vh = new ViewHolder();
            vh.nameView = (TextView)view.findViewById(R.id.name);
            vh.countView = (TextView)view.findViewById(R.id.count);
            vh.imageView = (ImageView)view.findViewById(R.id.icon);
            view.setTag(vh);
        } else {
            vh = (ViewHolder)view.getTag();
        }
        vh.populateFrom(getItem(position));
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

    private class ViewHolder {
        private TextView nameView;
        private TextView countView;
        private ImageView imageView;

        private void populateFrom(BaseItem item) {
            boolean hideWidgetSize = true;
            int widgetCount = 0;
            if (item instanceof GroupItem) {
                widgetCount = ((GroupItem)item).getItems().size();
                hideWidgetSize = (widgetCount == 1);
            }
            nameView.setText(item.getName());
            if (!hideWidgetSize) {
                countView.setText(String.format(context.getString(R.string.widget_count), widgetCount));
            }
            imageView.setImageDrawable(item.getImage());
            if (hideWidgetSize) {
                nameView.setMaxLines(2);
                countView.setVisibility(View.GONE);
            } else {
                nameView.setMaxLines(1);
                countView.setMaxLines(1);
            }
        }
    }
}
