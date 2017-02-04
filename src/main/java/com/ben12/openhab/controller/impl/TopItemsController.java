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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.controller.ContentController;
import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Item;
import com.ben12.openhab.model.util.BeanCopy;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Top view custom items controller.
 * 
 * @author Benoît Moreau (ben.12)
 */
public class TopItemsController implements ContentController<Void>
{
	/** First item configuration key. */
	private static final String		ITEM1_CFG	= "item1";

	/** Second item configuration key. */
	private static final String		ITEM2_CFG	= "item2";

	/** Third item configuration key. */
	private static final String		ITEM3_CFG	= "item3";

	/** Pattern item configuration key suffix. */
	private static final String		PATTERN_CFG	= ".pattern";

	/** The main view controller. */
	private MainViewController		mainViewController;

	/** Items top informations pane. */
	@FXML
	private Region					infosPane;

	/** First item {@link Label}. */
	@FXML
	private Label					item1Label;

	/** Second item {@link Label}. */
	@FXML
	private Label					item2Label;

	/** Third item {@link Label}. */
	@FXML
	private Label					item3Label;

	/** First {@link Item} model. */
	private Item					item1;

	/** Second {@link Item} model. */
	private Item					item2;

	/** Third {@link Item} model. */
	private Item					item3;

	/** Keeps references on items {@link StringExpression} for bindings. */
	private List<StringExpression>	stateProperties;

	@Override
	public void init(final Void data, final MainViewController pMainViewController)
	{
		mainViewController = pMainViewController;
		stateProperties = new ArrayList<>(3);

		final Properties configuration = mainViewController.getConfig();
		final String item1Name = configuration.getProperty(ITEM1_CFG, "");
		final String item1Pattern = configuration.getProperty(ITEM1_CFG + PATTERN_CFG);
		final String item2Name = configuration.getProperty(ITEM2_CFG, "");
		final String item2Pattern = configuration.getProperty(ITEM2_CFG + PATTERN_CFG);
		final String item3Name = configuration.getProperty(ITEM3_CFG, "");
		final String item3Pattern = configuration.getProperty(ITEM3_CFG + PATTERN_CFG);

		if (!item1Name.isEmpty())
		{
			item1 = new Item();
			item1.setState("-");
			createItem(item1Name, item1Pattern, item1, item1Label);
		}
		if (!item2Name.isEmpty())
		{
			item2 = new Item();
			item2.setState("-");
			createItem(item2Name, item2Pattern, item2, item2Label);
		}
		if (!item3Name.isEmpty())
		{
			item3 = new Item();
			item3.setState("-");
			createItem(item3Name, item3Pattern, item3, item3Label);
		}

		if (isEmpty())
		{
			infosPane = null;
		}
	}

	/**
	 * @param itemName
	 *            item name to bind
	 * @param itemPattern
	 *            item format pattern
	 * @param item
	 *            item model to fill
	 * @param itemLabel
	 *            item {@link Label} control
	 */
	private void createItem(final String itemName, final String itemPattern, final Item item, final Label itemLabel)
	{
		final OpenHabRestClient restClient = mainViewController.getRestClient();

		StringExpression stateProperty = item.stateProperty();
		if (itemPattern != null && !itemPattern.isEmpty())
		{
			stateProperty = Bindings.format(itemPattern, item.stateProperty());
			stateProperties.add(stateProperty);
		}

		itemLabel.textProperty().bind(stateProperty);

		restClient.item(itemName, new InvocationCallback<Item>()
		{
			@Override
			public void failed(final Throwable t)
			{
				t.printStackTrace();
			}

			@Override
			public void completed(final Item response)
			{
				try
				{
					BeanCopy.copy(response, item);

					restClient.addItemStateChangeListener(item.getName(), new ChangeListener()
					{
						@Override
						public void stateChanged(final ChangeEvent e)
						{
							restClient.update(item);
						}
					});
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * @return true if no item to display, false otherwise
	 */
	public boolean isEmpty()
	{
		return (item1 == null && item2 == null && item3 == null);
	}

	@Override
	public void reload()
	{
		if (infosPane != null)
		{
			final OpenHabRestClient restClient = mainViewController.getRestClient();

			if (item1 != null)
			{
				restClient.update(item1);
			}
			if (item2 != null)
			{
				restClient.update(item2);
			}
			if (item3 != null)
			{
				restClient.update(item3);
			}
		}
	}

	@Override
	public Region getInfosView()
	{
		return infosPane;
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
