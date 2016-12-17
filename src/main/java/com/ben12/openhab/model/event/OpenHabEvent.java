package com.ben12.openhab.model.event;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.sun.xml.txw2.annotation.XmlElement;

@XmlElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenHabEvent
{
	public String	topic;

	public String	type;
}
