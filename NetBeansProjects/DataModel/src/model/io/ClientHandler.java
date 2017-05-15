/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.io;

import com.google.sljson.Gson;
import com.google.sljson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import model.data.Data;
import model.data.Response;
import model.data.VirtualMachine;

/**
 *
 * для парсинга отввета сервера
 */
public class ClientHandler {

    private final Gson gson;

    public ClientHandler() {
        this.gson = JsonUtils.createGson();
    }

    public String data(Data data) {
        return gson.toJson(data);
    }

    public Response<Data> getData(String str) {
        Type type = new TypeToken<Response<Data>>() {
        }.getType();
        return getResponse(str, type);
    }

    public Response getResponse(String str) {
        Type type = new TypeToken<Response>() {
        }.getType();
        return getResponse(str, type);
    }
    
    public Response<List<VirtualMachine>> getVMState(String str) {
        Type type = new TypeToken<Response<List<VirtualMachine>>>() {
        }.getType();
        return getResponse(str, type);
    }

    private Response getResponse(String data, Type type) {
        Response res = gson.fromJson(data, type);
        return res;
    }
}
