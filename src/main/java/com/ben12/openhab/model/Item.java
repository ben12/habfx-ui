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
 * This is a java bean that is used with JAXB to serialize items
 * to XML or JSON.
 * 
 * @author Kai Kreuzer
 * @since 0.8.0
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Linked
{
	private final StringProperty					type				= new SimpleStringProperty();

	private final StringProperty					name				= new SimpleStringProperty();

	private final StringProperty					state				= new SimpleStringProperty();

	private final ObjectProperty<StateDescription>	stateDescription	= new SimpleObjectProperty<>();

	private final StringProperty					link				= new SimpleStringProperty();

	public final StringProperty typeProperty()
	{
		return type;
	}

	@XmlElement
	public final String getType()
	{
		return typeProperty().get();
	}

	public final void setType(final String type)
	{
		typeProperty().set(type);
	}

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

	public final StringProperty stateProperty()
	{
		return state;
	}

	@XmlElement
	public final String getState()
	{
		return stateProperty().get();
	}

	public final void setState(final String state)
	{
		stateProperty().set(state);
	}

	public final ObjectProperty<StateDescription> stateDescriptionProperty()
	{
		return stateDescription;
	}

	@XmlElement
	public final StateDescription getStateDescription()
	{
		return stateDescriptionProperty().get();
	}

	public final void setStateDescription(final StateDescription stateDescription)
	{
		stateDescriptionProperty().set(stateDescription);
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
}
