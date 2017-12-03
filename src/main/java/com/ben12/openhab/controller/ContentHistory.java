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

package com.ben12.openhab.controller;

public class ContentHistory
{
    private final ContentHistory  previous;

    private ContentHistory        next;

    private final ContentAccessor page;

    public ContentHistory(final ContentHistory previous, final ContentAccessor page)
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

    public void setNext(final ContentHistory next)
    {
        this.next = next;
    }

    public ContentAccessor getPage()
    {
        return page;
    }
}
