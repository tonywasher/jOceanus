/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Area Chart.
 */
public abstract class TethysAreaChart
        implements TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The angle for labels.
     */
    protected static final int LABEL_ANGLE = -45;

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
    private final Map<String, TethysAreaChartSeries> theSeriesMap;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    protected TethysAreaChart(final TethysGuiFactory pFactory) {
        /* Build standard fields */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        theFormatter = pFactory.getDataFormatter().getDecimalFormatter();

        /* Create the section map */
        theSeriesMap = new HashMap<>();
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
     * Update AreaChart with data.
     * @param pData the data
     */
    public void updateAreaChart(final TethysAreaChartData pData) {
        /* clear the data */
        resetData();

        /* Iterate through the sections */
        final Iterator<TethysAreaChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysAreaChartSeries myBase = myIterator.next();
            final String myKey = myBase.getName();
            theSeriesMap.put(myKey, myBase);

            /* Iterate through the sections */
            final Iterator<TethysAreaChartDataPoint> myPointIterator = myBase.pointIterator();
            while (myPointIterator.hasNext()) {
                final TethysAreaChartDataPoint myPoint = myPointIterator.next();

                /* Add the section */
                createPoint(myKey, myPoint);
            }
        }
    }

    /**
     * Reset chart data.
     */
    protected void resetData() {
        /* Clear existing data  */
        theSeriesMap.clear();
    }

    /**
     * Add chart point.
     * @param pName the name of the series
     * @param pPoint the point to add
     */
    protected abstract void createPoint(String pName,
                                        TethysAreaChartDataPoint pPoint);

    /**
     * Obtain tooltip for series.
     * @param pName the series name
     * @param pValue the value
     * @return the tooltip
     */
    protected String getToolTip(final String pName,
                                final TethysMoney pValue) {
         return pName  + " = " + theFormatter.formatMoney(pValue);
    }

    /**
     * handle selection.
     * @param pName the series name
     */
    protected void selectSeries(final String pName) {
        final TethysAreaChartSeries mySeries = theSeriesMap.get(pName);
        theEventManager.fireEvent(TethysUIEvent.PRESSED, mySeries);
    }

    /**
     * AreaChart Data.
     */
    public static class TethysAreaChartData {
        /**
         * The XAxis default label.
         */
        static final String XAXIS_LABEL = "Date";

        /**
         * The XAxis default label.
         */
        static final String YAXIS_LABEL = "Value";

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
        private final Map<String, TethysAreaChartSeries> theSeriesMap;

        /**
         * Constructor.
         * @param pTitle the title
         */
        public TethysAreaChartData(final String pTitle) {
            this(pTitle, XAXIS_LABEL, YAXIS_LABEL);
        }

        /**
         * Constructor.
         * @param pTitle the title
         * @param pXAxisLabel the XAxis label
         * @param pYAxisLabel the YAxis label
         */
        public TethysAreaChartData(final String pTitle,
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
         * Obtain the areaChart series.
         * @return the iterator
         */
        public Iterator<TethysAreaChartSeries> seriesIterator() {
            return theSeriesMap.values().iterator();
        }

        /**
         * Create a series.
         * @param pName the name
         * @return the series
         */
        public TethysAreaChartSeries createSeries(final String pName) {
            return createSeries(pName, pName);
        }

        /**
         * Create a series.
         * @param pName the name
         * @param pSource the source
         * @return the series
         */
        public TethysAreaChartSeries createSeries(final String pName,
                                                  final Object pSource) {
            final TethysAreaChartSeries mySeries = new TethysAreaChartSeries(pName, pSource);
            theSeriesMap.put(pName, mySeries);
            return mySeries;
        }
    }

    /**
     * The Series definition.
     */
    public static final class TethysAreaChartSeries {
        /**
         * The name of the series.
         */
        private final String theName;

        /**
         * The source of the series.
         */
        private final Object theSource;

        /**
         * The pointMap of the series.
         */
        private final Map<TethysDate, TethysAreaChartDataPoint> thePointMap;

        /**
         * Constructor.
         * @param pName the name
         * @param pSource the source
         */
        private TethysAreaChartSeries(final String pName,
                                      final Object pSource) {
            theName = pName;
            theSource = pSource;
            thePointMap = new HashMap<>();
        }

        /**
         * Add a dataPoint.
         * @param pDate the date
         * @param pValue the value
         */
        public void addPoint(final TethysDate pDate,
                             final TethysMoney pValue) {
            thePointMap.put(pDate, new TethysAreaChartDataPoint(this, pDate, pValue));
        }

        /**
         * Obtain the name.
         * @return the name.
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the areaChartSeries points.
         * @return the iterator
         */
        public Iterator<TethysAreaChartDataPoint> pointIterator() {
            return thePointMap.values().iterator();
        }

        /**
         * Obtain the source.
         * @return the source.
         */
        public Object getSource() {
            return theSource;
        }
    }

    /**
     * The Data Point definition.
     */
    public static final class TethysAreaChartDataPoint {
        /**
         * The series of the point.
         */
        private final TethysAreaChartSeries theSeries;

        /**
         * The date of the point.
         */
        private final TethysDate theDate;

        /**
         * The value of the point.
         */
        private final TethysMoney theValue;

        /**
         * Constructor.
         * @param pSeries the series
         * @param pDate the date
         * @param pValue the value
         */
        private TethysAreaChartDataPoint(final TethysAreaChartSeries pSeries,
                                         final TethysDate pDate,
                                         final TethysMoney pValue) {
            theSeries = pSeries;
            theDate = pDate;
            theValue = pValue;
        }

        /**
         * Obtain the series.
         * @return the series.
         */
        public TethysAreaChartSeries getSeries() {
            return theSeries;
        }

        /**
         * Obtain the date.
         * @return the date.
         */
        public TethysDate getDate() {
            return theDate;
        }

        /**
         * Obtain the value.
         * @return the value.
         */
        public TethysMoney getValue() {
            return theValue;
        }
    }
}
