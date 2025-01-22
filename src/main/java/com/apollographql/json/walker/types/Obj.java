package com.apollographql.json.walker.types;

import com.apollographql.json.walker.Context;
import com.apollographql.json.walker.NameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.apollographql.json.walker.log.Trace.trace;

public class Obj extends Type {
  private String type;
  private final Map<String, Type> fields;

  public Obj(final String name, final Type parent) {
    super(name, parent);
    this.type = generateType(parent, name);
    this.fields = new LinkedHashMap<>();
  }

  private static String generateType(final Type parent, final String name) {
    final String parentName = parent == null
      ? ""
      : StringUtils.capitalize(NameUtils.sanitiseField(parent.getName()));

    return parentName + StringUtils.capitalize(NameUtils.sanitiseField(name));
  }

  public void add(final String field, final Type type) {
    fields.put(field, type);
  }

  public Map<String, Type> getFields() {
    return fields;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public void write(final Context context, final Writer writer) throws IOException {
    if (fields.isEmpty()) return;
    trace(context, "[obj:write]", "-> in: " + getType());
    context.enter(this);

    writer
      .append("type ")
      .append(getType())
      .append(" {\n");

    for (Type field : fields.values()) {
      if (field instanceof final Obj obj) {
        final String name = NameUtils.sanitiseField(field.getName());
        writer
          .append(indent(context))
          .append(name).append(": ").append(obj.getType()).append("\n");
      }
      else if (field instanceof Scalar) {
        field.write(context, writer);
      }
      else {
        field.write(context, writer);
      }
    }

    writer
      .append("}\n");

    context.leave(this);
    trace(context, "[obj:write]", "<- out: " + getType());
  }

  @Override
  public void select(final Context context, final Writer writer) throws IOException {
    if (getFields().isEmpty()) return;

    trace(context, "[obj:select]", "-> in: " + getName());
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
    trace(context, "[obj:select]", "<- out: " + getName());
  }

  @Override
  public String toString() {
    return "obj:" + getName() + ":{" + String.join(",", getFields().keySet()) + "}";
  }

  @Override
  public String id() {
    return "obj:#" + super.id();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true; // Same reference
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false; // Null or different class
    }

    Obj other = (Obj) obj;

    // Compare fields maps recursively
    if (this.fields == null && other.fields == null) {
      return true; // Both maps are null
    }
    if (this.fields == null || other.fields == null) {
      return false; // One map is null
    }

    if (this.fields.size() != other.fields.size()) {
      return false; // Maps have different sizes
    }

    for (Map.Entry<String, Type> entry : this.fields.entrySet()) {
      Type otherValue = other.fields.get(entry.getKey());

      if (!entry.getValue().equals(otherValue)) {
        return false; // Mismatched values
      }
    }

    return true; // All entries match
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, fields);
  }
}
