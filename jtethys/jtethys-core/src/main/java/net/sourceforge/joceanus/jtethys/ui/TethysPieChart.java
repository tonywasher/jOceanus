/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Pie Chart.
 */
public abstract class TethysPieChart
        implements TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysPieChartSection> theSectionMap;

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
    protected TethysPieChart(final TethysGuiFactory pFactory) {
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

    /**
     * Update PieChart with data.
     * @param pData the data
     */
    public void updatePieChart(final TethysPieChartData pData) {
        /* reset the existing data  */
        resetData();

        /* Iterate through the sections */
        final Iterator<TethysPieChartSection> myIterator = pData.sectionIterator();
        while (myIterator.hasNext()) {
            final TethysPieChartSection mySection = myIterator.next();

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
    protected void createSlice(final TethysPieChartSection pSection) {
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
        final TethysPieChartSection mySection = theSectionMap.get(pName);
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
        final TethysPieChartSection mySection = theSectionMap.get(pName);
        theEventManager.fireEvent(TethysUIEvent.PRESSED, mySection);
    }

    /**
     * PieChart Data.
     */
    public static class TethysPieChartData {
        /**
         * The Chart Title.
         */
        private final String theTitle;

        /**
         * The SectionMap.
         */
        private final Map<String, TethysPieChartSection> theSectionMap;

        /**
         * Constructor.
         * @param pTitle the title
         */
        public TethysPieChartData(final String pTitle) {
            theTitle = pTitle;
            theSectionMap = new LinkedHashMap<>();
        }

        /**
         * Obtain the title.
         * @return the title.
         */
        public String getTitle() {
            return theTitle;
        }

        /**
         * Obtain the pieChart sections.
         * @return the iterator
         */
        public Iterator<TethysPieChartSection> sectionIterator() {
            return theSectionMap.values().iterator();
        }

        /**
         * Add a section.
         * @param pName the name
         * @param pValue the value
         */
        public void addSection(final String pName,
                               final TethysMoney pValue) {
            addSection(pName, pValue, pName);
        }

        /**
         * Add a section.
         * @param pName the name
         * @param pValue the value
         * @param pSource the source
         */
        public void addSection(final String pName,
                               final TethysMoney pValue,
                               final Object pSource) {
            final TethysPieChartSection mySection = new TethysPieChartSection(pName, pValue, pSource);
            theSectionMap.put(pName, mySection);
        }
    }

    /**
     * The Section definition.
     */
    public static final class TethysPieChartSection {
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
        private TethysPieChartSection(final String pName,
                                      final TethysMoney pValue,
                                      final Object pSource) {
            theName = pName;
            theValue = pValue;
            theSource = pSource;
        }

        /**
         * Obtain the name.
         * @return the name.
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the value.
         * @return the value.
         */
        public TethysMoney getValue() {
            return theValue;
        }

        /**
         * Obtain the source.
         * @return the source.
         */
        public Object getSource() {
            return theSource;
        }
    }
}
