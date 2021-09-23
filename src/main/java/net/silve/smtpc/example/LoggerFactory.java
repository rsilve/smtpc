package net.silve.smtpc.example;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {

    private static final Logger logger = Logger.getAnonymousLogger();
    private static final Handler handler = new ConsoleHandler();

    static {
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
    }

    private LoggerFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Logger getInstance() {
        return logger;
    }

    public static void configure(Level level) {
        logger.setLevel(level);
        handler.setLevel(level);
    }
}
