/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/5/14
 * Usage:
 *
 * RevisionHistory
 * Date    		Author      Description
 * 2015/7/7     Roder.Y.Hu  Add Chat functions:  setChat(String friendJid),
 *                                               removeChat(),
 *                                               sendChatState(ChatState chatState)
 *
 ********************************************************************/
package smack.sample.com.smacktest.smack;

import android.util.Log;

import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PlainStreamElement;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.privacy.PrivacyList;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import smack.sample.com.smacktest.smack.packet.MsgInitialIQ;

public class SmackModel{

    private static final String TAG = SmackModel.class.getSimpleName();
    private static final long IQ_TIMEOUNT = 3000L;

    private static volatile SmackModel mInstance = null;

    private final String BLOCK_LIST_TAG = "BLOCK_LIST_TAG";

    protected String SEARCH_DOMAIN = null;
    protected static final String SMACK_ANSWER_USER_NAME = "Username";
    protected static final String SMACK_ANSWER_SEARCH_KEY = "search";
    protected static final String SEARCH_KEY_STRING = "search.";

    private XMPPTCPConnection mXmppTCPConnection = null;
    private MultiUserChatManager mMultiUserChatManager = null;
    private MultiUserChat mMultiUserChat = null;
    private PrivacyListManager mPrivacyListMgr = null;
    private ArrayList<String> mBlockList = null;

    private ChatStateManager mChatStateManager = null;
    private Chat mChat = null;
    private String mChattingFriendJid = null;

    private int retryCount = 0;
    private IQ mTempRetryIQ = null;

