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
package net.sourceforge.joceanus.tethys.ui.core.chart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalFormatter;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.chart.TethysUIAreaChart;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;

/**
 * Area Chart.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a series is selected
 * </ul>
 */
public abstract class TethysUICoreAreaChart
        extends TethysUICoreComponent
        implements TethysUIAreaChart {
    /**
     * The angle for labels.
     */
    protected static final int LABEL_ANGLE = -45;

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
    private final Map<String, TethysUIAreaChartSeries> theSeriesMap;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    protected TethysUICoreAreaChart(final TethysUICoreFactory<?> pFactory) {
        /* Build standard fields */
        theId = pFactory.getNextId();
        theEventManager = new OceanusEventManager<>();
        theFormatter = pFactory.getDataFormatter().getDecimalFormatter();

        /* Create the section map */
        theSeriesMap = new HashMap<>();
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
    public void updateAreaChart(final TethysUIAreaChartData pData) {
        /* clear the data */
        resetData();

        /* Iterate through the sections */
        final Iterator<TethysUIAreaChartSeries> myIterator = pData.seriesIterator();
        while (myIterator.hasNext()) {
            final TethysUIAreaChartSeries myBase = myIterator.next();
            final String myKey = myBase.getName();
            theSeriesMap.put(myKey, myBase);

            /* Iterate through the sections */
            final Iterator<TethysUIAreaChartDataPoint> myPointIterator = myBase.pointIterator();
            while (myPointIterator.hasNext()) {
                final TethysUIAreaChartDataPoint myPoint = myPointIterator.next();

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
                                        TethysUIAreaChartDataPoint pPoint);

    /**
     * Obtain tooltip for series.
     * @param pName the series name
     * @param pValue the value
     * @return the tooltip
     */
    protected String getToolTip(final String pName,
                                final OceanusMoney pValue) {
        return pName  + " = " + theFormatter.formatMoney(pValue);
    }

    /**
     * handle selection.
     * @param pName the series name
     */
    protected void selectSeries(final String pName) {
        final TethysUIAreaChartSeries mySeries = theSeriesMap.get(pName);
        theEventManager.fireEvent(TethysUIEvent.PRESSED, mySeries);
    }

    /**
     * AreaChart Data.
     */
    public static final class TethysUICoreAreaChartData
            implements TethysUIAreaChartData {
        /**
         * The XAxis default label.
         */
        static final String XAXIS_LABEL = "Date";

        /**
         * The XAxis default label.
         */
        static final String YAXIS_LABEL = "Value";

        /**
         * The SeriesMap.
         */
        private final Map<String, TethysUIAreaChartSeries> theSeriesMap;

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
         * Constructor.
         * @param pTitle the title
         */
        TethysUICoreAreaChartData(final String pTitle) {
            this(pTitle, XAXIS_LABEL, YAXIS_LABEL);
        }

        /**
         * Constructor.
         * @param pTitle the title
         * @param pXAxisLabel the XAxis label
         * @param pYAxisLabel the YAxis label
         */
        private TethysUICoreAreaChartData(final String pTitle,
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
        public TethysUIAreaChartData setXAxisLabel(final String pLabel) {
            theXAxisLabel = pLabel;
            return this;
        }

        @Override
        public TethysUIAreaChartData setYAxisLabel(final String pLabel) {
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
        public Iterator<TethysUIAreaChartSeries> seriesIterator() {
            return theSeriesMap.values().iterator();
        }

        @Override
        public TethysUIAreaChartSeries createSeries(final String pName,
                                                    final Object pSource) {
            final TethysUIAreaChartSeries mySeries = new TethysUICoreAreaChartSeries(pName, pSource);
            theSeriesMap.put(pName, mySeries);
            return mySeries;
        }
    }

    /**
     * The Series definition.
     */
    public static final class TethysUICoreAreaChartSeries
            implements TethysUIAreaChartSeries {
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
        private final Map<OceanusDate, TethysUIAreaChartDataPoint> thePointMap;

        /**
         * Constructor.
         * @param pName the name
         * @param pSource the source
         */
        TethysUICoreAreaChartSeries(final String pName,
                                    final Object pSource) {
            theName = pName;
            theSource = pSource;
            thePointMap = new HashMap<>();
        }

        @Override
        public void addPoint(final OceanusDate pDate,
                             final OceanusMoney pValue) {
            thePointMap.put(pDate, new TethysUICoreAreaChartDataPoint(this, pDate, pValue));
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public Iterator<TethysUIAreaChartDataPoint> pointIterator() {
            return thePointMap.values().iterator();
        }

        @Override
        public Object getSource() {
            return theSource;
        }
    }

    /**
     * The Data Point definition.
     */
    public static final class TethysUICoreAreaChartDataPoint
            implements TethysUIAreaChartDataPoint {
        /**
         * The series of the point.
         */
        private final TethysUIAreaChartSeries theSeries;

        /**
         * The date of the point.
         */
        private final OceanusDate theDate;

        /**
         * The value of the point.
         */
        private final OceanusMoney theValue;

        /**
         * Constructor.
         * @param pSeries the series
         * @param pDate the date
         * @param pValue the value
         */
        TethysUICoreAreaChartDataPoint(final TethysUIAreaChartSeries pSeries,
                                       final OceanusDate pDate,
                                       final OceanusMoney pValue) {
            theSeries = pSeries;
            theDate = pDate;
            theValue = pValue;
        }

        @Override
        public TethysUIAreaChartSeries getSeries() {
            return theSeries;
        }

        @Override
        public OceanusDate getDate() {
            return theDate;
        }

        @Override
        public OceanusMoney getValue() {
            return theValue;
        }
    }
}
