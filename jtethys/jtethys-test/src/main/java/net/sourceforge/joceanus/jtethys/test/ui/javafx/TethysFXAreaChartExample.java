/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.test.ui.TethysAreaChartData;
import net.sourceforge.joceanus.jtethys.test.ui.TethysAreaChartData.TethysAreaChartDataPoint;
import net.sourceforge.joceanus.jtethys.test.ui.TethysAreaChartData.TethysAreaChartSeries;

/**
 * JavaFX AreaChart Example.
 */
public class TethysFXAreaChartExample extends Application {
    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter = new TethysDecimalFormatter();

    /**
     * The chart.
     */
    private final StackedAreaChart<Number, Number> theChart;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysAreaChartSeries> theSeriesMap;

    /**
     * Constructor.
     */
    public TethysFXAreaChartExample() {
        theSeriesMap = new HashMap<>();
        final NumberAxis myXAxis = new NumberAxis();
        myXAxis.setLabel("Date");
        myXAxis.setAutoRanging(true);
        myXAxis.setForceZeroInRange(false);
        myXAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(final Number pValue) {
                final LocalDate myDate = LocalDate.ofEpochDay(((Double) pValue).longValue());
                return new TethysDate(myDate).toString();
            }

            @Override
            public Number fromString(final String pValue) {
                return null;
            }
        });
        final NumberAxis myYAxis = new NumberAxis();
        myYAxis.setLabel("Value");
        myYAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(final Number pValue) {
                final TethysMoney myMoney = new TethysMoney(pValue.toString());
                return theFormatter.formatMoney(myMoney);
            }

            @Override
            public Number fromString(final String pValue) {
                return null;
            }
        });
        theChart = new StackedAreaChart<>(myXAxis, myYAxis);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        updateAreaChart(TethysAreaChartData.createTestData());

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        myPane.setCenter(theChart);
        pStage.setTitle("JavaFX AreaChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Update AreaChart with data.
     * @param pData the data
     */
    private void updateAreaChart(final TethysAreaChartData pData) {
        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Access and clear the data */
        final ObservableList<Series<Number, Number>> myData = theChart.getData();
        myData.clear();
        theSeriesMap.clear();

        /* Iterate through the sections */
        final Iterator<TethysAreaChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysAreaChartSeries myBase = myIterator.next();
            final Series<Number, Number> mySeries = new Series<>();
            final String myKey = myBase.getName();
            mySeries.setName(myKey);
            final ObservableList<Data<Number, Number>> myPoints = mySeries.getData();
            myData.add(mySeries);
            theSeriesMap.put(myKey, myBase);

            /* Iterate through the sections */
            final Iterator<TethysAreaChartDataPoint> myPointIterator = myBase.pointIterator();
            while (myPointIterator.hasNext()) {
                final TethysAreaChartDataPoint myPoint = myPointIterator.next();

                /* Add the section */
                myPoints.add(new Data<>(dateToEpoch(myPoint.getDate()), myPoint.getValue().doubleValue()));
            }
        }

        myData.forEach(s -> s.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println(s.getName())));
        myData.forEach(s -> s.getData().forEach(d -> {
            final TethysAreaChartSeries mySeries = theSeriesMap.get(s.getName());
            final TethysMoney myValue = new TethysMoney(d.getYValue().toString());
            final Tooltip myTip = new Tooltip(mySeries.getName() + " = " + theFormatter.formatMoney(myValue));
            Tooltip.install(d.getNode(), myTip);
        }));
    }

    /**
     * Convert date to epoch.
     * @param pDate the date
     * @return the epoch
     */
    private static long dateToEpoch(final TethysDate pDate) {
        return pDate.getDate().toEpochDay();
    }
}
