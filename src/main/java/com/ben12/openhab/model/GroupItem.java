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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is a java bean that is used with JAXB to serialize group items
 * to XML or JSON.
 * 
 * @author Kai Kreuzer
 * @since 0.8.0
 */
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class GroupItem extends Item
{
	public final ObservableList<Item> members = FXCollections.observableArrayList();

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
