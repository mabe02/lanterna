package com.googlecode.lanterna;

import com.googlecode.lanterna.bundle.MultilangLocator;

import java.util.Locale;

/**
 * This class permits to get i18n translations about lanterna components by an easy way.
 * @author silveryocha.
 */
public class I18n {

  private static final MultilangLocator multilang = MultilangLocator.getMultilang("lanterna-ui");

  /**
   * Gets the message with parameters
   */
  public static String get(String key, String... parameters) {
    return multilang.get(key, parameters);
  }

  /**
   * Gets the message with parameters
   */
  public static String get(Locale locale, String key, String... parameters) {
    return multilang.get(locale, key, parameters);
  }
}
