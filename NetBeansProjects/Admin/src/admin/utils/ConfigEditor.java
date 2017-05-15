/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import model.utils.Logger;

/**
 *
 * @author user
 */
public class ConfigEditor {

    private static final String TAG = "ConfigEditor";

    public static final String IP = "ip";
    public static final String PORT = "port";

    public static Map<String, String> getConfig(String fileName) {
        String ip, port;
        Map<String, String> map = new HashMap();
        try (FileReader file = new FileReader(fileName)) {
            BufferedReader reader = new BufferedReader(file);
            ip = reader.readLine();
            port = reader.readLine();
            map.put(IP, ip);
            map.put(PORT, port);
            file.close();
            Logger.i(TAG, "Config load success!");
        } catch (IOException ex) {
            Logger.e(TAG, ex.toString());
            return null;
        }

        return map;
    }

    public static boolean setConfig(Map<String, String> args, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(args.get(IP) + "\n");
            writer.write(args.get(PORT));
            writer.flush();
            writer.close();
            Logger.i(TAG, "Config update success!");
            return true;
        } catch (IOException ex) {
            Logger.e(TAG, ex.toString());
            return false;
        }
    }
}
