package com.apollographql.json.walker;

import static com.apollographql.json.walker.log.Trace.trace;
import static com.apollographql.json.walker.log.Trace.warn;

import com.apollographql.json.walker.types.Array;
import com.apollographql.json.walker.types.Obj;
import com.apollographql.json.walker.types.Scalar;
import com.apollographql.json.walker.types.Type;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

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
    final Optional<Type> root = context.getTypes().stream().filter(t -> t.getParent() == null).findFirst();

    if (root.isPresent()) {
      final Set<Type> orderedSet = new LinkedHashSet<>();
      writeType(root.get(), orderedSet);

      final Map<String, Type> generatedSet = new LinkedHashMap<>();
      for (Type t : orderedSet) {
        final Obj obj = (Obj) t;
        final String typeName = obj.getType();

        if (generatedSet.containsKey(typeName)) {
          // is it the same type tho? if so, we can reuse the type and just skip its generation
          if (obj.equals(generatedSet.get(typeName))) {
            continue;
          }

          obj.setType(generateNewObjType(generatedSet, t, typeName));
        }

        t.write(context, writer);
        generatedSet.put(typeName, t);
      }

      System.out.println("orderedSet = " + orderedSet);
    }
  }

  private void writeType(final Type type, final Set<Type> orderedSet) {
    if (type instanceof final Obj obj) {
      // traverse downwards first
      for (Type child : obj.getFields().values()) {
        writeType(child, orderedSet);
      }

      orderedSet.add(obj);
    }
    else if (type instanceof final Array array) {
      writeType(array.getArrayType(), orderedSet);
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

  // utility method for naming conflict resolution
  private static String generateNewObjType(final Map<String, Type> generatedSet, final Type t, final String typeName) {
    String newName;
    Type type = t;
    do {
      final Type parent = type.getParent();
      final String parentName = parent == null
        ? ""
        : StringUtils.capitalize(NameUtils.sanitiseField(parent.getName()));

      final String thisName = StringUtils.capitalize(NameUtils.sanitiseField(typeName));

      newName = parentName + thisName;
      type = parent;
    }
    while (type != null && generatedSet.containsKey(newName));
    return newName;
  }

}
