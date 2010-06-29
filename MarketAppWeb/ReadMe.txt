How to build this project in Eclipse:

Install the Google Plugin. This will give you support for the Google App Engine and Google Web Toolkit.
Instructions here:
http://code.google.com/eclipse/

Install the Scala 2.8 plugin. Instructions here:
http://www.scala-ide.org/

Because we don't put the GWT/GAE jars in Subversion, you have to populate war/WEB-INF/lib. Just right-
click the MarketAppWeb project. Select Properties, then Google, then App Engine. Change the radio 
button from Use Default SDK to something else. Save it and then put it back to the default. You should
then see the jars in the lib directory. Please remember to exclude the lib directory when you commit
code.

For Scala: if you don't see Scala library version 2.8.[whatever] in Package Explorer just below
MarketAppWeb, right-click the project, select Configure, then Add Scala Nature. Now, when you use 
the Scala perspective, will will be able to create, code and debug Java classes as usual, but you
can also add and use Scala classes in the same project.
