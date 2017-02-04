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

package com.ben12.openhab.model.util;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.WrapDynaBean;

import javafx.application.Platform;

public class BeanCopy extends WrapDynaBean
{
	private static final long serialVersionUID = 365623528334534437L;

	private BeanCopy(final Object instance)
	{
		super(instance);
	}

	public static <T> void copy(final List<T> source, final List<T> destination, final Function<T, String> idGetter)
	{
		for (int i = 0; i < source.size(); i++)
		{
			final T newWidget = source.get(i);
			T actualWidget = (i < destination.size() ? destination.get(i) : null);
			if (actualWidget != null && Objects.equals(idGetter.apply(newWidget), idGetter.apply(actualWidget)))
			{
				BeanCopy.copy(newWidget, actualWidget);
			}
			else
			{
				boolean found = false;
				for (int j = 0; j < destination.size(); j++)
				{
					actualWidget = destination.get(j);
					if (Objects.equals(idGetter.apply(newWidget), idGetter.apply(actualWidget)))
					{
						BeanCopy.copy(newWidget, actualWidget);
						destination.remove(j);
						destination.add(i, actualWidget);
						found = true;
						break;
					}
				}

				if (!found)
				{
					destination.add(i, newWidget);
				}
			}
		}

		if (destination.size() > source.size())
		{
			destination.subList(source.size(), destination.size()).clear();
		}
	}

	public static void copy(final Object source, final Object destination)
	{
		try
		{
			if (!Platform.isFxApplicationThread())
			{
				final CountDownLatch done = new CountDownLatch(1);
				Platform.runLater(() -> {
					copy(source, destination);
					done.countDown();
				});
				done.await();
				return;
			}
			BeanUtils.copyProperties(wrap(destination), source);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	private static BeanCopy wrap(final Object value)
	{
		return new BeanCopy(value);
	}

	private boolean isCopyable(final Object value)
	{
		boolean copyable = false;
		if (value != null)
		{
			final Class<?> type = value.getClass();
			final XmlElement xmlElement = type.getAnnotation(XmlElement.class);
			copyable = (xmlElement != null);
		}
		return copyable;
	}

	@Override
	public void set(final String name, final Object value)
	{
		try
		{
			final Object target = get(name);
			if (isCopyable(target))
			{
				BeanUtils.copyProperties(wrap(target), value);
			}
			else
			{
				super.set(name, value);
			}
		}
		catch (final InvocationTargetException ite)
		{
			final Throwable cause = ite.getTargetException();
			throw new IllegalArgumentException("Error setting property '" + name + "' nested exception -" + cause);
		}
		catch (final Throwable t)
		{
			throw new IllegalArgumentException("Error setting property '" + name + "', exception - " + t);
		}
	}
}
