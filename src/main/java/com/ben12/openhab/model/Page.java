/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ben12.openhab.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is a java bean that is used with JAXB to serialize page content
 * to XML or JSON.
 */
@XmlRootElement(name = "page")
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Page implements Linked
{
	private final StringProperty			id		= new SimpleStringProperty();

	private final StringProperty			title	= new SimpleStringProperty();

	private final StringProperty			icon	= new SimpleStringProperty();

	private final StringProperty			link	= new SimpleStringProperty();

	private final BooleanProperty			leaf	= new SimpleBooleanProperty();

	private final ObservableList<Widget>	widgets	= FXCollections.observableArrayList();

	public final StringProperty idProperty()
	{
		return id;
	}

	@XmlElement
	public final String getId()
	{
		return idProperty().get();
	}

	public final void setId(final String id)
	{
		idProperty().set(id);
	}

	public final StringProperty titleProperty()
	{
		return title;
	}

	@XmlElement
	public final String getTitle()
	{
		return titleProperty().get();
	}

	public final void setTitle(final String title)
	{
		titleProperty().set(title);
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

	public final BooleanProperty leafProperty()
	{
		return leaf;
	}

	@XmlElement
	public final boolean isLeaf()
	{
		return leafProperty().get();
	}

	public final void setLeaf(final boolean leaf)
	{
		leafProperty().set(leaf);
	}

	public ObservableList<Widget> widgetsProperty()
	{
		return widgets;
	}

	@XmlElement(name = "widgets")
	public List<Widget> getWidgets()
	{
		return widgets;
	}

	public void setWidgets(final List<Widget> widgets)
	{
		if (this.widgets != widgets)
		{
			this.widgets.clear();
			this.widgets.addAll(widgets);
		}
	}
}
