/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.web;

import com.google.sljson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import model.data.Data;
import model.data.HttpMetods;
import model.data.Job;
import model.data.Response;
import model.data.VirtualMachine;
import model.io.ClientHandler;
import model.io.JsonUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author user взаимодействие с сервером
 */
public class ClientHttp {
    
    public interface ConnectListener{
        void onStart();
        void onResult(Response response);
        void onError(Exception ex);
    }

    private final Gson gson;
    private String url;

    public ClientHttp(String url) {
        this.url = url;
        this.gson = JsonUtils.createGson();
    }

    public Response ping() throws Exception {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(HttpMetods.METHOD, HttpMetods.PING));
        return new ClientHandler().getResponse(execute(params));
        
        //return new ClientHandler().getResponse(executeGet(params));
    }

    public Response<Job<Data, Data>> addJob(Job job) throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(HttpMetods.DATA, gson.toJson(job.getInputData())));
        params.add(new BasicNameValuePair(HttpMetods.METHOD, HttpMetods.SEND_JOB));

        Response<Data> rData = new ClientHandler().getData(execute(params));
        job.setOutputData(rData.getData());
        job.setState(rData.getResultCode());
        return new Response(rData.getResultCode(), job);
    }
    
    public Response<List<VirtualMachine>> getVMState() throws Exception {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(HttpMetods.METHOD, HttpMetods.VM_STATE));

        return new ClientHandler().getVMState(execute(params));
    }
    
    public Response sendVMState(VirtualMachine vm) throws Exception {
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(HttpMetods.METHOD, HttpMetods.SEND_STATE));
        params.add(new BasicNameValuePair(HttpMetods.VM, gson.toJson(vm)));
        
        
        return new ClientHandler().getResponse(execute(params));
        
        //return new ClientHandler().getResponse(executeGet(params));
    }

    private String execute(List<NameValuePair> params) throws Exception {

        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + URLEncodedUtils.format(params, "utf-8"));

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url + "?" + URLEncodedUtils.format(params, "utf-8"));

        // add header
        post.setHeader("charset", "UTF-8");
        post.setHeader("Content-Type", "application/json");
        
        HttpResponse response = client.execute(post);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println("result: " + result.toString());
        return result.toString();
    }
    
    private String executeGet(List<NameValuePair> params) throws Exception {

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("GET parameters : " + params);

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url + "?" + URLEncodedUtils.format(params, "utf-8"));

        // add header
        get.setHeader("charset", "UTF-8");
        get.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(get);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        System.out.println("result: " + result.toString());
        return result.toString();
    }

}
