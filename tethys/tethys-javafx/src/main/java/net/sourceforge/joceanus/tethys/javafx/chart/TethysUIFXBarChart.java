/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.chart;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.core.chart.TethysUICoreBarChart;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * javaFX barChart.
 */
public class TethysUIFXBarChart
        extends TethysUICoreBarChart {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The chart.
     */
    private final StackedBarChart<String, Number> theChart;

    /**
     * The series map.
     */
    private final Map<String, Series<String, Number>> theSeries;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    TethysUIFXBarChart(final TethysUICoreFactory<?> pFactory) {
        /* initialise underlying class */
        super(pFactory);

        /* Create chart */
        final CategoryAxis myXAxis = new CategoryAxis();
        final NumberAxis myYAxis = new NumberAxis();
        myYAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(final Number pValue) {
                return getFormatter().formatMoney(new OceanusMoney(pValue.toString()));
            }

            @Override
            public Number fromString(final String pValue) {
                return null;
            }
        });
        theChart = new StackedBarChart<>(myXAxis, myYAxis);

        /* Create the map */
        theSeries = new HashMap<>();

        /* Create Node */
        theNode = new TethysUIFXNode(theChart);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theChart.setDisable(!pEnabled);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theChart.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theChart.setPrefHeight(pHeight);
    }

    @Override
    public void updateBarChart(final TethysUIBarChartData pData) {
        /* Update underlying data */
        super.updateBarChart(pData);

        /* Set the chart title and Axis labels */
        theChart.setTitle(pData.getTitle());
        theChart.getXAxis().setLabel(pData.getXAxisLabel());
        theChart.getYAxis().setLabel(pData.getYAxisLabel());
    }

    @Override
    protected void resetData() {
        /* Clear the data */
        final ObservableList<Series<String, Number>> myData = theChart.getData();
        myData.clear();
        theSeries.clear();

        /* Clear underlying data  */
        super.resetData();
    }

    @Override
    protected void createSection(final String pName,
                                 final TethysUIBarChartDataSection pSection) {
        /* Add to underlying data  */
        super.createSection(pName, pSection);

        /* Access the series */
        final Series<String, Number> mySeries = theSeries.computeIfAbsent(pName, n -> {
            final Series<String, Number> s = new Series<>();
            s.setName(n);
            final ObservableList<Series<String, Number>> myData = theChart.getData();
            myData.add(s);
            return s;
        });

        /* Create section */
        final ObservableList<Data<String, Number>> mySections = mySeries.getData();
        final Data<String, Number> myData = new Data<>(pSection.getReference(), pSection.getValue().doubleValue());
        mySections.add(myData);
        final Node myNode = myData.getNode();

        /* Build name */
        final String myName = pName + ":" + myData.getXValue();

        /* Create the toolTip */
        final String myTooltip = getToolTip(myName);
        Tooltip.install(myNode, new Tooltip(myTooltip));

        /* Install click handler for node */
        myNode.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> selectSection(myName));
    }
}
