# easytranscript

easy to use transcription-software with a variety of features: https://e-werkzeug.eu
easytranscript is published under GPL 3.

http://www.gnu.de/documents/gpl.de.html

Libs and Symbols:

* VLCJ: GPL V.3, Mark Lee, https://github.com/caprica/vlcj 
* Pandoc: GPL V.3, John MacFarlane, http://johnmacfarlane.net/pandoc/
* Commons IO: Apache License 2.0, http://www.apache.org/licenses/
* OpenIconLibrary: http://openiconlibrary.sourceforge.net oxygen - CC-BY-SA 3.0 or LGPL nuvola - LGPL-2.1 nuovext2 - LGPL-2.1 crystal_clear - LGPL-2.1 crystal - LGPL-2.1

# How to use

To use easytranscript you need to have Java 8, 11 or higher installed. In addition, the VLC Player may be required to play other formats.

# How to build

You can build easytranscript with maven:
```mvn clean package```
The file easytranscript-xxx-jar-with-dependencies.jar is the one you want to use.

# Note regarding the development and quality of the code

The development of easytranscript began in 2012 and at that time served primarily as a replacement for the f4 transcription program and for learning Java. The code was then steadily expanded over time, but no significant changes have been made since 2015. The code quality has therefore remained poor and the dependencies have also not been updated. Until a release of version 3, which will be developed from scratch, I will continue to ensure that easytranscript still runs, but I will no longer significantly clean up or restructure the code. The code is therefore to be considered legacy code.
