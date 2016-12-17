package com.ben12.openhab.controller.impl;

import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
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

public class WidgetController implements ContentController<Widget>
{
	private final Page				parent;

	private MainViewController		mainViewController;

	private Label					title;

	private VBox					accessView;

	private StringBinding			labelProperty;

	private ObjectExpression<Color>	labelColorProperty;

	private StringBinding			valueProperty;

	private ObjectExpression<Color>	valueColorProperty;

	private ObjectProperty<Image>	iconProperty;

	public WidgetController(final Page parent)
	{
		this.parent = parent;
	}

	@Override
	public void init(final Widget widget, final MainViewController mainViewController)
	{
		this.mainViewController = mainViewController;

		labelProperty = Bindings.createStringBinding(() -> {
			String label = widget.getLabel();
			label = label.replaceFirst(" \\[(.*?)\\]$", "");
			return label;
		}, widget.labelProperty());

		valueProperty = Bindings.createStringBinding(() -> {
			String label = widget.getLabel();
			label = label.replaceFirst("^.*?(?: \\[(.*?)\\])?$", "$1");
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
		widget.iconProperty().addListener((e) -> {
			final String icon = widget.getIcon();
			if (icon != null && !icon.isEmpty())
			{
				mainViewController.getRestClient().getImage(widget, iconCallback);
			}
			else
			{
				iconProperty.set(null);
			}
		});
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
		accessView.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		accessView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		accessView.prefWidth(0);
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

	@Override
	public Region getContentView()
	{
		return null;
	}
}
