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

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.PlainStreamElement;

import java.util.ArrayList;
import java.util.List;

import smack.sample.com.smacktest.smack.SmackModel;
import smack.sample.com.smacktest.smack.XmppConnectionAppManager;
import smack.sample.com.smacktest.smack.packet.RequestStanza;
import smack.sample.com.smacktest.smack.packet.RequestStanzaAdapter;

public class StanzaTestActivity extends AppCompatActivity implements CustomDebugger.DebugListener,
        ConsoleRecyclerAdapter.OnItemClickListener {


    private RecyclerView consoleListView;
    private Button sendButton;
    private EditText stanzaEditText;
    private Spinner stanzaTypeSpinner;

    private ConsoleRecyclerAdapter consoleAdapter;

    private RequestStanzaAdapter stanzaAdapter;
    private String currentJid = null;

    private Boolean batchHandling = false;
    private List<String> batchLogs = new ArrayList<>(1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stanza_test);
        currentJid = XmppConnectionAppManager.getInstance().getUserJid();

        initLayout();
    }

    private void initLayout() {
        consoleListView = (RecyclerView) findViewById(R.id.console_listView);
        sendButton = (Button) findViewById(R.id.sendbutton);
        stanzaEditText = (EditText) findViewById(R.id.plain_stanza_editText);
        stanzaTypeSpinner = (Spinner) findViewById(R.id.stanza_type_spinner);

        consoleAdapter = new ConsoleRecyclerAdapter(this, this);
        consoleAdapter.setData(CustomDebugger.getLogs());
//        consoleListView.setLayoutManager(new LinearLayoutManager(this));
        consoleListView.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        consoleListView.setAdapter(consoleAdapter);


        stanzaAdapter = new RequestStanzaAdapter(this);
        stanzaTypeSpinner.setAdapter(stanzaAdapter);

        stanzaTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RequestStanza requestStanza = stanzaAdapter.getItem(position);
                stanzaEditText.setText(requestStanza
                        .getStanza(currentJid, getAndroidId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SmackModel.getInstance().send(new PlainStreamElement() {
                        @Override
                        public CharSequence toXML() {
                            return stanzaEditText.getText().toString();
                        }
                    });
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomDebugger.registerListener(this);
    }

    @Override
    protected void onPause() {
        CustomDebugger.unRegisterListener(this);
        super.onPause();
    }

    public String getAndroidId(){
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }

    @Override
    public void onLog(String log) {
//        batchLogs.add(log);
        consoleAdapter.addLogs(log);
        consoleListView.scrollToPosition(consoleAdapter.getItemCount() - 1);
    }

//    private void batchHandleLog(){
//        synchronized (batchHandling) {
//            if (batchHandling) {
//                return;
//            }
//
//            batchHandling = true;
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Thread.sleep(1000);
//
//                    synchronized (batchLogs){
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                consoleAdapter.addLogs(batchLogs);
//                                consoleListView.scrollToPosition(consoleAdapter.getItemCount()-1);
//                            }
//                        });
//
//                        batchLogs.clear();
//                    }
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    private void showLogDialog(String log){
        new AlertDialog.Builder(this)
            .setTitle("Log")
            .setMessage(log).create().show();
    }

    @Override
    public void onItemClick(int position) {
        String log = consoleAdapter.getItem(position);
        if (log != null){
            showLogDialog(log);
        }
    }
}
