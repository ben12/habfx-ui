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
package com.ben12.openhab.control.skin;

import com.ben12.openhab.control.ColorPicker;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.SkinBase;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import jfxtras.labs.util.BiLinearGradient;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ColorPickerSkin extends SkinBase<ColorPicker>
{
	private final Rectangle				selectedColor;

	private final Rectangle				mainColor;

	private final Rectangle				secondColor;

	private final ObjectProperty<Color>	color	= new SimpleObjectProperty<Color>(this, "value");;

	/**
	 * @param control
	 */
	public ColorPickerSkin(final ColorPicker control)
	{
		super(control);

		control.getStyleClass().add("ben12-color-picker");

		color.set(control.getColor());

		final Stop[] stops = new Stop[] { new Stop(0.0, Color.RED), new Stop(0.17, Color.YELLOW),
				new Stop(0.33, Color.GREEN), new Stop(0.50, Color.CYAN), new Stop(0.67, Color.BLUE),
				new Stop(0.83, Color.FUCHSIA), new Stop(1.0, Color.RED) };
		final LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

		selectedColor = new Rectangle();
		selectedColor.fillProperty().bind(control.colorProperty());
		selectedColor.getStyleClass().add("selected-color");

		mainColor = new Rectangle();
		mainColor.setFill(gradient);
		mainColor.getStyleClass().add("main-color");

		secondColor = new Rectangle();
		secondColor.layoutBoundsProperty().addListener((r, o, n) -> Platform.runLater(() -> updateSecondColorShape()));
		secondColor.getStyleClass().add("second-color");
		color.addListener((r, o, n) -> Platform.runLater(() -> updateSecondColorShape()));

		final VBox box = new VBox(2);
		VBox.setVgrow(secondColor, Priority.ALWAYS);
		box.getChildren().addAll(selectedColor, secondColor, mainColor);
		box.setPrefSize(50, 50);
		box.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		box.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

		selectedColor.widthProperty().bind(box.widthProperty() //
				.subtract(selectedColor.strokeWidthProperty()));
		mainColor.widthProperty().bind(box.widthProperty());
		secondColor.widthProperty().bind(box.widthProperty());

		selectedColor.heightProperty().bind(box.heightProperty() //
				.subtract(6)
				.subtract(selectedColor.strokeWidthProperty())
				.multiply(0.05));
		secondColor.heightProperty().bind(box.heightProperty() //
				.subtract(6)
				.multiply(0.70));
		mainColor.heightProperty().bind(box.heightProperty() //
				.subtract(6)
				.multiply(0.25));

		getChildren().add(box);

		initListeners();
	}

	private void updateSecondColorShape()
	{
		final BiLinearGradient biLinearGradient = new BiLinearGradient(Color.WHITE, color.get(), Color.BLACK,
				Color.BLACK);

		final int x = (int) secondColor.getLayoutBounds().getMinX();
		final int y = (int) secondColor.getLayoutBounds().getMinY();
		final int width = (int) secondColor.getLayoutBounds().getWidth();
		final int height = (int) secondColor.getLayoutBounds().getHeight();
		secondColor.setFill(new ImagePattern(biLinearGradient.getImage(width, height), x, y, width, height, false));
	}

	private void initListeners()
	{
		final EventHandler<MouseEvent> mainColorEvent = (e) -> {
			final double width = mainColor.getWidth();
			final double height = mainColor.getHeight();
			final double x = adjust(e.getX(), 0.0, width);
			final double y = adjust(e.getY(), 0.0, height);

			final WritableImage image = new WritableImage((int) width + 1, (int) height + 1);
			mainColor.snapshot(null, image);
			final Color mcolor = image.getPixelReader().getColor((int) x, (int) y);
			color.set(mcolor);
			getSkinnable().setColor(mcolor);
		};

		mainColor.setOnMouseDragged(mainColorEvent);
		mainColor.setOnMouseClicked(mainColorEvent);

		final EventHandler<MouseEvent> secondColorEvent = (e) -> {
			final double width = secondColor.getWidth();
			final double height = secondColor.getHeight();
			final double x = adjust(e.getX(), 0.0, width);
			final double y = adjust(e.getY(), 0.0, height);

			final WritableImage image = new WritableImage((int) width + 1, (int) height + 1);
			secondColor.snapshot(null, image);
			final Color scolor = image.getPixelReader().getColor((int) x, (int) y);
			getSkinnable().setColor(scolor);
		};

		secondColor.setOnMouseDragged(secondColorEvent);
		secondColor.setOnMouseClicked(secondColorEvent);
	}

	private double adjust(final double value, final double min, final double max)
	{
		double adjusted = value;
		if (value < min)
		{
			adjusted = min;
		}
		if (value > max)
		{
			adjusted = max;
		}
		return adjusted;
	}
}
