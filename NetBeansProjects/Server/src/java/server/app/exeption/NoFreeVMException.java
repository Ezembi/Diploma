/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.app.exeption;

/**
 *
 * @author user
 */
public class NoFreeVMException extends Exception {
    
    public NoFreeVMException() {
    }

    public NoFreeVMException(String message) {
        super(message);
    }

    public NoFreeVMException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFreeVMException(Throwable cause) {
        super(cause);
    }
}
