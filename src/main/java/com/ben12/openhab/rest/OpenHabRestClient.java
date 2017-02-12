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

package com.ben12.openhab.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import com.ben12.openhab.model.Item;
import com.ben12.openhab.model.Linked;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Sitemap;
import com.ben12.openhab.model.Widget;
import com.ben12.openhab.model.event.OpenHabEvent;
import com.ben12.openhab.model.util.BeanCopy;

import javafx.scene.image.Image;

public class OpenHabRestClient
{
	private static final String						REST				= "rest";

	private static final String						SITEMAPS			= "sitemaps";

	private static final String						ITEMS				= "items";

	private static final String						IMAGES				= "icon";

	private static final String						EVENT_KEY			= "smarthome/items/%s/state";

	private static final String						ITEM_STATE_EVENT	= "ItemStateEvent";

	private final URI								uri;

	private final Client							client;

	private final EventSource						eventSource;

	private final Map<String, List<ChangeListener>>	listeners;

	public OpenHabRestClient(final URI pUri, final String username, final String password)
	{
		listeners = Collections.synchronizedMap(new HashMap<>());
		uri = pUri;

		final JacksonJaxbJsonProvider xmlJaxbProvider = new JacksonJaxbJsonProvider();
		final ImageProvider imageProvider = new ImageProvider();
		final SseFeature sseFeature = new SseFeature();

		client = ClientBuilder.newClient() //
				.register(xmlJaxbProvider)
				.register(imageProvider)
				.register(sseFeature);

		if (username != null && !username.isEmpty())
		{
			final HttpAuthenticationFeature authentication = HttpAuthenticationFeature.universalBuilder() //
					.credentials(username, password)
					.build();
			client.register(authentication);
		}

		final WebTarget eventsTarget = client.target(uri) //
				.path(REST)
				.path("events");
		eventSource = EventSource.target(eventsTarget).build();
		eventSource.register(new EventHandler());

		final Thread async = new Thread(() -> eventSource.open(), "Open SSE");
		async.start();
	}

	public void sitemaps(final InvocationCallback<Sitemap[]> callback)
	{
		final WebTarget sitemapsTarget = client.target(uri) //
				.path(REST)
				.path(SITEMAPS);
		sitemapsTarget.request(MediaType.APPLICATION_JSON) //
				.buildGet()
				.submit(callback);
	}

	public void item(final String itemName, final InvocationCallback<Item> callback)
	{
		final WebTarget sitemapsTarget = client.target(uri) //
				.path(REST)
				.path(ITEMS)
				.path(itemName);
		sitemapsTarget.request(MediaType.APPLICATION_JSON) //
				.buildGet()
				.submit(callback);
	}

	public void homepage(final String sitemapName, final InvocationCallback<Page> callback)
	{
		final InvocationCallback<Sitemap[]> sitemapListCallback = new InvocationCallback<Sitemap[]>()
		{
			@Override
			public void failed(final Throwable throwable)
			{
				callback.failed(throwable);
			}

			@Override
			public void completed(final Sitemap[] sitemapList)
			{
				if (sitemapList != null)
				{
					Page homepage = null;

					for (final Sitemap sitemapBean : sitemapList)
					{
						if (Objects.equals(sitemapBean.getName(), sitemapName))
						{
							homepage = sitemapBean.getHomepage();
							break;
						}
					}

					if (homepage != null)
					{
						update(homepage, callback);
					}
					else
					{
						callback.failed(new IllegalArgumentException("Sitemap not found."));
					}
				}
			}
		};

		sitemaps(sitemapListCallback);
	}

