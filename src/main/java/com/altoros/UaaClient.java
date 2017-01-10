package com.altoros;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by allomov on 1/9/17.
 */

@RestController
public class UaaClient {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/token2")
    public String getTokenWithUaaClient() throws Exception {
        Map<String,String> env = System.getenv();
        String uaaHost = env.get("UAA_HOST");
        String boshHost = env.get("BOSH_HOST");
        String user = env.get("BOSH_USER");
        String password = env.get("BOSH_PASSWORD");

        String url = "https://" + uaaHost + ":8443/oauth/token";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://" + uaaHost + ":8443/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("username", user));
        urlParameters.add(new BasicNameValuePair("client_id", "bosh_cli"));
        urlParameters.add(new BasicNameValuePair("client_secret", ""));
        urlParameters.add(new BasicNameValuePair("grant_type", "password"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

        return null;
    }

    @RequestMapping("/token1")
    public String getTokenSimple() throws Exception {
        Map<String,String> env = System.getenv();
        String uaaHost = env.get("UAA_HOST");
        String boshHost = env.get("BOSH_HOST");
        String user = env.get("BOSH_USER");
        String password = env.get("BOSH_PASSWORD");

        String url = "https://" + uaaHost + ":8443/oauth/token";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://" + uaaHost + ":8443/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("username", user));
        urlParameters.add(new BasicNameValuePair("client_id", "bosh_cli"));
        urlParameters.add(new BasicNameValuePair("client_secret", ""));
        urlParameters.add(new BasicNameValuePair("grant_type", "password"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " +
                response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println(result.toString());

        return result.toString();
    }

    @RequestMapping("/token")
    public String getToken() {
        Map<String,String> env = System.getenv();
        String uaaHost = env.get("UAA_HOST");
        String boshHost = env.get("BOSH_HOST");
        String user = env.get("BOSH_USER");
        String password = env.get("BOSH_PASSWORD");


        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("password", password);
        postParams.put("username", user);
        postParams.put("grant_type", "password");
        postParams.put("client_id", "bosh_cli");
        postParams.put("client_secret", "");

        URI uaaUri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(uaaHost)
                .port(8443).build().toUri();

        URI uaaOauthTokenUri = UriComponentsBuilder.fromUri(uaaUri)
                .pathSegment("oauth", "token")
                .build().toUri();

        Object response = null;
        try {
            response = restTemplate.postForObject(uaaOauthTokenUri, restTemplate, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return response.toString();
    }
}
