package com.the21codes.do_it;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class SettingsAdapter extends ArrayAdapter<String> {

    private List<Boolean> mEnabledList;

    public SettingsAdapter(Context context, List<String> settingsList, List<Boolean> enabledList) {
        super(context, R.layout.list_item_settings, settingsList);
        mEnabledList = enabledList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_settings, parent, false);
            textView = (TextView) convertView.findViewById(R.id.text_view_setting);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        textView.setText(getItem(position));
        if (!mEnabledList.get(position)) {
            textView.setAlpha(0.5f); // dim the text for disabled items
        }
        if (!mEnabledList.get(position)) {
            textView.setAlpha(0.5f); // dim the text for disabled items
        } else {
            textView.setAlpha(1f); // reset alpha value for enabled items
        }
        return convertView;
    }

    public void setEnabled(int position, boolean enabled) {
        mEnabledList.set(position, enabled);
        notifyDataSetChanged();
    }



}

