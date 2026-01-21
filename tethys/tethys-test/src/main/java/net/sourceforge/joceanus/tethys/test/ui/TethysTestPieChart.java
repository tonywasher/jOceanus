/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.joceanus.tethys.test.ui;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIPieChart;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIPieChart.TethysUIPieChartData;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIPieChart.TethysUIPieChartSection;
import net.sourceforge.joceanus.tethys.api.control.TethysUITextArea;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;

/**
 * PieChart Test Example.
 */
public class TethysTestPieChart {
    /**
     * Pie Values.
     */
    private static final int[] PIE_VALUES = {213, 67, 42, 36};

    /**
     * The chart.
     */
    private final TethysUIPieChart theChart;

    /**
     * Constructor.
     *
     * @param pFactory  the factory
     * @param pTextArea the text area
     */
    TethysTestPieChart(final TethysUIFactory<?> pFactory,
                       final TethysUITextArea pTextArea) {
        /* Create chart */
        final TethysUIChartFactory myFactory = pFactory.chartFactory();
        theChart = myFactory.newPieChart();
        final TethysUIPieChartData myData = myFactory.newPieData("PieChart Demo");
        createTestPieData(myData);
        theChart.updatePieChart(myData);

        /* Add listener */
        theChart.getEventRegistrar().addEventListener(e -> {
            pTextArea.appendText((String) ((TethysUIPieChartSection) e.getDetails()).getSource());
            pTextArea.appendText("\n");
        });
    }

    /**
     * Obtain the component.
     *
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return theChart;
    }

    /**
     * Create Test Data.
     *
     * @param pData the dataSet
     */
    public static void createTestPieData(final TethysUIPieChartData pData) {
        int i = 0;
        pData.addSection("Banking", OceanusMoney.getWholeUnits(PIE_VALUES[i++]));
        pData.addSection("Cash", OceanusMoney.getWholeUnits(PIE_VALUES[i++]));
        pData.addSection("Portfolios", OceanusMoney.getWholeUnits(PIE_VALUES[i++]));
        pData.addSection("Loans", OceanusMoney.getWholeUnits(PIE_VALUES[i]));
    }
}
