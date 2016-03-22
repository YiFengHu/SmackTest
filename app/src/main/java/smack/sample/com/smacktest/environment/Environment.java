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

    public static Environment Evironment_1 = new Environment("Evironment_1", "host", "service name", 5222, "account" , "pwd");

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
        allEnvironment.add(Evironment_1);
        return allEnvironment;
    }
}
