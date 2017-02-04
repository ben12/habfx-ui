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

import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * @author Benoît Moreau (ben.12)
 */
public class SliderController extends WidgetController
{
	private VBox content;

	/**
	 * @param parent
	 */
	public SliderController(final Page parent)
	{
		super(parent);
	}

	@Override
	public void init(final Widget pWidget, final MainViewController pMainViewController)
	{
		super.init(pWidget, pMainViewController);

		final ImageView iconImage = new ImageView();
		iconImage.imageProperty().bind(iconProperty());

		final Slider slider = new Slider(0.0, 100.0, 0.0);

		final Timeline submitState = new Timeline(
				new KeyFrame(Duration.millis(200), (ea) -> getMainViewController().getRestClient() //
						.submit(getWidget().getItem(), Integer.toString((int) slider.getValue()))));

		final ChangeListener<Number> sliderListener = (i, oldState, newState) -> submitState.play();

		itemStateProperty().addListener((i, oldState, newState) -> {
			if (submitState.getStatus() != Status.RUNNING && !slider.isValueChanging())
			{
				slider.valueProperty().removeListener(sliderListener);
				slider.setValue(newState == null ? 0.0 : Double.valueOf(newState));
				slider.valueProperty().addListener(sliderListener);
			}
		});

		slider.setValue(itemStateProperty().get() == null ? 0.0 : Double.valueOf(itemStateProperty().get()));
		slider.valueProperty().addListener(sliderListener);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);

		content = new VBox(iconImage, slider);
		content.setAlignment(Pos.CENTER);
		content.setMinSize(Region.USE_PREF_SIZE, 50);
		content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		content.prefWidth(0);
		content.setPadding(new Insets(0, 5, 0, 5));

		getAccessView().setOnMouseClicked((e) -> {
			if (e.isStillSincePress())
			{
				getMainViewController().display(SliderController.this);
			}
		});
	}

	@Override
	public Region getContentView()
	{
		return content;
	}
}
