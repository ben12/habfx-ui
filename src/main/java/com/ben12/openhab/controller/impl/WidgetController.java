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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Item;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public abstract class WidgetController implements ContentController<Widget>
{
	private final Page				parent;

	private Widget					widget;

	private MainViewController		mainViewController;

	private Label					title;

	private VBox					accessView;

	private StringBinding			labelProperty;

	private ObjectExpression<Color>	labelColorProperty;

	private StringBinding			valueProperty;

	private ObjectExpression<Color>	valueColorProperty;

	private ObjectProperty<Image>	iconProperty;

	private StringBinding			itemStateProperty;

	private ItemChangeHandler		itemChangeHandler;

	public WidgetController(final Page parent)
	{
		this.parent = parent;
	}

	@Override
	public void init(final Widget pWidget, final MainViewController pMainViewController)
	{
		mainViewController = pMainViewController;
		widget = pWidget;

		labelProperty = Bindings.createStringBinding(() -> {
			String label = widget.getLabel();
			label = label.replaceFirst("\\[(.*?)\\]$", "");
			return label;
		}, widget.labelProperty());

		valueProperty = Bindings.createStringBinding(() -> {
			String label = widget.getLabel();
			label = label.replaceFirst("^.*?(?:\\[(.*?)\\])?$", "$1");
			return label;
		}, widget.labelProperty());

		labelColorProperty = Bindings.createObjectBinding(() -> {
			Color color = Color.BLACK;
			final String labelcolor = widget.getLabelcolor();
			if (labelcolor != null)
			{
				color = Color.valueOf(labelcolor);
			}
			return color;
		}, widget.labelcolorProperty());

		valueColorProperty = Bindings.createObjectBinding(() -> {
			Color color = Color.BLACK;
			final String valuecolor = widget.getValuecolor();
			if (valuecolor != null)
			{
				color = Color.valueOf(valuecolor);
			}
			return color;
		}, widget.labelcolorProperty());

		itemStateProperty = Bindings.selectString(widget.itemProperty(), "state");

		iconProperty = new SimpleObjectProperty<>();
		final InvocationCallback<Image> iconCallback = new InvocationCallback<Image>()
		{
			@Override
			public void failed(final Throwable t)
			{
				t.printStackTrace();
			}

			@Override
			public void completed(final Image image)
			{
				Platform.runLater(() -> iconProperty.set(image));
			}
		};

		widget.iconProperty()
				.addListener((w, o, n) -> mainViewController.getRestClient().getImage(widget, iconCallback));
		itemStateProperty.addListener((w, o, n) -> mainViewController.getRestClient().getImage(widget, iconCallback));
		mainViewController.getRestClient().getImage(widget, iconCallback);

		title = new Label();
		title.getStyleClass().add("title");
		title.textProperty().bind(labelProperty);

		final Label labelLabel = new Label();
		labelLabel.textProperty().bind(labelProperty);
		labelLabel.textFillProperty().bind(labelColorProperty);
		labelLabel.setMinSize(0, 0);
		labelLabel.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			return labelProperty.isNotEmpty().get() ? Region.USE_COMPUTED_SIZE : 0;
		}, labelProperty));

		final Label valueLabel = new Label();
		valueLabel.textProperty().bind(valueProperty);
		valueLabel.textFillProperty().bind(valueColorProperty);
		valueLabel.setMinSize(0, 0);
		valueLabel.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			return valueProperty.isNotEmpty().get() ? Region.USE_COMPUTED_SIZE : 0;
		}, valueProperty));

		final ImageView iconImage = new ImageView();
		iconImage.imageProperty().bind(iconProperty);

		accessView = new VBox(2);
		accessView.getChildren().addAll(iconImage, labelLabel, valueLabel);
		accessView.setAlignment(Pos.CENTER);
		accessView.getStyleClass().add("widget");
		accessView.setMinSize(Region.USE_PREF_SIZE, 50);
		accessView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		accessView.prefWidth(0);

		widget.itemProperty().addListener((o, oldItem, newItem) -> {
			if (itemChangeHandler != null)
			{
				itemChangeHandler.release();
				itemChangeHandler = null;
			}
			if (newItem != null)
			{
				itemChangeHandler = new ItemChangeHandler(newItem.getName());
			}
		});
		final Item item = widget.getItem();
		if (item != null)
		{
			itemChangeHandler = new ItemChangeHandler(item.getName());
		}
	}

	@Override
	public void reload()
	{
		mainViewController.getRestClient().update(parent);
	}

	@Override
	public Region getInfosView()
	{
		return title;
	}

	@Override
	public Region getAccessView()
	{
		return accessView;
	}

	public MainViewController getMainViewController()
	{
		return mainViewController;
	}

	public Widget getWidget()
	{
		return widget;
	}

	public Page getParent()
	{
		return parent;
	}

	protected StringBinding itemStateProperty()
	{
		return itemStateProperty;
	}

	protected ObjectProperty<Image> iconProperty()
	{
		return iconProperty;
	}

	private class ItemChangeHandler implements ChangeListener
	{
		private final String itemName;

		public ItemChangeHandler(final String pItemName)
		{
			itemName = pItemName;
			mainViewController.getRestClient().addItemStateChangeListener(itemName, this);
		}

		@Override
		public void stateChanged(final ChangeEvent e)
		{
			reload();
		}

		public void release()
		{
			mainViewController.getRestClient().removeItemStateChangeListener(itemName, this);
		}
	}
}
