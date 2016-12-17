/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ben12.openhab.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This is a java bean that is used with JAXB to serialize sitemaps
 * to XML or JSON.
 * 
 * @author Kai Kreuzer
 * @author Chris Jackson
 * @since 0.8.0
 */
@XmlRootElement(name = "sitemap")
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sitemap implements Linked
{
	private final StringProperty		name		= new SimpleStringProperty();

	private final StringProperty		icon		= new SimpleStringProperty();

	private final StringProperty		label		= new SimpleStringProperty();

	private final StringProperty		link		= new SimpleStringProperty();

	private final ObjectProperty<Page>	homepage	= new SimpleObjectProperty<>();

	public final StringProperty nameProperty()
	{
		return name;
	}

	@XmlElement
	public final String getName()
	{
		return nameProperty().get();
	}

	public final void setName(final String name)
	{
		nameProperty().set(name);
	}

	public final StringProperty iconProperty()
	{
		return icon;
	}

	@XmlElement
	public final String getIcon()
	{
		return iconProperty().get();
	}

	public final void setIcon(final String icon)
	{
		iconProperty().set(icon);
	}

	public final StringProperty labelProperty()
	{
		return label;
	}

	@XmlElement
	public final String getLabel()
	{
		return labelProperty().get();
	}

	public final void setLabel(final String label)
	{
		labelProperty().set(label);
	}

	public final StringProperty linkProperty()
	{
		return link;
	}

	@Override
	@XmlElement
	public final String getLink()
	{
		return linkProperty().get();
	}

	@Override
	public final void setLink(final String link)
	{
		linkProperty().set(link);
	}

	public final ObjectProperty<Page> homepageProperty()
	{
		return homepage;
	}

	@XmlElement
	public final Page getHomepage()
	{
		return homepageProperty().get();
	}

	public final void setHomepage(final Page homepage)
	{
		homepageProperty().set(homepage);
	}
}
