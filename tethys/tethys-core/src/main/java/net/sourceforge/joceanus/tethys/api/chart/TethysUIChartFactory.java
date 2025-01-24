/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.chart;

import net.sourceforge.joceanus.tethys.api.chart.TethysUIAreaChart.TethysUIAreaChartData;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIBarChart.TethysUIBarChartData;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIPieChart.TethysUIPieChartData;

/**
 * Chart Factory.
 */
public interface TethysUIChartFactory {
    /**
     * Obtain a new area chart.
     * @return the new area chart
     */
    TethysUIAreaChart newAreaChart();

    /**
     * Create new area dataSet.
     * @param pTitle the title
     * @return the dataset
     */
    TethysUIAreaChartData newAreaData(String pTitle);

    /**
     * Obtain a new bar chart.
     * @return the new bar chart
     */
    TethysUIBarChart newBarChart();

    /**
     * Create new bar dataSet.
     * @param pTitle the title
     * @return the dataset
     */
    TethysUIBarChartData newBarData(String pTitle);

    /**
     * Obtain a new pie chart.
     * @return the new pie chart
     */
    TethysUIPieChart newPieChart();

    /**
     * Create new pie dataSet.
     * @param pTitle the title
     * @return the dataset
     */
    TethysUIPieChartData newPieData(String pTitle);
}
