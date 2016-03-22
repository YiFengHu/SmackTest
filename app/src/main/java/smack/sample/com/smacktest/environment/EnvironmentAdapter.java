/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/10/20
 * Usage:
 *
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/
package smack.sample.com.smacktest.environment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import smack.sample.com.smacktest.R;

public class EnvironmentAdapter extends BaseAdapter{

    List<Environment> data = Environment.values();

    private Context context;

    public EnvironmentAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Environment getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.environment_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.environmentName.setText(getItem(position).getName());

        return convertView;
    }

    class ViewHolder{

        public TextView environmentName;

        ViewHolder(View rootView){
            environmentName = (TextView)rootView.findViewById(R.id.environmentItem_name);
        }

    }
}
