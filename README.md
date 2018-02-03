# HABFX-UI
openHAB2 javaFX User Interface

[![GitHub license](https://img.shields.io/github/license/ben12/habfx-ui.svg)](https://github.com/ben12/habfx-ui/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/ben12/habfx-ui.svg?branch=master)](https://travis-ci.org/ben12/habfx-ui)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=com.ben12:habfx-ui)](https://sonarcloud.io/dashboard?id=com.ben12%3Ahabfx-ui)
[![GitHub version](https://badge.fury.io/gh/ben12%2Fhabfx-ui.svg)](https://github.com/ben12/habfx-ui/releases)

HABFX-UI is an OpenHAB2 client.<br />
It use OpenHAB2 REST-API and SSE.

UI is written in JavaFX and designed for small touch screen.<br />
I use it with an adafruit PiTFT of 2.8" plugged on a Raspberry Pi 3.

Supported sitemap widgets :
* Frame
* Group
* Text
* Switch (On/Off, Up/Stop/Down and mappings supported)
* Setpoint
* Selection
* Slider
* Colorpicker
* Webview
* Chart (for 'Number' item and 'Group' items containing 'Number' items)

## Install HABFX-UI

* [Download last released](https://bintray.com/ben12/HABFX-UI/habfx-ui) bin archive _habfx-ui-&lt;version&gt;-bin.zip_
* Unzip the archive
* Launch _habfx-ui/bin/habfx-ui_ on Linux, or _habfx-ui/bin/habfx-ui.bat_ on Windows

## Configuration

* Edit _habfx-ui/conf/config.properties_ file
* Change the value of _openhab.url_ with your openHAB 2 URL (example: https://myopenhab.org)
* Change the value of _openhab.user_ with your openHAB 2 user name (example: your e-mail)
* Change the value of _openhab.password_ with your openHAB 2 password
* Change the value of _sitemap_ with your favorite openHAB 2 sitemap name

## Build HABFX-UI with Maven :

Execute the command line :<br />
```
  mvn clean package
```

## Launch HABFX-UI :

* Go to _target/habfx-ui-&lt;version&gt;-bin/conf_
* Configure the application using _config.properties_ file.
* Go to _target/habfx-ui-&lt;version&gt;-bin/bin_
* Execute _habfx-ui_

## Demo :
[![HABFX-UI Demo](http://img.youtube.com/vi/8xlThwPW1MQ/0.jpg)](https://youtu.be/8xlThwPW1MQ)

## Screenshots :
![Screenshot 001](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/001.png)
![Screenshot 002](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/002.png)
![Screenshot 003](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/003.png)
![Screenshot 004](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/004.png)
![Screenshot 005](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/005.png)
![Screenshot 006](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/006.png)
![Screenshot 007](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/007.png)
![Screenshot 008](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/008.png)
![Screenshot 009](https://raw.githubusercontent.com/ben12/habfx-ui/master/doc/screenshots/009.png)
