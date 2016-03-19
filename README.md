# AntTargetOnSave-Netbeans
A Netbeans plugin to run an ant target when clicking Save in Netbeans

NBM file is included in build folder for quick install: [AntTargetOnSave](build/org-braheem-nbmodules-AntTargetOnSave.nbm)

Instructions on how to install a plugin module: http://wiki.netbeans.org/FaqPluginInstall

**PLEASE READ BEFORE USING:**
* As can be viewed in the source code, the ant target name is configured as "executeOnIDESave" by default.
* The API is not very flexible in allowing one to determine the "active" project based on the active file being edited in the IDE. While solving that problem, it occurred to me that there is also a potential issue for users working with multiple dependent projects. To solve this, there are two ways to know which build.xml you will invoke the Ant target from:
  1. By default, the Ant target will be run from the project associated with the active file that is opened.
  2. You can select a project before clicking save to run the Ant target from that project's build.xml file.
