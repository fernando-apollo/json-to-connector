package com.apollographql.json.walker;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "walk", mixinStandardHelpOptions = true, version = "walk 1.0",
  description = "Walks a JSON file and derives a GQL schema")
class Command implements Callable<Integer> {

  @Parameters(index = "0", description = "The folder where JSON payloads exist.")
  private File folder;

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

    return 0;
  }

  // this example implements Callable, so parsing, error handling and handling user
  // requests for usage help or version help can be done with one line of code.
  public static void main(String... args) {
    int exitCode = new CommandLine(new Command()).execute(args);
    System.exit(exitCode);
  }
}