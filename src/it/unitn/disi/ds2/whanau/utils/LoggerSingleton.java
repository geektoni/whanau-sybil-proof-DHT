package it.unitn.disi.ds2.whanau.utils;

import java.util.logging.Formatter;
import java.util.logging.*;

public class LoggerSingleton {
    private static LoggerSingleton instance;
    private Logger log;

    private LoggerSingleton(boolean enable) {
        log = Logger.getLogger("whanau-logger");
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(this.getFormatter());
        log.addHandler(handler);
        this.enable = enable;
    }

    private LoggerSingleton(String name, boolean enable)
    {
        log = Logger.getLogger("whanau-logger"+":"+name);
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(this.getFormatter());
        log.addHandler(handler);
        this.enable = enable;
    }

    public static LoggerSingleton getInstance(String name, boolean enable) {
        if(instance == null) {
            instance = new LoggerSingleton(name, enable);
        }
        return instance;
    }

    public static LoggerSingleton getInstance(boolean enable) {
        if(instance == null) {
            instance = new LoggerSingleton(enable);
        }
        return instance;
    }

    public void log(Level level, String message)
    {
        if (enable)
            log.log(level, message);
    }

    public  void log(String message)
    {
        if (enable)
            log.info(message);
    }

    public Formatter getFormatter()
    {
        return new Formatter() {

            @Override
            public String format(LogRecord arg0) {
                StringBuilder b = new StringBuilder();
                b.append("[*] ");
                b.append(arg0.getLevel());
                b.append(" ");
                b.append(arg0.getMessage());
                b.append(System.getProperty("line.separator"));
                return b.toString();
            }

        };
    }

    private boolean enable;
}