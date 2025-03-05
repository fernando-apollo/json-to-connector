package com.apollographql.json.walker.web;

import com.apollographql.json.walker.ConnectorWriter;
import com.apollographql.json.walker.Walker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@RestController
public class WebController {
  @Autowired
  public WebController() {
  }

  @GetMapping("/hello")
  public String sayHello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return "Hello, " + name + "!";
  }

  @PostMapping(value = "/generate")
  public Map<String, Object> generate(@RequestBody String json) throws IOException {
    System.out.println("[generate] input = \n" + json);

    final StringWriter connector = new StringWriter();
    final StringWriter types = new StringWriter();
    final StringWriter selection = new StringWriter();

    final Walker walker = Walker.fromReader(json);

    ConnectorWriter.write(walker, connector);
    walker.writeSelection(selection);
    walker.writeTypes(types);

    return Map.of("connector", connector.toString(), "selection", selection.toString(), "types", types.toString());
  }
}