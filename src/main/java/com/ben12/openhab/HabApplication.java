package com.ben12.openhab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class HabApplication extends Application
{

	@Override
	public void start(final Stage primaryStage) throws Exception
	{
		final boolean debug = System.getProperty("debug") != null;

		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(HabApplication.class.getResource("ui/Main.fxml"));
		final Parent root = (Parent) loader.load();
		final Scene scene = new Scene(root);

		if (!debug)
		{
			scene.setCursor(Cursor.NONE);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setAlwaysOnTop(true);
			primaryStage.setX(0);
			primaryStage.setY(0);
			primaryStage.setFullScreen(true);
			primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		}

		primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (e) -> System.exit(0));
		primaryStage.setScene(scene);
		primaryStage.show();
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
