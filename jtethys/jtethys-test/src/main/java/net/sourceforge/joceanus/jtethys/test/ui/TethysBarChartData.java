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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.time.Month;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * BarChart Data.
 */
public class TethysBarChartData {
    /**
     * The Chart Title.
     */
    private final String theTitle;

    /**
     * The SeriesMap.
     */
    private final Map<String, TethysBarChartSeries> theSeriesMap;

    /**
     * Constructor.
     * @param pTitle the title
     */
    public TethysBarChartData(final String pTitle) {
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

    /**
     * Create Test Data.
     * @return the test data
     */
    public static TethysBarChartData createTestData() {
        final TethysBarChartData myData = new TethysBarChartData("Test BarChart");

        final TethysBarChartSeries mySeries1 = myData.createSeries("Base");
        mySeries1.addSection(dateToString(new TethysDate(2010, Month.APRIL, 5)), TethysMoney.getWholeUnits(567));
        mySeries1.addSection(dateToString(new TethysDate(2011, Month.APRIL, 5)), TethysMoney.getWholeUnits(612));
        mySeries1.addSection(dateToString(new TethysDate(2012, Month.APRIL, 5)), TethysMoney.getWholeUnits(800));
        mySeries1.addSection(dateToString(new TethysDate(2013, Month.APRIL, 5)), TethysMoney.getWholeUnits(780));

        final TethysBarChartSeries mySeries2 = myData.createSeries("Extra");
        mySeries2.addSection(dateToString(new TethysDate(2010, Month.APRIL, 5)), TethysMoney.getWholeUnits(167));
        mySeries2.addSection(dateToString(new TethysDate(2011, Month.APRIL, 5)), TethysMoney.getWholeUnits(212));
        mySeries2.addSection(dateToString(new TethysDate(2012, Month.APRIL, 5)), TethysMoney.getWholeUnits(100));
        mySeries2.addSection(dateToString(new TethysDate(2013, Month.APRIL, 5)), TethysMoney.getWholeUnits(280));

        return myData;
    }


    /**
     * Convert a date to a string.
     * @param pDate the date
     * @return the string representation
     */
    private static String dateToString(final TethysDate pDate) {
        return pDate.toString();
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
            theSectionMap = new HashMap<>();
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
