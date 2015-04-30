### Developer How To & Guidelines ###

note: needs updating (last updated 2009)

To submit issues using the Issues tracker (tab above) see the [UserGuide](UserGuide.md)

Build Prerequisites:
  * Install a Java JRE/SDK (Java SDK 6 recommended) if you already don't have one installed by default.
  * Install the latest version of Eclipse.
  * Install the latest Android SDK (done only initially - updates managed by plugin afterwards)
  * Install the latest ADT plug-in for Eclipse (initially specify where you installed SDK)
  * Install the a subversion plug-in for Eclipse (we recommend Subclipse).
> > ''note: for all Eclipse plugins you can now find them via Help-> Eclipse Marketplace from within Eclipse.''

Use the following repository (add one within eclipse using SVN Repository View):
http://skylight1.googlecode.com/svn/trunk   (committers will use https instead).

To check out a project, open the SVN Repository view (Window->Show View) in Eclipse and right click on the project you are interested in (for demos go into subfolders) and then and select "check out as project..."  See example below.

Coding Guidelines: http://source.android.com/source/code-style.html.


### How to build Balance the Beer (listed as Skylight Game): ###

Checkout the following projects from trunk: (right click on these within SVN Repositories View)

  * Skylight Game
  * Skylight DI
  * Skylight Utils

The Skylight Game project depends on the other two subprojects, so you need to build those as well.

Note: if you have any issues building, go to Project Properties -> Clean Project or delete the bin and gen folders to force a rebuild of the project.

To run, simply Run As Android Project (right click on SkylightGame project).

Obviously some features (such as sensors, accelerometer) are not available with the emulator. You can easily switch between device and emulator(s) (you can have more than one open) if you set the Run Configuration Target property to Manual so that you can select which device to run when you click on Run.


Additional Notes:

Currently we are migrating from including third party libraries (found in libs folder) to using a maven repository for third party dependencies (which may require installing maven / maven plugin). alternatively you can manually add the third party libraries to the libs folder (the list of libraries will be added to a README.txt once they are removed from the svn repo)

---

The Skylight1 open source project is open to all to participate in (submit patches, test, create derivative projects). The closed source project(s) is currently limited to those members who attend our meetings in NYC.

_**If in NYC, stop by a [NYC GDG (Google Developer Group)](http://meetup.com/nyc-gdg) meeting.**_