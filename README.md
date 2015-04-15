Saturn
======

Financial Analysis Application


Installation
============

Initial Setup
--------

1. Install gradle (http://www.gradle.org/installation)
2. Install node (https://nodejs.org/download/)
3. Install Java 8 (https://java.com/en/download/)
4. ``export JAVA_HOME=<java 8 install directory>``
5. ``git clone https://github.com/akshaypundle/saturn.git``
6. ``cd saturn/src/web``
7. ``npm install``

You should now be able to edit the java and web source files. 

Eclipse setup
--------
In the ``saturn`` directory, run ``gradle eclipse``. This will generate a project that you can import into eclipse. 

Web build
------

* ``grunt`` in the ``saturn/src/web`` directory for building.
* ``grunt devWatch`` in the ``saturn/src/web`` directory for running the build in "dev" mode. This will launch a http server at 8080 (so you can test out changes by going to http://localhost:8080/). It will also watch files and rebuild the ones that change.

Java build
------
In the ``saturn`` directory, run ``gradle build``

Deploying to ec2
---------
in the ``saturn`` directory, run ``scripts/deploy.sh``. You will need the private key in your ``~/.ssh`` folder.
