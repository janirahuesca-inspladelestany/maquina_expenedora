package shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLogger {
    private final Logger logger = LogManager.getLogger(AppLogger.class);
    private static AppLogger instance;

    protected AppLogger() {
    }

    public static AppLogger getInstance() {
        if (AppLogger.instance == null) {
            AppLogger.instance = new AppLogger();
        }

        return AppLogger.instance;
    }

    public void debug(Object o) {
        logger.debug(o);
    }
    public void info(Object o) {
        logger.info(o);
    }
    public void error(Object o) {
        logger.error(o);
    }
}
