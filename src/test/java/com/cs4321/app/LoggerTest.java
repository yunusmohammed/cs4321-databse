package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Logger class
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
@Ignore("Ignore until tests can be isolated")
public class LoggerTest {
  static Logger logger = Logger.getInstance();

  String expectedFirstLog = "First Item";
  String expectedSecondLog = "Second Item";
  String expectedThirdLog = "Third Item";

  @Test
  @Order(1)
  void testSeveralItemsWrittenToLogAllPersist() throws IOException {
    logger.log(expectedFirstLog);
    logger.log(expectedSecondLog);
    BufferedReader reader = new BufferedReader(new FileReader(logger.getLogsFilePath()));
    assertEquals(expectedFirstLog, reader.readLine());
    assertEquals(expectedSecondLog, reader.readLine());
    reader.close();
  }

  @Test
  @Order(2)
  void testItemWrittenToAlreadyExistingLogCorrectlyHandled() throws IOException {
    logger.log(expectedThirdLog);
    BufferedReader reader = new BufferedReader(new FileReader(logger.getLogsFilePath()));
    assertEquals(expectedFirstLog, reader.readLine());
    assertEquals(expectedSecondLog, reader.readLine());
    assertEquals(expectedThirdLog, reader.readLine());
    reader.close();
  }

  @AfterAll
  static void cleanUp() {
    File logFile = new File(logger.getLogsFilePath());
    logFile.delete();
  }
}
