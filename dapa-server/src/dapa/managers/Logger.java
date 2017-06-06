package dapa.managers;

import java.util.Date;

public class Logger {
    private static Logger logger = new Logger();
    public static Logger getInstace(){
        return logger;
    }
    public void warning(String message){
        write(new Date(), "WARNING", message);
    }
    public void error(String message){
        write(new Date(), "ERROR", message);
    }
    public void info(String message){
        write(new Date(), "INFO", message);
    }
    private void write(Date time, String level, String message){
        System.out.println("["+time+"] [" + level + "] : " + message);
    }
}
