This project retrieves download numbers and other information for Android applications published on the Android Market. It does this by logging into the Android Market Publisher Console and extracting the numbers from the web page. It is currently designed to run from inside a deployed WAR file. The unit tests do run it as a standalone Java program as well, however. 

Account information needs to be placed in the config/scraper-config.properties file and a path to that file added to src/com/wsl/marketconsolescraper/LocalConfigurationFlags.java or DefaultConfigurationFlags. Also these libraries and their dependencies need to be in the classpath:
gwt-2.0.4
htmlunit-2.8
jee-2.4
spring-2.5.6.SEC02

This source code was contributed by WorkSmart Labs ( http://www.worksmartlabs.com/ ).