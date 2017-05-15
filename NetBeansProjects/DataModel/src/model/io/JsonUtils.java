/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.io;

import com.google.sljson.Gson;
import com.google.sljson.GsonBuilder;

/**
 *
 * @author user
 * билдер json'a
 */
public class JsonUtils {
    public static Gson createGson(){
        return new GsonBuilder().create();
    }
}