	public <T extends Linked> void update(final T data)
	{
		final String link = data.getLink();
		if (link != null && !link.isEmpty())
		{
			final GenericType<T> genericType = new GenericType<>(data.getClass());
			final WebTarget dataTarget = client.target(data.getLink());
			final Future<T> future = dataTarget.request(MediaType.APPLICATION_JSON) //
					.buildGet()
					.submit(genericType);

			new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						final T response = future.get();
						if (response != null)
						{
							BeanCopy.copy(response, data);
						}
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	public <T extends Linked> void update(final T data, final InvocationCallback<T> callback)
	{
		final WebTarget dataTarget = client.target(data.getLink());
		dataTarget.request(MediaType.APPLICATION_JSON) //
				.buildGet()
				.submit(callback);
	}

	public void getImage(final Widget widget, final InvocationCallback<Image> callback)
	{
		final String icon = widget.getIcon();
		if (icon != null && !icon.isEmpty())
		{
			WebTarget imgTarget = client.target(uri) //
					.path(IMAGES)
					.path(icon)
					.queryParam("format", "PNG");
			final Item item = widget.getItem();
			if (item != null)
			{
				imgTarget = imgTarget.queryParam("state", item.getState());
			}
			imgTarget.request("image/*") //
					.buildGet()
					.submit(callback);
		}
		else
		{
			callback.failed(new NullPointerException("No image"));
		}
	}

	public void submit(final Item item, final String state)
	{
		final WebTarget target = client.target(uri) //
				.path(REST)
				.path(ITEMS)
				.path(item.getName());
		target.request() //
				.accept(MediaType.WILDCARD)
				.buildPost(Entity.entity(state, MediaType.TEXT_PLAIN))
				.submit(new InvocationCallback<Response>()
				{
					@Override
					public void failed(final Throwable t)
					{
						t.printStackTrace();
					}

					@Override
					public void completed(final Response response)
					{
						if (response.getStatusInfo().getFamily() != Status.Family.SUCCESSFUL)
						{
							System.err.println(response.getStatusInfo().getStatusCode() + ": "
									+ response.getStatusInfo().getReasonPhrase());
						}
					}
				});
	}

	public void submitPut(final Item item, final String state)
	{
		final WebTarget target = client.target(uri) //
				.path(REST)
				.path(ITEMS)
				.path(item.getName());
		target.request() //
				.accept(MediaType.WILDCARD)
				.buildPut(Entity.entity(state, MediaType.TEXT_PLAIN))
				.submit(new InvocationCallback<Response>()
				{
					@Override
					public void failed(final Throwable t)
					{
						t.printStackTrace();
					}

					@Override
					public void completed(final Response response)
					{
						if (response.getStatusInfo().getFamily() != Status.Family.SUCCESSFUL)
						{
							System.err.println(response.getStatusInfo().getStatusCode() + ": "
									+ response.getStatusInfo().getReasonPhrase());
						}
					}
				});
	}

	public void addItemStateChangeListener(final String itemName, final ChangeListener l)
	{
		final String key = String.format(EVENT_KEY, itemName);
		List<ChangeListener> changeListeners = listeners.get(key);
		if (changeListeners == null)
		{
			changeListeners = new ArrayList<>();
			listeners.put(key, changeListeners);
		}
		changeListeners.add(l);
	}

	public void removeItemStateChangeListener(final String itemName, final ChangeListener l)
	{
		final String key = String.format(EVENT_KEY, itemName);
		final List<ChangeListener> changeListeners = listeners.get(key);
		if (changeListeners != null)
		{
			changeListeners.remove(l);
			if (changeListeners.isEmpty())
			{
				listeners.remove(key);
			}
		}
	}

	private class EventHandler implements EventListener
	{
		@Override
		public void onEvent(final InboundEvent inboundEvent)
		{
			final OpenHabEvent event = inboundEvent.readData(OpenHabEvent.class);
			if (ITEM_STATE_EVENT.equals(event.type))
			{
				// System.out.println(event.type + " on " + event.topic);
				final List<ChangeListener> changeListeners = listeners.get(event.topic);
				if (changeListeners != null)
				{
					final ChangeEvent e = new ChangeEvent(OpenHabRestClient.this);
					for (final ChangeListener l : changeListeners)
					{
						l.stateChanged(e);
					}
				}
			}
		}
	}
}
