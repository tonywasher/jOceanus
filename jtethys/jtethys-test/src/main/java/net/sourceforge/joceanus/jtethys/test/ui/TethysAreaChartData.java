/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.time.Month;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * AreaChart Data.
 */
public class TethysAreaChartData {
    /**
     * The Chart Title.
     */
    private final String theTitle;

    /**
     * The SeriesMap.
     */
    private final Map<String, TethysAreaChartSeries> theSeriesMap;

    /**
     * Constructor.
     * @param pTitle the title
     */
    public TethysAreaChartData(final String pTitle) {
        theTitle = pTitle;
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

    /**
     * Create Test Data.
     * @return the test data
     */
    public static TethysAreaChartData createTestData() {
        final TethysAreaChartData myData = new TethysAreaChartData("Test BarChart");

        final TethysAreaChartSeries mySeries1 = myData.createSeries("Base");
        mySeries1.addPoint(new TethysDate(2010, Month.APRIL, 5), TethysMoney.getWholeUnits(567));
        mySeries1.addPoint(new TethysDate(2011, Month.APRIL, 5), TethysMoney.getWholeUnits(612));
        mySeries1.addPoint(new TethysDate(2012, Month.APRIL, 5), TethysMoney.getWholeUnits(800));
        mySeries1.addPoint(new TethysDate(2013, Month.APRIL, 5), TethysMoney.getWholeUnits(780));

        final TethysAreaChartSeries mySeries2 = myData.createSeries("Extra");
        mySeries2.addPoint(new TethysDate(2010, Month.APRIL, 5), TethysMoney.getWholeUnits(167));
        mySeries2.addPoint(new TethysDate(2011, Month.APRIL, 5), TethysMoney.getWholeUnits(212));
        mySeries2.addPoint(new TethysDate(2012, Month.APRIL, 5), TethysMoney.getWholeUnits(100));
        mySeries2.addPoint(new TethysDate(2013, Month.APRIL, 5), TethysMoney.getWholeUnits(280));

        return myData;
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
