package com.ben12.openhab.controller.impl;

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

public class TopItemsController implements ContentController<Void>
{
	private static final String	ITEM1_CFG	= "item1";

	private static final String	ITEM2_CFG	= "item2";

	private static final String	ITEM3_CFG	= "item3";

	private static final String	PATTERN_CFG	= "pattern";

	private MainViewController	mainViewController;

	@FXML
	private Region				infosPane;

	@FXML
	private Label				item1Label;

	@FXML
	private Label				item2Label;

	@FXML
	private Label				item3Label;

	private Item				item1;

	private Item				item2;

	private Item				item3;

	private StringExpression	stateProperty;

	@Override
	public void init(final Void data, final MainViewController mainViewController)
	{
		this.mainViewController = mainViewController;

		final Properties configuration = mainViewController.getConfig();
		final String item1Name = configuration.getProperty(ITEM1_CFG, "");
		final String item1Pattern = configuration.getProperty(ITEM1_CFG + '.' + PATTERN_CFG);
		final String item2Name = configuration.getProperty(ITEM2_CFG, "");
		final String item2Pattern = configuration.getProperty(ITEM2_CFG + '.' + PATTERN_CFG);
		final String item3Name = configuration.getProperty(ITEM3_CFG, "");
		final String item3Pattern = configuration.getProperty(ITEM3_CFG + '.' + PATTERN_CFG);

		if (!item1Name.isEmpty())
		{
			item1 = new Item();
			createItem(item1Name, item1Pattern, item1, item1Label);
		}
		if (!item2Name.isEmpty())
		{
			item2 = new Item();
			createItem(item2Name, item2Pattern, item2, item2Label);
		}
		if (!item3Name.isEmpty())
		{
			item3 = new Item();
			createItem(item3Name, item3Pattern, item3, item3Label);
		}

		if (isEmpty())
		{
			infosPane = null;
		}
	}

	private void createItem(final String itemName, final String itemPattern, final Item item, final Label itemLabel)
	{
		final OpenHabRestClient restClient = mainViewController.getRestClient();

		stateProperty = item.stateProperty();
		if (itemPattern != null && !itemPattern.isEmpty())
		{
			stateProperty = Bindings.format(itemPattern, item.stateProperty());
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
