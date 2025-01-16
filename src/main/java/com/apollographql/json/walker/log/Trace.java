package com.apollographql.json.walker.log;

import com.apollographql.json.walker.Context;

import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;
import static java.util.logging.Level.FINE;

public class Trace {
  private static final Logger logger = Logger.getLogger(Trace.class.getName());

  public static void trace(final Context ctx, final String context, final String message) {
    final int count = ctx != null ? ctx.getStack().size() : 0;
    logger.log(FINE, " ".repeat(count) + ("(" + count + ")") + context + " " + message);
  }

  public static void warn(final Context ctx, final String context, final String message) {
    final int count = ctx != null ? ctx.getStack().size() : 0;
    logger.log(WARNING, " ".repeat(count) + context + " " + message);
  }
}
