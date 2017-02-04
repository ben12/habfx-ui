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

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.HabApplication;
import com.ben12.openhab.controller.impl.PageController;
import com.ben12.openhab.controller.impl.TopItemsController;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class MainController implements Initializable, MainViewController
{
	private static final String	CONFIG_FILE				= "config.properties";

	private static final String	OPENHAB_URL_CFG			= "openhab.url";

	private static final String	OPENHAB_USER_CFG		= "openhab.user";

	private static final String	OPENHAB_PASSWORD_CFG	= "openhab.password";

	private static final String	SITEMAP_CFG				= "sitemap";

	private final Properties	configuration			= new Properties();

	private Page				homepage;

	private OpenHabRestClient	openhabClient;

	private ContentHistory		currentPage;

	@FXML
	private BorderPane			infosPane;

	@FXML
	private ScrollPane			contentPane;

	private TopItemsController	topItemsController;

	@Override
	public void initialize(final URL location, final ResourceBundle resources)
	{
		String config = System.getProperty("config.file");
		if (config == null || config.isEmpty())
		{
			config = CONFIG_FILE;
		}

		if (Files.exists(Paths.get(config)))
		{
			try (FileReader reader = new FileReader(config))
			{
				configuration.load(reader);
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}

		final String uriCfg = configuration.getProperty(OPENHAB_URL_CFG);
		try
		{
			final URI uri = new URI(uriCfg);
			final String user = configuration.getProperty(OPENHAB_USER_CFG);
			final String password = configuration.getProperty(OPENHAB_PASSWORD_CFG);

			openhabClient = new OpenHabRestClient(uri, user, password);

			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(HabApplication.class.getResource("ui/TopItems.fxml"));
			loader.load();
			topItemsController = loader.getController();
			topItemsController.init(null, this);

			final String sitemap = configuration.getProperty(SITEMAP_CFG, "default");

			final InvocationCallback<Page> homepageCallback = new InvocationCallback<Page>()
			{
				@Override
				public void failed(final Throwable t)
				{
					t.printStackTrace();
				}

				@Override
				public void completed(final Page page)
				{
					homepage = page;
					Platform.runLater(() -> {
						final PageController homepageController = new PageController();
						homepageController.init(homepage, MainController.this);

						display(homepageController);
					});
				}
			};

			openhabClient.homepage(sitemap, homepageCallback);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public Region getDefaultInfosView()
	{
		return topItemsController.getInfosView();
	}

	public void showDefaultInfos()
	{
		topItemsController.reload();
		setInfos(topItemsController.getInfosView());
	}

	@Override
	public void display(final ContentController<?> contentController)
	{
		final ContentHistory previous = currentPage;
		currentPage = new ContentHistory(currentPage, contentController);
		if (previous != null)
		{
			previous.setNext(currentPage);
		}
		displayCurrentPage();
	}

	public void displayCurrentPage()
	{
		final ContentController<?> contentController = currentPage.getPage();
		if (currentPage.getPrevious() == null)
		{
			showDefaultInfos();
		}
		else
		{
			setInfos(contentController.getInfosView());
		}
		setContent(contentController.getContentView());
		contentController.reload();
	}

	private void setInfos(final Region content)
	{
		infosPane.setCenter(content);
	}

	private void setContent(final Region content)
	{
		content.minWidthProperty().bind(contentPane.widthProperty()
				.subtract(contentPane.getInsets().getLeft() + contentPane.getInsets().getRight()));
		content.maxWidthProperty().bind(contentPane.widthProperty()
				.subtract(contentPane.getInsets().getLeft() + contentPane.getInsets().getRight()));
		contentPane.setContent(content);
	}

	@Override
	public OpenHabRestClient getRestClient()
	{
		return openhabClient;
	}

	@FXML
	protected void goHomepage(final ActionEvent event)
	{
		ContentHistory prev = currentPage.getPrevious();
		while (prev != null)
		{
			currentPage = prev;
			prev = currentPage.getPrevious();
		}
		displayCurrentPage();
	}

	@FXML
	protected void goPrev(final ActionEvent event)
	{
		final ContentHistory prev = currentPage.getPrevious();
		if (prev != null)
		{
			currentPage = prev;
		}
		displayCurrentPage();
	}

	@FXML
	protected void goNext(final ActionEvent event)
	{
		final ContentHistory next = currentPage.getNext();
		if (next != null)
		{
			currentPage = next;
		}
		displayCurrentPage();
	}

	@Override
	public Properties getConfig()
	{
		return configuration;
	}

	@Override
	public Page getHomepage()
	{
		return homepage;
	}
}
