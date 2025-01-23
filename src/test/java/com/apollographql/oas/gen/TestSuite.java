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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuite {
  private StringWriter writer;

  @BeforeEach
  void setUp() {
    System.out.println("ParserTest.setUp creating writer...");
    this.writer = new StringWriter();
  }

  ImmutablePair<Integer, String> checkCompose(final Writer writer) throws IOException, InterruptedException {
    final String schema = writer.toString();
    return Rover.compose(schema);
  }

  private StringWriter getWriter() {
    return writer;
  }

  @Test
  void test_000() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("test/merge/a.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }


  @Test
  void test_001() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("preferences/user/50.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_002() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("live-scores/all"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());
    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_003() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("stats/fixtures/championship"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_004() throws IOException, InterruptedException {
    //
    final Walker walker = new Walker(resourceFile("stats/leagues"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_005() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("stats/line-ups"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_006() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("stats/results/scottish-premiership"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_007() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("stats/tables/championship"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_008() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("stats/tables/not-found.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_009() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("live-scores/all/"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_010() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("fronts"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_011() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("articles/search.json"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_012() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("articles/clockwatch"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(1, result.getLeft());
    assertTrue(result.getRight().contains("SELECTED_FIELD_NOT_FOUND"));
  }

  @Test
  void test_013() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("test/merge"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
  }

  @Test
  void test_014() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("articles/blog"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(1, result.getLeft());
    assertTrue(result.getRight().contains("SELECTED_FIELD_NOT_FOUND"));
  }

  @Test
  void test_015() throws IOException, InterruptedException {
    final Walker walker = new Walker(resourceFile("articles/article/"));
    walker.walk();

    ConnectorWriter.write(walker, getWriter());

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(1, result.getLeft());
    assertTrue(result.getRight().contains("SELECTED_FIELD_NOT_FOUND"));
  }

  @Test
  void test_016() throws IOException, InterruptedException {
    final Walker walker = new Walker(
      resourceFile("articles/article/2023_dec_01_premier-league-10-things-to-look-out-for-this-weekend.json")
    );
    walker.walk();

    final StringWriter writer = getWriter();
    ConnectorWriter.write(walker, writer);

    String schema = writer.toString() // we'll manuall fix the schema
      .replaceAll("### NO TYPE FOUND -- FIX MANUALLY! field: ", "")
      .replaceAll("attributes: ", "# attributes: ");

    StringWriter newSchema = new StringWriter();
    newSchema.write(schema);

    final Pair<Integer, String> result = checkCompose(newSchema);
    assertEquals(0, result.getLeft());
    // assertTrue(result.getRight().contains("SELECTED_FIELD_NOT_FOUND"));
  }

  @Test
  void test_017() throws IOException, InterruptedException {
    final Walker walker = new Walker(
      resourceFile("live-scores/all/2023-12-23_15_00.json")
    );
    walker.walk();

    final StringWriter writer = getWriter();
    ConnectorWriter.write(walker, writer);

//    String schema = writer.toString() // we'll manuall fix the schema
//      .replaceAll("### NO TYPE FOUND -- FIX MANUALLY! field: ", "")
//      .replaceAll("attributes: ", "# attributes: ");
//
//    StringWriter newSchema = new StringWriter();
//    newSchema.write(schema);

    final Pair<Integer, String> result = checkCompose(getWriter());
    assertEquals(0, result.getLeft());
    // assertTrue(result.getRight().contains("SELECTED_FIELD_NOT_FOUND"));
  }

  // internal methods
  private static File resourceFile(final String resource) {
    URL input = TestSuite.class.getClassLoader().getResource(resource);
    assertNotNull(input);

    return new File(input.getPath());
  }

  static class Rover {
    public static void main(String[] args) throws IOException, InterruptedException {
      compose("""
      type Root {
        id: ID!
      }
      
      type Query {
        root: Root
      }""");
    }

    public static ImmutablePair<Boolean, String> isCommandAvailable(String command) {
      String os = System.getProperty("os.name").toLowerCase();
      String[] checkCommand = os.contains("win") ? new String[]{"where", command} : new String[]{"which", command};

      try {
        Process process = new ProcessBuilder(checkCommand).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
          final String line = reader.readLine();
          return new ImmutablePair<>(line != null, line);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
        return new ImmutablePair<>(false, e.getMessage());
      }
    }

    public static ImmutablePair<Integer, String> compose(final String schema) throws IOException, InterruptedException {
      final Map<String, String> env = System.getenv();
      final String workdir = env.get("WORKDIR");

      final String basePath = workdir != null ? workdir : System.getProperty("java.io.tmpdir");
      System.out.println("Rover.compose pathPath = " + basePath);

      final Path specPath = Files.createTempFile("test-spec", ".graphql");
      Files.write(specPath, schema.getBytes());

      // write supergraph.yaml file
      String content = """
      federation_version: =2.10.0-preview.3
      subgraphs:
        test_spec:
          name: test-spec
          routing_url: http://localhost # this value is ignored
          schema:     
      """ + "      file: " + specPath.toAbsolutePath() + " # path to the schema file\n";

      final Path supergraphPath = Files.createTempFile("supergraph", ".yaml");
      Files.write(supergraphPath, content.getBytes());

      final ImmutablePair<Boolean, String> roverAvailable = isCommandAvailable("rover");
      if (roverAvailable.getLeft()) {
        final String rover = roverAvailable.getRight();
        System.out.println("Rover.compose rover is available in: " + rover);

        final String command = String.format("%s supergraph compose --config %s/supergraph.yaml", rover, basePath); // Replace with your desired command
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
      }
      else {
        return new ImmutablePair<>(-1, "rover command not found");
      }
    }
  }
}
