package de.dieser1memesprech.proxsync._9animescraper.util;

import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class HtmlUtils {

    public static String getHtmlContent(String url) {
        CloseableHttpClient httpclient  = Configuration.instance.httpclient;
        String content = "";
        try {
            URL url1 = new URL(url);
            URI uri = new URI(url1.getProtocol(), url1.getUserInfo(), url1.getHost(), url1.getPort(), url1.getPath(), url1.getQuery(), url1.getRef());
            url=uri.toASCIIString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                content = EntityUtils.toString(entity, "US-ASCII");
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
