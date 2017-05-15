/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.tasks;

import model.web.ClientHttp;


/**
 *
 * @author user
 */
public class VMStateTask  implements Runnable{
    
    private ClientHttp.ConnectListener listener;
    private ClientHttp client;

    public VMStateTask(ClientHttp.ConnectListener listener, ClientHttp client) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        listener.onStart();
        try {
            listener.onResult(client.getVMState());
        } catch (Exception ex) {
            listener.onError(ex);
        }
    }
    
}