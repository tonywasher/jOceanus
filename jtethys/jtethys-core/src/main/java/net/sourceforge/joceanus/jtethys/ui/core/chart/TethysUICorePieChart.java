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
package net.sourceforge.joceanus.jtethys.ui.core.chart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIPieChart;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Pie Chart.
 */
public abstract class TethysUICorePieChart
        extends TethysUICoreComponent
        implements TethysUIPieChart {
    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysUIPieChartSection> theSectionMap;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The total.
     */
    private TethysMoney theTotal;

    /**
     * Constructor.
     * @param pFactory the Gui factory
     */
    protected TethysUICorePieChart(final TethysUICoreFactory<?> pFactory) {
        /* Build standard fields */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        theFormatter = pFactory.getDataFormatter().getDecimalFormatter();

        /* Create the section map */
        theSectionMap = new HashMap<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void updatePieChart(final TethysUIPieChartData pData) {
        /* reset the existing data  */
        resetData();

        /* Iterate through the sections */
        final Iterator<TethysUIPieChartSection> myIterator = pData.sectionIterator();
        while (myIterator.hasNext()) {
            final TethysUIPieChartSection mySection = myIterator.next();

            /* Create the slice */
            createSlice(mySection);
        }
    }

    /**
     * Reset chart data.
     */
    protected void resetData() {
        /* Clear existing data  */
        theSectionMap.clear();
        theTotal = null;
    }

    /**
     * Add chart section.
     * @param pSection the section to add
     */
    protected void createSlice(final TethysUIPieChartSection pSection) {
        /* Add to the section map */
        theSectionMap.put(pSection.getName(), pSection);

        /* Adjust the total */
        if (theTotal == null) {
            theTotal = new TethysMoney(pSection.getValue());
        } else {
            theTotal.addAmount(pSection.getValue());
        }
    }

    /**
     * Obtain tooltip for sectionName.
     * @param pName the section name
     * @return the tooltip
     */
    protected String getToolTip(final String pName) {
        final TethysUIPieChartSection mySection = theSectionMap.get(pName);
        final TethysMoney myValue = mySection.getValue();
        final TethysRate myPerCent = new TethysRate(myValue, theTotal);
        return pName + ": ("
                + theFormatter.formatMoney(myValue) + ", "
                + theFormatter.formatRate(myPerCent) + ")";
    }

    /**
     * handle selection.
     * @param pName the section name
     */
    protected void selectSection(final String pName) {
        final TethysUIPieChartSection mySection = theSectionMap.get(pName);
        theEventManager.fireEvent(TethysUIEvent.PRESSED, mySection);
    }

    /**
     * PieChart Data.
     */
    public static final class TethysUICorePieChartData
            implements TethysUIPieChartData {
        /**
         * The Chart Title.
         */
        private final String theTitle;

        /**
         * The SectionMap.
         */
        private final Map<String, TethysUIPieChartSection> theSectionMap;

        /**
         * Constructor.
         * @param pTitle the title
         */
        TethysUICorePieChartData(final String pTitle) {
            theTitle = pTitle;
            theSectionMap = new LinkedHashMap<>();
        }

        @Override
        public String getTitle() {
            return theTitle;
        }

        @Override
        public Iterator<TethysUIPieChartSection> sectionIterator() {
            return theSectionMap.values().iterator();
        }

        @Override
        public void addSection(final String pName,
                               final TethysMoney pValue,
                               final Object pSource) {
            final TethysUIPieChartSection mySection = new TethysUICorePieChartSection(pName, pValue, pSource);
            theSectionMap.put(pName, mySection);
        }
    }

    /**
     * The Section definition.
     */
    public static final class TethysUICorePieChartSection
            implements TethysUIPieChartSection {
        /**
         * The name of the section.
         */
        private final String theName;

        /**
         * The value of the section.
         */
        private final TethysMoney theValue;

        /**
         * The source of the section.
         */
        private final Object theSource;

        /**
         * Constructor.
         * @param pName the name
         * @param pValue the value
         * @param pSource the source
         */
        private TethysUICorePieChartSection(final String pName,
                                            final TethysMoney pValue,
                                            final Object pSource) {
            theName = pName;
            theValue = pValue;
            theSource = pSource;
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public TethysMoney getValue() {
            return theValue;
        }

        @Override
        public Object getSource() {
            return theSource;
        }
    }
}
