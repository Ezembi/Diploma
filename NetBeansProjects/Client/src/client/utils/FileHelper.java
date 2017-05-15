/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.data.Data;
import model.data.Job;
import model.utils.Logger;

/**
 *
 * @author user
 */
public class FileHelper {

    private static final String TAG = "FileHelper";

    public static void saveResultJob(Data data, File file) {

        try (FileWriter writer = new FileWriter(file)) {
            if(data != null){
                writer.write(data.getData() + "\n");
            } else {
               writer.write("null\n");
            }
            writer.flush();
            writer.close();
            Logger.i(TAG, "File \"" + file.getAbsolutePath() + file.getName() + "\" save success!");
        } catch (IOException ex) {
            Logger.e(TAG, ex.toString());
        }
    }
    
    
   
    public static Data inputLoad(File file) throws IOException {
        try (FileReader fileR = new FileReader(file)) {
            Data data = new Data(fileR);
            Logger.i(TAG, file.getAbsolutePath() + " load success!");
            return data;
        } catch (IOException ex) {
            Logger.e(TAG, ex.toString());
            return null;
        }
    }
}
