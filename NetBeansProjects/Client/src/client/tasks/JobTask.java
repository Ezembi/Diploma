/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.tasks;

import model.data.Job;
import model.web.ClientHttp;

/**
 *
 * @author user
 */
public class JobTask implements Runnable{
    
    private ClientHttp.ConnectListener listener;
    private ClientHttp client;
    private Job data;

    public JobTask(ClientHttp.ConnectListener listener, ClientHttp client, Job data) {
        this.listener = listener;
        this.client = client;
        this.data = data;
    }

    @Override
    public void run() {
        listener.onStart();
        try {
            listener.onResult(client.addJob(data));
        } catch (Exception ex) {
            listener.onError(ex);
        }
    }
}