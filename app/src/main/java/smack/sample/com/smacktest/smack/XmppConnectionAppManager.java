/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/5/11
 * Usage:
 *
 * RevisionHistory
 * Date    		Author      Description
 * 2015/07/15   Chungsheng  fix checkAndReconnect method,add login status condition
 * 2015/08/11   Roder.Y.Hu  add initRosterConfig method to set roster config
 * 2015/08/18   Roder.Y.Hu  add connection FromMode setting
 * 2015/08/18   Roder.Y.Hu  add IQProvider, AckExtensionProvider, ReadExtensionProvider
 * 2015/09/09   Roder.Y.Hu  add SendExtensionProvider
 * 2015/09/21   Roder.Y.Hu  add addOfflineMessageAsyncStanzaListener, mOfflineMessageFilter, mNotOfflineMessageFilter
 * 2015/09/22   Roder.Y.Hu  add BroadcastExtensionProvider, ChatJidExtensionProvider
 * 2015/10/13   Chungsheng  fix bug [NCHATAN-650]
 ********************************************************************/
package smack.sample.com.smacktest.smack;

import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;

import java.io.IOException;
import java.util.ArrayList;

import smack.sample.com.smacktest.config.Config;
import smack.sample.com.smacktest.smack.packet.CustomPacketTag;
import smack.sample.com.smacktest.smack.packet.MessageInitialIQProvider;
import smack.sample.com.smacktest.smack.ssl.HTTPSTrustManager;


public class XmppConnectionAppManager implements ConnectionListener {

    public static final String TAG = XmppConnectionAppManager.class.getSimpleName();

    private static XmppConnectionAppManager instance;

    private String host;
    private String serviceName;
    private int port;
    private final int connectTimeout = 15000;

    private Context mContext = null;

    private String mResource = null;
    private boolean showSmackLog = true;
    private XMPPTCPConnectionConfiguration.Builder mConnectionConfigBuilder = null;
    private XMPPTCPConnection mXmppTcpConnection = null;
    private ReconnectionManager mReconnectionManager = null;
    private ArrayList<StanzaListener> mPacketListenerList = null;

    private String mCurrentRemoteJid = null;

    /**
     * retry count
     */
    private int retryCount;

    /**
     * max retry connection count
     */
    public static final int maxRetryCount=3;

    private XmppConnectionAppManager(){
        mPacketListenerList = new ArrayList<StanzaListener>(10);
    }

    public static XmppConnectionAppManager getInstance(){
        if (instance == null){
            instance = new XmppConnectionAppManager();
        }

        return instance;
    }

    /**
     * Init XmppConnectionAppManager before using.
     * @param resource Xmpp connection resource
     */
    public void init(Context context, String resource, boolean allowSSLWithCert){
        mContext = context;
        mResource = resource;

        host = Config.HOST;
        port = Integer.valueOf(Config.PORT);
        serviceName = Config.SERVICE_NAME;


        initConfig(allowSSLWithCert);
        initProvider();
        initXmppConnection();
        initRosterConfig();
        initListener();
        initSmackModel();
    }

    private void initConfig(boolean allowSSLWithCert){

        mConnectionConfigBuilder = XMPPTCPConnectionConfiguration.builder()
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setServiceName(serviceName)
                .setSendPresence(true)
                .setCompressionEnabled(false)
                .setHost(host)
                .setPort(port)
                .setResource(mResource)
                .setDebuggerEnabled(showSmackLog)
                .setConnectTimeout(connectTimeout);
        if (allowSSLWithCert) {
            try {
                mConnectionConfigBuilder.setSocketFactory(HTTPSTrustManager.getSSLContextAllowAll().getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }

            SASLAuthentication.unBlacklistSASLMechanism(SASLMechanism.PLAIN);
            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.DIGESTMD5);
            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.CRAMMD5);
            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.EXTERNAL);
            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.GSSAPI);

        }


//            SASLAuthentication.unBlacklistSASLMechanism(SASLMechanism.PLAIN);
//            SASLAuthentication.blacklistSASLMechanism(SASLMechanism.DIGESTMD5);

        //Openfire
