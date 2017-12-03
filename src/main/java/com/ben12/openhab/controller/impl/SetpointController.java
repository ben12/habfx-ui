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

import java.math.BigDecimal;

import com.ben12.openhab.controller.MainViewController;
import com.ben12.openhab.model.Mapping;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.Widget;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class SetpointController extends WidgetController
{
    private VBox content;

    public SetpointController(final Page parent)
    {
        super(parent);
    }

    @Override
    public void init(final Widget pWidget, final MainViewController pMainViewController)
    {
        super.init(pWidget, pMainViewController);

        final Node iconImage = createIconNode();

        final Spinner<?> spinner = new Spinner<>();
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL);

        if (pWidget.getMappings().isEmpty())
        {
            initSpinnerWithoutMapping(spinner);
        }
        else
        {
            initSpinnerWithMapping(spinner, pWidget.mappingsProperty());
        }

        content = new VBox(iconImage, spinner);
        content.setAlignment(Pos.CENTER);
        content.setMinSize(Region.USE_PREF_SIZE, 50);
        content.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        content.prefWidth(0);
        content.setPadding(new Insets(0, 5, 0, 5));
    }

    private void initSpinnerWithoutMapping(final Spinner<?> spinner)
    {
        final BigDecimal minValue = getWidget().getMinValue();
        final BigDecimal maxValue = getWidget().getMaxValue();
        final BigDecimal stepValue = getWidget().getStep();

        final SpinnerValueFactory<Double> spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(
                (minValue != null ? minValue.doubleValue() : Double.NEGATIVE_INFINITY), //
                (maxValue != null ? maxValue.doubleValue() : Double.POSITIVE_INFINITY), //
                doubleFromState(itemStateProperty().get()), //
                (stepValue != null ? stepValue.doubleValue() : 1.0));

        ((Spinner<Double>) spinner).setValueFactory(spinnerValueFactory);

        final Timeline submitState = new Timeline(new KeyFrame(Duration.millis(1000),
                ea -> getMainViewController().getRestClient() //
                                             .submit(getWidget().getItem(),
                                                     Double.toString((Double) spinner.getValue()))));

        final ChangeListener<Number> spinnerListener = (i, oldState, newState) -> submitState.play();

        itemStateProperty().addListener((i, oldState, newState) -> {
            if (submitState.getStatus() != Status.RUNNING)
            {
                spinnerValueFactory.valueProperty().removeListener(spinnerListener);
                spinnerValueFactory.setValue(doubleFromState(newState));
                spinnerValueFactory.valueProperty().addListener(spinnerListener);
            }
        });

        spinnerValueFactory.valueProperty().addListener(spinnerListener);
    }

    private void initSpinnerWithMapping(final Spinner<?> spinner, final ObservableList<Mapping> mappings)
    {
        final SpinnerValueFactory<Mapping> spinnerValueFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(
                mappings);
        spinnerValueFactory.setValue(mappingFromState(itemStateProperty().get()));
        spinnerValueFactory.setConverter(new StringConverter<Mapping>()
        {
            @Override
            public String toString(final Mapping mapping)
            {
                return mapping != null ? mapping.getLabel() : null;
            }

            @Override
            public Mapping fromString(final String label)
            {
                return mappingFromLabel(label);
            }
        });

        ((Spinner<Mapping>) spinner).setValueFactory(spinnerValueFactory);

        final Timeline submitState = new Timeline(new KeyFrame(Duration.millis(1000),
                ea -> getMainViewController().getRestClient() //
                                             .submit(getWidget().getItem(),
                                                     ((Mapping) spinner.getValue()).getCommand())));

        final ChangeListener<Mapping> spinnerListener = (i, oldState, newState) -> submitState.play();

        itemStateProperty().addListener((i, oldState, newState) -> {
            if (submitState.getStatus() != Status.RUNNING)
            {
                spinnerValueFactory.valueProperty().removeListener(spinnerListener);
                spinnerValueFactory.setValue(mappingFromState(newState));
                spinnerValueFactory.valueProperty().addListener(spinnerListener);
            }
        });

        spinnerValueFactory.valueProperty().addListener(spinnerListener);
    }

    private double doubleFromState(final String state)
    {
        final BigDecimal minValue = getWidget().getMinValue();

        double value = (minValue != null ? minValue.doubleValue() : 0.0);
        if (state != null && !state.isEmpty())
        {
            value = Double.valueOf(state);
        }

        return value;
    }

    private Mapping mappingFromState(final String state)
    {
        return getWidget().getMappings().stream().filter(m -> m.getCommand().equals(state)).findFirst().orElse(null);
    }

    private Mapping mappingFromLabel(final String label)
    {
        return getWidget().getMappings().stream().filter(m -> m.getLabel().equals(label)).findFirst().orElse(null);
    }

    @Override
    public Region getContentView()
    {
        return content;
    }
}
