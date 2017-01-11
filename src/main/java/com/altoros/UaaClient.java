package com.altoros;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cloudfoundry.identity.client.UaaContext;
import org.cloudfoundry.identity.client.UaaContextFactory;
import org.cloudfoundry.identity.client.token.GrantType;
import org.cloudfoundry.identity.client.token.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
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

    @Autowired
    private String uaaHost;

    @Autowired
    private String boshUser;

    @Autowired
    private String boshPassword;

    @RequestMapping("/get-token-with-uaa-client")
    public String getTokenWithUaaClient() throws Exception {
        String uaaUrl = "https://" + this.uaaHost + ":8443";

        UaaContextFactory factory = UaaContextFactory.factory(new URI(uaaUrl))
                        .authorizePath("/oauth/authorize")
                        .tokenPath("/oauth/token");
        TokenRequest passwordGrant = factory.tokenRequest()
                .setClientId("bosh_cli")
                .setClientSecret("")
                .setGrantType(GrantType.PASSWORD)
                .setUsername(this.boshUser)
                .setPassword(this.boshPassword)
                .withIdToken();
        UaaContext context = factory.authenticate(passwordGrant);
        String token = context.getToken().getIdTokenValue();
        return token;
    }

    @RequestMapping("/get-token-with-httppost")
    public String getTokenWithUaaHttpPost() throws Exception {

        String url = "https://" + uaaHost + ":8443/oauth/token";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://" + uaaHost + ":8443/oauth/token");

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("password", boshPassword));
        urlParameters.add(new BasicNameValuePair("username", boshUser));
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

        return result.toString();
    }

    // NOTICE: at the moment this returns an error
    @RequestMapping("/get-token-with-resttemplate")
    public String getToken() {

        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("password", boshPassword);
        postParams.put("username", boshUser);
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
