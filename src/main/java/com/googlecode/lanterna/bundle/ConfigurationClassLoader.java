package com.googlecode.lanterna.bundle;

import com.googlecode.lanterna.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This configuration class loader permits to load bundle from the personal data of the user. That
 * is useful for users who want to get different labels than the default ones<br/>
 * If the user wants to override the labels, he can create into USER.HOME/.lenterna/multilang path
 * the file lanterna-ui_[language, "fr" for example].properties.
 * @author silveryocha
 */
public class ConfigurationClassLoader extends ClassLoader {

  private File userPath = Config.getLocaleStorageFile();

  public ConfigurationClassLoader(ClassLoader parent) {
    super(parent);
  }

  @Override
  public URL getResource(String name) {
    URL resource = super.getResource(name);
    if (name != null) {
      File file = new File(userPath, name);
      if (file.exists()) {
        try {
          resource = file.toURI().toURL();
        } catch (MalformedURLException ex) {
          Logger.getLogger(ConfigurationClassLoader.class.getName()).log(Level.SEVERE, null, ex);
          resource = super.getResource(name);
        }
      }
    }
    return resource;
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    InputStream inputStream = super.getResourceAsStream(name);
    if (name != null) {
      File file = new File(userPath, name);
      if (file.exists()) {
        try {
          inputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
          Logger.getLogger(ConfigurationClassLoader.class.getName()).log(Level.SEVERE, null, ex);
          return null;
        }
      }
    }
    return inputStream;
  }
}
