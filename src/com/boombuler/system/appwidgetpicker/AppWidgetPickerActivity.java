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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import com.boombuler.system.appwidgetpicker.item.BaseItem;
import com.boombuler.system.appwidgetpicker.item.GroupItem;
import com.boombuler.system.appwidgetpicker.item.ItemComparator;
import com.boombuler.system.appwidgetpicker.item.WidgetItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppWidgetPickerActivity extends Activity {
    private Intent startIntent;
    private PackageManager pkgManager;
    private AppWidgetManager appManager;
    private ArrayList<BaseItem> items;
    private int widgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startIntent = getIntent();
        if (startIntent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            widgetId = startIntent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            pkgManager = getPackageManager();
            appManager = AppWidgetManager.getInstance(this);

            addAndroidWidgets();
            addLauncherWidgets();
            sortItems();

            new PickWidgetDialog(this).showDialog();
        } else {
            finish();
        }
    }

    public ArrayList<BaseItem> getWidgetList() {
        return items;
    }

    public void pickWidget(WidgetItem item) {
        if (item.getExtra() != null) {
            // If there are any extras, it's because this entry is custom.
            // Don't try to bind it, just pass it back to the app.
            setResult(RESULT_OK, getIntent(item));
        } else {
            int result;
            try {
                bindWidget(item);
                result = RESULT_OK;
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.secyrity_err, Toast.LENGTH_LONG).show();
                result = RESULT_CANCELED;
            } catch (InvocationTargetException e) {
                Toast.makeText(this, R.string.secyrity_err, Toast.LENGTH_LONG).show();
                result = RESULT_CANCELED;
            } catch (IllegalAccessException e) {
                result = RESULT_CANCELED;
            } catch (NoSuchMethodException e) {
                result = RESULT_CANCELED;
            } catch (IllegalArgumentException e) {
                // This is thrown if they're already bound, or otherwise somehow
                // bogus.  Set the result to canceled, and exit.  The app *should*
                // clean up at this point.  We could pass the error along, but
                // it's not clear that that's useful -- the widget will simply not
                // appear.
                result = RESULT_CANCELED;
            }
            setResult(result, startIntent);
        }
        finish();
    }

    private Intent getIntent(WidgetItem itm) {
        Intent intent;
        Parcelable parcel = startIntent.getParcelableExtra(Intent.EXTRA_INTENT);
        if (parcel instanceof Intent) {
            intent = new Intent((Intent)parcel);
        } else {
            intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
        }

        if (itm.getProvider() != null) {
            // Valid package and class, so fill details as normal intent
            intent.setClassName(itm.getProvider().getPackageName(), itm.getProvider().getClassName());
        } else {
            // No valid package or class, so treat as shortcut with label
            intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, itm.getName());
        }
        if (itm.getExtra() != null) {
            intent.putExtras(itm.getExtra());
        }
        return intent;
    }

    private GroupItem getPackageItem(String pkgName) {
        for (BaseItem itm : items) {
            if (itm instanceof GroupItem) {
                GroupItem i = (GroupItem)itm;
                if (i.getPackageName().equals(pkgName)) {
                    return i;
                }
            }
        }
        return null;
    }

    private void addAndroidWidgets() {
        items = new ArrayList<BaseItem>();
        List<AppWidgetProviderInfo> infos = appManager.getInstalledProviders();
        for (AppWidgetProviderInfo info : infos) {
            try {
                String pkgName = info.provider.getPackageName();
                android.content.pm.ApplicationInfo appInfo = pkgManager.getApplicationInfo(pkgName, 0);
                WidgetItem widgetItem = new WidgetItem(info.label, pkgManager.getDrawable(info.provider.getPackageName(), info.icon, appInfo));
                widgetItem.setProvider(info.provider);
                GroupItem groupItem = getPackageItem(pkgName);
                if (null == groupItem) {
                    Drawable icon = pkgManager.getApplicationIcon(appInfo);
                    String name = pkgManager.getApplicationLabel(appInfo).toString();
                    groupItem = new GroupItem(name, icon);
                    groupItem.setPackageName(pkgName);
                    items.add(groupItem);
                    Log.d("AppWidgetPicker", "addAndroidWidgets: " + groupItem.getName());
                }
                groupItem.getItems().add(widgetItem);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
    }

    private void addLauncherWidgets() {
        final Bundle extras = startIntent.getExtras();
        ArrayList<AppWidgetProviderInfo> customInfo = extras.getParcelableArrayList(AppWidgetManager.EXTRA_CUSTOM_INFO);
        if (customInfo == null) {
            return;
        }
        ArrayList<Bundle> customExtras = extras.getParcelableArrayList(AppWidgetManager.EXTRA_CUSTOM_EXTRAS);

        final int size = customInfo.size();
        for (int i = 0; i < size; i++) {
            AppWidgetProviderInfo info = customInfo.get(i);
            if (null != info) {
                String label = info.label;
                Drawable icon = null;
                if (info.icon != 0) {
                    icon = pkgManager.getDrawable(info.provider.getPackageName(), info.icon, null);
                }

                GroupItem groupItem = new GroupItem(label, icon);
                WidgetItem widgetItem = new WidgetItem(label, icon);
                groupItem.getItems().add(widgetItem);

                groupItem.setPackageName(info.provider.getPackageName());
                if (customExtras != null) {
                    widgetItem.setExtra(customExtras.get(i));
                }
                items.add(groupItem);
                Log.d("AppWidgetPicker", "addLauncherWidgets: " + groupItem.getName());
            }
        }
    }

    private void sortItems() {
        Collections.sort(items, new ItemComparator());
        for (BaseItem itm : items) {
            if (itm instanceof GroupItem) {
                ((GroupItem)itm).sort();
            }
        }
    }

    private void bindWidget(WidgetItem item)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ComponentName provider = item.getProvider();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // API10 hasn't method AppWidgetManager.bindAppWidgetId
            // But it can be invoked using Reflection API
            Class[] argTypes = new Class[]{int.class, ComponentName.class};
            Method m = AppWidgetManager.class.getMethod("bindAppWidgetId", argTypes);
            Object[] args = new Object[]{widgetId, provider};
            m.invoke(appManager, args);
        } else {
            // invoke method directly
            appManager.bindAppWidgetIdIfAllowed(widgetId, provider);
        }
    }
}
