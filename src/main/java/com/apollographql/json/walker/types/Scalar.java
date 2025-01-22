package com.apollographql.json.walker.types;

import com.apollographql.json.walker.Context;
import com.apollographql.json.walker.NameUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static com.apollographql.json.walker.log.Trace.trace;

public class Scalar extends Type {
  private final String type;

  public Scalar(final String name, final Type parent, final String type) {
    super(name, parent);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public void write(final Context context, final Writer writer) throws IOException {
    trace(context, "[scalar:write]", "-> in: " + getName());
    writer
      .append(indent(context));

    final String field = NameUtils.sanitiseField(getName());
    writer
      .append(field);

    writer
      .append(": ")
      .append(getType())
      .append("\n");
    trace(context, "[scalar:write]", "<- out: " + getName());
  }

  @Override
  public void select(final Context context, final Writer writer) throws IOException {
    trace(context, "[scalar:select]", "-> in: " + getName());

    final String originalName = getName();
    final String fieldName = NameUtils.sanitiseFieldForSelect(originalName);

    writer
      .append(indent(context))
      .append(fieldName)
      .append("\n");

    trace(context, "[scalar:select]", "<- out: " + getName());
  }

  @Override
  public String toString() {
    return id() + '{' + getType() + '}';
  }

  @Override
  public String id() {
    return "scalar:#" + super.id();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof final Scalar scalar)) {
      return false;
    }

    final boolean nameIsEqual = Objects.equals(getName(), scalar.getName());
    final boolean typeIsEqual = Objects.equals(type, scalar.type);

    return nameIsEqual && typeIsEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
