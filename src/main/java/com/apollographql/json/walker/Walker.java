package com.apollographql.json.walker;

import static com.apollographql.json.walker.log.Trace.trace;
import static com.apollographql.json.walker.log.Trace.warn;

import com.apollographql.json.walker.types.Array;
import com.apollographql.json.walker.types.Obj;
import com.apollographql.json.walker.types.Scalar;
import com.apollographql.json.walker.types.Type;
import com.google.gson.*;

import java.io.*;
import java.util.Optional;
import java.util.Set;

public class Walker {

  private final File fileOrFolder;
  private final Context context;

  public Walker(final File fileOrFolder) {
    if (!fileOrFolder.exists())
      throw new IllegalArgumentException("Argument does not exist");

    this.fileOrFolder = fileOrFolder;
    this.context = new Context();
  }

  public File getFileOrFolder() {
    return fileOrFolder;
  }

  public Context getContext() {
    return context;
  }

  public void walk() throws IOException {
    if (getFileOrFolder().isDirectory()) {
      final File[] sources = getFileOrFolder().listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
      if (sources != null) {
        for (final File source : sources) {
          walkSource(source);
        }
      }
    }
    else {
      walkSource(getFileOrFolder());
    }
  }

  public void writeSelection(final Writer writer) throws IOException {
    // now we need to select -- so find the Root node:
    final Optional<Type> root = context.getTypes().stream().filter(t -> t.getParent() == null).findFirst();
    if (root.isPresent()) {
      root.get().select(context, writer);
    }
  }

  public void writeTypes(final Writer writer) throws IOException {
    for (Type t : context.getTypes()) {
      t.write(context, writer);
    }
  }

  private void walkSource(final File source) throws FileNotFoundException {
    trace(context, "-> [walkSource]", "in: " + source.getName());
    final JsonElement root = JsonParser.parseReader(new FileReader(source));

    walkElement(getContext(), null, "root", root);
    trace(context, "   [walkSource]", "types found: " + context.getTypes().size());

    trace(context, "<- [walkSource]", "out: " + source.getName());
  }

  private Type walkElement(final Context context, final Type parent, final String name, final JsonElement element) {
    trace(context, "-> [walkElement]", "in: " + name);

    Type result;

    if (element.isJsonObject()) {
      result = walkObject(context, parent, name, element.getAsJsonObject());
      context.store(result);
    }
    else if (element.isJsonArray()) {
      result = walkArray(context, parent, name, element.getAsJsonArray());
    }
    else if (element.isJsonPrimitive()) {
      result = walkPrimitive(context, parent, name, element.getAsJsonPrimitive());
    }
    else {
      throw new IllegalStateException("Cannot yet handle '" + name + "' of type " + element);
    }

    trace(context, "<- [walkElement]", "out: " + name);
    return result;
  }

  private Obj walkObject(final Context context, final Type parent, final String name, final JsonObject object) {
    trace(context, "-> [walkObject]", "in: " + name);
    Obj result = new Obj(name, parent);

    final Set<String> fieldSet = object.keySet();
    trace(context, "  [walkObject]", "fieldSet: " + fieldSet);

    for (String field : fieldSet) {
      trace(context, "  [walkObject]", "field: " + field);
      Type type = walkElement(context, result, field, object.get(field));

      result.add(field, type);
    }

    trace(context, "<- [walkObject]", "out: " + name);
    return result;
  }

  private Array walkArray(final Context context, Type parent, final String name, final JsonArray array) {
    trace(context, "-> [walkArray]", "in: " + name);
    Array result = new Array(name, parent);

    if (!array.isEmpty()) {
      final JsonElement element = array.get(0);
      final Type arrayType = walkElement(context, parent, name, element);
      result.setArrayType(arrayType);
    }
    else {
      warn(context, "   [walkArray]", "Array is empty -- cannot derive type for field '" + name + "'");
    }

    trace(context, "-> [walkArray]", "in: " + name);
    return result;
  }

  private Scalar walkPrimitive(final Context ignoredContext, final Type parent, final String name, final JsonPrimitive primitive) {
    Scalar result;
    if (primitive.isString()) {
      result = new Scalar(name, parent, "String");
    }
    else if (primitive.isBoolean()) {
      result = new Scalar(name, parent, "Boolean");
    }
    else if (primitive.isNumber()) {
      result = new Scalar(name, parent, "Int");
    }
    else {
      throw new IllegalStateException("Cannot yet handle '" + primitive + "'");
    }
    return result;
  }
}
