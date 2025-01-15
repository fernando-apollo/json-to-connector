package com.apollographql.json.walker;

import com.apollographql.json.walker.types.Array;
import com.apollographql.json.walker.types.Obj;
import com.apollographql.json.walker.types.Scalar;
import com.apollographql.json.walker.types.Type;
import com.google.gson.*;

import java.io.*;
import java.util.Optional;
import java.util.Set;

public class Walker {

  private final File folder;
  private final Context context;

  public Walker(final File folder) {
    if (!folder.exists() || !folder.isDirectory())
      throw new IllegalArgumentException("Argument either doesn't exist or is not a folder");

    this.folder = folder;
    this.context = new Context();
  }

  public File getFolder() {
    return folder;
  }

  public Context getContext() {
    return context;
  }

  public void walk() throws IOException {
    final File[] sources = getFolder().listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

    if (sources != null) {
      for (final File source : sources) {
        walkSource(source);
      }
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
    final JsonElement root = JsonParser.parseReader(new FileReader(source));

    Type rootType = walkElement(getContext(), null, "root", root);
    System.out.println("Walker.walkSource types found: " + context.getTypes().size());
  }

  private Type walkElement(final Context context, final Type parent, final String name, final JsonElement element) {
    Type result = null;

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

    return result;
  }

  private Obj walkObject(final Context context, final Type parent, final String name, final JsonObject object) {
    Obj result = new Obj(name, parent);

    final Set<String> fieldSet = object.keySet();
    System.out.println("  [walkObject] fieldSet: " + fieldSet);

    for (String field : fieldSet) {
      System.out.println("  [walkObject] field: " + field);
      Type type = walkElement(context, result, field, object.get(field));

      result.add(field, type);
    }

    return result;
  }

  private Array walkArray(final Context context, Type parent, final String name, final JsonArray array) {
    Array result = new Array(name, parent);

    if (!array.isEmpty()) {
      final JsonElement element = array.get(0);
      final Type arrayType = walkElement(context, parent, name, element);
      result.setArrayType(arrayType);
    }
    else {
       System.err.println("Array is empty -- cannot derive type for field '" + name + "'");
      // or we can assume that it's of type string for now
//      result.setArrayType(new Scalar(name, result, "string"));
    }

    return result;
  }

  private Scalar walkPrimitive(final Context context, final Type parent, final String name, final JsonPrimitive primitive) {
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
