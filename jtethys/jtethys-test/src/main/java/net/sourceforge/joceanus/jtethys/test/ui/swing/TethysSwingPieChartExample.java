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
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.test.ui.TethysPieChartData;
import net.sourceforge.joceanus.jtethys.test.ui.TethysPieChartData.TethysPieChartSection;

/**
 * Swing PieChart Example.
 */
public class TethysSwingPieChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingPieChartExample.class);

    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter = new TethysDecimalFormatter();

    /**
     * The dataSet.
     */
    private final DefaultPieDataset theDataSet;

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
    private final Map<String, TethysPieChartSection> theSectionMap;

    /**
     * The total.
     */
    private final TethysMoney theTotal = new TethysMoney();

    /**
     * Constructor.
     */
    TethysSwingPieChartExample() {
        /* Create the dataSet */
        theDataSet = new DefaultPieDataset();

        /* Create the section map */
        theSectionMap = new HashMap<>();

        /* Create the chart */
        theChart = ChartFactory.createPieChart(
                null,
                theDataSet, true,
                true,
                false);
        final PiePlot myPlot = (PiePlot) theChart.getPlot();
        myPlot.setStartAngle(0);
        myPlot.setToolTipGenerator((pDataSet, pKey) -> {
            final String myName = (String) pKey;
            final TethysPieChartSection mySection = theSectionMap.get(myName);
            final TethysMoney myValue = mySection.getValue();
            final TethysRate myPerCent = new TethysRate(myValue, theTotal);
            return myName + ": ("
                    + theFormatter.formatMoney(myValue) + ", "
                    + theFormatter.formatRate(myPerCent) + ")";
        });

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
                    System.out.println(section.getSectionKey());
                }
            }
        });
    }

    /**
     * Update PieChart with data.
     * @param pData the data
     */
    private void updatePieChart(final TethysPieChartData pData) {
        /* Set the chart title */
        theChart.setTitle(pData.getTitle());

        /* Clear existing data  */
        theDataSet.clear();
        theSectionMap.clear();
        theTotal.setZero();

        /* Iterate through the sections */
        final Iterator<TethysPieChartSection> myIterator = pData.sectionIterator();
        while (myIterator.hasNext()) {
            final TethysPieChartSection mySection = myIterator.next();

            /* Create the slice */
            theDataSet.setValue(mySection.getName(), mySection.getValue().doubleValue());

            /* Add to the section map */
            theSectionMap.put(mySection.getName(), mySection);
            theTotal.addAmount(mySection.getValue());
        }

        /* Declare changes */
        theChart.fireChartChanged();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TethysSwingPieChartExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("PieChart Demo");

            /* Create the UI */
            final TethysSwingPieChartExample myChart = new TethysSwingPieChartExample();
            myChart.updatePieChart(TethysPieChartData.createTestData());

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
}
