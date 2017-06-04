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

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
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
	private static final Logger														LOGGER	= Logger
			.getLogger(WidgetControllerFactory.class.getName());

	private static final Map<String, Function<Page, ? extends WidgetController>>	CONTROLLERS;

	static
	{
		CONTROLLERS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		CONTROLLERS.put("Frame", FrameController::new);
		CONTROLLERS.put("Group", GroupController::new);
		CONTROLLERS.put("Text", TextController::new);
		CONTROLLERS.put("Switch", SwitchController::new);
		CONTROLLERS.put("Setpoint", SetpointController::new);
		CONTROLLERS.put("Selection", SetpointController::new);
		CONTROLLERS.put("Slider", SliderController::new);
		CONTROLLERS.put("Colorpicker", ColorpickerController::new);
		CONTROLLERS.put("Webview", WebviewController::new);
	}

	private WidgetControllerFactory()
	{
	}

	public static WidgetController createWidgetController(final String type, final Page page)
	{
		WidgetController controller = null;
		final Function<Page, ? extends WidgetController> constructor = CONTROLLERS.get(type);
		if (constructor != null)
		{
			controller = constructor.apply(page);
		}
		else
		{
			LOGGER.log(Level.WARNING, "Not yet implemented widget type %s", type);
		}
		return controller;
	}
}
