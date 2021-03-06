// Copyright (C) 2016 Beno�t Moreau (ben.12)
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

import java.util.Objects;

import org.controlsfx.control.ToggleSwitch;

import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Mapping;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SwitchController extends WidgetController
{
    private VBox    content;

    private boolean isOnOff;

    private boolean isButton;

    public SwitchController(final Page parent)
    {
        super(parent);
    }

    @Override
    public void init(final Widget pWidget, final MainViewController pMainViewController)
    {
        final boolean isRollershutter = "Rollershutter".equals(pWidget.getItem().getType());
        isOnOff = !isRollershutter && pWidget.getMappings().isEmpty();
        isButton = !isRollershutter && pWidget.getMappings().size() == 1;

        super.init(pWidget, pMainViewController);

        if (isRollershutter)
        {
            initRollershutter();
        }
        else if (!isOnOff && !isButton)
        {
            initSwitchWithMapping(pWidget.mappingsProperty());
        }
    }

    @Override
    protected Node createValueNode()
    {
        if (isOnOff)
        {
            final ToggleSwitch stateButton = new ToggleSwitch();
            stateButton.getStyleClass().add("value-label");
            stateButton.textProperty().bind(Bindings.createStringBinding(() -> getWidget().getMappings()
                                                                                          .stream()
                                                                                          .filter(m -> Objects.equals(itemStateProperty().get(),
                                                                                                                      m.getCommand()))
                                                                                          .map(Mapping::getLabel)
                                                                                          .findFirst()
                                                                                          .orElse(itemStateProperty().get()),
                                                                         itemStateProperty()));
            stateButton.selectedProperty().bind(itemStateProperty().isEqualToIgnoreCase("ON"));
            stateButton.setMinSize(0, 0);
            stateButton.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            stateButton.setMouseTransparent(true);

            return stateButton;
        }
        else if (isButton)
        {
            final Label label = new Label();
            label.textProperty()
                 .bind(Bindings.concat(getWidget().getMappings().get(0).getLabel(),
                                       Bindings.when(valueProperty().isEmpty()).then("").otherwise(" "),
                                       valueProperty()));
            label.styleProperty().bind(valueStyleProperty());
            label.getStyleClass().add("value-label");
            return label;
        }
        else
        {
            return super.createValueNode();
        }
    }

    @Override
    protected void display()
    {
        if (isOnOff)
        {
            getMainViewController() //
                                   .getRestClient()
                                   .submit( //
                                           getWidget().getItem(),
                                           "ON".equalsIgnoreCase(getWidget().getItem().getState()) ? "OFF" : "ON");
        }
        else if (isButton)
        {
            getMainViewController() //
                                   .getRestClient().submit( //
                                                           getWidget().getItem(), //
                                                           getWidget().getMappings().get(0).getCommand());
        }
        else
        {
            super.display();
        }
    }

    private void initRollershutter()
    {
        final Node iconImage = createIconNode();

        final Button upCommand = new Button(
                getWidget().getMappings()
                           .stream()
                           .filter(m -> "UP".equals(m.getCommand()))
                           .map(Mapping::getLabel)
                           .findFirst()
                           .orElse("UP"));
        upCommand.setOnAction(e -> getMainViewController().getRestClient() //
                                                          .submit(getWidget().getItem(), "UP"));
        upCommand.setMinSize(50, 40);
        upCommand.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        final Button stopCommand = new Button(getWidget().getMappings()
                                                         .stream()
                                                         .filter(m -> "STOP".equals(m.getCommand()))
                                                         .map(Mapping::getLabel)
                                                         .findFirst()
                                                         .orElse("STOP"));
        stopCommand.setOnAction(e -> getMainViewController().getRestClient() //
                                                            .submit(getWidget().getItem(), "STOP"));
        stopCommand.setMinSize(50, 40);
        stopCommand.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        final Button downCommand = new Button(getWidget().getMappings()
                                                         .stream()
                                                         .filter(m -> "DOWN".equals(m.getCommand()))
                                                         .map(Mapping::getLabel)
                                                         .findFirst()
                                                         .orElse("DOWN"));
        downCommand.setOnAction(e -> getMainViewController().getRestClient() //
                                                            .submit(getWidget().getItem(), "DOWN"));
        downCommand.setMinSize(50, 40);
        downCommand.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        final VBox commands = new VBox(2, upCommand, stopCommand, downCommand);
        commands.setFillWidth(true);

        content = new VBox(iconImage, commands);
        content.setFillWidth(false);
        content.setAlignment(Pos.CENTER);
        content.setMinSize(50, 50);
        content.setMaxSize(Double.MAX_VALUE, Region.USE_PREF_SIZE);
        content.prefWidth(0);
        content.setPadding(new Insets(0, 5, 0, 5));
    }

    /**
     * @param mappings
     */
    private void initSwitchWithMapping(final ObservableList<Mapping> mappings)
    {
        final Node iconImage = createIconNode();

        final ListView<Mapping> listView = new ListView<>(mappings);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setCellFactory(l -> new MappingListCell());
        listView.setMinSize(0, Region.USE_PREF_SIZE);
        listView.setMaxSize(Double.MAX_VALUE, Region.USE_PREF_SIZE);
        listView.setFixedCellSize(25);
        listView.prefHeightProperty()
                .bind(Bindings.size(mappings)
                              .multiply(listView.getFixedCellSize()) //
                              .add(Bindings.selectDouble(listView.insetsProperty(), "top"))
                              .add(Bindings.selectDouble(listView.insetsProperty(), "bottom")));

        final Timeline submitState = new Timeline(new KeyFrame(Duration.millis(200), ea -> {
            final Mapping mapping = listView.getSelectionModel().getSelectedItem();
            if (mapping != null)
            {
                getMainViewController().getRestClient() //
                                       .submit(getWidget().getItem(), mapping.getCommand());
            }
        }));

        final ChangeListener<Mapping> listViewListener = new ChangeListener<Mapping>()
        {
            @Override
            public void changed(final ObservableValue<? extends Mapping> o, final Mapping oldValue,
                    final Mapping newValue)
            {
                if (listView.getSelectionModel().isEmpty())
                {
                    Platform.runLater(() -> {
                        listView.getSelectionModel().selectedItemProperty().removeListener(this);
                        listView.getSelectionModel().select(getMapping(itemStateProperty().get()));
                        listView.getSelectionModel().selectedItemProperty().addListener(this);
                    });
                }
                else
                {
                    submitState.play();
                }
            }
        };

        itemStateProperty().addListener((i, oldState, newState) -> {
            if (submitState.getStatus() != Status.RUNNING)
            {
                listView.getSelectionModel().selectedItemProperty().removeListener(listViewListener);
                listView.getSelectionModel().select(getMapping(newState));
                listView.getSelectionModel().selectedItemProperty().addListener(listViewListener);
            }
        });
        listView.getSelectionModel().select(getMapping(itemStateProperty().get()));
        listView.getSelectionModel().selectedItemProperty().addListener(listViewListener);

        content = new VBox(iconImage, listView);
        content.setAlignment(Pos.CENTER);
        content.setMinSize(50, 50);
        content.setMaxSize(Double.MAX_VALUE, Region.USE_PREF_SIZE);
        content.prefWidth(0);
        content.setPadding(new Insets(0, 5, 0, 5));
    }

    private Mapping getMapping(final String command)
    {
        return getWidget().getMappings()
                          .stream() //
                          .filter(m -> m.getCommand().equals(command))
                          .findFirst()
                          .orElse(null);
    }

    @Override
    public Region getContentView()
    {
        return content;
    }

    private class MappingListCell extends ListCell<Mapping>
    {
        @Override
        protected void updateItem(final Mapping item, final boolean empty)
        {
            super.updateItem(item, empty);
            setGraphic(null);
            if (empty)
            {
                setText(null);
            }
            else
            {
                setText(item.getLabel());
            }
        }
    }
}
