package com.utils;

/**
 * Created by Greenwood0 on 2014-09-02.
 */
public class AppLogger {

    private static AppLogger instance = null;
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
        INFO,         // ordinal value: 1
        WARNING,      // ordinal value: 2
        ERROR,        // ordinal value: 3
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

    public void i(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.INFO.ordinal()) android.util.Log.i(tag, string);
    }
    public void w(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.WARNING.ordinal()) android.util.Log.w(tag, string);
    }
    public void e(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.ERROR.ordinal()) android.util.Log.e(tag, string);
    }
    public void d(String tag, String string) {
        if (logLvl.ordinal() >= LogLevel.DEBUG.ordinal()) android.util.Log.d(tag, string);
    }
}
