// Copyright (C) 2017 Benoît Moreau (ben.12)
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
package com.ben12.openhab.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.ProcessingException;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ErrorController implements ContentController<Throwable>
{
	private VBox		content;

	private Timeline	blink;

	@Override
	public void init(final Throwable t, final MainViewController mainViewController)
	{
		content = new VBox(2);
		content.setFillWidth(true);

		final Label infoLabel = new Label("ERROR: Check your configuration file");
		infoLabel.getStyleClass().add("error");
		infoLabel.setWrapText(true);
		infoLabel.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

		blink = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> infoLabel.setOpacity(0.7)),
				new KeyFrame(Duration.seconds(1.0), e -> infoLabel.setOpacity(1.0)));
		blink.setCycleCount(Animation.INDEFINITE);

		final Label errorLabel = new Label(t.getLocalizedMessage());
		errorLabel.getStyleClass().add("widget");
		errorLabel.setWrapText(true);
		errorLabel.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

		if ((t instanceof ProcessingException || t instanceof InvocationTargetException) && t.getCause() != null)
		{
			errorLabel.setText(t.getCause().getLocalizedMessage());
		}
		else
		{
			errorLabel.setText(t.getLocalizedMessage());
		}

		final StringWriter writer = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(writer);
		t.printStackTrace(printWriter);

		final Label detailsLabel = new Label(writer.toString());
		detailsLabel.getStyleClass().add("error-details");
		detailsLabel.setWrapText(true);
		detailsLabel.setVisible(false);
		detailsLabel.managedProperty().bind(detailsLabel.visibleProperty());
		detailsLabel.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

		errorLabel.setOnMouseClicked(e -> detailsLabel.setVisible(!detailsLabel.isVisible()));

		content.getChildren().setAll(infoLabel, errorLabel, detailsLabel);
	}

	@Override
	public void reload()
	{
		blink.play();
	}

	@Override
	public void hidding()
	{
		blink.stop();
	}

	@Override
	public Region getInfosView()
	{
		return new Label("ERROR!");
	}

	@Override
	public Region getAccessView()
	{
		return null;
	}

	@Override
	public Region getContentView()
	{
		return content;
	}
}
