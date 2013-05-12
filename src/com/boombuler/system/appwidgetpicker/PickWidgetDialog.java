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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import com.boombuler.system.appwidgetpicker.item.BaseItem;
import com.boombuler.system.appwidgetpicker.item.GroupItem;
import com.boombuler.system.appwidgetpicker.item.WidgetItem;

public class PickWidgetDialog {
    private final AppWidgetPickerActivity fOwner;
    private AlertDialog dialog;
    private ItemAdapter adapter;

    public PickWidgetDialog(AppWidgetPickerActivity owner) {
        fOwner = owner;
    }

    public void showDialog() {
        showDialog(null);
    }

    public void showDialog(BaseItem item) {
        if (item == null || item instanceof GroupItem) {
            AlertDialog.Builder ab = new AlertDialog.Builder(fOwner);
            if (item == null) {
                ab.setTitle(fOwner.getString(R.string.widget_picker_title));
                adapter = new ItemAdapter(fOwner, fOwner.getWidgetList());
            } else {
                GroupItem groupItem = (GroupItem)item;
                if (groupItem.getItems().size() == 1) {
                    fOwner.pickWidget(groupItem.getItems().get(0));
                    return;
                }
                ab.setTitle(item.getName());
                adapter = new ItemAdapter(fOwner, groupItem.getItems());
            }
            ab.setAdapter(adapter, new ClickListener());
            ab.setOnCancelListener(new CancelListener(item == null));
            dialog = ab.create();
            dialog.show();
        } else {
            fOwner.pickWidget((WidgetItem)item);
        }
    }

    private void dismiss() {
        dialog.dismiss();
    }

    private class ClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            BaseItem subItem = PickWidgetDialog.this.adapter.getItem(which);
            PickWidgetDialog.this.dismiss();
            PickWidgetDialog.this.showDialog(subItem);
        }
    }

    private class CancelListener implements OnCancelListener {
        private final boolean fCancelOwner;

        public CancelListener(boolean cancelOwner) {
            fCancelOwner = cancelOwner;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (fCancelOwner) {
                PickWidgetDialog.this.fOwner.setResult(AppWidgetPickerActivity.RESULT_CANCELED);
                PickWidgetDialog.this.fOwner.finish();
            } else {
                PickWidgetDialog.this.showDialog();
            }
        }
    }
}
