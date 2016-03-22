/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/10/20
 * Usage:
 *
 * RevisionHistory
 * Date    		Author    Description
 ********************************************************************/
package smack.sample.com.smacktest.environment;

import java.util.ArrayList;
import java.util.List;

public class Environment {

    public static Environment BOX_PREPRD_BROADCAST = new Environment("BOX_PREPRD_BROADCAST", "test-broad.neweggtech.com", "test-broad.neweggtech.com", 5222, "box.service" , "123456");


    public static Environment PREDEV_3_in_1 = new Environment("PREDEV_3_in_1", "10.16.197.79", "10.16.197.79", 5222, "roder12" , "123456");//Dara2

    public static Environment CHAT_PREDEV = new Environment("CHAT_PREDEV", "10.16.197.78", "c1alboc-2063163598.ap-northeast-1.elb.amazonaws.com", 5222, "3e549f95b22d2825df3384207aa5dad62f4dc6ae" , "123456");//Dara2
    public static Environment CHAT_DEV = new Environment("CHAT_DEV", "c1alboc-2063163598.ap-northeast-1.elb.amazonaws.com", "c1alboc-2063163598.ap-northeast-1.elb.amazonaws.com", 5222, "2fbfe1a04282d1fab25f11362504dfa18f153895" , "123456");//RoderTest1
    public static Environment CHAT_PREPRD = new Environment("CHAT_PREPRD", "test-chat-service.neweggtech.com", "test-chat-service.neweggtech.com", 5222, "0df2f1b5fd2734d71129d34e2676e37ea080a31e" , "123456");//Roder.Y.Hu
    public static Environment CHAT_PRD = new Environment("CHAT_PRD", "chat-service.neweggtech.com", "chat-service.neweggtech.com", 5222, "1f561fddcca6a7dcd8350d8de37faceeed919579" , "123456");//Roder.Y.Hu

    private String name;
    private String host;
    private String serviceName;
    private int port = 5222;

    private String account;
    private String pwd = "123456";

    Environment(String name, String host, String serviceName, int port, String account, String pwd){
        this.name = name;
        this.host = host;
        this.serviceName = serviceName;
        this.port = port;
        this.account = account;
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getAccount() {
        return account;
    }

    public String getPwd() {
        return pwd;
    }

    public String getServiceName() {
        return serviceName;
    }

    public static List<Environment> values(){
        List<Environment> allEnvironment = new ArrayList<>();
        allEnvironment.add(BOX_PREPRD_BROADCAST);
        allEnvironment.add(CHAT_PREDEV);
        allEnvironment.add(CHAT_DEV);
        allEnvironment.add(CHAT_PREPRD);
        allEnvironment.add(CHAT_PRD);

        allEnvironment.add(PREDEV_3_in_1);

        return allEnvironment;
    }
}
