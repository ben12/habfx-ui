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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ben12.openhab.model.util.BeanCopy;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is a java bean that is used with JAXB to serialize widgets
 * to XML or JSON.
 * 
 * @author Kai Kreuzer
 * @author Chris Jackson
 * @since 0.8.0
 */
@XmlRootElement(name = "widget")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Widget
{
	private final StringProperty				widgetId		= new SimpleStringProperty();

	private final StringProperty				type			= new SimpleStringProperty();

	private final StringProperty				name			= new SimpleStringProperty();

	private final StringProperty				label			= new SimpleStringProperty();

	private final StringProperty				icon			= new SimpleStringProperty();

	private final StringProperty				labelcolor		= new SimpleStringProperty();

	private final StringProperty				valuecolor		= new SimpleStringProperty();

	private final ObservableList<Mapping>		mappings		= FXCollections.observableArrayList();

	private final BooleanProperty				switchSupport	= new SimpleBooleanProperty();

	private final IntegerProperty				sendFrequency	= new SimpleIntegerProperty();

	private final StringProperty				separator		= new SimpleStringProperty();

	private final IntegerProperty				refresh			= new SimpleIntegerProperty();

	private final IntegerProperty				height			= new SimpleIntegerProperty();

	private final ObjectProperty<BigDecimal>	minValue		= new SimpleObjectProperty<>();

	private final ObjectProperty<BigDecimal>	maxValue		= new SimpleObjectProperty<>();

	private final ObjectProperty<BigDecimal>	step			= new SimpleObjectProperty<>();

	private final StringProperty				url				= new SimpleStringProperty();

	private final StringProperty				encoding		= new SimpleStringProperty();

	private final StringProperty				service			= new SimpleStringProperty();

	private final StringProperty				period			= new SimpleStringProperty();

	private final ObjectProperty<Item>			item			= new SimpleObjectProperty<>();

	private final ObjectProperty<Page>			linkedPage		= new SimpleObjectProperty<>();

	private final ObservableList<Widget>		widgets			= FXCollections.observableArrayList();

	public final StringProperty widgetIdProperty()
	{
		return widgetId;
	}

	@XmlElement
	public final String getWidgetId()
	{
		return widgetIdProperty().get();
	}

	public final void setWidgetId(final String widgetId)
	{
		widgetIdProperty().set(widgetId);
	}

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

	public final StringProperty iconProperty()
	{
		return icon;
	}

	@XmlElement
	public final String getIcon()
	{
		return iconProperty().get();
	}

	public final void setIcon(final String icon)
	{
		iconProperty().set(icon);
	}

	public final StringProperty labelcolorProperty()
	{
		return labelcolor;
	}

	@XmlElement
	public final String getLabelcolor()
	{
		return labelcolorProperty().get();
	}

	public final void setLabelcolor(final String labelcolor)
	{
		labelcolorProperty().set(labelcolor);
	}

	public final StringProperty valuecolorProperty()
	{
		return valuecolor;
	}

	@XmlElement
	public final String getValuecolor()
	{
		return valuecolorProperty().get();
	}

	public final void setValuecolor(final String valuecolor)
	{
		valuecolorProperty().set(valuecolor);
	}

	public final BooleanProperty switchSupportProperty()
	{
		return switchSupport;
	}

	@XmlElement
	public final boolean isSwitchSupport()
	{
		return switchSupportProperty().get();
	}

	public final void setSwitchSupport(final boolean switchSupport)
	{
		switchSupportProperty().set(switchSupport);
	}

	public final IntegerProperty sendFrequencyProperty()
	{
		return sendFrequency;
	}

	@XmlElement
	public final int getSendFrequency()
	{
		return sendFrequencyProperty().get();
	}

	public final void setSendFrequency(final int sendFrequency)
	{
		sendFrequencyProperty().set(sendFrequency);
	}

	public final StringProperty separatorProperty()
	{
		return separator;
	}

	@XmlElement
	public final String getSeparator()
	{
		return separatorProperty().get();
	}

	public final void setSeparator(final String separator)
	{
		separatorProperty().set(separator);
	}

	public final IntegerProperty refreshProperty()
	{
		return refresh;
	}

	@XmlElement
	public final int getRefresh()
	{
		return refreshProperty().get();
	}

	public final void setRefresh(final int refresh)
	{
		refreshProperty().set(refresh);
	}

	public final IntegerProperty heightProperty()
	{
		return height;
	}

	@XmlElement
	public final int getHeight()
	{
		return heightProperty().get();
	}

	public final void setHeight(final int height)
	{
		heightProperty().set(height);
	}

	public final ObjectProperty<BigDecimal> minValueProperty()
	{
		return minValue;
	}

	@XmlElement
	public final BigDecimal getMinValue()
	{
		return minValueProperty().get();
	}

	public final void setMinValue(final BigDecimal minValue)
	{
		minValueProperty().set(minValue);
	}

	public final ObjectProperty<BigDecimal> maxValueProperty()
	{
		return maxValue;
	}

	@XmlElement
	public final BigDecimal getMaxValue()
	{
		return maxValueProperty().get();
	}

	public final void setMaxValue(final BigDecimal maxValue)
	{
		maxValueProperty().set(maxValue);
	}

	public final ObjectProperty<BigDecimal> stepProperty()
	{
		return step;
	}

	@XmlElement
	public final BigDecimal getStep()
	{
		return stepProperty().get();
	}

	public final void setStep(final BigDecimal step)
	{
		stepProperty().set(step);
	}

	public final StringProperty urlProperty()
	{
		return url;
	}

	@XmlElement
	public final String getUrl()
	{
		return urlProperty().get();
	}

	public final void setUrl(final String url)
	{
		urlProperty().set(url);
	}

	public final StringProperty encodingProperty()
	{
		return encoding;
	}

	@XmlElement
	public final String getEncoding()
	{
		return encodingProperty().get();
	}

	public final void setEncoding(final String encoding)
	{
		encodingProperty().set(encoding);
	}

	public final StringProperty serviceProperty()
	{
		return service;
	}

	@XmlElement
	public final String getService()
	{
		return serviceProperty().get();
	}

	public final void setService(final String service)
	{
		serviceProperty().set(service);
	}

	public final StringProperty periodProperty()
	{
		return period;
	}

	@XmlElement
	public final String getPeriod()
	{
		return periodProperty().get();
	}

	public final void setPeriod(final String period)
	{
		periodProperty().set(period);
	}

	public final ObjectProperty<Item> itemProperty()
	{
		return item;
	}

	@XmlElement
	public final Item getItem()
	{
		return itemProperty().get();
	}

	public final void setItem(final Item item)
	{
		itemProperty().set(item);
	}

	public final ObjectProperty<Page> linkedPageProperty()
	{
		return linkedPage;
	}

	@XmlElement
	public final Page getLinkedPage()
	{
		return linkedPageProperty().get();
	}

	public final void setLinkedPage(final Page linkedPage)
	{
		linkedPageProperty().set(linkedPage);
	}

	public ObservableList<Mapping> mappingsProperty()
	{
		return mappings;
	}

	@XmlElement(name = "mappings")
	public List<Mapping> getMappings()
	{
		return mappings;
	}

	public void setMappings(final List<Mapping> pMappings)
	{
		if (mappings != pMappings)
		{
			BeanCopy.copy(pMappings, mappings, Mapping::getCommand);
		}
	}

	public ObservableList<Widget> widgetsProperty()
	{
		return widgets;
	}

	@XmlElement(name = "widgets")
	public List<Widget> getWidgets()
	{
		return widgets;
	}

	public Widget getWidgets(final String id)
	{
		Widget widget = null;
		for (final Widget w : widgets)
		{
			if (Objects.equals(w.getWidgetId(), id))
			{
				widget = w;
				break;
			}
		}
		return widget;
	}

	public void setWidgets(final List<Widget> pWidgets)
	{
		if (widgets != pWidgets)
		{
			BeanCopy.copy(pWidgets, widgets, Widget::getWidgetId);
		}
	}
}
