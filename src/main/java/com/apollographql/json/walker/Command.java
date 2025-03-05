package com.apollographql.json.walker;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.logging.LogManager;


@CommandLine.Command(name = "JsonToGQL", mixinStandardHelpOptions = true, version = "JsonToGQL 1.0",
  description = "Converts a JSON payload (or a collection of JSON payloads) to an Apollo Connector spec.  When " +
    "passing a folder, it is expected that these are from the same collection - for instance, a collection " +
    "of articles, or blog posts - as the tool will attempt to merge these to derive a single connector schema.\n")
class Command implements Callable<Integer> {

  @Parameters(index = "0", description = "A single JSON file or a folder with a collection of JSON files.", paramLabel = "<file|folder>")
  private File fileOrFolder;

  @Option(names = "-c", description = "output an Apollo Connector template")
  boolean connector;

  @Option(names = "-t", description = "output the GraphQL schema types")
  boolean types;

  @Option(names = "-s", description = "[default] output the Apollo Connector selection")
  boolean selection;

  @Option(names = {"-o", "--output-file"}, paramLabel = "output", description = "where to write the output")
  File output;

  @Override
  public Integer call() throws Exception {
    if (!this.fileOrFolder.exists()) {
      throw new IllegalArgumentException("Source folder does not exist");
    }

    final Walker walker = Walker.fromFileOrFolder(fileOrFolder);
    final BufferedWriter writer;
    if (this.output == null) {
      writer = new BufferedWriter(new OutputStreamWriter(System.out));
    }
    else {
      if (this.output.exists() && !this.output.delete()) {
        throw new IllegalStateException("Could not delete existing output file: " + this.output.getPath());
      }

      writer = new BufferedWriter(new FileWriter(this.output));
    }

    if ((!this.types && !this.selection) || this.connector) {
      System.out.println("\nConnector spec:\n");
      ConnectorWriter.write(walker, writer);
    }
    else {
      if (this.types) {
        System.out.println("\nGraphQL schema types:\n");
        walker.writeTypes(writer);
      }

      if (this.selection) {
        System.out.println("\nConnector selection:\n");
        walker.writeSelection(writer);
      }
    }

    writer.close();

    System.out.println("All done!");

    return 0;
  }

  // this example implements Callable, so parsing, error handling and handling user
  // requests for usage help or version help can be done with one line of code.
  public static void main(String... args) throws IOException {
    InputStream configFile = Command.class.getClassLoader().getResourceAsStream("logging.properties");
    if (configFile != null) {
      LogManager.getLogManager().readConfiguration(configFile);
    }

    int exitCode = new CommandLine(new Command()).execute(args);
    System.exit(exitCode);
  }
}