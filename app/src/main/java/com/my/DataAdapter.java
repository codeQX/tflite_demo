package com.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.android.tflitecamerademo.R;

import java.util.ArrayList;

/**
 * Created by wxd on 2017-06-09.
 */
public class DataAdapter extends BaseAdapter{

    private LayoutInflater mLayoutInflater = null;
    @SuppressWarnings("unused")
    private Context mContext = null;
    private ArrayList<BeanData> mArrayList = null;

    public DataAdapter(Context context, ArrayList<BeanData> arrayList) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mArrayList = arrayList;
    }

    @Override
    public int getCount() {
        int size = 0;
        if (mArrayList != null && mArrayList.size() > 0) {
            size = mArrayList.size();
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.activity_heart_listitem, null);

            holder = new ViewHolder();

            TextView resultTextView = (TextView) convertView.findViewById(R.id.resultTextView);
            TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
            TextView positionTextView = (TextView) convertView.findViewById(R.id.positionTextView);

            holder.resultTextView = resultTextView;
            holder.timeTextView = timeTextView;
            holder.positionTextView = positionTextView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BeanData item = mArrayList.get(position);
        holder.positionTextView.setText(String.valueOf(position));
        holder.resultTextView.setText(item.result);
        holder.timeTextView.setText(String.format("耗时：%d 毫秒",item.time));


        return convertView;
    }

    private static class ViewHolder {
        TextView positionTextView;
        TextView resultTextView;
        TextView timeTextView;
    }
}
