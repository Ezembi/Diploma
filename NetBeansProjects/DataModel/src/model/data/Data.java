/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.data;

import com.google.sljson.annotations.SerializedName;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import model.utils.Logger;

/**
 *
 * @author user
 */
public class Data implements Serializable {

    static final long serialVersionUID = 1L;

    @SerializedName("d")
    private String data = "";

    public Data(String data) {
        this.data = data;
    }

    public Data(FileReader file) {
        int c;
        try {
            while ((c = file.read()) != -1) {
                data += ((char) c);
            }
        } catch (IOException ex) {
            Logger.e(Data.class.getName(), ex.toString());
        }
    }

    public String getData() {
        return data;
    }

}
