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

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger			LOGGER	= Logger.getLogger(WidgetController.class.getName());

	private final Page					parent;

	private Widget						widget;

	private MainViewController			mainViewController;

	private Region						title;

	private VBox						accessView;

	private StringBinding				labelProperty;

	private ObjectExpression<String>	labelStyleProperty;

	private StringBinding				valueProperty;

	private ObjectExpression<String>	valueStyleProperty;

	private ObjectProperty<Image>		iconProperty;

	private StringBinding				itemStateProperty;

	private ItemChangeHandler			itemChangeHandlerRef;

	public WidgetController(final Page parent)
	{
		this.parent = parent;
	}

	@Override
	public void init(final Widget pWidget, final MainViewController pMainViewController)
	{
		mainViewController = pMainViewController;
		widget = pWidget;

		title = createTitleNode();

		final Node labelLabel = createLabelNode();
		final Node valueLabel = createValueNode();
		final Node iconImage = createIconNode();

		accessView = new VBox(2);
		accessView.getChildren().addAll(iconImage, labelLabel, valueLabel);
		accessView.setAlignment(Pos.CENTER);
		accessView.getStyleClass().add("widget");
		accessView.setMinSize(Region.USE_PREF_SIZE, 50);
		accessView.setMaxHeight(Double.MAX_VALUE);
		accessView.maxWidthProperty().bind(Bindings.selectDouble(accessView.parentProperty(), "layoutBounds", "width"));
		accessView.prefWidth(0);
		accessView.setOnMouseClicked(e -> {
			if (e.isStillSincePress())
			{
				display();
			}
		});

		final ChangeListener itemChangeHandler = e -> reload();

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

	protected void display()
	{
		if (getContentView() != null)
		{
			getMainViewController().display(WidgetController.this);
		}
	}

	public ObjectProperty<Image> iconProperty()
	{
		if (iconProperty == null)
		{
			iconProperty = new SimpleObjectProperty<>();
			final InvocationCallback<Image> iconCallback = new InvocationCallback<Image>()
			{
				@Override
				public void failed(final Throwable t)
				{
					LOGGER.log(Level.WARNING, "Cannot load image", t);
				}

				@Override
				public void completed(final Image image)
				{
					Platform.runLater(() -> iconProperty.set(image));
				}
			};

			widget.iconProperty()
					.addListener((w, o, n) -> mainViewController.getRestClient().getImage(widget, iconCallback));
			itemStateProperty()
					.addListener((w, o, n) -> mainViewController.getRestClient().getImage(widget, iconCallback));
			mainViewController.getRestClient().getImage(widget, iconCallback);
		}
		return iconProperty;
	}

	protected Region createTitleNode()
	{
		final Label titleNode = new Label();
		titleNode.getStyleClass().add("title");
		titleNode.textProperty().bind(labelProperty());

		// Maximum font height
		titleNode.heightProperty().addListener((e, o, n) -> {
			final Text textUtil = new Text(titleNode.getText());
			textUtil.setFont(titleNode.getFont());
			final double scale = titleNode.getHeight() / textUtil.getBoundsInLocal().getHeight();
			final Node text = titleNode.lookup(".text");
			text.setScaleX(scale);
			text.setScaleY(scale);
		});
		titleNode.boundsInLocalProperty().addListener(new javafx.beans.value.ChangeListener<Bounds>()
		{
			@Override
			public void changed(final ObservableValue<? extends Bounds> observable, final Bounds oldValue,
					final Bounds newValue)
			{
				titleNode.boundsInLocalProperty().removeListener(this);
				final Text textUtil = new Text(titleNode.getText());
				textUtil.setFont(titleNode.getFont());
				final double scale = titleNode.getHeight() / textUtil.getBoundsInLocal().getHeight();
				final Node text = titleNode.lookup(".text");
				text.setScaleX(scale);
				text.setScaleY(scale);
				titleNode.boundsInLocalProperty().addListener(this);
			}
		});

		return titleNode;
	}

	protected Node createIconNode()
	{
		final ImageView iconImage = new ImageView();
		iconImage.imageProperty().bind(iconProperty());

		return iconImage;
	}

	protected Node createLabelNode()
	{
		final Label labelNode = new Label();
		labelNode.getStyleClass().add("label-label");
		labelNode.textProperty().bind(labelProperty());
		labelNode.styleProperty().bind(labelStyleProperty());
		labelNode.setMinSize(0, 0);
		labelNode.prefHeightProperty().bind(Bindings.createDoubleBinding(
				() -> (labelProperty().isNotEmpty().get() ? Region.USE_COMPUTED_SIZE : 0), labelProperty()));

		return labelNode;
	}

	protected Node createValueNode()
	{
		final Label valueNode = new Label();
		valueNode.getStyleClass().add("value-label");
		valueNode.textProperty().bind(valueProperty());
		valueNode.styleProperty().bind(valueStyleProperty());
		valueNode.setMinSize(0, 0);
		valueNode.prefHeightProperty().bind(Bindings.createDoubleBinding(
				() -> (valueProperty().isNotEmpty().get() ? Region.USE_COMPUTED_SIZE : 0), valueProperty()));

		return valueNode;
	}

	@Override
	public void reload()
	{
		mainViewController.getRestClient().update(parent);
	}

	@Override
	public void hidding()
	{
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
		if (itemStateProperty == null)
		{
			itemStateProperty = Bindings.selectString(widget.itemProperty(), "state");
		}
		return itemStateProperty;
	}

	protected StringBinding labelProperty()
	{
		if (labelProperty == null)
		{
			labelProperty = Bindings.createStringBinding(() -> {
				String label = widget.getLabel();
				label = label.replaceFirst("\\s*\\[.*?\\]$", "");
				return label;
			}, widget.labelProperty());
		}
		return labelProperty;
	}

	protected ObjectExpression<String> labelStyleProperty()
	{
		if (labelStyleProperty == null)
		{
			labelStyleProperty = Bindings.createObjectBinding(() -> {
				String style = "";
				final String labelcolor = widget.getLabelcolor();
				if (labelcolor != null)
				{
					style += "-fx-text-fill :" + labelcolor + ";";
				}
				return style;
			}, widget.labelcolorProperty());
		}
		return labelStyleProperty;
	}

	protected StringBinding valueProperty()
	{
		if (valueProperty == null)
		{
			valueProperty = Bindings.createStringBinding(() -> {
				String label = widget.getLabel();
				label = label.replaceFirst("^.*?(?:\\[(.*?)\\])?$", "$1");
				return label;
			}, widget.labelProperty());
		}
		return valueProperty;
	}

	protected ObjectExpression<String> valueStyleProperty()
	{
		if (valueStyleProperty == null)
		{
			valueStyleProperty = Bindings.createObjectBinding(() -> {
				String style = "";
				final String valuecolor = widget.getValuecolor();
				if (valuecolor != null)
				{
					style += "-fx-text-fill :" + valuecolor + ";";
				}
				return style;
			}, widget.labelcolorProperty());
		}
		return valueStyleProperty;
	}

	protected static class ItemChangeHandler extends WeakReference<ChangeListener> implements ChangeListener
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
