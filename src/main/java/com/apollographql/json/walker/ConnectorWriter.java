package com.apollographql.json.walker;

import java.io.IOException;
import java.io.Writer;

public class ConnectorWriter {
  public static void write(Walker walker, Writer writer) throws IOException {
    writeConnector(writer);
    walker.writeTypes(writer);
    writeQuery(walker, writer);
  }

  static void writeConnector(final Writer writer) throws IOException {
    writer.append("""
  extend schema
    @link(url: "https://specs.apollo.dev/federation/v2.10", import: ["@key"])
    @link(
      url: "https://specs.apollo.dev/connect/v0.1"
      import: ["@connect", "@source"]
    )
    @source(name: "api", http: { baseURL: "http://localhost:4010" })
    
   """);
  }

  private static void writeQuery(final Walker walker, final Writer writer) throws IOException {
    writer.append("\n").append("""
      type Query {
        root: Root
          @connect(
            source: "api"
            http: { GET: "/test" }
            selection: ""\"
      """);

    walker.writeSelection(writer);
    writer.append("\"\"\"\n)}\n");
  }

}
