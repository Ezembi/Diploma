/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.data;

import com.google.sljson.annotations.SerializedName;
import java.io.Serializable;

/**
 *
 * @author user
 * ответ на запрос серверу
 */
public class Response<T> implements Serializable{
    static final long serialVersionUID = 1L;
    
    public static final int OK = 0;             //успех
    public static final int IN_PROCESS = 1;     //данные в обработке
    public static final int WRONG_METHOD = 2;   //неизвестный метод
    public static final int INTERNAL_ERROR = 3; //внутренняя ошибка сервера
    public static final int DB_ERROR = 4;       //ошибка БД
    public static final int WRONG_DATA = 5;     //неверные данные в запросе
    public static final int PROCESS_ERROR = 6;  //ошибка обработки данных
    public static final int VM_NOT_FOUND = 7;   //нет домтупных VM
    
    
    @SerializedName("r")
    private final int resultCode;
    @SerializedName("d")
    private T data;

    public Response(int resultCode, T data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    public Response(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }

    @Override
    public String toString() {
        return "Response{" + "resultCode=" + resultCode + ", data=" + data + '}';
    }
    
}
