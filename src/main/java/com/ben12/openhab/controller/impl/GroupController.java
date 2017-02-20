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

import javafx.scene.layout.Region;

public class GroupController extends WidgetController
{
	private PageController pageController;

	public GroupController(final Page parent)
	{
		super(parent);
	}

	@Override
	public void init(final Widget widget, final MainViewController pMainViewController)
	{
		super.init(widget, pMainViewController);

		pageController = new PageController();
		pageController.init(getWidget().getLinkedPage(), getMainViewController());
	}

	@Override
	protected void display()
	{
		getMainViewController().display(pageController);
	}

	@Override
	public Region getContentView()
	{
		return null;
	}
}
