package com.ben12.openhab.rest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import com.sun.jersey.api.json.JSONJAXBContext;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JSONJaxbProvider<T> implements MessageBodyReader<T>
{
	private final Map<Class<?>, JSONJAXBContext> contexts;

	public JSONJaxbProvider()
	{
		contexts = new HashMap<>();
	}

	public JSONJAXBContext getContexts(final Class<?> type) throws JAXBException
	{
		JSONJAXBContext context = contexts.get(type);
		if (context == null)
		{
			context = new JSONJAXBContext(type);
			contexts.put(type, context);
		}
		return context;
	}

	@Override
	public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType)
	{
		try
		{
			getContexts(type);
		}
		catch (final JAXBException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public T readFrom(final Class<T> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
			throws IOException, WebApplicationException
	{
		try
		{
			return getContexts(type).createJSONUnmarshaller().unmarshalFromJSON(entityStream, type);
		}
		catch (final JAXBException e)
		{
			throw new WebApplicationException(e);
		}
	}
}
