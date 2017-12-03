// Copyright (C) 2017 Benoît Moreau (ben.12)
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
package com.ben12.openhab.model.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Persistence
{
    private final StringProperty  name       = new SimpleStringProperty();

    private final IntegerProperty datapoints = new SimpleIntegerProperty();

    private final List<Measure>   data       = new ArrayList<>();

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

    public final IntegerProperty datapointsProperty()
    {
        return datapoints;
    }

    @XmlElement
    public final int getDatapoints()
    {
        return datapointsProperty().get();
    }

    public final void setDatapoints(final int datapoints)
    {
        datapointsProperty().set(datapoints);
    }

    @XmlElement(name = "data")
    public List<Measure> getData()
    {
        return data;
    }

    public void setData(final List<Measure> pData)
    {
        if (data != pData)
        {
            data.clear();
            data.addAll(pData);
        }
    }
}
