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

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.controller.WidgetControllerFactory;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;
import com.ben12.openhab.ui.FullWidthTilePane;

import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class FrameController extends WidgetController
{
	private Pane pane;

	public FrameController(final Page parent)
	{
		super(parent);
	}

	@Override
	public void init(final Widget pWidget, final MainViewController pMainViewController)
	{
		super.init(pWidget, pMainViewController);

		pane = new FullWidthTilePane();

		final Function<Widget, Node> mapper = widget -> {
			final WidgetController controller = WidgetControllerFactory.createWidgetController(widget.getType(),
					getParent());
			Node view = null;
			if (controller != null)
			{
				controller.init(widget, getMainViewController());
				view = controller.getAccessView();
			}
			return view;
		};

		getWidget().widgetsProperty().addListener((final Change<? extends Widget> c) -> {
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
							.filter(Objects::nonNull)
							.collect(Collectors.toList()));
				}
			}
		});

		pane.getChildren().addAll(getWidget().widgetsProperty() //
				.stream()
				.map(mapper)
				.filter(Objects::nonNull)
				.collect(Collectors.toList()));
	}

	@Override
	public Region getContentView()
	{
		return pane;
	}
}
