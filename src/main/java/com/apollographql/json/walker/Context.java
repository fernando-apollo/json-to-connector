package com.apollographql.json.walker;

import com.apollographql.json.walker.types.Obj;
import com.apollographql.json.walker.types.Type;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import static com.apollographql.json.walker.log.Trace.trace;

public class Context {
  private final Stack<Type> stack;
  private Map<String, Type> types;

  public Context() {
    this.stack = new Stack<>();
    this.types = new LinkedHashMap<>();
  }

  public Stack<Type> getStack() {
    return stack;
  }

  public void enter(final Type element) {
    // trace(this, "[context]", "-> enter: (" + getStack().size() + ") " + element.getName());
    this.stack.push(element);
  }

  public void leave(final Type element) {
    // trace(this, "[context]", "<- leave: (" + getStack().size() + ") " + element.getName());
    this.stack.pop();
  }

  public void store(final Type type) {
    if (this.types.containsKey(type.getName())) {
      merge(type);
    }
    else {
      this.types.put(type.getName(), type);
    }
  }

  private void merge(final Type type) {
    final Type source = this.types.get(type.getName());
    // only merge-able if source is also an object
    if (source instanceof final Obj srcObj && type instanceof final Obj trgObj) {
      srcObj.getFields().putAll(trgObj.getFields());
      this.types.put(srcObj.getName(), srcObj);
    }
  }

  public Collection<Type> getTypes() {
    return types.values();
  }
}
