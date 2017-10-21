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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ben12.openhab.model.util.BeanCopy;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is a java bean that is used with JAXB to serialize items
 * to XML or JSON.
 * 
 * @author Kai Kreuzer
 * @since 0.8.0
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Item implements Linked
{
	private final StringProperty					type				= new SimpleStringProperty();

	private final StringProperty					name				= new SimpleStringProperty();

	private final StringProperty					state				= new SimpleStringProperty();

	private final ObjectProperty<StateDescription>	stateDescription	= new SimpleObjectProperty<>();

	private final StringProperty					link				= new SimpleStringProperty();

	public final ObservableList<Item>				members				= FXCollections.observableArrayList();

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

	public ObservableList<Item> membersProperty()
	{
		return members;
	}

	@XmlElement
	public List<Item> getMembers()
	{
		return members;
	}

	public void setMembers(final List<Item> pMembers)
	{
		if (members != pMembers)
		{
			BeanCopy.copy(pMembers, members, Item::getName);
		}
	}
}
