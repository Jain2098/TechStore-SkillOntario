package techStore.logger;

import techStore.Constants;

import java.io.File;
import java.io.IOException;

import java.util.logging.*;

public class AppLogger {
    private static final Logger logger = Logger.getLogger("MyAppLogger");

    static {
        try {
            new File(Constants.DATA_FOLDER).mkdirs();

            LogManager.getLogManager().reset();
            Formatter formatter = new SimpleLogFormatter();

            // Console Logging
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);

            // File Logging
            FileHandler fileHandler = new FileHandler(Constants.LOG_FILE_PATH, true);
            fileHandler.setFormatter(formatter);

            logger.addHandler(fileHandler);

            boolean isProduction = true;
            if (isProduction) {
                logger.setLevel(Level.OFF);
                consoleHandler.setLevel(Level.OFF);
                fileHandler.setLevel(Level.OFF);
            } else {
                logger.setLevel(Level.ALL);
                consoleHandler.setLevel(Level.ALL);
                fileHandler.setLevel(Level.ALL);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String str) {
        logger.info(str);
    }

    public static void log(String str, Level level) {
        logger.log(level, str);
    }

    public static void error(String str) {
        logger.log(Level.SEVERE, str);
    }

    public static void error(String str, Throwable throwable) {
        logger.log(Level.SEVERE, str, throwable);
    }
}
