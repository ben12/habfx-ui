package com.ben12.openhab.model.util;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

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
