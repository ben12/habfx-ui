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
package com.ben12.openhab.controller.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.model.Item;
import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.persistence.Measure;
import com.ben12.openhab.model.persistence.Persistence;
import com.ben12.openhab.model.util.BeanCopy;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ChartController extends WidgetController
{
    private LineChart<Number, Number>                       chart;

    private NumberAxis                                      timeAxis;

    private ScheduledService<List<Pair<Item, Persistence>>> scheduler;

    private ObservableList<Item>                            members;

    private Map<String, XYChart.Series<Number, Number>>     seriesCache;

    public ChartController(final Page parent)
    {
        super(parent);

        seriesCache = new HashMap<>();

        timeAxis = new NumberAxis();
        timeAxis.setTickLabelFormatter(new StringConverter<Number>()
        {
            private final DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

            private final DateTimeFormatter timeFormatter     = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

            @Override
            public String toString(final Number number)
            {
                if (timeAxis.getUpperBound() - timeAxis.getLowerBound() > Duration.hours(48.0).toMillis())
                {
                    return datetimeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(number.longValue()),
                                                                            ZoneId.systemDefault()));
                }
                else
                {
                    return timeFormatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(number.longValue()),
                                                                        ZoneId.systemDefault()));
                }
            }

            @Override
            public Number fromString(final String datetime)
            {
                return null;
            }
        });
        timeAxis.setAutoRanging(false);
        timeAxis.setTickLabelRotation(-90.0);

        final NumberAxis stateAxis = new NumberAxis();
        stateAxis.setForceZeroInRange(false);

        chart = new LineChart<>(timeAxis, stateAxis);
        chart.setAnimated(false);
        chart.setLegendSide(Side.BOTTOM);
        chart.prefHeightProperty().bind(Bindings.selectDouble(chart.parentProperty(), "layoutBounds", "height"));
        chart.setMinSize(0, 0);
        chart.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        scheduler = new ScheduledService<List<Pair<Item, Persistence>>>()
        {
            @Override
            protected Task<List<Pair<Item, Persistence>>> createTask()
            {
                return new Task<List<Pair<Item, Persistence>>>()
                {
                    @Override
                    protected List<Pair<Item, Persistence>> call() throws Exception
                    {
                        final OpenHabRestClient restClient = getMainViewController().getRestClient();

                        final String period = getWidget().getPeriod();

                        final LocalDateTime start;
                        if (period.matches("\\d*[YMWD]"))
                        {
                            start = LocalDateTime.now().minus(Period.parse("P"
                                    + (Character.isDigit(period.charAt(0)) ? "" : "1") + period));
                        }
                        else if (period.matches("\\d*h"))
                        {
                            start = LocalDateTime.now().minus(java.time.Duration.parse("PT"
                                    + (Character.isDigit(period.charAt(0)) ? "" : "1") + period.toUpperCase()));
                        }
                        else
                        {
                            start = LocalDateTime.now().minus(Period.ofDays(1));
                        }

                        final List<Pair<Item, Persistence>> allResult = new ArrayList<>();

                        synchronized (members)
                        {
                            members.parallelStream().forEach(item -> {
                                final CompletableFuture<Persistence> result = new CompletableFuture<>();

                                // TODO other than Number ? (Switch, ...)
                                if ("Number".equals(item.getType()))
                                {
                                    restClient.persistence(item.getName(), getWidget().getService(), start, null,
                                                           new InvocationCallback<Persistence>()
                                                           {
                                                               @Override
                                                               public void failed(final Throwable throwable)
                                                               {
                                                                   result.completeExceptionally(throwable);
                                                               }

                                                               @Override
                                                               public void completed(final Persistence response)
                                                               {
                                                                   result.complete(response);
                                                               }
                                                           });

                                    try
                                    {
                                        final Persistence persistence = result.get(1, TimeUnit.MINUTES);
                                        synchronized (allResult)
                                        {
                                            allResult.add(new Pair<>(item, persistence));
                                        }
                                    }
                                    catch (final Exception e)
                                    {
                                        Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                                                                                   "Cannot load persistence", e);
                                    }
                                }
                            });
                        }

                        return allResult;
                    }
                };
            }
        };
        scheduler.setDelay(Duration.millis(100));

        scheduler.valueProperty().addListener((o, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty())
            {
                chart.setLegendVisible(newValue.size() > 1);

                long startTime = Long.MAX_VALUE;
                long endTime = Long.MIN_VALUE;

                for (final Pair<Item, Persistence> persistence : newValue)
                {
                    XYChart.Series<Number, Number> series = seriesCache.get(persistence.getKey().getName());
                    if (series == null)
                    {
                        series = new XYChart.Series<>();
                        series.setName(persistence.getKey().getLabel());
                        chart.getData().add(series);
                        seriesCache.put(persistence.getKey().getName(), series);
                    }

                    final List<Measure> data = persistence.getValue().getData();
                    series.getData()
                          .setAll(data.stream()
                                      .map(m -> new XYChart.Data<Number, Number>(m.getTime(),
                                              new BigDecimal(m.getState()).round(MathContext.DECIMAL32)))
                                      .toArray(XYChart.Data[]::new));

                    if (data.size() > 1)
                    {
                        startTime = Math.min(startTime, data.get(0).getTime());
                        endTime = Math.max(endTime, data.get(data.size() - 1).getTime());
                    }
                    else if (data.size() == 1)
                    {
                        startTime = Math.min(startTime, data.get(0).getTime() - TimeUnit.MINUTES.toMillis(10));
                        endTime = Math.max(endTime, data.get(0).getTime() + TimeUnit.MINUTES.toMillis(10));
                    }
                }

                final Duration duration = Duration.millis((double) endTime - startTime);
                final Duration tickPeriod = duration.divide(10);
                timeAxis.setTickUnit(tickPeriod.toMillis());
                timeAxis.setLowerBound(startTime);
                timeAxis.setUpperBound(endTime);
            }
        });

        members = FXCollections.observableArrayList();
        members.addListener((ListChangeListener<Item>) c -> {
            while (c.next())
            {
                if (c.wasRemoved())
                {
                    for (final Item item : c.getRemoved())
                    {
                        final XYChart.Series<Number, Number> series = seriesCache.remove(item.getName());
                        chart.getData().remove(series);
                    }
                }
                if (c.wasAdded() && scheduler.isRunning())
                {
                    scheduler.restart();
                }
            }
        });
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
                Platform.runLater(() -> {
                    synchronized (members)
                    {
                        BeanCopy.copy(response.getMembers(), members, Item::getName);
                    }
                });
            }
        });
    }

    private void restart()
    {
        if ("Group".equals(getWidget().getItem().getType()))
        {
            loadMembers();
        }
        else
        {
            synchronized (members)
            {
                members.setAll(getWidget().getItem());
            }
        }

        if (getWidget().getRefresh() >= 1000)
        {
            scheduler.setPeriod(Duration.millis(getWidget().getRefresh()));
        }
        else
        {
            scheduler.setPeriod(Duration.seconds(1));
        }

        scheduler.restart();
    }

    @Override
    public void hiding()
    {
        scheduler.cancel();
    }

    @Override
    protected void display()
    {
        super.display();
        restart();
    }

    @Override
    public void reload()
    {
        super.reload();
        restart();
    }

    @Override
    public Region getContentView()
    {
        return chart;
    }
}
