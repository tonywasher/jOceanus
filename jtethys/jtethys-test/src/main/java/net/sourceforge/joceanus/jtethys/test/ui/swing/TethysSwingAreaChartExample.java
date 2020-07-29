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
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.test.ui.TethysTestChartData;
import net.sourceforge.joceanus.jtethys.ui.TethysAreaChart;
import net.sourceforge.joceanus.jtethys.ui.TethysAreaChart.TethysAreaChartSeries;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;

/**
 * Swing Area Chart Example.
 */
public final class TethysSwingAreaChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingAreaChartExample.class);

    /**
     * Private constructor.
     */
    private TethysSwingAreaChartExample() {
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
            final JFrame myFrame = new JFrame("Swing AreaChart Demo");

            /* Create GUI Factory */
            final TethysSwingGuiFactory myFactory = new TethysSwingGuiFactory();

            /* Create chart */
            final TethysAreaChart myChart = myFactory.newAreaChart();
            final TethysAreaChart.TethysAreaChartData myData = TethysTestChartData.createTestAreaData();
            myChart.updateAreaChart(myData);

            /* Add listener */
            myChart.getEventRegistrar().addEventListener(e ->  System.out.println(((TethysAreaChartSeries) e.getDetails()).getSource()));

            /* Attach the panel to the frame */
            myFrame.setContentPane(TethysSwingNode.getComponent(myChart));
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

