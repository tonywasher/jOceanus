/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.test.ui.TethysBarChartData;
import net.sourceforge.joceanus.jtethys.test.ui.TethysBarChartData.TethysBarChartDataSection;
import net.sourceforge.joceanus.jtethys.test.ui.TethysBarChartData.TethysBarChartSeries;

/**
 * JavaFX BarChart Example.
 */
public class TethysFXBarChartExample extends Application {
    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter = new TethysDecimalFormatter();

    /**
     * The chart.
     */
    private final StackedBarChart<String, Number> theChart;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysBarChartDataSection> theSectionMap;

    /**
     * Constructor.
     */
    public TethysFXBarChartExample() {
        theSectionMap = new HashMap<>();
        final CategoryAxis myXAxis = new CategoryAxis();
        myXAxis.setLabel("Date");
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
        theChart = new StackedBarChart<>(myXAxis, myYAxis);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        updateBarChart(TethysBarChartData.createTestData());

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        myPane.setCenter(theChart);
        pStage.setTitle("JavaFX BarChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Update BarChart with data.
     * @param pData the data
     */
    private void updateBarChart(final TethysBarChartData pData) {
        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Access and clear the data */
        final ObservableList<XYChart.Series<String, Number>> myData = theChart.getData();
        myData.clear();
        theSectionMap.clear();

        /* Iterate through the sections */
        final Iterator<TethysBarChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysBarChartSeries myBase = myIterator.next();
            final Series<String, Number> mySeries = new XYChart.Series<>();
            mySeries.setName(myBase.getName());
            final ObservableList<Data<String, Number>> mySections = mySeries.getData();
            myData.add(mySeries);

            /* Iterate through the sections */
            final Iterator<TethysBarChartDataSection> mySectIterator = myBase.sectionIterator();
            while (mySectIterator.hasNext()) {
                final TethysBarChartDataSection mySection = mySectIterator.next();

                /* Add the section */
                mySections.add(new Data<>(mySection.getReference(), mySection.getValue().doubleValue()));
                final String myKey = myBase.getName() + ":" + mySection.getReference();
                theSectionMap.put(myKey, mySection);
            }
        }

        /* Add Click handlers and toolTips */
        myData.forEach(s -> s.getData().forEach(d -> {
            final String myKey = s.getName() + ":" + d.getXValue();
            final TethysBarChartDataSection mySection = theSectionMap.get(myKey);
            final TethysMoney myValue = mySection.getValue();
            d.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println(myKey));
            final Tooltip myTip = new Tooltip(myKey + " = " + theFormatter.formatMoney(myValue));
            Tooltip.install(d.getNode(), myTip);
        }));
    }
}

