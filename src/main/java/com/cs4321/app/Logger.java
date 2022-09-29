package com.cs4321.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A singleton that writes and maintains logs in files
 */
public class Logger {

  private static String sep = File.separator;
  private static Logger instance;
  private static String logsFilePath;

  /**
   * Private constructor of the Logger
   */
  private Logger() {
    String logsDir = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    logsDir = logsDir + ".." + sep + ".." + sep + "src" + sep + "main" + sep + "resources" + sep + "com" + sep
        + "cs4321" + sep + ".logs";
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    String logsFilePath = logsDir + sep + dateFormat.format(date) + ".txt";
    Logger.logsFilePath = logsFilePath.replaceAll("%20", " ");
  }

  /**
   * Returns the Logger singleton. Initializes the singleton if it has not been
   * initialiazed yet
   * 
   * @return The Logger Singleton
   */
  public static Logger getInstance() {
    if (Logger.instance == null) {
      Logger.instance = new Logger();
    }
    return Logger.instance;
  }

  /**
   * Writes a log to the logFile
   * 
   * @param logString The log to write to the log file
   * @return True if and only if the log was succesfully written
   */
  public boolean log(String logString) {
    try {
      File file = new File(Logger.logsFilePath);
      FileWriter fw = new FileWriter(file, true);
      PrintWriter printWriter = new PrintWriter(fw);
      printWriter.println(logString);
      printWriter.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
