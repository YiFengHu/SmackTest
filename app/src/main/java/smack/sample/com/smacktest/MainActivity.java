package smack.sample.com.smacktest;

import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.debugger.ReflectionDebuggerFactory;
import org.jivesoftware.smack.packet.PlainStreamElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.debugger.android.AndroidDebugger;

import smack.sample.com.smacktest.config.Config;
import smack.sample.com.smacktest.environment.Environment;
import smack.sample.com.smacktest.environment.EnvironmentAdapter;
import smack.sample.com.smacktest.smack.SmackModel;
import smack.sample.com.smacktest.smack.XmppConnectionAppManager;
import smack.sample.com.smacktest.smack.packet.MsgInitialIQ;
import smack.sample.com.smacktest.smack.packet.RequestStanza;
import smack.sample.com.smacktest.smack.packet.RequestStanzaAdapter;


public class MainActivity extends AppCompatActivity {

    private Spinner environmentSpinner;
//    private Spinner stanzaSpinner;
    private EnvironmentAdapter environmentAdapter;
//    private RequestStanzaAdapter stanzaAdapter;
    private CheckBox certificationCheckBox;

    private EditText hostEditText;
    private EditText serviceNameEditText;

    private EditText portEditText;

    private EditText userNameEditText;
    private EditText passwordEditText;

    private Button loginButton;
    private Button disconnectButton;

//    private EditText stanzaEditText;
//    private Button sendButton;
//    private Button sendObjectButton;

    private String currentJid = null;


    public static XmppConnectionAppManager xmppConnectionAppManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReflectionDebuggerFactory.setDebuggerClass(CustomDebugger.class);
//        ReflectionDebuggerFactory.setDebuggerClass(AndroidDebugger.class);

        initLayout();
    }

    private void initLayout() {

        hostEditText = (EditText)findViewById(R.id.main_hostText);
        serviceNameEditText = (EditText)findViewById(R.id.main_serviceNameText);

        serviceNameEditText.setText("broad.neweggtech.com");
        hostEditText.setText("broad.neweggtech.com");

        portEditText = (EditText)findViewById(R.id.main_portText);
        userNameEditText = (EditText)findViewById(R.id.main_userNameText);
        passwordEditText = (EditText)findViewById(R.id.main_passwordText);
        loginButton = (Button)findViewById(R.id.main_loginButton);
        disconnectButton = (Button)findViewById(R.id.main_disconnectButton);

//        stanzaEditText = (EditText)findViewById(R.id.main_msgText);
//        sendButton = (Button)findViewById(R.id.main_sendPlainStanzaButton);
//        sendObjectButton = (Button)findViewById(R.id.main_sendStanzaObjectButton);

        environmentSpinner = (Spinner)findViewById(R.id.main_environmentSpinner);
        certificationCheckBox = (CheckBox)findViewById(R.id.main_certificationCheckBox);
        environmentAdapter = new EnvironmentAdapter(this);
        environmentSpinner.setAdapter(environmentAdapter);


//        stanzaSpinner = (Spinner)findViewById(R.id.main_stanzaSpinner);
//        stanzaAdapter = new RequestStanzaAdapter(this);
//        stanzaSpinner.setAdapter(stanzaAdapter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Config.HOST = hostEditText.getText().toString();
                Config.PORT = portEditText.getText().toString();
                Config.SERVICE_NAME = serviceNameEditText.getText().toString();

                xmppConnectionAppManager = XmppConnectionAppManager.getInstance();

                xmppConnectionAppManager.init(getApplicationContext(), getAndroidId(), certificationCheckBox.isChecked());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        xmppConnectionAppManager.getConnection().addConnectionListener(new ConnectionListener() {
                            @Override
                            public void connected(XMPPConnection connection) {
                                SmackModel.getInstance().initConnection(xmppConnectionAppManager.getConnection());
                                SmackModel.getInstance().login(userNameEditText.getText().toString(), passwordEditText.getText().toString());

                                currentJid = xmppConnectionAppManager.getConnection().getUser();

                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, StanzaTestActivity.class);
                                startActivity(intent);
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
                        xmppConnectionAppManager.connect();
                    }

                }).start();
            }
        });

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    SmackModel.getInstance().send(new PlainStreamElement() {
//                        @Override
//                        public CharSequence toXML() {
//                            return stanzaEditText.getText().toString();
//                        }
//                    });
//                } catch (SmackException.NotConnectedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

//        sendObjectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//
//                    MsgInitialIQ iq = new MsgInitialIQ();
//                    iq.setInitialTimeStamp(1440000000000L);
//                    iq.setDeviceId(getAndroidId());
//                    SmackModel.getInstance().sendIqWithResponseCallback(iq);
//                } catch (SmackException.NotConnectedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xmppConnectionAppManager.disconnect();
            }
        });

        environmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Environment environment = environmentAdapter.getItem(position);
                hostEditText.setText(environment.getHost());
                serviceNameEditText.setText(environment.getServiceName());
                portEditText.setText(String.valueOf(environment.getPort()));
                userNameEditText.setText(environment.getAccount());
                passwordEditText.setText(environment.getPwd());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        stanzaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                RequestStanza requestStanza = stanzaAdapter.getItem(position);
//                stanzaEditText.setText(requestStanza
//                            .getStanza(currentJid, getAndroidId()));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getAndroidId(){
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }
}
