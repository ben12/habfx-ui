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
package com.ben12.openhab.controller.impl;

import com.ben12.openhab.control.ColorPicker;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ColorpickerController extends WidgetController
{
	private VBox content;

	/**
	 * @param parent
	 */
	public ColorpickerController(final Page parent)
	{
		super(parent);
	}

	@Override
	public void init(final Widget pWidget, final MainViewController pMainViewController)
	{
		super.init(pWidget, pMainViewController);

		final ImageView iconImage = new ImageView();
		iconImage.imageProperty().bind(iconProperty());

		final ColorPicker colorpicker = new ColorPicker();

		final Timeline submitState = new Timeline(
				new KeyFrame(Duration.millis(200), (ea) -> getMainViewController().getRestClient() //
						.submit(getWidget().getItem(), toState(colorpicker.getColor()))));

		final ChangeListener<Color> colorListener = (i, oldState, newState) -> submitState.play();

		itemStateProperty().addListener((i, oldState, newState) -> {
			if (submitState.getStatus() != Status.RUNNING)
			{
				colorpicker.colorProperty().removeListener(colorListener);
				colorpicker.setColor(parseColor(itemStateProperty().getValue()));
				colorpicker.colorProperty().addListener(colorListener);
			}
		});

		colorpicker.setColor(parseColor(itemStateProperty().getValue()));
		colorpicker.colorProperty().addListener(colorListener);

		content = new VBox(iconImage, colorpicker);
		VBox.setVgrow(colorpicker, Priority.ALWAYS);
		content.setAlignment(Pos.CENTER);
		content.prefHeightProperty().bind(Bindings.selectDouble(content.parentProperty(), "layoutBounds", "height"));
		content.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		content.prefWidth(0);

		getAccessView().setOnMouseClicked((e) -> {
			if (e.isStillSincePress())
			{
				getMainViewController().display(ColorpickerController.this);
			}
		});
	}

	private Color parseColor(final String value)
	{
		Color color = Color.WHITE;
		if (value != null)
		{
			final String[] hsb = value.split(",");
			if (hsb.length == 3)
			{
				color = Color.hsb(Double.parseDouble(hsb[0]), Double.parseDouble(hsb[1]) / 100.0,
						Double.parseDouble(hsb[2]) / 100.0);
			}
		}
		return color;
	}

	private String toState(final Color color)
	{
		String state = "0,0,0";
		if (color != null)
		{
			state = color.getHue() + "," + (color.getSaturation() * 100) + "," + (color.getBrightness() * 100);
		}
		return state;
	}

	@Override
	public Region getContentView()
	{
		return content;
	}
}
