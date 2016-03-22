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

import android.util.Log;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.debugger.AbstractDebugger;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CustomDebugger extends AbstractDebugger{

    private static final String TAG = CustomDebugger.class.getSimpleName();

    private static List<String> logs = new ArrayList<>(1000);
    private static List<DebugListener> listeners = new ArrayList<>();

    public CustomDebugger(XMPPConnection connection, Writer writer, Reader reader) {
        super(connection, writer, reader);
    }

    @Override
    protected void log(String logMessage) {
        logs.add(logMessage);

        for (int i = 0; i < listeners.size(); i++) {
            if(listeners.get(i)!=null){
                listeners.get(i).onLog(logMessage);
            }
        }
    }

    public static synchronized void registerListener(DebugListener listener){
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
    }

    public static synchronized void unRegisterListener(DebugListener listener){
            if (listeners.contains(listener)) {
                listeners.remove(listener);
            }

    }

    public static List<String> getLogs(){
        return logs;
    }

    public interface DebugListener{
        void onLog(String log);
    }

}
