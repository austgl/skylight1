How to build this project in Eclipse:

Install the Google Plugin. This will give you support for the Google App Engine and Google Web Toolkit.
Instructions here:
http://code.google.com/eclipse/

Install the Scala 2.8 plugin. Instructions here:
http://www.scala-ide.org/

Because we don't put the GWT/GAE jars in Subversion, you have to populate war/WEB-INF/lib. Just right-
click the MarketAppWeb project. Select Properties, then Google, then App Engine. Click on Configure SDKs..
and in the new dialog click OK. Click OK in the previous dialog and you should then see the jars in the
lib directory. You may have to clean the project. Please remember to exclude the lib directory when you
commit code.

For Scala: if you don't see Scala library version 2.8.[whatever] in Package Explorer at the level just below
MarketAppWeb, enter the Scala perspective, right-click the project, select Configure, then 
Add Scala Nature. Now, when you use the Scala perspective, you will will be able to create, code and
debug Java classes as usual, but you can also add and use Scala classes in the same project. Note
that as of June 29, 2010 the plugin site states, "Support for Eclipse 3.6 (Helios) will be available
shortly".

To run the application, right click on the project in Package Explorer, select Run As, then Web
Application. Open your browser and go to http://localhost:8888.
