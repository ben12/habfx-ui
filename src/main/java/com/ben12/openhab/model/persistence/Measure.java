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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Benoît Moreau (ben.12)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Measure
{
	private long	time;

	private String	state;

	@XmlElement
	public long getTime()
	{
		return time;
	}

	public void setTime(final long time)
	{
		this.time = time;
	}

	@XmlElement
	public String getState()
	{
		return state;
	}

	public void setState(final String state)
	{
		this.state = state;
	}
}
