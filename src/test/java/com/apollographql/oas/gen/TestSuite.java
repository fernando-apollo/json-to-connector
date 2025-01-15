package com.apollographql.oas.gen;

import com.apollographql.json.walker.ConnectorWriter;
import com.apollographql.json.walker.Walker;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSuite {
  private StringWriter writer;

  @BeforeEach
  void setUp() {
    System.out.println("ParserTest.setUp creating writer...");
    this.writer = new StringWriter();
  }

  ImmutablePair<Integer, String> checkCompose() throws IOException, InterruptedException {
    final String schema = getWriter().toString();
    return Rover.compose(schema);
  }

  private StringWriter getWriter() {
    return writer;
  }

  @Test
  void test_001() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("preferences/user/50.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_002() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("live-scores/all/2023-12-23_15_00.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());
    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_003() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("stats/fixtures/championship/2023-12-23.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_004() throws IOException, InterruptedException {
    //
    final Walker walker = new Walker(parentFolder("stats/leagues/scottish-premiership.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_005() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("stats/line-ups/luton-vs-newcastle.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_006() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("stats/results/scottish-premiership/2023-12-23.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

@Test
  void test_007() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("stats/tables/championship/2023-12-23.json"));
    walker.walk();

  ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_008() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("stats/tables/not-found.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_009() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("live-scores/all/2023-12-23_15_01.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_010() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("fronts/2023-12-23.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_011() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("articles/search.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_012() throws IOException, InterruptedException {
    final Walker walker = new Walker(parentFolder("articles/clockwatch/2023-12-16.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose();
    assertEquals(1, result.getLeft());
  }

  // internal methods

  private static File parentFolder(final String resource) {
    URL input = TestSuite.class.getClassLoader().getResource(resource);
    assertNotNull(input);

    final String path = input.getPath();
    return new File(path).getParentFile();
  }

  static class Rover {
    public static void main(String[] args) throws IOException, InterruptedException {
      compose("nothing");
    }

    public static ImmutablePair<Integer, String> compose(final String schema) throws IOException, InterruptedException {
      final String basePath = "/Users/fernando/Documents/Opportunities/Vodafone/tmf-apis/supergraph";
      final Path path = Paths.get(basePath + "/test-spec.graphql");
      Files.write(path, schema.getBytes());

      final String command = String.format("/Users/fernando/.rover/bin/rover supergraph compose --config %s/supergraph.yaml", basePath); // Replace with your desired command
      System.out.println("command = " + command);

      // Run the command
      final ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
      final Process process = processBuilder.start();

      // Read the command output
      final BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      // also collect the stream
      final StringWriter writer = new StringWriter();

      String e;
      while ((e = error.readLine()) != null) {
        System.err.println(e);
        writer.write(e);
      }

      // Wait for the process to finish and get the exit code
      final int errorCode = process.waitFor();

      return new ImmutablePair<>(errorCode, writer.toString());
//      return errorCode;
    }
  }
}