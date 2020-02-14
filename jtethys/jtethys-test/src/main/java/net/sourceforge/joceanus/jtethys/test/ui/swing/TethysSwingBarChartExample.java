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
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.util.HexNumberFormat;
import org.jfree.data.category.DefaultCategoryDataset;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.test.ui.TethysBarChartData;
import net.sourceforge.joceanus.jtethys.test.ui.TethysBarChartData.TethysBarChartDataSection;
import net.sourceforge.joceanus.jtethys.test.ui.TethysBarChartData.TethysBarChartSeries;

/**
 * Swing BarChart Example.
 */
public class TethysSwingBarChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingBarChartExample.class);

    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter = new TethysDecimalFormatter();

    /**
     * The dataSet.
     */
    private final DefaultCategoryDataset theDataSet;

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
    private final Map<String, TethysBarChartDataSection> theSectionMap;

    /**
     * Constructor.
     */
    TethysSwingBarChartExample() {
        theDataSet = new DefaultCategoryDataset();
        theSectionMap = new HashMap<>();

        theChart = ChartFactory.createStackedBarChart(
                null,
                "Date",
                "Value",
                theDataSet,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        final CategoryPlot myPlot = (CategoryPlot) theChart.getPlot();
        final CategoryItemRenderer myRenderer = myPlot.getRenderer();
        myRenderer.setDefaultToolTipGenerator((pDataset, pSeries, pItem) -> {
            final String myKey = pDataset.getRowKey(pSeries) + ":" + pDataset.getColumnKey(pItem);
            final TethysBarChartDataSection mySection = theSectionMap.get(myKey);
            final TethysMoney myValue = mySection.getValue();
            return myKey + " = " + theFormatter.formatMoney(myValue);
        });
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
                if (entity instanceof CategoryItemEntity) {
                    final CategoryItemEntity section = (CategoryItemEntity) entity;
                    System.out.println(section.getRowKey() + ":" + section.getColumnKey());
                }
            }
        });
    }

    /**
     * Update BarChart with data.
     * @param pData the data
     */
    private void updateBarChart(final TethysBarChartData pData) {
        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Access and clear the data */
        theDataSet.clear();
        theSectionMap.clear();

        /* Iterate through the sections */
        final Iterator<TethysBarChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysBarChartSeries myBase = myIterator.next();
            final String myName = myBase.getName();

            /* Iterate through the sections */
            final Iterator<TethysBarChartDataSection> mySectIterator = myBase.sectionIterator();
            while (mySectIterator.hasNext()) {
                final TethysBarChartDataSection mySection = mySectIterator.next();

                /* Add the section */
                theDataSet.addValue(mySection.getValue().doubleValue(), myName, mySection.getReference());
                final String myKey = myName + ":" + mySection.getReference();
                theSectionMap.put(myKey, mySection);
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
        SwingUtilities.invokeLater(TethysSwingBarChartExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("BarChart Demo");

            /* Create the UI */
            final TethysSwingBarChartExample myChart = new TethysSwingBarChartExample();
            myChart.updateBarChart(TethysBarChartData.createTestData());

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

    /**
     * Money Format class.
     */
    private class MoneyFormat extends HexNumberFormat {
        private static final long serialVersionUID = 8503446251225761695L;

        @Override
        public StringBuffer format(final double pValue,
                                   final StringBuffer pBuffer,
                                   final FieldPosition pLoc) {
            final TethysMoney myMoney = new TethysMoney(Double.toString(pValue));
            return new StringBuffer(theFormatter.formatMoney(myMoney));
        }
    }
}
