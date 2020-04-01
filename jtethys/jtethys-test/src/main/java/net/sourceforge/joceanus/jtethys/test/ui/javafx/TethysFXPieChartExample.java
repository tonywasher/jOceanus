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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.test.ui.TethysPieChartData;
import net.sourceforge.joceanus.jtethys.test.ui.TethysPieChartData.TethysPieChartSection;

/**
 * JavaFX PieChart Example.
 */
public class TethysFXPieChartExample extends Application {
    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter = new TethysDecimalFormatter();

    /**
     * The chart.
     */
    private final PieChart theChart;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysPieChartSection> theSectionMap;

    /**
     * Constructor.
     */
    public TethysFXPieChartExample() {
        theSectionMap = new HashMap<>();
        theChart = new PieChart();
        theChart.setLabelsVisible(true);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        updatePieChart(TethysPieChartData.createTestData());

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        myPane.setCenter(theChart);
        pStage.setTitle("JavaFX PieChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * Update PieChart with data.
     * @param pData the data
     */
    private void updatePieChart(final TethysPieChartData pData) {
        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Access and clear the data */
        final ObservableList<Data> myData = theChart.getData();
        myData.clear();
        theSectionMap.clear();

        /* Initialise the total */
        final TethysMoney myTotal = new TethysMoney();

        /* Iterate through the sections */
        final Iterator<TethysPieChartSection> myIterator = pData.sectionIterator();
        while (myIterator.hasNext()) {
            final TethysPieChartSection mySection = myIterator.next();

            /* Create the slice */
            final Data mySlice = new Data(mySection.getName(), mySection.getValue().doubleValue());
            myData.add(mySlice);

            /* Add to the section map */
            theSectionMap.put(mySection.getName(), mySection);
            myTotal.addAmount(mySection.getValue());
        }

        /* Install click handlers for each node */
        myData.forEach(d -> d.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println(d.getName())));

        /* Install toolTips for each node */
        myData.forEach(d -> {
            final String myName = d.getName();
            final TethysPieChartSection mySection = theSectionMap.get(myName);
            final TethysMoney myValue = mySection.getValue();
            final TethysRate myPerCent = new TethysRate(myValue, myTotal);
            final Tooltip myTip = new Tooltip(myName + ": ("
                    + theFormatter.formatMoney(myValue) + ", "
                    + theFormatter.formatRate(myPerCent) + ")");
            Tooltip.install(d.getNode(), myTip);
        });
    }
}
