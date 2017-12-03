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

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;

import javafx.scene.layout.Region;

public class TextController extends WidgetController
{
    private ContentController<?> controller;

    public TextController(final Page parent)
    {
        super(parent);
    }

    @Override
    public void init(final Widget pWidget, final MainViewController pMainViewController)
    {
        super.init(pWidget, pMainViewController);

        if (pWidget.getLinkedPage() != null)
        {
            final Page linkedPage = pWidget.getLinkedPage();
            final String url = linkedPage.getLink();
            if (url != null && !url.isEmpty())
            {
                final PageController pageController = new PageController();
                pageController.init(linkedPage, pMainViewController);
                controller = pageController;
            }
        }

        if (controller == null && !pWidget.getWidgets().isEmpty())
        {
            final FrameController frameController = new FrameController(getParent());
            frameController.init(pWidget, pMainViewController);
            controller = frameController;
        }
    }

    @Override
    protected void display()
    {
        if (controller != null)
        {
            getMainViewController().display(controller);
        }
    }

    @Override
    public Region getContentView()
    {
        return null;
    }
}
