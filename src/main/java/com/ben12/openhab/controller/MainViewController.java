package com.ben12.openhab.controller;

import java.util.Properties;

import com.ben12.openhab.model.Page;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.scene.layout.Region;

public interface MainViewController
{
	Region getDefaultInfosView();

	void display(ContentController<?> contentController);

	OpenHabRestClient getRestClient();

	Properties getConfig();

	Page getHomepage();
}
