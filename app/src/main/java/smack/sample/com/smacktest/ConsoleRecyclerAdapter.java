/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 * <p/>
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2016/3/21
 * Usage:
 * <p/>
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/
package smack.sample.com.smacktest;

/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 * <p/>
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2016/1/7
 * Usage:
 * <p/>
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ConsoleRecyclerAdapter extends RecyclerView.Adapter<ConsoleRecyclerAdapter.ViewHolder>{

    private static final String TAG = ConsoleRecyclerAdapter.class.getSimpleName();

    private Context context;
    private List<String> data;
    private OnItemClickListener listener;


    public ConsoleRecyclerAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.data = new ArrayList<>(1000);
        this.listener = listener;
    }

    public void setData(List<String> data){
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // create a new view

        View itemLayoutView = LayoutInflater.from(context)
                .inflate(R.layout.console_item, viewGroup, false);
        return new ViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.txt.setText(data.get(position));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getItem(int position){
        return data.get(position);
    }

    public void addLogs(String log) {
        data.add(log);
        notifyItemInserted(data.size()-1);
    }

    public synchronized void addLogs(List<String> log) {
        for (int i = 0; i < log.size(); i++) {
            data.add(log.get(i));
            notifyItemInserted(data.size()-1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private TextView txt;

        ViewHolder(View view) {
            super(view);
            txt = (TextView) view.findViewById(R.id.consoleItem_log);
        }


        @Override
        public void onClick(View v) {
            if(listener!=null){
                listener.onItemClick(getLayoutPosition());
            }
        }

    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
