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
package net.sourceforge.joceanus.jtethys.ui.javafx;

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

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.TethysBarChart;

/**
 * javaFX barChart.
 */
public class TethysFXBarChart
        extends TethysBarChart {
    /**
     * The Node.
     */
    private final TethysFXNode theNode;

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
    TethysFXBarChart(final TethysFXGuiFactory pFactory) {
        /* initialise underlying class */
        super(pFactory);

        /* Create chart */
        final CategoryAxis myXAxis = new CategoryAxis();
        myXAxis.setLabel("Date");
        final NumberAxis myYAxis = new NumberAxis();
        myYAxis.setLabel("Value");
        myYAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(final Number pValue) {
                final TethysMoney myMoney = new TethysMoney(pValue.toString());
                return getFormatter().formatMoney(myMoney);
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
        theNode = new TethysFXNode(theChart);
    }

    @Override
    public TethysFXNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theNode.getNode().setDisable(!pEnabled);
    }

    @Override
    public void updateBarChart(final TethysBarChartData pData) {
        /* Update underlying data */
        super.updateBarChart(pData);

        /* Set the chart title */
        theChart.setTitle(pData.getTitle());
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
                                 final TethysBarChartDataSection pSection) {
        /* Add to underlying data  */
        super.createSection(pName, pSection);

        /* Access the series */
        Series<String, Number> mySeries = theSeries.get(pName);
        if (mySeries == null) {
            mySeries = new Series<>();
            mySeries.setName(pName);
            final ObservableList<Series<String, Number>> myData = theChart.getData();
            myData.add(mySeries);
            theSeries.put(pName, mySeries);
        }

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
