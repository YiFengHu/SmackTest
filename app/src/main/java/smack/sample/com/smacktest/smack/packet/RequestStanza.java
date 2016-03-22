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

import java.util.ArrayList;
import java.util.List;

public class RequestStanza {

    public static final RequestStanza IQ_MsgInitial = new RequestStanza("MsgInitialReqIQ",
            "<iq id=\"oc252-3\" type=\"get\" from=\"%s\">" +
                    "<query xmlns=\"neweggchat:message:initial\">" +
                    "<initial>1440000000000</initial>" +
                    "<deviceId>%s</deviceId>" +
                    "</query>" +
                    "</iq>");

    public static final RequestStanza IQ_OfflineReq = new RequestStanza("OfflineMsgReqIQ",
            "<iq id=\"oc252-3\" type=\"result\" to=\"%s\">\n" +
                    "<query xmlns=\"neweggchat:message:offline\">\n" +
                    "<offline>1440000000000</offline>\n" +
                    "</query>\n" +
                    "</iq>");



    private final String stanzaName;
    private final String stanza;

    protected RequestStanza(String stanzaName, String stanza){

        this.stanzaName = stanzaName;
        this.stanza = stanza;
    }

    public String getStanzaName() {
        return stanzaName;
    }

    public String getStanza(String jid, String resource) {
        return String.format(stanza, jid, resource);
    }

    public static List<RequestStanza> values(){
        List<RequestStanza> values = new ArrayList<>();

        values.add(IQ_MsgInitial);
        values.add(IQ_OfflineReq);
        return values;
    }
}