//          .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
//          .setCustomSSLContext(HTTPSTrustManager.getSSLContextWithCert(this))

        //ELB
    }

    /**
     * register custom tag
     */
    private void initProvider(){

        ProviderManager.addIQProvider(CustomPacketTag.INITIAL_IQ_ELEMENT_NAME, CustomPacketTag.INITIAL_IQ_ELEMENT_NAME, new MessageInitialIQProvider());

    }

    private void initXmppConnection(){
        mXmppTcpConnection = new XMPPTCPConnection(mConnectionConfigBuilder.build());
        mXmppTcpConnection.setFromMode(XMPPConnection.FromMode.USER);
        mXmppTcpConnection.addConnectionListener(XmppConnectionAppManager.this);
        //Handle reconnection
        mReconnectionManager = ReconnectionManager.getInstanceFor(mXmppTcpConnection);
        mReconnectionManager.enableAutomaticReconnection();
    }


    private void initRosterConfig() {
        Roster.getInstanceFor(mXmppTcpConnection).setSubscriptionMode(Roster.SubscriptionMode.manual);
//        mXmppTcpConnection.addSyncStanzaListener(getSubscriptionListener(), getSubscriptionFilter());
    }

    private void initListener() {

    }

    private void initSmackModel() {
        SmackModel.getInstance().initConnection(mXmppTcpConnection);
    }

    public String getUserJid(){
        return mXmppTcpConnection.getUser();
    }

    public void connect(){
        Log.i(TAG, "XmppConnection connect: host= " + host + ", serviceName= " + serviceName+ ", port= " + port);

        (new Thread(){
            public void run() {
                try {
                    //Method connect() can't be called in main thread - Roder.Y.Hu
                    mXmppTcpConnection.connect();
                    enableReconnection(true);
//                    init retry count
                    initRetryCount();
                } catch (SmackException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * detect can retry connection
     * @return true,can retry
     */
    public boolean canRetry(){
        return retryCount<=maxRetryCount?true:false;
    }

    /**
     * init retry count
     */
    public void initRetryCount(){
        retryCount=0;
    }

    public void disconnect(){
        Log.i(TAG, "XmppConnection disconnect");

        if(mXmppTcpConnection.isConnected()){
            enableReconnection(false);
            mXmppTcpConnection.disconnect();
            mXmppTcpConnection.removeConnectionListener(this);
        }
    }

    public boolean isConnect(){
        if(mXmppTcpConnection!=null) {
            return mXmppTcpConnection.isConnected();
        }else{
            return false;
        }
    }

    public boolean isLogin(){
        if(mXmppTcpConnection!=null) {
            return mXmppTcpConnection.isAuthenticated();
        }else{
            return false;
        }
    }

    public boolean isRecconectEnable(){
        if(mReconnectionManager!=null) {
            return mReconnectionManager.isAutomaticReconnectEnabled();
        }else{
            return false;
        }
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG, "The connection is connected.");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG, "The connection is authenticated.");
    }

    @Override
    public void connectionClosed() {
        //Notification that the connection was closed normally or that the reconnection process has been aborted.
        Log.e(TAG, "The connection is closed.");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        //Notification that the connection was closed due to an exception.
        Log.e(TAG, "The connection is closed on error. Exception e= "+e);
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(TAG, "The connection is reconnecting in " + seconds);

    }

    @Override
    public void reconnectionSuccessful() {
        Log.d(TAG, "The connection is reconnected successfully.");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.e(TAG, "The connection is reconnected fail.");
    }

    private void enableReconnection(boolean enable){
        if(mXmppTcpConnection!=null){
            if(enable){
                ReconnectionManager.getInstanceFor(mXmppTcpConnection).enableAutomaticReconnection();
                ReconnectionManager.getInstanceFor(mXmppTcpConnection).setFixedDelay(5);
            }else{
                ReconnectionManager.getInstanceFor(mXmppTcpConnection).disableAutomaticReconnection();
            }
        }
    }

    /**
     * remove all chat message listener
     */
    public void removeAllListeners(){
        for(StanzaListener listener :mPacketListenerList){
            mXmppTcpConnection.removeSyncStanzaListener(listener);
        }
    }

    public void addSyncStanzaListener(StanzaListener stanzaListener){
            int i = mPacketListenerList.indexOf(stanzaListener);
        if (i == -1) {
            mXmppTcpConnection.addSyncStanzaListener(stanzaListener, null);
            mPacketListenerList.add(stanzaListener);
        }

    }

    public void addAsyncStanzaListener(StanzaListener stanzaListener){
        int i = mPacketListenerList.indexOf(stanzaListener);
        if (i == -1) {
            mXmppTcpConnection.addAsyncStanzaListener(stanzaListener, null);
            mPacketListenerList.add(stanzaListener);
        }

    }


    public void removeIQListener(StanzaListener iqStanzaListener){
        int i = mPacketListenerList.indexOf(iqStanzaListener);
        if (i != -1) {
            mPacketListenerList.remove(i);
            mXmppTcpConnection.removeSyncStanzaListener(iqStanzaListener);
        }
    }

    public XMPPTCPConnection getConnection(){
        return mXmppTcpConnection;
    }


    public String getResource() {
        return mResource;
    }

    /**
     * Set current remote jid if you are at chat page.
     * @param currentRemoteJid Current remote jid (friend jid or group jid)
     */
    public void setCurrentRemoteJid(String currentRemoteJid) {
        this.mCurrentRemoteJid = currentRemoteJid;
    }

    public String getCurrentRemoteJid() {
        return mCurrentRemoteJid;
    }

    public enum XmppConnectionStatus{
        Connect, Disconnect, Authenticated, Reconnect;
    }
}
