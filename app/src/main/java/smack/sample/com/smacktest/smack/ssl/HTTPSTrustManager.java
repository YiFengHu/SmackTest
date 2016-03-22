/*******************************************************************
 * Copyright  (C) Newegg Corporation. All rights reserved.
 *
 * Author: Roder.Y.Hu (Roder.Y.Hu@newegg.com)
 * Create Date: 2015/8/14
 * Usage:
 *
 * RevisionHistory
 * Date    		Author      Description
 * 2015/8/14    Roder.Y.Hu  Import from BOX
 ********************************************************************/
package smack.sample.com.smacktest.smack.ssl;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HTTPSTrustManager {

    public static SSLContext getSSLContextAllowAll() throws KeyManagementException,
            NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(
                    X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    X509Certificate[] certs, String authType) {
            }
        }};

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(null, trustAllCerts, new java.security.SecureRandom());
        context.createSSLEngine().setEnabledCipherSuites(new String[]{
                "TLS_DHE_RSA_WITH_AES_128_CBC_SHA"
        });
        return context;
    }

    public static SocketFactory allowAllSSL() throws KeyManagementException,
            NoSuchAlgorithmException {
        return getSSLContextAllowAll().getSocketFactory();
    }

    public static void checkSocketFactoryExist
            (Context context, Map<String, SocketFactory> socketFactoryMap, String urlString) {
        try {
            URL url = new URL(urlString);
            if (socketFactoryMap != null){
                String domainName = "neweggbox.com";
                if (url.getHost().toLowerCase().endsWith("neweggtech.com")) {
                    domainName = "neweggtech.com";
                }

                if (!socketFactoryMap.containsKey(domainName)) {
                    socketFactoryMap.put(domainName, allowSSLWithCert(context, domainName));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static SSLContext getSSLContextWithCert(Context context, String domainName) throws Exception {

        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt

        InputStream caInput = null;
        if (domainName.equalsIgnoreCase("neweggtech.com")) {
            caInput = new BufferedInputStream(context.getAssets().open("neweggtech.com.cer"));
        } else {
            caInput = new BufferedInputStream(context.getAssets().open("neweggbox.com.cer"));
        }

        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            Log.d("CA", "ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }

    public static SocketFactory allowSSLWithCert(Context context, String domainName) throws Exception {
        return getSSLContextWithCert(context, domainName).getSocketFactory();
    }

}

