package com.googlecode.lanterna.bundle;

import java.util.Locale;

/**
 * This class permits to deal easily with multilang bundles.
 * @author silveryocha
 */
public class MultilangLocator extends BundleLocator {

  /**
   * Hidden constructor.
   * @param bundleName the name of the bundle.
   */
  protected MultilangLocator(final String bundleName) {
    super(bundleName);
  }

  /**
   * All bundles are in a same repository.
   * So, this method gets a bundle by its simple name.
   * @param name the simple name of the aimed bundled.
   * @return an initialized {@link MultilangLocator} instance.
   */
  public static MultilangLocator getMultilang(String name) {
    return new MultilangLocator("multilang." + name);
  }

  /**
   * Gets the message with parameters
   */
  public String get(String key, String... parameters) {
    return get(Locale.getDefault(), key, parameters);
  }

  /**
   * Gets the message with parameters
   */
  public String get(Locale locale, String key, String... parameters) {
    return getBundleKeyValue(locale, key, parameters);
  }
}
