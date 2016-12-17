package com.ben12.openhab.controller;

import javafx.scene.layout.Region;

public interface ContentController<T>
{
	void init(T data, MainViewController mainViewController);

	void reload();

	Region getInfosView();

	Region getAccessView();

	Region getContentView();
}
