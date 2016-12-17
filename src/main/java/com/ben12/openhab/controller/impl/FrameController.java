package com.ben12.openhab.controller.impl;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Widget;

import javafx.scene.layout.Region;

public class FrameController implements ContentController<Widget>
{

	@Override
	public void init(final Widget data, final MainViewController mainViewController)
	{
	}

	@Override
	public void reload()
	{
	}

	@Override
	public Region getInfosView()
	{
		return null;
	}

	@Override
	public Region getAccessView()
	{
		return null;
	}

	@Override
	public Region getContentView()
	{
		return null;
	}

}
