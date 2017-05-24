/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.web;

import com.google.sljson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import model.data.Data;
import model.data.HttpMetods;
import model.data.VMCommand;
import model.data.VirtualMachine;
import model.io.JsonUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class HttpRequestHelper {

    private static final Logger log = Logger.getLogger("super_server");

    public static String sendingStartKillRequest(String url, String command) throws IOException {
        String sendingUrl = "http://" + url;

        HttpGet request = new HttpGet(sendingUrl);
        request.addHeader(VMCommand.COMMAND, command);

        log.info("Sending 'GET' request (command = " + command + ") to URL : " + sendingUrl);

        return send(request);
    }

    public static String sendingRequest(String url, String data) throws IOException {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(HttpMetods.DATA, data));

        String sendingUrl = "http://" + url;

        log.info("Sending 'GET' request to URL : " + sendingUrl);
        log.info("GET parameters : " + URLEncodedUtils.format(params, "utf-8"));
        log.info("Request : " + sendingUrl + "?" + URLEncodedUtils.format(params, "utf-8"));

        HttpGet request = new HttpGet(sendingUrl + "?" + URLEncodedUtils.format(params, "utf-8"));
        request.setHeader("charset", "UTF-8");
        request.setHeader("Content-Type", "application/json");

        return send(request);
    }

    private static String send(HttpGet request) throws IOException {
        HttpClient client = getHttpClient();
        HttpResponse response = client.execute(request);

        log.info("Response Code : "
                + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line;

        // !!!!!без head'еров в ответе от VM повисает
        while ((line = rd.readLine()) != null) {
            log.info("line: " + line);
            result.append(line);
        }

        log.info("Response: " + result.toString());

        return result.toString();
    }

    private static DefaultHttpClient getHttpClient() {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        return new DefaultHttpClient(httpParameters);
    }
}
