// Copyright (C) 2017 Benoît Moreau (ben.12)
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

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 * @author Benoît Moreau (ben.12)
 */
public class WebviewController extends WidgetController
{
	private VBox	content;

	private WebView	webView;

	/**
	 * @param parent
	 *            parent page
	 */
	public WebviewController(final Page parent)
	{
		super(parent);
	}

	@Override
	public void init(final Widget widget, final MainViewController mainViewController)
	{
		super.init(widget, mainViewController);

		getAccessView().setOnMouseClicked(e -> {
			if (e.isStillSincePress())
			{
				getMainViewController().display(this);
				webView.getEngine().load(widget.getUrl());
			}
		});

		webView = new WebView();
		webView.getEngine().setOnError((e) -> e.getException().printStackTrace());

		content = new VBox(webView);
		VBox.setVgrow(webView, Priority.ALWAYS);
		content.setAlignment(Pos.CENTER);
		content.prefHeightProperty().bind(Bindings.selectDouble(content.parentProperty(), "layoutBounds", "height"));
		content.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		content.prefWidth(0);
	}

	@Override
	public Region getContentView()
	{
		return content;
	}
}
