/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.data;

import com.google.sljson.annotations.SerializedName;

/**
 *
 * @author user
 */
public class VirtualMachine {
    static final long serialVersionUID = 1L;
    
    @SerializedName("id")
    private int id = 0;
    
    @SerializedName("ram")
    private long ram;
    
    @SerializedName("cpu")
    private int cpu;
    
    @SerializedName("ip")
    private String ip;
    
    @SerializedName("port")
    private int port;
    
    public VirtualMachine(int id, long ram, int cpu, String ip, int port) {
        this(ram, cpu, ip, port);
        this.id = id;
    }
    
    public VirtualMachine(int id, long ram, int cpu, String ip) {
        this(ram, cpu, ip);
        this.id = id;
    }

    public VirtualMachine( long ram, int cpu, String ip, int port) {
        this.ram = ram;
        this.cpu = cpu;
        this.ip = ip;
        this.port = port;
    }
    
    public VirtualMachine( long ram, int cpu, String ip) {
        this.ram = ram;
        this.cpu = cpu;
        this.ip = ip;
        this.port = 80;
    }
    
    public boolean isFree(){
        return cpu < 20;
    }

    public long getRam() {
        return ram;
    }

    public int getCpu() {
        return cpu;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return "VirtualMachine{" + " ram=" + ram + ", cpu=" + cpu + ", ip=" + ip + ", port=" + port + '}';
    }
    
}
