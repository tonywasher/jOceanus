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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.text.FieldPosition;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.util.HexNumberFormat;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeTableXYDataset;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.TethysAreaChart;

/**
 * Swing areaChart.
 */
public class TethysSwingAreaChart
        extends TethysAreaChart {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The dataSet.
     */
    private final TimeTableXYDataset theDataSet;

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
     * @param pFactory the Gui Factory
     */
    TethysSwingAreaChart(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the dataSet */
        theDataSet = new TimeTableXYDataset();

        /* Create the chart */
        theChart = ChartFactory.createTimeSeriesChart("XYChart XDemo", "Date", "Value", theDataSet);
        final XYPlot myPlot = (XYPlot) theChart.getPlot();
        final StackedXYAreaRenderer2 myRenderer = new StackedXYAreaRenderer2();
        myRenderer.setDefaultToolTipGenerator((pDataset, pSeries, pItem) -> {
            final TethysMoney myValue = new TethysMoney(pDataset.getY(pSeries, pItem).toString());
            return getToolTip(pDataset.getSeriesKey(pSeries).toString(), myValue);
        });
        myPlot.setRenderer(0, myRenderer);
        final NumberAxis myYAxis = (NumberAxis) myPlot.getRangeAxis();
        myYAxis.setNumberFormatOverride(new MoneyFormat());

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
                if (entity instanceof XYItemEntity) {
                    final XYItemEntity section = (XYItemEntity) entity;
                    selectSeries((String) theDataSet.getSeriesKey(section.getSeriesIndex()));
                }
            }
        });

        /* Create the node */
        theNode = new TethysSwingNode(thePanel);
    }

    @Override
    public TethysSwingNode getNode() {
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
    public void updateAreaChart(final TethysAreaChartData pData) {
        /* Update underlying data */
        super.updateAreaChart(pData);

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
    protected void createPoint(final String pName,
                               final TethysAreaChartDataPoint pPoint) {
        /* Add the point */
        theDataSet.add(dateToDay(pPoint.getDate()), pPoint.getValue().doubleValue(), pName);
    }

    private static Day dateToDay(final TethysDate pDate) {
        return new Day(pDate.getDay(), pDate.getMonth(), pDate.getYear());
    }

    /**
     * Money Format class.
     */
    private class MoneyFormat extends HexNumberFormat {
        private static final long serialVersionUID = 2233789189711420564L;

        @Override
        public StringBuffer format(final double pValue,
                                   final StringBuffer pBuffer,
                                   final FieldPosition pLoc) {
            final TethysMoney myMoney = new TethysMoney(Double.toString(pValue));
            return new StringBuffer(getFormatter().formatMoney(myMoney));
        }
    }
}
