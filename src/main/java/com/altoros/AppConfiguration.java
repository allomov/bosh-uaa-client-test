package com.altoros;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by allomov on 1/9/17.
 */

@Configuration
@ComponentScan("com.altoros")
public class AppConfiguration {

    @Value("${uaa.host}")
    String uaaHost;

    @Value("${bosh.host}")
    String boshHost;

    @Value("${bosh.user}")
    String boshUser;

    @Value("${bosh.password}")
    String boshPassword;

    @Autowired
    @Bean
    public RestTemplate restTemplate(Environment environment, ResourceLoader resourceLoader, RestTemplateBuilder builder) throws Exception {
        //TODO: this may solve this issue, didn't have time to test it
//        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        });

        String keystore = environment.getProperty("client.keystore");
        char[] password = environment.getProperty("client.keystore.password").toCharArray();

        //load the keystore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(resourceLoader.getResource(keystore).getInputStream(), password);

        //add to known keystore
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        //default SSL connections are initialized with the keystore above
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustManagers, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


        return builder.build();
    }
}
