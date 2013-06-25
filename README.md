Sikuli IDE 1.0.1 -- Branch development
===========

**Download of current versions (1.0.0)** visit the official [**download page on Launchpad**](https://launchpad.net/sikuli/+download).<br />

The IDE to edit and run Sikuli scripts (supported scripting languages Python/Jython).
<br /><br />
**MANDATORY ;-)** [Have a look at major improvements and new features](https://github.com/RaiMan/SikuliX-IDE/wiki/Release-Notes-IDE)
<br /><br />
Sikuli IDE is targeted at people who want to develop and run scripts using Sikuli features with one of the supported scripting languages (currently Python - more scripting languages to come).
<br /><br />
For the use of Sikuli features in Java programs, other Java based languages or Java aware scripting languages currently not supported by Sikuli IDE you should use [Sikuli API](https://github.com/RaiMan/SikuliX-API) with other IDE's like Eclipse, NetBeans, ...

Same goes for people who want to develop, run and debug scripts using SikuliX-IDE supported scripting languages in other IDE's like Eclipse, Netbeans, ...
<br /><br />
This repo is **fully Maven**, so a fork of this repo can be directly used as project in NetBeans/Eclipse/... or with mvn on commandline. <br />
It depends on [Sikuli API](https://github.com/RaiMan/SikuliX-API), [Sikuli Jython](https://github.com/RaiMan/SikuliX-Jython) and [Sikuli Setup](https://github.com/RaiMan/SikuliX-Setup).<br />
[... click for more info about package production](https://github.com/RaiMan/SikuliX-IDE/wiki/Maven-support)
<br /><br />
The downloadable packages of Sikuli IDE contain everything needed <br />
including a current version of [Sikuli API](https://github.com/RaiMan/SikuliX-API).

**Roadmap**
 - **2013 June 10:** open a developement branch for Sikuli IDE 1.1
  - better support for images and usage of Sikuli features
  - implement more standard IDE features
  - support JRuby as scripting language
<br />
<br />
 - **2013 Juli 29:** service update Sikuli IDE 1.0.1
  - bug fixes and some enhancements
<br />
<br />
 - **2013 October 31:** release of Sikuli IDE 1.1
  - merge branch develop into branch master
  - open a developement branch for Sikuli IDE 1.2
  - more scripting languages
  - new features tbd.
<br />
<br />
 - **2014:** new versions in April and October

**History**
 - this is based on the developement at MIT (Tsung-Hsiang Chang (Sean aka vgod) and Tom Yeh) which was discontinued end 2011 (https://github.com/sikuli/sikuli) with a latest version called Sikuli X-1.0r930.
 - and the [follow up repo](https://github.com/RaiMan/Sikuli12.11), where I prepared the creation of a final version 1.0
 - in April 2013 I decided, to divide Sikuli into the 2 packages Sikuli IDE and [Sikuli API](https://github.com/RaiMan/SikuliX-API), to better support future contributions.

**Support**
 - until otherwise noted: [questions, requests and bugs can still be posted here](https://answers.launchpad.net/sikuli)
 - the wiki in this repo will be used extensively to document anything (taking over this roll from the webpage and lauchpad)
 - you might always post an issue with any content in this repo of course

**Contribution**
 - pull requests are always welcome (beginning mid June 2013)
 - everyone is welcome to add interesting stuff, experiences, solutions to the wiki in this repo
