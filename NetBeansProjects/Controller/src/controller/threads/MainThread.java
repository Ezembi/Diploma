package controller.threads;

import controller.tasks.SendVMStateTask;
import controller.threads.VMThread.VMThreadListener;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import model.data.Response;
import model.data.VMCommand;
import model.data.VirtualMachine;
import model.utils.Logger;
import model.web.ClientHttp;

/**
 * главный поток контроллера
 *
 * @author user
 */
public class MainThread implements Runnable {

    private static String TAG = "MainThread";
    private String sudo;
    private BlockingQueue<String> vmQueue;  //очередь машин на запуск, хранит ip
    private String serverIp;
    private String serverPort;

    public MainThread(String sudo, String serverIp, String serverPort, String firstIp) {
        this.sudo = sudo;
        this.vmQueue = new LinkedBlockingDeque<>();
        vmQueue.add(firstIp);    //первая дефолтная машина
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

        Logger.i(TAG, "Start");
        String[] cmd = {"/bin/bash", "-c", "echo " + sudo + "| sudo -S ./test.sh"};
        String ip;

        while (true) {
            try {

                ip = vmQueue.poll(5, TimeUnit.SECONDS);

                if (ip != null) {
                    if (!ip.equals("")) {
                        Logger.i(TAG, "Try start new vm with ip: " + ip);
                        new Thread(
                                new VMThread(
                                        new LocalVMThreadListener(ip, serverIp, serverPort),
                                        new String[]{cmd[0], cmd[1], cmd[2] + " " + ip}
                                )
                        ).start();
                    }
                }

            } catch (Exception e) {
                Logger.e(TAG, e.getMessage(), e);
            }
        }
    }

    private class LocalVMThreadListener implements VMThreadListener {

        private String TAG = "LocalVMThreadListener";
        private final String PID = "Main pid";
        private final String PING = "Go ping server";
        private int pid = -1;
        private ClientHttp client = null;
        private String ip;

        public LocalVMThreadListener(String ip, String sIp, String sPort) {
            client = new ClientHttp("http://" + sIp + ":" + sPort + "/Server/ServerServlet");
            this.ip = ip;
        }

        @Override
        public void onStart() {
            Logger.i(TAG, "Start");
        }

        @Override
        public void setData(String data) {
            if (data.equals(VMCommand.COMMAND + ": " + VMCommand.KILL)) {
                Logger.i(TAG, "Stop the servise");
                if (killProces(pid) == 0) {
                    Logger.i(TAG, "VM kill success");
                } else {
                    Logger.i(TAG, "Error on VM kill");
                }
            } else if (data.contains(VMCommand.COMMAND + ": " + VMCommand.START)) {
                String[] cmd = data.split("!");
                Logger.i(TAG, "Start new servise:" + cmd[1]);
                vmQueue.add(cmd[1]);
            } else if (data.contains(PID)) {
                //костыль, но работает идеально
                String[] cmd = data.split("=");
                pid = Integer.parseInt(cmd[1].replace(" ", ""));
                Logger.i(TAG, "Find new pid:" + pid);
            } else if (data.contains(PING)) {
                String[] state = data.split(":");
                VirtualMachine vm = new VirtualMachine(Long.parseLong(state[2]), Integer.parseInt(state[1]), ip);
                new Thread(new SendVMStateTask(new LocalStateListener(), client, vm)).start();
            }
        }

        @Override
        public void onDestroy() {
            Logger.i(TAG, "Destroy");
        }

        private int killProces(int pid) {
            String[] cmd = {"/bin/bash", "-c", "echo " + sudo + "| sudo -S kill " + pid};
            try {
                Process p = new ProcessBuilder(cmd).start();
                return p.waitFor();
            } catch (IOException | InterruptedException e) {
                Logger.e(TAG, e.getMessage(), e);
                return -1;
            }
        }

        private class LocalStateListener implements ClientHttp.ConnectListener {

            @Override
            public void onStart() {
                Logger.i(TAG, "Send state");
            }

            @Override
            public void onResult(Response response) {
                Logger.i(TAG, "Send state error code:" + response.getResultCode());
            }

            @Override
            public void onError(Exception ex) {
                Logger.e(TAG, ex.getMessage());
            }
        }
    }
}
