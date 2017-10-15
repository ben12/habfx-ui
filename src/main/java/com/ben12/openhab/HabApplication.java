// Copyright (C) 2016 Benoît Moreau (ben.12)
// 
// This file is part of HABFX-UI (openHAB javaFX User Interface).
// 
// HABFX-UI is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// HABFX-UI is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with HABFX-UI.  If not, see <http://www.gnu.org/licenses/>.

package com.ben12.openhab;

import java.util.Properties;
import java.util.ServiceLoader;

import com.ben12.openhab.controller.MainController;
import com.ben12.openhab.plugin.HabApplicationPlugin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * {@link HabApplication} main entry point.
 *
 * @author Benoît Moreau (ben.12)
 */
public class HabApplication extends Application
{
	private static final String	FULLSCREEN_CFG		= "fullscreen";

	private static final String	HIDE_CURSOR_CFG		= "hide.cursor";

	private static final String	ALWAYS_ON_TOP_CFG	= "always.on.top";

	@Override
	public void start(final Stage primaryStage) throws Exception
	{
		final MainController mainController = new MainController();
		final Properties config = mainController.getConfig();

		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(HabApplication.class.getResource("ui/Main.fxml"));
		loader.setControllerFactory(c -> mainController);
		final Parent root = loader.load();
		final Scene scene = new Scene(root);

		if (Boolean.valueOf(config.getProperty(FULLSCREEN_CFG, "false")))
		{
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setX(0);
			primaryStage.setY(0);
			primaryStage.setFullScreen(true);
			primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		}

		if (Boolean.valueOf(config.getProperty(HIDE_CURSOR_CFG, "false")))
		{
			scene.setCursor(Cursor.NONE);
		}

		primaryStage.setAlwaysOnTop(Boolean.valueOf(config.getProperty(ALWAYS_ON_TOP_CFG, "false")));
		primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> System.exit(0));
		primaryStage.setScene(scene);
		primaryStage.show();

		// Starts plugins
		final ServiceLoader<HabApplicationPlugin> serviceLoader = ServiceLoader.load(HabApplicationPlugin.class);
		for (final HabApplicationPlugin plugin : serviceLoader)
		{
			plugin.init(primaryStage);
		}
	}

	/**
	 * @param args
	 *            application arguments
	 */
	public static void main(final String[] args)
	{
		Application.launch(HabApplication.class, args);
	}
}
