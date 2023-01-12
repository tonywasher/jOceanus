/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.chart;

import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;

/**
 * Pie Chart.
 */
public interface TethysUIPieChart
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Update PieChart with data.
     * @param pData the data
     */
    void updatePieChart(TethysUIPieChartData pData);

    /**
     * PieChart Data.
     */
    interface TethysUIPieChartData {
        /**
         * Obtain the title.
         * @return the title.
         */
        String getTitle();

        /**
         * Add a section.
         * @param pName the name
         * @param pValue the value
         */
        default void addSection(final String pName,
                                final TethysMoney pValue) {
            addSection(pName, pValue, pName);
        }

        /**
         * Add a section.
         * @param pName the name
         * @param pValue the value
         * @param pSource the source
         */
        void addSection(String pName,
                        TethysMoney pValue,
                        Object pSource);

        /**
         * Obtain the pieChart sections.
         * @return the iterator
         */
        Iterator<TethysUIPieChartSection> sectionIterator();
    }

    /**
     * The Section definition.
     */
    interface TethysUIPieChartSection {
        /**
         * Obtain the name.
         * @return the name.
         */
        String getName();

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
