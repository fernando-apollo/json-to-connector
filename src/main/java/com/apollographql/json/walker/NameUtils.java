package com.apollographql.json.walker;


import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

public class NameUtils {
  private static String capitaliseParts(final String cleanedPath, final String splitChar) {
    String[] parts = cleanedPath.split(splitChar);
    StringBuilder formattedPath = new StringBuilder();

    for (String part : parts) {
      if (!part.isEmpty()) {
        // Capitalize the first letter of each part
        formattedPath //.append("/")
          .append(part.substring(0, 1).toUpperCase())
          .append(part.substring(1));
      }
    }

    // Step 3: Ensure the final string starts with a single slash.
    return formattedPath.toString();
  }


  public static String genParamName(final String param) {
    return StringUtils.uncapitalize(capitaliseParts(param, "[\\-_\\.]"));
  }

  private static String formatPath(final String path, final List<String> parameters) {
    if (path == null || path.isEmpty()) {
      return path; // Return as-is if null or empty
    }

    // Step 1: Remove parameters enclosed in `{}`.
    String cleanedPath = path.replaceAll("\\{[^}]*}", String.join("", parameters));
    cleanedPath = capitaliseParts(cleanedPath, "[:\\-\\.]+");

    // Step 2: Split the path into parts and capitalize each part.
    return capitaliseParts(cleanedPath, "/");
  }

  public static String sanitiseField(final String name) {
    final String fieldName = name.startsWith("@") ? name.substring(1) : name;

    return genParamName(fieldName);
  }

  public static String sanitiseFieldForSelect(final String name) {
    final String fieldName = name.startsWith("@") ? name.substring(1) : name;

    final String sanitised = genParamName(fieldName);

    if (sanitised.equals(name)) {
      return sanitised;
    }
    else {
      final boolean needsQuotes = fieldName.matches(".*[:_\\-\\.].*") || name.startsWith("@");
      final StringBuilder builder = new StringBuilder();
      builder.append(sanitised)
        .append(": ");

      if (needsQuotes) {
        builder.append('"');
      }

      builder.append(name.startsWith("@") ? name : fieldName);

      if (needsQuotes) {
        builder.append('"');
      }

      return builder.toString();
    }
  }

  public static String genArrayItems(final String name) {
    return StringUtils.capitalize(NameUtils.genParamName(name)) + "Item";
  }
}
