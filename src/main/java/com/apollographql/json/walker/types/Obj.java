package com.apollographql.json.walker.types;

import com.apollographql.json.walker.Context;
import com.apollographql.json.walker.NameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class Obj extends Type {
  private Map<String, Type> fields;

  public Obj(final String name, final Type parent) {
    super(name, parent);
    this.fields = new LinkedHashMap<>();
  }

  public void add(final String field, final Type type) {
    fields.put(field, type);
  }

  public Map<String, Type> getFields() {
    return fields;
  }

  @Override
  public void write(final Context context, final Writer writer) throws IOException {
    if (fields.isEmpty()) return;

    context.enter(this);

    writer
      .append("type ")
      .append(StringUtils.capitalize(NameUtils.sanitiseField(getName())))
      .append(" {\n");

    for (Type field : fields.values()) {
      if (field instanceof Obj) {
        final String name = NameUtils.sanitiseField(field.getName());
        writer
          .append(indent(context))
          .append(name).append(": ").append(StringUtils.capitalize(name)).append("\n");
      }
      else {
        field.write(context, writer);
      }
    }

    writer
      .append("}\n");

    context.leave(this);
  }

  @Override
  public void select(final Context context, final Writer writer) throws IOException {
    context.enter(this);

    if (getParent() != null) {
      writer
        .append(indent(context, 1))
        .append(NameUtils.sanitiseFieldForSelect(getName()))
        .append(" {")
        .append("\n");
    }

    for (Type field : fields.values()) {
      field.select(context, writer);
    }

    if (getParent() != null) {
      writer
        .append(indent(context, 1))
        .append("}")
        .append("\n");
    }

    context.leave(this);
  }

  @Override
  public String toString() {
    return "obj:" + getName() + ":{" + String.join(",", getFields().keySet()) + "}";
  }
}
