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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.InvocationCallback;

import com.ben12.openhab.model.Page;
import com.ben12.openhab.model.persistence.Measure;
import com.ben12.openhab.model.persistence.Persistence;
import com.ben12.openhab.rest.OpenHabRestClient;

import javafx.beans.binding.Bindings;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ChartController extends WidgetController
{
	private LineChart<Number, Number>		chart;

	private NumberAxis						timeAxis;

	private ScheduledService<Persistence>	scheduler;

	public ChartController(final Page parent)
	{
		super(parent);

		timeAxis = new NumberAxis();
		timeAxis.setTickLabelFormatter(new StringConverter<Number>()
		{
			private final DateTimeFormatter	datetimeFormatter	= DateTimeFormatter
					.ofLocalizedDateTime(FormatStyle.SHORT);

			private final DateTimeFormatter	timeFormatter		= DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

			@Override
			public String toString(final Number number)
			{
				if (timeAxis.getUpperBound() - timeAxis.getLowerBound() > Duration.hours(48.0).toMillis())
				{
					return datetimeFormatter.format(
							LocalDateTime.ofInstant(Instant.ofEpochMilli(number.longValue()), ZoneId.systemDefault()));
				}
				else
				{
					return timeFormatter.format(
							LocalDateTime.ofInstant(Instant.ofEpochMilli(number.longValue()), ZoneId.systemDefault()));
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
		chart.prefHeightProperty().bind(Bindings.selectDouble(chart.parentProperty(), "layoutBounds", "height"));
		chart.setMinSize(0, 0);
		chart.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

		scheduler = new ScheduledService<Persistence>()
		{
			@Override
			protected Task<Persistence> createTask()
			{
				return new Task<Persistence>()
				{
					@Override
					protected Persistence call() throws Exception
					{
						final OpenHabRestClient restClient = getMainViewController().getRestClient();

						final String period = getWidget().getPeriod();

						LocalDateTime start = LocalDateTime.now();
						if (period.matches("\\d*[YMWD]"))
						{
							start = start.minus(
									Period.parse("P" + (Character.isDigit(period.charAt(0)) ? "" : "1") + period));
						}
						else if (period.matches("\\d*h"))
						{
							start = start.minus(java.time.Duration.parse(
									"PT" + (Character.isDigit(period.charAt(0)) ? "" : "1") + period.toUpperCase()));
						}

						final CountDownLatch latch = new CountDownLatch(1);
						final AtomicReference<Persistence> result = new AtomicReference<>();

						restClient.persistence(getWidget().getItem().getName(), getWidget().getService(), start, null,
								new InvocationCallback<Persistence>()
								{
									@Override
									public void failed(final Throwable throwable)
									{
										latch.countDown();
									}

									@Override
									public void completed(final Persistence response)
									{
										result.set(response);
										latch.countDown();
									}
								});

						if (!latch.await(1, TimeUnit.MINUTES))
						{
							Logger.getLogger(getClass().getName())
									.warning(() -> "Timeout expired during persistence recovery for "
											+ getWidget().getItem().getName());
						}

						return result.get();
					}
				};
			}
		};
		scheduler.setDelay(Duration.millis(100));

		scheduler.valueProperty().addListener((o, oldValue, newValue) -> {
			if (newValue != null)
			{
				final XYChart.Series<Number, Number> series = new XYChart.Series<>();
				final List<Measure> data = newValue.getData();
				data.stream()
						.map(m -> new XYChart.Data<Number, Number>(m.getTime(),
								new BigDecimal(m.getState()).round(MathContext.DECIMAL32)))
						.collect(Collectors.toCollection(series::getData));
				chart.getData().setAll(series);

				long startTime = 0;
				long endTime = 0;

				if (data.size() > 1)
				{
					startTime = data.get(0).getTime();
					endTime = data.get(data.size() - 1).getTime();
				}
				else if (data.size() == 1)
				{
					startTime = data.get(0).getTime() - TimeUnit.MINUTES.toMillis(10);
					endTime = data.get(0).getTime() + TimeUnit.MINUTES.toMillis(10);
				}

				final Duration duration = Duration.millis((double) endTime - startTime);
				final Duration tickPeriod = duration.divide(10);
				timeAxis.setTickUnit(tickPeriod.toMillis());
				timeAxis.setLowerBound(startTime);
				timeAxis.setUpperBound(endTime);
			}
		});
	}

	private void restart()
	{
		// TODO other than Number ? (Group, Switch, ...)
		if ("Number".equals(getWidget().getItem().getType()))
		{
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
