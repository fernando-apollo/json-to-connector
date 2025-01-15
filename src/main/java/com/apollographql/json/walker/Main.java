package com.apollographql.json.walker;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "walk", mixinStandardHelpOptions = true, version = "walk 1.0",
  description = "Walks a folder containing JSON file and derives an Apollo Connector GQL schema")
class Command implements Callable<Integer> {

  @Parameters(index = "0", description = "The folder where JSON payloads exist.")
  private File folder;

  @Option(names = "-c", description = "output an Apollo Connector template", defaultValue = "true")
  boolean connector;

  @Option(names = "-t", description = "output the GraphQL schema types")
  boolean types;

  @Option(names = "-s", description = "[default] output the Apollo Connector selection")
  boolean selection;

  @Option(names = {"-o", "--output-file"}, paramLabel = "output", description = "where to write the output")
  File output;

  @Override
  public Integer call() throws Exception {
    if (!this.folder.exists()) {
      throw new IllegalArgumentException("Source folder does not exist");
    }

    if (!this.folder.isDirectory()) {
      throw new IllegalArgumentException("Source is not a folder");
    }

    final Walker walker = new Walker(folder);
    walker.walk();

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

    if (this.connector) {
      ConnectorWriter.write(walker, writer);
    }
    else {
      if (this.types) {
        walker.writeTypes(writer);
      }

      if (this.selection) {
        walker.writeSelection(writer);
      }
    }

    writer.close();

    System.out.println("All done!");

    return 0;
  }

  // this example implements Callable, so parsing, error handling and handling user
  // requests for usage help or version help can be done with one line of code.
  public static void main(String... args) {
    int exitCode = new CommandLine(new Command()).execute(args);
    System.exit(exitCode);
  }
}