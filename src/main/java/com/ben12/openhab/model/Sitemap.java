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
