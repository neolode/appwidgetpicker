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
    private ArrayList<BaseItem> items;

    public ItemAdapter(Context context, ArrayList<BaseItem> items) {
        this.items = items;
        fContext = context;
        inflater = (LayoutInflater)fContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.appwidgetpicker, null);
        }
        BaseItem o = getItem(position);
        TextView nameView = (TextView)v.findViewById(R.id.name);
        TextView countView = (TextView)v.findViewById(R.id.count);
        ImageView imageView = (ImageView)v.findViewById(R.id.icon);
        if (nameView != null) {
            nameView.setText(o.getName());
        }
        if (o instanceof GroupItem) {
            int cnt = ((GroupItem)o).getItems().size();
            if (cnt > 1) {
                countView.setText(String.format(fContext.getString(R.string.widget_count), cnt));
                countView.setVisibility(View.VISIBLE);
            } else {
                countView.setVisibility(View.GONE);
            }
        } else {
            countView.setVisibility(View.GONE);
        }
        imageView.setImageDrawable(o.getImage());
        return v;
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
