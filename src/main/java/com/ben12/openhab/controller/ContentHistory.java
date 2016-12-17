package com.ben12.openhab.controller;

public class ContentHistory
{
	private final ContentHistory		previous;

	private ContentHistory				next;

	private final ContentController<?>	page;

	public ContentHistory(final ContentHistory previous, final ContentController<?> page)
	{
		this.previous = previous;
		this.page = page;
	}

	public ContentHistory getPrevious()
	{
		return previous;
	}

	public ContentHistory getNext()
	{
		return next;
	}

	public ContentController<?> getPage()
	{
		return page;
	}
}
