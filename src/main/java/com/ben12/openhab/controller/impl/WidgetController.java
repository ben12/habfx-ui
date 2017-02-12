// Copyright (C) 2016 Beno�t Moreau (ben.12)
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

import java.lang.ref.WeakReference;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Item;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public abstract class WidgetController implements ContentController<Widget>
{
	private final Page					parent;

	private Widget						widget;

	private MainViewController			mainViewController;

	private Label						title;

	private VBox						accessView;

	private StringBinding				labelProperty;

	private ObjectExpression<String>	labelStyleProperty;

	private StringBinding				valueProperty;

	private ObjectExpression<String>	valueStyleProperty;

	private ObjectProperty<Image>		iconProperty;

	private StringBinding				itemStateProperty;

	private ItemChangeHandler			itemChangeHandlerRef;

	private ChangeListener				itemChangeHandler;

	private Label						labelLabel;

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

		labelStyleProperty = Bindings.createObjectBinding(() -> {
			String style = "";
			final String labelcolor = widget.getLabelcolor();
			if (labelcolor != null)
			{
				style += "-fx-text-fill :" + labelcolor + ";";
			}
			return style;
		}, widget.labelcolorProperty());

		valueStyleProperty = Bindings.createObjectBinding(() -> {
			String style = "";
			final String valuecolor = widget.getValuecolor();
			if (valuecolor != null)
			{
				style += "-fx-text-fill :" + valuecolor + ";";
			}
			return style;
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

		// Maximum font height
		title.heightProperty().addListener((e, o, n) -> {
			final Text textUtil = new Text(title.getText());
			textUtil.setFont(title.getFont());
			final double scale = title.getHeight() / textUtil.getBoundsInLocal().getHeight();
			final Node text = title.lookup(".text");
			text.setScaleX(scale);
			text.setScaleY(scale);
		});
		title.boundsInLocalProperty().addListener(new javafx.beans.value.ChangeListener<Bounds>()
		{
			@Override
			public void changed(final ObservableValue<? extends Bounds> observable, final Bounds oldValue,
					final Bounds newValue)
			{
				title.boundsInLocalProperty().removeListener(this);
				final Text textUtil = new Text(title.getText());
				textUtil.setFont(title.getFont());
				final double scale = title.getHeight() / textUtil.getBoundsInLocal().getHeight();
				final Node text = title.lookup(".text");
				text.setScaleX(scale);
				text.setScaleY(scale);
				title.boundsInLocalProperty().addListener(this);
			}
		});

		labelLabel = new Label();
		labelLabel.textProperty().bind(labelProperty);
		labelLabel.styleProperty().bind(labelStyleProperty);
		labelLabel.setMinSize(0, 0);
		labelLabel.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
			return labelProperty.isNotEmpty().get() ? Region.USE_COMPUTED_SIZE : 0;
		}, labelProperty));

		final Label valueLabel = new Label();
		valueLabel.textProperty().bind(valueProperty);
		valueLabel.styleProperty().bind(valueStyleProperty);
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

		itemChangeHandler = new ChangeListener()
		{
			@Override
			public void stateChanged(final ChangeEvent e)
			{
				reload();
			}
		};

		widget.itemProperty().addListener((o, oldItem, newItem) -> {
			if (itemChangeHandlerRef != null)
			{
				itemChangeHandlerRef.release();
				itemChangeHandlerRef = null;
			}
			if (newItem != null)
			{
				itemChangeHandlerRef = new ItemChangeHandler(mainViewController.getRestClient(), newItem.getName(),
						itemChangeHandler);
			}
		});
		final Item item = widget.getItem();
		if (item != null)
		{
			itemChangeHandlerRef = new ItemChangeHandler(mainViewController.getRestClient(), item.getName(),
					itemChangeHandler);
		}
	}

	@Override
	public void reload()
	{
		mainViewController.getRestClient().update(parent);
	}

	@Override
	public Label getInfosView()
	{
		return title;
	}

	@Override
	public VBox getAccessView()
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

	private static class ItemChangeHandler extends WeakReference<ChangeListener> implements ChangeListener
	{
		private final String			itemName;

		private final OpenHabRestClient	openHabRestClient;

		public ItemChangeHandler(final OpenHabRestClient pOpenHabRestClient, final String pItemName,
				final ChangeListener l)
		{
			super(l);

			openHabRestClient = pOpenHabRestClient;
			itemName = pItemName;
			openHabRestClient.addItemStateChangeListener(itemName, this);
		}

		@Override
		public void stateChanged(final ChangeEvent e)
		{
			final ChangeListener l = get();
			if (l != null)
			{
				l.stateChanged(e);
			}
			else
			{
				release();
			}
		}

		public void release()
		{
			openHabRestClient.removeItemStateChangeListener(itemName, this);
		}
	}
}