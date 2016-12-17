package com.ben12.openhab.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateOption
{
	private final StringProperty	value	= new SimpleStringProperty();

	private final StringProperty	label	= new SimpleStringProperty();

	public final StringProperty valueProperty()
	{
		return value;
	}

	@XmlElement
	public final String getValue()
	{
		return valueProperty().get();
	}

	public final void setValue(final String value)
	{
		valueProperty().set(value);
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
