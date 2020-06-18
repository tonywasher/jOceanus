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
package net.sourceforge.joceanus.jtethys.test.ui.swing;

import java.awt.HeadlessException;
import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.test.ui.TethysAreaChartData;
import net.sourceforge.joceanus.jtethys.test.ui.TethysAreaChartData.TethysAreaChartDataPoint;
import net.sourceforge.joceanus.jtethys.test.ui.TethysAreaChartData.TethysAreaChartSeries;

public class TethysSwingAreaChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingAreaChartExample.class);

    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter = new TethysDecimalFormatter();

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
     * The sectionMap.
     */
    private final Map<String, TethysAreaChartSeries> theSeriesMap;

    /**
     * Constructor.
     */
    TethysSwingAreaChartExample() {
        theDataSet = new TimeTableXYDataset();
        theSeriesMap = new HashMap<>();

        theChart = ChartFactory.createTimeSeriesChart("XYChart XDemo", "Date","Value", theDataSet);
        final XYPlot myPlot = (XYPlot) theChart.getPlot();
        final StackedXYAreaRenderer2 myRenderer = new StackedXYAreaRenderer2();
        myRenderer.setDefaultToolTipGenerator((pDataset, pSeries, pItem) -> {
            final TethysAreaChartSeries mySeries = theSeriesMap.get(pDataset.getSeriesKey(pSeries).toString());
            final TethysMoney myValue = new TethysMoney(pDataset.getY(pSeries, pItem).toString());
            return mySeries.getName() + " = " + theFormatter.formatMoney(myValue);
        });
        myPlot.setRenderer(0, myRenderer);
        final NumberAxis myYAxis = (NumberAxis) myPlot.getRangeAxis();
        myYAxis.setNumberFormatOverride(new MoneyFormat());

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
                    System.out.println(theDataSet.getSeriesKey(section.getSeriesIndex()));
                }
            }
        });
    }

    /**
     * Update AreaChart with data.
     * @param pData the data
     */
    private void updateXYChart(final TethysAreaChartData pData) {
        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Access and clear the data */
        theDataSet.clear();
        theSeriesMap.clear();

        /* Iterate through the sections */
        final Iterator<TethysAreaChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysAreaChartSeries myBase = myIterator.next();
            final String myKey = myBase.getName();
            theSeriesMap.put(myKey, myBase);

            /* Iterate through the sections */
            final Iterator<TethysAreaChartDataPoint> myPointIterator = myBase.pointIterator();
            while (myPointIterator.hasNext()) {
                final TethysAreaChartDataPoint myPoint = myPointIterator.next();

                /* Add the section */
                theDataSet.add(dateToDay(myPoint.getDate()), myPoint.getValue().doubleValue(), myKey);
            }
        }

        /* Declare changes */
        theChart.fireChartChanged();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TethysSwingAreaChartExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("PieChart Demo");

            /* Create the UI */
            final TethysSwingAreaChartExample myChart = new TethysSwingAreaChartExample();
            myChart.updateXYChart(TethysAreaChartData.createTestData());

            /* Attach the panel to the frame */
            myFrame.setContentPane(myChart.thePanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
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
            return new StringBuffer(theFormatter.formatMoney(myMoney));
        }
    }
}

