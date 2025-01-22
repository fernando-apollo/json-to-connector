package com.apollographql.json.walker.types;

import com.apollographql.json.walker.Context;
import com.apollographql.json.walker.NameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import static com.apollographql.json.walker.log.Trace.trace;

public class Array extends Type {
  private Type arrayType;

  public Array(final String name, final Type parent) {
    super(name, parent);
  }

  public void setArrayType(final Type type) {
    this.arrayType = type;
  }

  public Type getArrayType() {
    return arrayType;
  }

  @Override
  public void write(final Context context, final Writer writer) throws IOException {
    trace(context, "[array:write]", "-> in: " + getName());
    final String field = NameUtils.sanitiseField(getName());

    final Type itemsType = getArrayType();
    if (itemsType == null) {
      writer.append("### NO TYPE FOUND -- FIX MANUALLY! field: ").append(field).append(": [String]").append("\n");
      return;
    }

    writer
      .append(indent(context))
      .append(field)
      .append(": [");

    if (itemsType instanceof final Scalar scalar) {
      writer.append(scalar.getType());
    }
    else if (itemsType instanceof final Obj obj) {
      writer.append(obj.getType());
    }
    else {
      writer.append(StringUtils.capitalize(itemsType.getName()));
    }

    writer.append("]");

    // for testing, mostly
    if (!getName().equals(field)) writer.append(" # ").append(getName());

    writer.append("\n");
    trace(context, "[array:write]", "<- out: " + getName());
  }

  @Override
  public void select(final Context context, final Writer writer) throws IOException {
    trace(context, "[array:select]", "-> in: " + getName());

    final Type itemsType = getArrayType();
    if (itemsType instanceof Obj) {
      itemsType.select(context, writer);
    }
    else {
      final String fieldName = NameUtils.sanitiseFieldForSelect(getName());

      writer
        .append(indent(context))
        .append(fieldName)
        .append("\n");
    }

    trace(context, "[array:select]", "<- out: " + getName());
  }

  @Override
  public String toString() {
    return id() + '[' + getArrayType().getName() + ']';
  }

  @Override
  public String id() {
    return "array:#" + super.id();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof final Array array)) {
      return false;
    }

    return Objects.equals(arrayType, array.arrayType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), arrayType);
  }
}
