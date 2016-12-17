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
@JsonIgnoreProperties(ignoreUnknown = true)
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
