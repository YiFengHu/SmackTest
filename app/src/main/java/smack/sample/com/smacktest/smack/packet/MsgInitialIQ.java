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
package smack.sample.com.smacktest.smack.packet;

import org.jivesoftware.smack.packet.IQ;

public class MsgInitialIQ  extends IQ{

    public static final String CHILD_ELEMENT_NAME = IQ.QUERY_ELEMENT;
    public static final String CHILD_NAMESPACE = CustomPacketTag.INITIAL_IQ_NAMESPACE;
    public static final String INITIAL_ELEMENT_NAME = CustomPacketTag.INITIAL_IQ_ELEMENT_NAME;
    public static final String DEVICE_ID_ELEMENT_NAME = CustomPacketTag.INITIAL_DEVICE_ID;

    private long mInitialTimeStamp;
    private String mDeviceId;

    public MsgInitialIQ(IQ iq) {
        super(iq);
        addExtensions(iq.getExtensions());
    }

    public MsgInitialIQ(){
        this(CHILD_ELEMENT_NAME, CHILD_NAMESPACE);
        setType(IQ.Type.get);
    }

    protected MsgInitialIQ(String childElementName) {
        super(childElementName);
    }

    protected MsgInitialIQ(String childElementName, String childElementNamespace) {
        super(childElementName, childElementNamespace);
    }

    public void setInitialTimeStamp(long offlineTimestamp){
        mInitialTimeStamp = offlineTimestamp;
    }

    public long getInitialTimeStamp(){
        return mInitialTimeStamp;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    @Override
    protected IQ.IQChildElementXmlStringBuilder getIQChildElementBuilder(IQ.IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.append("<").append(INITIAL_ELEMENT_NAME).append(">");
        xml.append(String.valueOf(getInitialTimeStamp()));
        xml.append("</").append(INITIAL_ELEMENT_NAME).append(">");

        xml.append("<").append(DEVICE_ID_ELEMENT_NAME).append(">");
        xml.append(String.valueOf(getDeviceId()));
        xml.append("</").append(DEVICE_ID_ELEMENT_NAME).append(">");
        return xml;
    }
}
