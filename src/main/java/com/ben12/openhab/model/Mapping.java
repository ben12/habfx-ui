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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This is a java bean that is used with JAXB to serialize command mappings
 * to XML or JSON.
 * 
 * @author Kai Kreuzer
 * @since 0.9.0
 */
@XmlRootElement(name = "mapping")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Mapping
{
	private final StringProperty	command	= new SimpleStringProperty();

	private final StringProperty	label	= new SimpleStringProperty();

	public final StringProperty commandProperty()
	{
		return command;
	}

	@XmlElement
	public final String getCommand()
	{
		return commandProperty().get();
	}

	public final void setCommand(final String command)
	{
		commandProperty().set(command);
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
}
