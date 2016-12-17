package com.ben12.openhab.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateDescription
{
	private final DoubleProperty				minimum		= new SimpleDoubleProperty();

	private final DoubleProperty				maximum		= new SimpleDoubleProperty();

	private final DoubleProperty				step		= new SimpleDoubleProperty();

	private final BooleanProperty				readOnly	= new SimpleBooleanProperty();

	private final StringProperty				pattern		= new SimpleStringProperty();

	private final ObservableList<StateOption>	options		= FXCollections.observableArrayList();

	public final DoubleProperty minimumProperty()
	{
		return minimum;
	}

	@XmlElement
	public final double getMinimum()
	{
		return minimumProperty().get();
	}

	public final void setMinimum(final double minimum)
	{
		minimumProperty().set(minimum);
	}

	public final DoubleProperty maximumProperty()
	{
		return maximum;
	}

	@XmlElement
	public final double getMaximum()
	{
		return maximumProperty().get();
	}

	public final void setMaximum(final double maximum)
	{
		maximumProperty().set(maximum);
	}

	public final DoubleProperty stepProperty()
	{
		return step;
	}

	@XmlElement
	public final double getStep()
	{
		return stepProperty().get();
	}

	public final void setStep(final double step)
	{
		stepProperty().set(step);
	}

	public final BooleanProperty readOnlyProperty()
	{
		return readOnly;
	}

	@XmlElement
	public final boolean isReadOnly()
	{
		return readOnlyProperty().get();
	}

	public final void setReadOnly(final boolean readOnly)
	{
		readOnlyProperty().set(readOnly);
	}

	public final StringProperty patternProperty()
	{
		return pattern;
	}

	@XmlElement
	public final String getPattern()
	{
		return patternProperty().get();
	}

	public final void setPattern(final String pattern)
	{
		patternProperty().set(pattern);
	}

	public ObservableList<StateOption> optionsProperty()
	{
		return options;
	}

	@XmlElement
	public List<StateOption> getOptions()
	{
		return options;
	}

	public void setOptions(final List<StateOption> options)
	{
		if (this.options != options)
		{
			this.options.clear();
			this.options.addAll(options);
		}
	}
}
