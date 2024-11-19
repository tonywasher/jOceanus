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
package net.sourceforge.joceanus.tethys.ui.api.chart;

import java.util.Iterator;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;

/**
 * Area Chart.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a series is selected
 * </ul>
 */
public interface TethysUIAreaChart
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Update AreaChart with data.
     * @param pData the data
     */
    void updateAreaChart(TethysUIAreaChartData pData);

    /**
     * AreaChart Data.
     */
    interface TethysUIAreaChartData {
        /**
         * Set the XAxis label.
         * @param pLabel the label.
         * @return the data
         */
        TethysUIAreaChartData setXAxisLabel(String pLabel);

        /**
         * Set the YAxis label.
         * @param pLabel the label.
         * @return the data
         */
        TethysUIAreaChartData setYAxisLabel(String pLabel);

        /**
         * Obtain the title.
         * @return the title.
         */
        String getTitle();

        /**
         * Obtain the XAxis label.
         * @return the label.
         */
        String getXAxisLabel();

        /**
         * Obtain the YAxis label.
         * @return the label.
         */
        String getYAxisLabel();

        /**
         * Create a series.
         * @param pName the name
         * @return the series
         */
        default TethysUIAreaChartSeries createSeries(final String pName) {
            return createSeries(pName, pName);
        }

        /**
         * Create a series.
         * @param pName the name
         * @param pSource the source
         * @return the series
         */
        TethysUIAreaChartSeries createSeries(String pName,
                                             Object pSource);

        /**
         * Obtain the areaChart series.
         * @return the iterator
         */
        Iterator<TethysUIAreaChartSeries> seriesIterator();
    }

    /**
     * The Series definition.
     */
    interface TethysUIAreaChartSeries {
        /**
         * Add a dataPoint.
         * @param pDate the date
         * @param pValue the value
         */
        void addPoint(OceanusDate pDate,
                      OceanusMoney pValue);

        /**
         * Obtain the name.
         * @return the name.
         */
        String getName();

        /**
         * Obtain the source.
         * @return the source.
         */
        Object getSource();

        /**
         * Obtain the areaChartSeries points.
         * @return the iterator
         */
        Iterator<TethysUIAreaChartDataPoint> pointIterator();
     }

    /**
     * The Data Point definition.
     */
    interface TethysUIAreaChartDataPoint {
        /**
         * Obtain the series.
         * @return the series.
         */
        TethysUIAreaChartSeries getSeries();

        /**
         * Obtain the date.
         * @return the date.
         */
        OceanusDate getDate();

        /**
         * Obtain the value.
         * @return the value.
         */
        OceanusMoney getValue();
    }
}
