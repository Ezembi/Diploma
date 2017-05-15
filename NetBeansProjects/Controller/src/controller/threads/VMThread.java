/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import model.utils.Logger;

/**
 * поток работы виртуальной машины
 *
 * @author user
 */
public class VMThread implements Runnable {

    /**
     * слушатель работы виртуальной машины
     */
    public interface VMThreadListener {

        /**
         * Событие на запуск
         */
        void onStart();

        /**
         * главный обработчик: получает сообщения от vm и обрабатывает их
         *
         * @param data
         * @param p
         */
        void setData(String data);

        /**
         * событие на остановку
         */
        void onDestroy();
    }

    private final VMThreadListener listener;
    private static final String TAG = "VMThread";
    private final String[] cmd;

    public VMThread(VMThreadListener listener, String[] cmd) {
        this.listener = listener;
        this.cmd = cmd;
    }

    @Override
    public void run() {

        Logger.i(TAG, "Start");
        listener.onStart();

        try {
            Process p = new ProcessBuilder(cmd).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                Logger.i(TAG, line);
                listener.setData(line);
            }

        } catch (Exception e) {
            Logger.e(TAG, e.getMessage(), e);
        }

        Logger.i(TAG, "Finish");
        listener.onDestroy();
    }
}
