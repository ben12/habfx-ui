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
package com.ben12.openhab.controller;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ben12.openhab.controller.impl.ColorpickerController;
import com.ben12.openhab.controller.impl.FrameController;
import com.ben12.openhab.controller.impl.GroupController;
import com.ben12.openhab.controller.impl.SetpointController;
import com.ben12.openhab.controller.impl.SliderController;
import com.ben12.openhab.controller.impl.SwitchController;
import com.ben12.openhab.controller.impl.TextController;
import com.ben12.openhab.controller.impl.WebviewController;
import com.ben12.openhab.controller.impl.WidgetController;
import com.ben12.openhab.model.Page;

public final class WidgetControllerFactory
{
	private static final Logger											LOGGER	= Logger
			.getLogger(WidgetControllerFactory.class.getName());

	private static final Map<String, Class<? extends WidgetController>>	CONTROLLERS;

	private static final MethodType										CONSTRUCTOR;

	static
	{
		CONTROLLERS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		CONTROLLERS.put("Frame", FrameController.class);
		CONTROLLERS.put("Group", GroupController.class);
		CONTROLLERS.put("Text", TextController.class);
		CONTROLLERS.put("Switch", SwitchController.class);
		CONTROLLERS.put("Setpoint", SetpointController.class);
		CONTROLLERS.put("Selection", SetpointController.class);
		CONTROLLERS.put("Slider", SliderController.class);
		CONTROLLERS.put("Colorpicker", ColorpickerController.class);
		CONTROLLERS.put("Webview", WebviewController.class);

		CONSTRUCTOR = MethodType.methodType(void.class, Page.class);
	}

	private WidgetControllerFactory()
	{
	}

	public static WidgetController createWidgetController(final String type, final Page page)
	{
		WidgetController controller = null;
		final Class<? extends WidgetController> clazz = CONTROLLERS.get(type);
		if (clazz != null)
		{
			try
			{
				controller = (WidgetController) MethodHandles.lookup().findConstructor(clazz, CONSTRUCTOR).invoke(page);
			}
			catch (final Throwable e)
			{
				LOGGER.log(Level.SEVERE, "Cannot display widget", e);
			}
		}
		else
		{
			LOGGER.log(Level.WARNING, "Not yet implemented widget type %s", type);
		}
		return controller;
	}
}
