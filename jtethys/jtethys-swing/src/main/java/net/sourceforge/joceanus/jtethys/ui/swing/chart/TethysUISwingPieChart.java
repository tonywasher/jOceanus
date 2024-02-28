/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import net.sourceforge.joceanus.jtethys.ui.core.chart.TethysUICorePieChart;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * Swing Pie Chart.
 */
public class TethysUISwingPieChart
        extends TethysUICorePieChart {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The dataSet.
     */
    private final DefaultPieDataset<String> theDataSet;

    /**
     * The chart.
     */
    private final JFreeChart theChart;

    /**
     * The panel.
     */
    private final ChartPanel thePanel;

    /**
     * Constructor.
     * @param pFactory the Gui factory
     */
    TethysUISwingPieChart(final TethysUICoreFactory<?> pFactory) {
        /* initialise underlying class */
        super(pFactory);

        /* Create the dataSet */
        theDataSet = new DefaultPieDataset<>();

        /* Create the chart */
        theChart = ChartFactory.createPieChart(
                null,
                theDataSet,
                true,
                true,
                false);
        @SuppressWarnings("unchecked")
        final PiePlot<String> myPlot = (PiePlot<String>) theChart.getPlot();
        myPlot.setStartAngle(0);
        myPlot.setToolTipGenerator((pDataSet, pKey) -> getToolTip((String) pKey));

        /* Create the panel */
        thePanel = new ChartPanel(theChart);
        thePanel.setOpaque(true);
        thePanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseMoved(final ChartMouseEvent e) {
                /* NoOp */
            }

            @Override
            public void chartMouseClicked(final ChartMouseEvent e) {
                final ChartEntity entity = e.getEntity();
                if (entity instanceof PieSectionEntity) {
                    final PieSectionEntity section = (PieSectionEntity) entity;
                    selectSection((String) section.getSectionKey());
                }
            }
        });

        /* Create the node */
        theNode = new TethysUISwingNode(thePanel);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    @Override
    public void updatePieChart(final TethysUIPieChartData pData) {
        /* Update underlying data */
        super.updatePieChart(pData);

        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Declare changes */
        theChart.fireChartChanged();
    }

    @Override
    protected void resetData() {
        /* Clear existing data  */
        theDataSet.clear();

        /* Clear underlying data  */
        super.resetData();
    }

    @Override
    protected void createSlice(final TethysUIPieChartSection pSection) {
        /* Create the slice */
        theDataSet.setValue(pSection.getName(), pSection.getValue().doubleValue());

        /* Add to underlying data  */
        super.createSlice(pSection);
    }
}
