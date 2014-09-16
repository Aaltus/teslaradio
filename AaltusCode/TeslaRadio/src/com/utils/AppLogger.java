package com.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Greenwood0 on 2014-09-02.
 * This is the Logger use for the Android Application. Please only use this logger.
 */
public class AppLogger {

    private static AppLogger instance = null;
    final Logger logger = LoggerFactory.getLogger(AppLogger.class.getName());


    protected AppLogger(){
        logLvl = LogLevel.NONE;
    }
    public static AppLogger getInstance(){
        if (instance == null){
            instance = new AppLogger();
        }
        return instance;
    }

    public enum LogLevel{
        NONE,         // ordinal value: 0
        ERROR,        // ordinal value: 3
        WARNING,      // ordinal value: 2
        INFO,         // ordinal value: 1
        DEBUG,        // ordinal value: 4
        ALL           // ordinal value: 5
    };

    private LogLevel logLvl;

    public LogLevel getLogLvl() {
        return logLvl;
    }

    public void setLogLvl(LogLevel logLvl) {
        this.logLvl = logLvl;
    }

    public void d(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.DEBUG.ordinal()){
            logger.debug(tag + " : " + string);
        }//android.util.Log.d(tag, string);
    }

    public void i(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.INFO.ordinal()){
            logger.info(tag + " : " + string);
        }//android.util.Log.i(tag, string);
    }
    public void w(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.WARNING.ordinal()){
            logger.warn(tag + " : " + string);
        }//android.util.Log.w(tag, string);
    }
    public void e(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.ERROR.ordinal()){
            logger.error(tag + " : " + string);
        }//android.util.Log.e(tag, string);
    }

}
