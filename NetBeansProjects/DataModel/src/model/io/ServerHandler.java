/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.io;

import com.google.sljson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.data.Data;
import model.data.Response;
import model.data.VirtualMachine;

/**
 * обьекты -> gson -> отсылаем ответ
 */
public class ServerHandler {

    private Appendable appendable;
    private Gson gson;

    public ServerHandler(Appendable appendable) {
        this.appendable = appendable;
        this.gson = JsonUtils.createGson();
    }

    /**
     * отправить код обработки без данных
     *
     * @param errorCode
     */
    public void process(int errorCode) {
        Response res = new Response(errorCode);
        dataSend(res);
    }

    /**
     * отправть обработанные данные
     * @param data обработанные данные
     */
    public void process(Data data) {
        if (data == null) {
            throw new NullPointerException("ProcessedData is not set");
        } else {
            Response res = new Response(Response.OK, data);
            dataSend(res);
        }
    }
    
    /**
     * отправть состояние VM
     * @param data обработанные данные
     */
    public void process(List<VirtualMachine> data) {
        if (data == null) {
            throw new NullPointerException("List<VirtualMachine> is not set");
        } else {
            Response res = new Response(Response.OK, data);
            dataSend(res);
        }
    }

    public Data getProcessedData(String data){
        return gson.fromJson(data, Data.class);
    }
    
    public VirtualMachine getVirtualMachine(String data){
        return gson.fromJson(data, VirtualMachine.class);
    }

    /**
     * парсим Response в gson для ответа сервера
     *
     * @param res
     */
    private void dataSend(Response res) {
        try {
            String data = gson.toJson(res);
            this.appendable.append(data);
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
