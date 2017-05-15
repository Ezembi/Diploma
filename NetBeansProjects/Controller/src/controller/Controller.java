/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.threads.MainThread;
import java.util.Scanner;
import model.utils.Logger;

/**
 *
 * @author user
 */
public class Controller {

    private static final String TAG = "Controller";

    public static void main(String[] args) {
        Logger.i(TAG, "Start");
        
        Logger.i(TAG, "Enter sudo password:");
        String sudo = new Scanner(System.in).nextLine();
        
        Logger.i(TAG, "Enter server IP:");
        String sIp = new Scanner(System.in).nextLine();
        
        Logger.i(TAG, "Enter server port:");
        String sPort = new Scanner(System.in).nextLine();
        
        Logger.i(TAG, "Enter ip fist vm:");
        String ip = new Scanner(System.in).nextLine();
        
        new Thread(new MainThread(sudo, sIp, sPort, ip)).start();
    }

}
