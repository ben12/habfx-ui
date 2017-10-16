# habfx-ui
openHAB2 javaFX User Interface

HABFX-UI is an OpenHAB2 client.<br />
It use OpenHAB2 REST-API and SSE.

UI is written in JavaFX and designed for small touch screen.<br />
I use it with an adafruit PiTFT of 2.8" plugged on a Raspberry Pi 3.

Supported sitemap widgets :
* Frame
* Group
* Text
* Switch
* Setpoint
* Selection
* Slider
* Colorpicker
* Webview
* Chart (for 'Number' item)

## Build HABFX-UI :

Execute the command line :<br />
```
  mvn clean package
```

## Launch HABFX-UI :

* Go to _target/appassembler/conf_
* Configure the application using _config.properties_ file.
* Go to _target/appassembler/bin_
* Execute _habfx-ui_

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
