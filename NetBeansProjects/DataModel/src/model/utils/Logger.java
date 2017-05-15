package model.utils;

/**
 *
 * @author user
 */
public class Logger {
    public static void i(String TAG, String str){
        System.out.println(TAG + ": " + str);
    }
    
    public static void e(String TAG, String str){
        System.err.println(TAG + ": " + str);
    }
    
    public static void e(String TAG, String str, Exception ex){
        System.err.println(TAG + ": " + str  + "\n" + ex);
        ex.printStackTrace();
    }
}
