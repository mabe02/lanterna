package com.googlecode.lanterna;

import com.googlecode.lanterna.bundle.BundleLocator;

import java.io.File;

/**
 * Centralized configuration class.
 * @author silveryocha
 */
public class Config {

  /**
   * Gets the locale user directory into which the lenterna data will be read or save.<br/>
   * {@link BundleLocator}, for example, can read multilingual property files from this space.
   * @return
   */
  public static File getLocaleStorageFile() {
    return new File(System.getProperty("user.home"), ".lanterna");
  }
}
