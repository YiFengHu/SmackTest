/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/8/17
 * Usage:
 *
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/
package smack.sample.com.smacktest.smack.packet;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MessageInitialIQProvider extends IQProvider<MsgInitialIQ> {

    @Override
    public MsgInitialIQ parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {

        String offlineTimeString = "";
        String deviceId = "";
        String currentTag = null;
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_TAG || !parser.getName().equalsIgnoreCase(MsgInitialIQ.CHILD_ELEMENT_NAME)) {

            switch (eventType) {

                case XmlPullParser.START_TAG:
                    currentTag = parser.getName();

                    break;

                case XmlPullParser.TEXT:

                    if (MsgInitialIQ.INITIAL_ELEMENT_NAME.equalsIgnoreCase(currentTag)) {
                        offlineTimeString = parser.getText();
                    }else if(MsgInitialIQ.DEVICE_ID_ELEMENT_NAME.equalsIgnoreCase(currentTag)){
                        deviceId = parser.getText();
                    }
                    break;

                case XmlPullParser.END_TAG:

                    break;

                default:
                    break;
            }
            eventType = parser.next();
        }

        MsgInitialIQ iq = new MsgInitialIQ();
        iq.setInitialTimeStamp(Long.valueOf(offlineTimeString));
        iq.setDeviceId(deviceId);
        return iq;
    }
}
