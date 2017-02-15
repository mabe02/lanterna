# Changes in 2.1.9 #
Lanterna 2.1.9 is a minor bugfix release with a few enhancements
  * TextBox now accepts input of non-latin characters
  * Enable EOF 'key' when the input stream is closed (you need to set system property "com.googlecode.lanterna.input.enable-eof" to "true")
  * Better ESC key detection
  * `KeyMappingProfile` patterns now public
  * Regression fixed with high CPU load when opening a window with no interactable components

For a more complete list of changes, please click on the Source tab above and browse the changesets.