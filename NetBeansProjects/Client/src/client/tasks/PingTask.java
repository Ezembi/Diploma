/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.tasks;

import model.web.ClientHttp;
import model.web.ClientHttp.ConnectListener;


/**
 *
 * @author user
 */
public class PingTask implements Runnable{
    
    private ConnectListener listener;
    private ClientHttp client;

    public PingTask(ConnectListener listener, ClientHttp client) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        listener.onStart();
        try {
            listener.onResult(client.ping());
        } catch (Exception ex) {
            listener.onError(ex);
        }
    }
}
