/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.tasks;

import model.data.VirtualMachine;
import model.web.ClientHttp;

/**
 *
 * @author user
 */
public class SendVMStateTask implements Runnable{

    private VirtualMachine vm;
    private ClientHttp.ConnectListener listener;
    private ClientHttp client;
    
    public SendVMStateTask(ClientHttp.ConnectListener listener, ClientHttp client, VirtualMachine vm) {
        this.listener = listener;
        this.client = client;
        this.vm = vm;
    }
    
    @Override
    public void run() {
        listener.onStart();
        try {
            listener.onResult(client.sendVMState(vm));
        } catch (Exception ex) {
            listener.onError(ex);
        }
    }
}
