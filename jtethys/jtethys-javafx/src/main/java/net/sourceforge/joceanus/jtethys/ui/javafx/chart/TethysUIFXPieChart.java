/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.chart;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import net.sourceforge.joceanus.jtethys.ui.core.chart.TethysUICorePieChart;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;

/**
 * javaFX Pie Chart.
 */
public class TethysUIFXPieChart
        extends TethysUICorePieChart {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The chart.
     */
    private final PieChart theChart;

    /**
     * Constructor.
     * @param pFactory the Gui factory
     */
    TethysUIFXPieChart(final TethysUICoreFactory<?> pFactory) {
        /* initialise underlying class */
        super(pFactory);

        /* Create chart */
        theChart = new PieChart();
        theChart.setLabelsVisible(true);
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
    public void updatePieChart(final TethysUIPieChartData pData) {
        /* Update underlying data */
        super.updatePieChart(pData);

        /* Set the chart title */
        theChart.setTitle(pData.getTitle());
    }

    @Override
    protected void resetData() {
        /* Clear the data */
        final ObservableList<Data> myData = theChart.getData();
        myData.clear();

        /* Clear underlying data  */
        super.resetData();
    }

    @Override
    protected void createSlice(final TethysUIPieChartSection pSection) {
        /* Add to underlying data  */
        super.createSlice(pSection);

        /* Create the slice */
        final String myName = pSection.getName();
        final Data mySlice = new Data(myName, pSection.getValue().doubleValue());
        final ObservableList<Data> myData = theChart.getData();
        myData.add(mySlice);
        final Node myNode = mySlice.getNode();

        /* Create the toolTip */
        final String myTooltip = getToolTip(myName);
        Tooltip.install(myNode, new Tooltip(myTooltip));

        /* Install click handler for node */
        myNode.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> selectSection(myName));
    }
}