    private StanzaListener mIQResponseListener = new StanzaListener() {
        @Override
        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
            Log.d(TAG, "IQ response");
            if(packet instanceof MsgInitialIQ) {
                MsgInitialIQ iq = ((MsgInitialIQ) packet);
                Log.d(TAG, "IQ response: iq[" + iq + "]");
            }

        }
    };

    private ExceptionCallback mExceptionCallBack = new ExceptionCallback() {
        @Override
        public void processException(Exception exception) {
                retryCount++;

                if(retryCount < 3){
                    if(mTempRetryIQ!=null){

                        try {
                            sendIqWithResponseCallback(mTempRetryIQ);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                            Log.e(TAG, "retry send IQ error: NotConnectedException["+e.toString()+"]");
                        }

                    }else{
                        Log.e(TAG, "retry send IQ error: mTempRetryIQ is null!!");
                    }
                }else{
                    Log.e(TAG, "retry send IQ error: retry more than 3 times!!");
                }
            }
    };

    private SmackModel() {

    }

    public static SmackModel getInstance() {
        if (mInstance == null) {
            mInstance = new SmackModel();
        }
        return mInstance;
    }

    public void initConnection(XMPPTCPConnection connection){
        mXmppTCPConnection = connection;
        mMultiUserChatManager = MultiUserChatManager.getInstanceFor(mXmppTCPConnection);
        mPrivacyListMgr = PrivacyListManager.getInstanceFor(mXmppTCPConnection);

        //Init search domain
        SEARCH_DOMAIN = SEARCH_KEY_STRING+mXmppTCPConnection.getServiceName();
    }

    /**
     * Login to Openfire Server
     *
     * @param account  Registed account (SSO token)
     * @param password Registed password (Openfire account password, default:123456)
     * @return The login result, true if success
     */
    public boolean login(String account, String password) {
        Log.d(TAG, "login user: "+account+", "+password);
        try {
            mXmppTCPConnection.login(account, password);
            Log.e(TAG, "Openfire Login Success!");

            return true;
        } catch (XMPPException e) {
            Log.e(TAG, "Login fail! XMPPException e=" + e);
            e.printStackTrace();
            return false;

        } catch (SmackException e) {
            Log.e(TAG, "Login fail! SmackException e=" + e);
            e.printStackTrace();

            return (e instanceof SmackException.AlreadyLoggedInException);

        } catch (IOException e) {
            Log.e(TAG, "Login fail! IOException e=" + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send a stanza
     *
     * @param packet A Stanza object to send
     */
    public void sendStanza(Stanza packet) throws SmackException.NotConnectedException {
        try {
            mXmppTCPConnection.sendStanza(packet);

            Log.e(TAG, "Stanza Sent!");

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Send stanza fail! NotConnectedException e=" + e);
            e.printStackTrace();
            throw e;
        }
    }


    public void send(PlainStreamElement element) throws SmackException.NotConnectedException {
        try {
            mXmppTCPConnection.send(element);
            Log.e(TAG, "Stanza Sent!");

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Send stanza fail! NotConnectedException e=" + e);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Send a IQ stanza and invoke callback if there is a result of 'IQ.Type.result' with that result IQ.
     *
     * If there is an error response exceptionCallback will be invoked,
     * if not null, with the received error as XMPPException.XMPPErrorException.
     *
     * If there is no response after timeout,
     * then exceptionCallback will be invoked with a SmackException.NoResponseException.
     *
     * @param iq
     * @throws SmackException.NotConnectedException
     */
    public void sendIqWithResponseCallback(IQ iq) throws SmackException.NotConnectedException {
        try {
            mTempRetryIQ = iq;
            mXmppTCPConnection.sendIqWithResponseCallback(iq, mIQResponseListener, mExceptionCallBack, IQ_TIMEOUNT);
            Log.e(TAG, "Stanza Sent!");

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Send stanza fail! NotConnectedException e=" + e);
            e.printStackTrace();
            throw e;
        }
    }

//    public boolean joinChatRoom(String roomJid) {
//        mMultiUserChat = mMultiUserChatManager.getMultiUserChat(roomJid);
//
//        if (mMultiUserChat != null) {
//            try {
//                mMultiUserChat.join(AppManagerCenter.getInstance().getLoginAppManager().getJid());
//                return true;
//            } catch (SmackException.NoResponseException e) {
//                Log.e(TAG, "join room:" + roomJid + " fail! NoResponseException e=" + e);
//                e.printStackTrace();
//                return false;
//
//            } catch (XMPPException.XMPPErrorException e) {
//                Log.e(TAG, "join room:" + roomJid + " fail! XMPPErrorException e=" + e);
//                e.printStackTrace();
//                return false;
//
//            } catch (SmackException.NotConnectedException e) {
//                Log.e(TAG, "join room:" + roomJid + " fail! NotConnectedException e=" + e);
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        Log.e(TAG, "join room:" + roomJid + " fail! ");
//        return false;
//    }

    /**
     * Block user's message
     *
     * @param userJid
     * @return block result
     */
    public boolean blockUser(String userJid) {
        try {
            if (isPrivacyListExisting() == false) {
                List<PrivacyItem> items = new ArrayList<PrivacyItem>();
                PrivacyItem newitem = new PrivacyItem(PrivacyItem.Type.jid, userJid, false, 1);
                newitem.setFilterMessage(true);
                newitem.setFilterIQ(false);
                newitem.setFilterPresenceIn(false);
                newitem.setFilterPresenceOut(false);
                Log.d(TAG, "blockUser XML :" + newitem.toXML());
                items.add(newitem);
                mPrivacyListMgr.createPrivacyList(BLOCK_LIST_TAG, items);
                mPrivacyListMgr.setDefaultListName(BLOCK_LIST_TAG);
                mPrivacyListMgr.setActiveListName(BLOCK_LIST_TAG);

            } else {
                PrivacyList privacyList = mPrivacyListMgr.getPrivacyList(BLOCK_LIST_TAG);
                if (privacyList != null) {
                    List<PrivacyItem> items = privacyList.getItems();
                    PrivacyItem newitem = new PrivacyItem(PrivacyItem.Type.jid, userJid, false, 1);
                    newitem.setFilterMessage(true);
                    newitem.setFilterIQ(false);
                    newitem.setFilterPresenceIn(false);
                    newitem.setFilterPresenceOut(false);
                    Log.d(TAG, "blockUser XML :" + newitem.toXML());
                    items.add(newitem);
                    mPrivacyListMgr.updatePrivacyList(BLOCK_LIST_TAG, items);
                    mPrivacyListMgr.setActiveListName(BLOCK_LIST_TAG);
                }
            }

            return true;
        } catch (SmackException.NoResponseException e) {
            Log.e(TAG, "block user:" + userJid + " fail! NoResponseException e=" + e);
            e.printStackTrace();

        } catch (XMPPException.XMPPErrorException e) {
            Log.e(TAG, "block user:" + userJid + " fail! XMPPErrorException e=" + e);
            e.printStackTrace();

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "block user:" + userJid + " fail! NotConnectedException e=" + e);
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Unblock user's message
     *
     * @param userJid
     * @return unblock result
     */
    public boolean unblockUser(String userJid) {
        Log.d(TAG, "unblockUser:" + userJid);
        try {
            PrivacyList privacyList = mPrivacyListMgr.getPrivacyList(BLOCK_LIST_TAG);
            if (privacyList != null) {
                List<PrivacyItem> items = privacyList.getItems();

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getValue().equals(userJid)) {
                        items.remove(i);
                        Log.d(TAG, "unblockUser remove:" + userJid);
                    }
                }

                if (items.size() == 0) {
                    mPrivacyListMgr.deletePrivacyList(BLOCK_LIST_TAG);
                } else {
                    mPrivacyListMgr.updatePrivacyList(BLOCK_LIST_TAG, items);
                    mPrivacyListMgr.setActiveListName(BLOCK_LIST_TAG);
                }

                return true;
            }
        } catch (SmackException.NoResponseException e) {
            Log.e(TAG, "unblock user:" + userJid + " fail! NoResponseException e=" + e);
            e.printStackTrace();
            return false;
        } catch (XMPPException.XMPPErrorException e) {
            Log.e(TAG, "unblock user:" + userJid + " fail! XMPPErrorException e=" + e);
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "unblock user:" + userJid + " fail! NotConnectedException e=" + e);
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * isPrivacyListExisting
     * return false: not exist
     * return true: exist
     *
     * @return boolean
     */
    private boolean isPrivacyListExisting() {
        try {
            PrivacyList list = mPrivacyListMgr.getPrivacyList(BLOCK_LIST_TAG);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Set {@link org.jivesoftware.smack.chat.Chat} and {@link org.jivesoftware.smackx.chatstates.ChatStateManager} to target friendJid.
     * @param friendJid Current chatting target jid.
     */
    public void setChat(String friendJid){
        Log.i(TAG, "set Chat: "+friendJid);
        mChat = ChatManager.getInstanceFor(mXmppTCPConnection).createChat(friendJid, null);
        mChatStateManager = ChatStateManager.getInstance(mXmppTCPConnection);
        mChattingFriendJid = friendJid;
    }

    /**
     * Remove resource of Chat and ChatStateManager
     */
    public void removeChat(){
        Log.i(TAG, "removeChat");
        mChat = null;
        mChatStateManager = null;
    }

    /**
     * Send ChateState to current chatting target.
     * @param chatState {@link org.jivesoftware.smackx.chatstates.ChatState} you want to send.
     */
    public void sendChatState(ChatState chatState){

        if(mChat !=null && mChatStateManager !=null) {
            try {

                mChatStateManager.setCurrentState(chatState, mChat);
                Log.i(TAG, "send ChatState: "+chatState.name()+" to "+mChattingFriendJid);

            } catch (SmackException.NotConnectedException e) {
                Log.e(TAG, "sendChatState: " + chatState.name()
                        + " to " + mChat.getParticipant() + " fail! NotConnectedException e=" + e);

                e.printStackTrace();
            }

        }else{

            Log.e(TAG, "sendChatState fail! You should invoke setChat(friendJid) first!!");
        }

    }

    public void setMultiUseChat(String roomJid){
        mMultiUserChat = MultiUserChatManager.getInstanceFor(mXmppTCPConnection)
                .getMultiUserChat(roomJid);
    }

    public boolean joinGroupChatRoom(String userJid, String roomJid){
        setMultiUseChat(roomJid);

        try {
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxChars(0);

            mMultiUserChat.join(userJid, null, history,
                    SmackConfiguration.getDefaultPacketReplyTimeout());
            Log.d(TAG, "Join Room: " + roomJid);
            return true;
        } catch (XMPPException.XMPPErrorException e) {
            Log.e(TAG, "joinRoom fail, Exception= " + e);
            e.printStackTrace();
            return false;

        } catch (SmackException.NoResponseException e) {
            Log.e(TAG, "joinRoom fail, Exception= " + e);
            e.printStackTrace();
            return false;

        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "joinRoom fail, Exception= " + e);
            e.printStackTrace();
            return false;
        }
    }
}
