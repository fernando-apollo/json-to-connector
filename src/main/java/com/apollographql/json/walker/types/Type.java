package com.apollographql.json.walker.types;

import com.apollographql.json.walker.Context;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class Type {
  private final String name;
  private final Type parent;

  public Type(final String name, final Type parent) {
    this.name = name;
    this.parent = parent;
  }

  public String getName() {
    return name;
  }

  public Type getParent() {
    return parent;
  }

  public abstract void write(final Context context, final Writer writer) throws IOException;

  protected String indent(final Context context) {
    return " ".repeat(context.getStack().size());
  }

  protected String indent(final Context context, int substract) {
    return " ".repeat(context.getStack().size() - substract);
  }

  public abstract void select(final Context context, final Writer writer) throws IOException;

  public String id() {
    StringBuilder paths = new StringBuilder();
    Type parent = this;
    while ((parent = parent.getParent()) != null) {
      paths.insert(0, "/" + parent.getName());
    }

    return paths.append("/").append(getName()).toString();
  }
}
