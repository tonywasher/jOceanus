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
package net.sourceforge.joceanus.tethys.swing.chart;

import net.sourceforge.joceanus.tethys.api.chart.TethysUIAreaChart;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIBarChart;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIPieChart;
import net.sourceforge.joceanus.tethys.core.chart.TethysUICoreChartFactory;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * swing Control factory.
 */
public class TethysUISwingChartFactory
        extends TethysUICoreChartFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUISwingChartFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIAreaChart newAreaChart() {
        return new TethysUISwingAreaChart(theFactory);
    }

    @Override
    public TethysUIBarChart newBarChart() {
        return new TethysUISwingBarChart(theFactory);
    }

    @Override
    public TethysUIPieChart newPieChart() {
        return new TethysUISwingPieChart(theFactory);
    }
}
