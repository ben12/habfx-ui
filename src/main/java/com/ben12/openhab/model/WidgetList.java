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
 * This is a java bean that is used with JAXB to serialize a list of widgets
 * to XML or JSON.
 * 
 * @author Oliver Mazur
 * @since 1.0.0
 */

@XmlRootElement(name = "widgets")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WidgetList
{
	private final ObservableList<Widget> entries = FXCollections.observableArrayList();

	public ObservableList<Widget> entriesProperty()
	{
		return entries;
	}

	@XmlElement(name = "widget")
	public List<Widget> getEntries()
	{
		return entries;
	}

	public void setEntries(final List<Widget> entries)
	{
		if (this.entries != entries)
		{
			this.entries.clear();
			this.entries.addAll(entries);
		}
	}
}
