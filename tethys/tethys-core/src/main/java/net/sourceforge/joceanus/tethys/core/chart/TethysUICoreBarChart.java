/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.core.chart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalFormatter;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.chart.TethysUIBarChart;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.chart.TethysUICoreAreaChart.TethysUICoreAreaChartData;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Bar Chart.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a bar is selected
 * </ul>
 */
public abstract class TethysUICoreBarChart
        extends TethysUICoreComponent
        implements TethysUIBarChart {
    /**
     * The formatter.
     */
    private final OceanusDecimalFormatter theFormatter;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysUIBarChartDataSection> theSectionMap;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    protected TethysUICoreBarChart(final TethysUICoreFactory<?> pFactory) {
        /* Build standard fields */
        theId = pFactory.getNextId();
        theEventManager = new OceanusEventManager<>();
        theFormatter = pFactory.getDataFormatter().getDecimalFormatter();

        /* Create the section map */
        theSectionMap = new HashMap<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    protected OceanusDecimalFormatter getFormatter() {
        return theFormatter;
    }

    @Override
    public void updateBarChart(final TethysUIBarChartData pData) {
        /* reset the existing data  */
        resetData();

        /* Iterate through the sections */
        final Iterator<TethysUIBarChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysUIBarChartSeries myBase = myIterator.next();
            final String myName = myBase.getName();

            /* Iterate through the sections */
            final Iterator<TethysUIBarChartDataSection> mySectIterator = myBase.sectionIterator();
            while (mySectIterator.hasNext()) {
                final TethysUIBarChartDataSection mySection = mySectIterator.next();

                /* Add the section */
                createSection(myName, mySection);
            }
        }
    }

    /**
     * Reset chart data.
     */
    protected void resetData() {
        /* Clear existing data  */
        theSectionMap.clear();
    }

    /**
     * Add chart section.
     * @param pName the name of the bar
     * @param pSection the section to add
     */
    protected void createSection(final String pName,
                                 final TethysUIBarChartDataSection pSection) {
        final String myKey = pName + ":" + pSection.getReference();
        theSectionMap.put(myKey, pSection);
    }

    /**
     * Obtain tooltip for sectionName.
     * @param pName the section name
     * @return the tooltip
     */
    protected String getToolTip(final String pName) {
        final TethysUIBarChartDataSection mySection = theSectionMap.get(pName);
        final OceanusMoney myValue = mySection.getValue();
        return pName + " = " + theFormatter.formatMoney(myValue);
    }

    /**
     * handle selection.
     * @param pName the section name
     */
    protected void selectSection(final String pName) {
        final TethysUIBarChartDataSection mySection = theSectionMap.get(pName);
        theEventManager.fireEvent(TethysUIEvent.PRESSED, mySection);
    }

    /**
     * BarChart Data.
     */
    public static final class TethysUICoreBarChartData
            implements TethysUIBarChartData {
        /**
         * The XAxis default label.
         */
        static final String XAXIS_LABEL = TethysUICoreAreaChartData.XAXIS_LABEL;

        /**
         * The XAxis default label.
         */
        static final String YAXIS_LABEL = TethysUICoreAreaChartData.YAXIS_LABEL;

        /**
         * The Chart Title.
         */
        private final String theTitle;

        /**
         * The Chart XAxisLabel.
         */
        private String theXAxisLabel;

        /**
         * The Chart YAxisLabel.
         */
        private String theYAxisLabel;

        /**
         * The SeriesMap.
         */
        private final Map<String, TethysUIBarChartSeries> theSeriesMap;

        /**
         * Constructor.
         * @param pTitle the title
         */
        TethysUICoreBarChartData(final String pTitle) {
            this(pTitle, XAXIS_LABEL, YAXIS_LABEL);
        }

        /**
         * Constructor.
         * @param pTitle the title
         * @param pXAxisLabel the XAxis label
         * @param pYAxisLabel the YAxis label
         */
        private TethysUICoreBarChartData(final String pTitle,
                                         final String pXAxisLabel,
                                         final String pYAxisLabel) {
            /* Store parameters */
            theTitle = pTitle;
            theXAxisLabel = pXAxisLabel;
            theYAxisLabel = pYAxisLabel;

            /* Create map */
            theSeriesMap = new LinkedHashMap<>();
        }

        @Override
        public TethysUIBarChartData setXAxisLabel(final String pLabel) {
            theXAxisLabel = pLabel;
            return this;
        }

        @Override
        public TethysUIBarChartData setYAxisLabel(final String pLabel) {
            theYAxisLabel = pLabel;
            return this;
        }

        @Override
        public String getTitle() {
            return theTitle;
        }

        @Override
        public String getXAxisLabel() {
            return theXAxisLabel;
        }

        @Override
        public String getYAxisLabel() {
            return theYAxisLabel;
        }

        @Override
        public Iterator<TethysUIBarChartSeries> seriesIterator() {
            return theSeriesMap.values().iterator();
        }

        @Override
        public TethysUIBarChartSeries createSeries(final String pName) {
            final TethysUIBarChartSeries mySeries = new TethysUICoreBarChartSeries(pName);
            theSeriesMap.put(pName, mySeries);
            return mySeries;
        }
    }

    /**
     * The Series definition.
     */
    public static final class TethysUICoreBarChartSeries
            implements TethysUIBarChartSeries {
        /**
         * The name of the series.
         */
        private final String theName;

        /**
         * The sectionMap of the series.
         */
        private final Map<String, TethysUIBarChartDataSection> theSectionMap;

        /**
         * Constructor.
         * @param pName the name
         */
        TethysUICoreBarChartSeries(final String pName) {
            theName = pName;
            theSectionMap = new LinkedHashMap<>();
        }

        @Override
        public void addSection(final String pRef,
                               final OceanusMoney pValue) {
            addSection(pRef, pValue, theName + ":" + pRef);
        }

        @Override
        public void addSection(final String pRef,
                               final OceanusMoney pValue,
                               final Object pSource) {
            theSectionMap.put(pRef, new TethysUICoreBarChartDataSection(this, pRef, pValue, pSource));
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public Iterator<TethysUIBarChartDataSection> sectionIterator() {
            return theSectionMap.values().iterator();
        }
    }

    /**
     * The Data Section definition.
     */
    public static final class TethysUICoreBarChartDataSection
            implements TethysUIBarChartDataSection {
        /**
         * The series of the section.
         */
        private final TethysUIBarChartSeries theSeries;

        /**
         * The reference of the section.
         */
        private final String theRef;

        /**
         * The value of the section.
         */
        private final OceanusMoney theValue;

        /**
         * The source of the section.
         */
        private final Object theSource;

        /**
         * Constructor.
         * @param pSeries the series
         * @param pRef the reference
         * @param pValue the value
         * @param pSource the source
         */
        TethysUICoreBarChartDataSection(final TethysUIBarChartSeries pSeries,
                                        final String pRef,
                                        final OceanusMoney pValue,
                                       final Object pSource) {
            theSeries = pSeries;
            theRef = pRef;
            theValue = pValue;
            theSource = pSource;
        }

        @Override
        public TethysUIBarChartSeries getSeries() {
            return theSeries;
        }

        @Override
        public String getReference() {
            return theRef;
        }

        @Override
        public OceanusMoney getValue() {
            return theValue;
        }

        @Override
        public Object getSource() {
            return theSource;
        }
    }
}
