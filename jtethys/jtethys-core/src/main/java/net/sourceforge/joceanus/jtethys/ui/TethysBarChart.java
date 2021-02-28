/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysAreaChart.TethysAreaChartData;

/**
 * Bar Chart.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a bar is selected
 * </ul>
 */
public abstract class TethysBarChart
        implements TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The formatter.
     */
    private final TethysDecimalFormatter theFormatter;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The sectionMap.
     */
    private final Map<String, TethysBarChartDataSection> theSectionMap;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    protected TethysBarChart(final TethysGuiFactory pFactory) {
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
     * Obtain the formatter.
     * @return the formatter
     */
    protected TethysDecimalFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Update BarChart with data.
     * @param pData the data
     */
    public void updateBarChart(final TethysBarChartData pData) {
        /* reset the existing data  */
        resetData();

        /* Iterate through the sections */
        final Iterator<TethysBarChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysBarChartSeries myBase = myIterator.next();
            final String myName = myBase.getName();

            /* Iterate through the sections */
            final Iterator<TethysBarChartDataSection> mySectIterator = myBase.sectionIterator();
            while (mySectIterator.hasNext()) {
                final TethysBarChartDataSection mySection = mySectIterator.next();

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
                                 final TethysBarChartDataSection pSection) {
        final String myKey = pName + ":" + pSection.getReference();
        theSectionMap.put(myKey, pSection);
    }

    /**
     * Obtain tooltip for sectionName.
     * @param pName the section name
     * @return the tooltip
     */
    protected String getToolTip(final String pName) {
        final TethysBarChartDataSection mySection = theSectionMap.get(pName);
        final TethysMoney myValue = mySection.getValue();
        return pName + " = " + theFormatter.formatMoney(myValue);
    }

    /**
     * handle selection.
     * @param pName the section name
     */
    protected void selectSection(final String pName) {
        final TethysBarChartDataSection mySection = theSectionMap.get(pName);
        theEventManager.fireEvent(TethysUIEvent.PRESSED, mySection);
    }

    /**
     * BarChart Data.
     */
    public static class TethysBarChartData {
        /**
         * The XAxis default label.
         */
        static final String XAXIS_LABEL = TethysAreaChartData.XAXIS_LABEL;

        /**
         * The XAxis default label.
         */
        static final String YAXIS_LABEL = TethysAreaChartData.YAXIS_LABEL;

        /**
         * The Chart Title.
         */
        private final String theTitle;

        /**
         * The Chart XAxisLabel.
         */
        private final String theXAxisLabel;

        /**
         * The Chart YAxisLabel.
         */
        private final String theYAxisLabel;

        /**
         * The SeriesMap.
         */
        private final Map<String, TethysBarChartSeries> theSeriesMap;

        /**
         * Constructor.
         * @param pTitle the title
         */
        public TethysBarChartData(final String pTitle) {
            this(pTitle, XAXIS_LABEL, YAXIS_LABEL);
        }

        /**
         * Constructor.
         * @param pTitle the title
         * @param pXAxisLabel the XAxis label
         * @param pYAxisLabel the YAxis label
         */
        public TethysBarChartData(final String pTitle,
                                  final String pXAxisLabel,
                                  final String pYAxisLabel) {
            /* Store parameters */
            theTitle = pTitle;
            theXAxisLabel = pXAxisLabel;
            theYAxisLabel = pYAxisLabel;

            /* Create map */
            theSeriesMap = new LinkedHashMap<>();
        }

        /**
         * Obtain the title.
         * @return the title.
         */
        public String getTitle() {
            return theTitle;
        }

        /**
         * Obtain the XAxis label.
         * @return the label.
         */
        public String getXAxisLabel() {
            return theXAxisLabel;
        }

        /**
         * Obtain the YAxis label.
         * @return the label.
         */
        public String getYAxisLabel() {
            return theYAxisLabel;
        }

        /**
         * Obtain the barChart series.
         * @return the iterator
         */
        public Iterator<TethysBarChartSeries> seriesIterator() {
            return theSeriesMap.values().iterator();
        }

        /**
         * Add a series.
         * @param pName the name
         * @return the series
         */
        public TethysBarChartSeries createSeries(final String pName) {
            final TethysBarChartSeries mySeries = new TethysBarChartSeries(pName);
            theSeriesMap.put(pName, mySeries);
            return mySeries;
        }
    }

    /**
     * The Series definition.
     */
    public static final class TethysBarChartSeries {
        /**
         * The name of the series.
         */
        private final String theName;

        /**
         * The sectionMap of the series.
         */
        private final Map<String, TethysBarChartDataSection> theSectionMap;

        /**
         * Constructor.
         * @param pName the name
         */
        private TethysBarChartSeries(final String pName) {
            theName = pName;
            theSectionMap = new LinkedHashMap<>();
        }

        /**
         * Add a dataSection.
         * @param pRef the reference
         * @param pValue the value
         */
        public void addSection(final String pRef,
                               final TethysMoney pValue) {
            addSection(pRef, pValue, theName + ":" + pRef);
        }

        /**
         * Add a dataSection.
         * @param pRef the reference
         * @param pValue the value
         * @param pSource the source
         */
        public void addSection(final String pRef,
                               final TethysMoney pValue,
                               final Object pSource) {
            theSectionMap.put(pRef, new TethysBarChartDataSection(this, pRef, pValue, pSource));
        }

        /**
         * Obtain the name.
         * @return the name.
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the barChartSeries sections.
         * @return the iterator
         */
        public Iterator<TethysBarChartDataSection> sectionIterator() {
            return theSectionMap.values().iterator();
        }
    }

    /**
     * The Data Section definition.
     */
    public static final class TethysBarChartDataSection {
        /**
         * The series of the section.
         */
        private final TethysBarChartSeries theSeries;

        /**
         * The reference of the section.
         */
        private final String theRef;

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
         * @param pSeries the series
         * @param pRef the reference
         * @param pValue the value
         * @param pSource the source
         */
        private TethysBarChartDataSection(final TethysBarChartSeries pSeries,
                                          final String pRef,
                                          final TethysMoney pValue,
                                          final Object pSource) {
            theSeries = pSeries;
            theRef = pRef;
            theValue = pValue;
            theSource = pSource;
        }

        /**
         * Obtain the series.
         * @return the series.
         */
        public TethysBarChartSeries getSeries() {
            return theSeries;
        }

        /**
         * Obtain the reference.
         * @return the reference.
         */
        public String getReference() {
            return theRef;
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
