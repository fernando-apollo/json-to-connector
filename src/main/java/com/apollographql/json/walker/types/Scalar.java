package com.apollographql.json.walker.types;

import com.apollographql.json.walker.Context;
import com.apollographql.json.walker.NameUtils;

import java.io.IOException;
import java.io.Writer;

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
    writer
      .append(indent(context));

    final String field = NameUtils.sanitiseField(getName());
    writer
      .append(field);

//    if (!getName().equals(field)) writer.append(" # ").append(getName());
    writer
      .append(": ")
      .append(getType())
      .append("\n");
  }

  @Override
  public void select(final Context context, final Writer writer) throws IOException {
    final String originalName = getName();
    final String fieldName = NameUtils.sanitiseFieldForSelect(originalName);

    writer
      .append(indent(context))
      .append(fieldName)
      .append("\n");
  }

  @Override
  public String toString() {
    return "scalar:" + getName() + '=' + getType();
  }
}
