package smack.sample.com.smacktest.smack.packet;

/**
 * define xmpp custom tag
 */
public class CustomPacketTag {

    /* Custom Tag : Share */

	public static final String SHARE_ELEMENT_NAME = "share";
	
	public static final String SHARE_NAMESPACE = "neweggchat/message/share";
	
	public static final String PATH_ELEMENT_NAME = "path";
	
	public static final String URL_ELEMENT_NAME = "url";
	
	public static final String OWNER_ID_ELEMENT_NAME = "owner_id";
	
	public static final String FILE_NAME_ELEMENT_NAME = "filename";

    /* Custom Tag : Location */

    public static final String LOCATION_ELEMENT_NAME = "location";
	
	public static final String LONGITUDE_ELEMENT_NAME = "longitude";
	
	public static final String LATITUDE_ELEMENT_NAME = "latitude";

    /* Custom Tag : Category */

    public static final String CATEGORY_ELEMENT_NAME = "category";

	public static final String CATEGORY_NAMESPACE = "neweggchat/message/category";

    /* Custom Tag : Data Type */

    public static final String DATA_TYPE_ELEMENT_NAME = "datatype";
	
	public static final String DATA_TYPE_NAMESPACE = "neweggchat/message/datatype";

    public static final String CHAT_STATE_NAMESPACE = "http://jabber.org/protocol/chatstates";

    /* Custom Tag : Offline Message */

    public static final String OFFLINE_IQ_ELEMENT_NAME = "offline";

    public static final String OFFLINE_IQ_NAMESPACE = "neweggchat:message:offline";

    public static final String DELAY_INFORMATION_END_NAME = "end";

     /* Custom Tag : Online Message (disconnect for short time) */

    public static final String ONLINE_IQ_ELEMENT_NAME = "online";

    public static final String ONLINE_IQ_SELECT_ELEMENT_NAME = "select";

    public static final String ONLINE_IQ_NAMESPACE = "neweggchat:message:online";

    /* Custom Tag : ACK */

    public static final String ACK_ELEMENT_NAME = "ack";

    public static final String ACK_NAMESPACE = "neweggchat.message.ack";

    public static final String ACK_SERVER_MID = "server_mid";

    public static final String ACK_CHAT_JID = "chat_jid";

    public static final String ACK_STATUS = "status";


    /* Custom Tag : Read */

    public static final String READ_ELEMENT_NAME = "read";

    public static final String READ_NAMESPACE = "neweggchat.message.read";

    public static final String READ_SERVER_MID_START = "server_mid_start";

    public static final String READ_SERVER_MID_END = "server_mid_end";

    public static final String READ_CHAT_JID = "chat_jid";

    /* Custom Tag : Server_Mid */

    public static final String SERVER_MID_ELEMENT_NAME = "server_mid";

    public static final String SERVER_MID_NAMESPACE = "neweggchat.message";

     /* Custom Tag : Send */

    public static final String SEND_ELEMENT_NAME = "send";

    public static final String SEND_NAMESPACE = "newegg.send.end";

     /* Custom Tag : Send */

    public static final String BROADCAST_ELEMENT_NAME = "broadcast";

    public static final String BROADCAST_NAMESPACE = "neweggchat.broadcast";

    public static final String BROADCAST_TYPE = "type";

    public static final String BROADCAST_INFO = "info";

    /* Custom Tag : Chat_jid */

    public static final String CHAT_JID_ELEMENT_NAME = "chat_jid";

    public static final String CHAT_JID_NAMESPACE = "newegg.message.chat_jid";

     /* Custom Tag : Multi Device IQ */

    public static final String MULTI_DEVICE_IQ_ELEMENT_NAME = "enable";

    public static final String MULTI_DEVICE_IQ_NAMESPACE = "urn:xmpp:carbons:2";

    /* Custom Tag : Message Initial */

    public static final String INITIAL_IQ_ELEMENT_NAME = "initial";

    public static final String INITIAL_IQ_NAMESPACE = "neweggchat:message:initial";

    public static final String INITIAL_DEVICE_ID = "deviceId";
}
