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

package com.ben12.openhab.controller.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.ChangeListener;
import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Item;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;
import com.ben12.openhab.model.util.BeanCopy;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;

public class GroupController extends WidgetController
{
    private PageController       pageController;

    private ObservableList<Item> members;

    public GroupController(final Page parent)
    {
        super(parent);
    }

    @Override
    public void init(final Widget widget, final MainViewController pMainViewController)
    {
        super.init(widget, pMainViewController);

        pageController = new PageController();
        pageController.init(getWidget().getLinkedPage(), getMainViewController());

        final ChangeListener memberChangeHandler = e -> reload();

        final OpenHabRestClient restClient = getMainViewController().getRestClient();
        final Map<String, ItemChangeHandler> itemChangeHandlers = new HashMap<>();

        members = FXCollections.observableArrayList();
        members.addListener((ListChangeListener<Item>) c -> {
            while (c.next())
            {
                if (c.wasRemoved())
                {
                    for (final Item item : c.getRemoved())
                    {
                        final ItemChangeHandler handler = itemChangeHandlers.remove(item.getName());
                        if (handler != null)
                        {
                            handler.release();
                        }
                    }
                }
                if (c.wasAdded())
                {
                    for (final Item item : c.getAddedSubList())
                    {
                        itemChangeHandlers.computeIfAbsent(item.getName(), itemName -> new ItemChangeHandler(restClient,
                                itemName, memberChangeHandler));
                    }
                }
            }
        });

        loadMembers();
    }

    public void loadMembers()
    {
        final OpenHabRestClient restClient = getMainViewController().getRestClient();
        restClient.item(getWidget().getItem().getName(), new InvocationCallback<Item>()
        {
            @Override
            public void failed(final Throwable throwable)
            {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Cannot load group members", throwable);
            }

            @Override
            public void completed(final Item response)
            {
                Platform.runLater(() -> BeanCopy.copy(response.getMembers(), members, Item::getName));
            }
        });
    }

    @Override
    public void reload()
    {
        super.reload();
        loadMembers();
    }

    @Override
    protected void display()
    {
        getMainViewController().display(pageController);
    }

    @Override
    public Region getContentView()
    {
        return null;
    }
}
