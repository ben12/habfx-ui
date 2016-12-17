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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is a java bean that is used with JAXB to serialize item lists.
 * 
 * @author Kai Kreuzer
 * @since 0.9.0
 */
@XmlRootElement(name = "items")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ItemList
{
	private final ObservableList<Item> entries = FXCollections.observableArrayList();

	public ObservableList<Item> entriesProperty()
	{
		return entries;
	}

	@XmlElement(name = "item")
	public List<Item> getEntries()
	{
		return entries;
	}

	public void setEntries(final List<Item> entries)
	{
		if (this.entries != entries)
		{
			this.entries.clear();
			this.entries.addAll(entries);
		}
	}
}
