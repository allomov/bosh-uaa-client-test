# bosh-uaa-client-test

### Description

This is a simple project to test connection to BOSH UAA in different ways. UAA is used to authenticate access to BOSH. 
It provides a token that can be used to run request to BOSH. You can see example of host it work using curl in 
[this gist](https://gist.github.com/allomov/48a61475b95d89da11cdb18c329897bc). For more details you can check [UAA documentation](https://docs.cloudfoundry.org/api/uaa/).

### Prepare to run

To use UAA with CA certs you'll need to generate java keystore with the following command.
```
keytool -genkeypair -import -trustcacerts -alias bosh-root -file rootCA.pem -storepass <change-me> -keystore clientsidestore.jks
```
After that you'll be able to run the app with following system properties:
```
-Djava.net.preferIPv4Stack=true -Djavax.net.ssl.trustStore=/path/to/clientsidestore.jks -Djavax.net.ssl.trustStorePassword=<change-me>
```

### Endpoints

| Endpoint | Description
| --- | -----------
| `/get-token-with-uaa-client` | Fetch UAA token using `org.cloudfoundry.identity.client`
| `/get-token-with-httppost` | Use simple http client to fetch UAA token 
| `/get-token-with-resttemplate` | Fetch UAA token using Spring `RestTemplate` (not working right now) 

### TODO

You should to be able to load cert during runtime using initialized empty KeyStore. For some reason this don't work now. 
