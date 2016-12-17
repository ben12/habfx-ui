package com.ben12.openhab.controller.impl;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;
import com.ben12.openhab.ui.FullWidthTilePane;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class PageController implements ContentController<Page>
{
	private MainViewController	mainViewController;

	private Label				title;

	private Page				page;

	private Pane				pane;

	@Override
	public void init(final Page data, final MainViewController pMainViewController)
	{
		page = data;
		mainViewController = pMainViewController;

		title = new Label();
		title.getStyleClass().add("title");
		title.textProperty().bind(page.titleProperty());

		pane = new FullWidthTilePane();

		final Function<Widget, Node> mapper = (widget) -> {
			final WidgetController controller = new WidgetController(page);
			controller.init(widget, mainViewController);
			return controller.getAccessView();
		};

		page.widgetsProperty().addListener(new ListChangeListener<Widget>()
		{
			@Override
			public void onChanged(final Change<? extends Widget> c)
			{
				while (c.next())
				{
					final int from = c.getFrom();

					if (c.wasRemoved())
					{
						pane.getChildren().remove(from, from + c.getRemovedSize());
					}
					if (c.wasAdded())
					{
						pane.getChildren().addAll(from, c.getAddedSubList() //
								.stream()
								.map(mapper)
								.collect(Collectors.toList()));
					}
				}
			}
		});

		pane.getChildren().addAll(page.widgetsProperty() //
				.stream()
				.map(mapper)
				.collect(Collectors.toList()));
	}

	@Override
	public void reload()
	{
		mainViewController.getRestClient().update(page);
	}

	@Override
	public Region getAccessView()
	{
		return null;
	}

	@Override
	public Region getInfosView()
	{
		Region infosView = null;
		if (page == null)
		{
			infosView = mainViewController.getDefaultInfosView();
		}
		if (infosView == null)
		{
			infosView = title;
		}
		return infosView;
	}

	@Override
	public Region getContentView()
	{
		return pane;
	}
}
