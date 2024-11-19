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

import net.sourceforge.joceanus.tethys.decimal.TethysMoney;
import net.sourceforge.joceanus.tethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;

/**
 * Bar Chart.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a bar is selected
 * </ul>
 */
public interface TethysUIBarChart
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Update BarChart with data.
     * @param pData the data
     */
    void updateBarChart(TethysUIBarChartData pData);

    /**
     * BarChart Data.
     */
    interface TethysUIBarChartData {
        /**
         * Set the XAxis label.
         * @param pLabel the label.
         * @return the data
         */
        TethysUIBarChartData setXAxisLabel(String pLabel);

        /**
         * Set the YAxis label.
         * @param pLabel the label.
         * @return the data
         */
        TethysUIBarChartData setYAxisLabel(String pLabel);

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
         * Add a series.
         * @param pName the name
         * @return the series
         */
        TethysUIBarChartSeries createSeries(String pName);

        /**
         * Obtain the barChart series.
         * @return the iterator
         */
        Iterator<TethysUIBarChartSeries> seriesIterator();
    }

    /**
     * The Series definition.
     */
    interface TethysUIBarChartSeries {
        /**
         * Add a dataSection.
         * @param pRef the reference
         * @param pValue the value
         */
        void addSection(String pRef,
                        TethysMoney pValue);

        /**
         * Add a dataSection.
         * @param pRef the reference
         * @param pValue the value
         * @param pSource the source
         */
        void addSection(String pRef,
                        TethysMoney pValue,
                        Object pSource);

        /**
         * Obtain the name.
         * @return the name.
         */
        String getName();

        /**
         * Obtain the barChartSeries sections.
         * @return the iterator
         */
        Iterator<TethysUIBarChartDataSection> sectionIterator();
    }

    /**
     * The Data Section definition.
     */
    interface TethysUIBarChartDataSection {
        /**
         * Obtain the series.
         * @return the series.
         */
        TethysUIBarChartSeries getSeries();

        /**
         * Obtain the reference.
         * @return the reference.
         */
        String getReference();

        /**
         * Obtain the value.
         * @return the value.
         */
        TethysMoney getValue();

        /**
         * Obtain the source.
         * @return the source.
         */
        Object getSource();
    }
}
