/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 * <p/>
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2016/1/22
 * Usage:
 * <p/>
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/
package smack.sample.com.smacktest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smack.sample.com.smacktest.smack.packet.RequestStanza;

public class ConsoleAdapter extends BaseAdapter {

    private List<String> data = new ArrayList<>(1000);

    private Context context;

    public ConsoleAdapter(Context context) {
        this.context = context;
    }
    
    public void setData(List<String> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.console_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.log.setText(getItem(position));

        return convertView;
    }

    public synchronized void addLogs(String log) {
        data.add(log);
        notifyDataSetChanged();
    }

    public synchronized void addLogs(List<String> log) {
        for (int i = 0; i < log.size(); i++) {
            data.add(log.get(i));
        }
        notifyDataSetChanged();
    }

    class ViewHolder {

        public TextView log;

        ViewHolder(View rootView) {
            log = (TextView) rootView.findViewById(R.id.consoleItem_log);
        }

    }

}
