/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.data;

import com.google.sljson.annotations.SerializedName;

/**
 * Задача на обработку
 * @author user
 */
public class Job<T,M> {
    static final long serialVersionUID = 1L;
    
    @SerializedName("id")
    private T inputData;
    
    @SerializedName("od")
    private T outputData;
    
    @SerializedName("n")
    private String name;
    
    @SerializedName("s")
    private int state;

    public Job(T inputData, T outputData, String name, int state) {
        this.inputData = inputData;
        this.outputData = outputData;
        this.name = name;
        this.state = state;
    }

    public T getInputData() {
        return inputData;
    }

    public void setInputData(T inputData) {
        this.inputData = inputData;
    }

    public T getOutputData() {
        return outputData;
    }

    public void setOutputData(T outputData) {
        this.outputData = outputData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    

    @Override
    public String toString() {
        return "Job{" + "inputData=" + inputData + ", outputData=" + outputData + ", name=" + name + ", state=" + state + '}';
    }
}
