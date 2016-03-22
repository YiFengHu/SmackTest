/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/10/21
 * Usage:
 *
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/
package smack.sample.com.smacktest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.List;

public class ConnecttionTestActivity extends Activity{

    List<String> data;

    private Button startButton;
    private TextView summaryTextView;
    private ListView testRowListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_test);
        data = new ArrayList<>();

        initLayout();


    }

    private void initLayout() {
        startButton = (Button)findViewById(R.id.connectionTest_startTestButton);
        summaryTextView = (TextView)findViewById(R.id.connectionTest_summaryReportTextView);
        testRowListView = (ListView)findViewById(R.id.connectionTest_testListView);
    }

    private void updateAdapter(String testMsg){
        data.add(testMsg);
        testRowListView.setAdapter(new TestAdapter(this, data));
    }

    private void startTest(){
        MainActivity.xmppConnectionAppManager.getConnection().addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {

            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {

            }

            @Override
            public void connectionClosed() {

            }

            @Override
            public void connectionClosedOnError(Exception e) {

            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int seconds) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        });
    }

    class TestAdapter extends BaseAdapter {

        List<String> data;

        private Context context;

        public TestAdapter(Context context, List<String> data) {
            this.context = context;
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
                convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.environment_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.result.setText(getItem(position));

            return convertView;
        }

        class ViewHolder {

            public TextView result;

            ViewHolder(View rootView) {
                result = (TextView) rootView.findViewById(R.id.environmentItem_name);
            }

        }
    }
}
