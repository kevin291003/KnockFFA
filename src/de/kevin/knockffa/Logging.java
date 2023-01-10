package de.kevin.knockffa;


import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class used for logging
 */
public class Logging {

    /**
     * The enum Log level.
     */
    public enum LogLevel {
        /**
         * Debug log level.
         */
        DEBUG,
        /**
         * Info log level.
         */
        INFO,
        /**
         * Warning log level.
         */
        WARNING,
        /**
         * Error log level.
         */
        ERROR
    }

    public static Logger logger;
    public static FileWriter fileWriter;
    public static LogLevel loggingLevel;
    public static KnockFFA knockFFA;

    public static void setLoggingLevel(String level) {
        if (level == null) level = "INFO";
        switch (level.toUpperCase()) {
            case "DEBUG":
                loggingLevel = LogLevel.DEBUG;
                break;
            case "WARNING":
                loggingLevel = LogLevel.WARNING;
                break;
            case "ERROR":
                loggingLevel = LogLevel.ERROR;
                break;
            default:
                loggingLevel = LogLevel.INFO;
                break;
        }
    }

    /**
     * This message is used to log a message.
     *
     * @param message the message
     * @param level   the level (LogLevel.DEBUG,INFO,WARNING,ERROR)
     */
    public static void log(String message, LogLevel level) {
        if (fileWriter == null)
            try {
                fileWriter = new FileWriter(knockFFA == null ? "src/latest.log" : "plugins/KnockFFA/logs/latest.log", true);
            } catch (IOException ignored) {
            }
        try {

            fileWriter
                    .append(String.format("[%s] ", new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date())))
                    .append(String.format("%s: ", level.name().toUpperCase()))
                    .append(message).append("\n").flush();
        } catch (IOException ignored) {
        }
        if (logger == null) {
            System.out.println("[KnockFFA] " + level + ": " + message);
            return;
        }

        if (level == null) return;
        switch (level) {
            case DEBUG:
                message = "DEBUG: " + message;
                logger.log(Level.FINEST, message);
                break;
            case INFO:
                message = "INFO: " + message;
                logger.log(Level.INFO, message);
                break;
            case WARNING:
                message = "WARNING: " + message;
                logger.log(Level.WARNING, message);
                break;
            case ERROR:
                message = "ERROR: " + message;
                logger.log(Level.SEVERE, message);
                break;
            default:
                break;
        }
    }

    /**
     * Logs message as debug
     *
     * @param messages the message
     */
    public static void debug(String... messages) {
        for (String message : messages)
            log(message, LogLevel.DEBUG);
    }

    /**
     * Logs message as info
     *
     * @param messages the message
     */
    public static void info(String... messages) {
        for (String message : messages)
            log(message, LogLevel.INFO);
    }

    /**
     * Logs message as warning
     *
     * @param messages the message
     */
    public static void warning(String... messages) {
        for (String message : messages)
            log(message, LogLevel.WARNING);
    }

    /**
     * Logs message as error
     *
     * @param messages the message
     */
    public static void error(String... messages) {
        for (String message : messages)
            log(message, LogLevel.ERROR);
    }

}
