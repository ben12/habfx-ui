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

import java.util.function.Function;
import java.util.stream.Collectors;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.controller.WidgetControllerFactory;
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
			Node view = null;
			final WidgetController controller = WidgetControllerFactory.createWidgetController(widget.getType(), page);
			if (controller != null)
			{
				controller.init(widget, mainViewController);
				view = controller.getAccessView();
			}
			return view;
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
								.filter(o -> o != null)
								.collect(Collectors.toList()));
					}
				}
			}
		});

		pane.getChildren().addAll(page.widgetsProperty() //
				.stream()
				.map(mapper)
				.filter(o -> o != null)
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
