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
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupItem extends Item
{
	public ObservableList<Item> members = FXCollections.observableArrayList();

	public ObservableList<Item> membersProperty()
	{
		return members;
	}

	@XmlElement
	public List<Item> getMembers()
	{
		return members;
	}

	public void setMembers(final List<Item> members)
	{
		if (this.members != members)
		{
			this.members.clear();
			this.members.addAll(members);
		}
	}
}
