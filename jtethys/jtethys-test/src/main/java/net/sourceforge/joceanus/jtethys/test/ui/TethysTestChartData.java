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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.time.Month;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.TethysAreaChart.TethysAreaChartData;
import net.sourceforge.joceanus.jtethys.ui.TethysAreaChart.TethysAreaChartSeries;
import net.sourceforge.joceanus.jtethys.ui.TethysBarChart.TethysBarChartData;
import net.sourceforge.joceanus.jtethys.ui.TethysBarChart.TethysBarChartSeries;
import net.sourceforge.joceanus.jtethys.ui.TethysPieChart.TethysPieChartData;

/**
 * Test chartData.
 */
public final class TethysTestChartData {
    /**
     * 2010 Yesr.
     */
    private static final int YEAR_2010 = 2010;

    /**
     * 2011 Year.
     */
    private static final int YEAR_2011 = 2011;

    /**
     * 2012 Year.
     */
    private static final int YEAR_2012 = 2012;

    /**
     * 2013 Year.
     */
    private static final int YEAR_2013 = 2013;

    /**
     * 5th day.
     */
    private static final int DAY_5 = 5;

    /**
     * Area Values.
     */
    private static final int[] AREA_VALUES = { 567, 612, 800, 780, 167, 212, 100, 280 };

    /**
     * Pie Values.
     */
    private static final int[] PIE_VALUES = { 213, 67, 42, 36 };

    /**
     * Private constructor.
     */
    private TethysTestChartData() {
    }

    /**
     * Create Test Area Data.
     * @return the test data
     */
    public static TethysAreaChartData createTestAreaData() {
        final TethysAreaChartData myData = new TethysAreaChartData("Test AreaChart");
        int i = 0;

        final TethysAreaChartSeries mySeries1 = myData.createSeries("Base");
        mySeries1.addPoint(new TethysDate(YEAR_2010, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addPoint(new TethysDate(YEAR_2011, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addPoint(new TethysDate(YEAR_2012, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addPoint(new TethysDate(YEAR_2013, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));

        final TethysAreaChartSeries mySeries2 = myData.createSeries("Extra");
        mySeries2.addPoint(new TethysDate(YEAR_2010, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addPoint(new TethysDate(YEAR_2011, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addPoint(new TethysDate(YEAR_2012, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addPoint(new TethysDate(YEAR_2013, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i]));

        return myData;
    }

    /**
     * Create Test Bar Data.
     * @return the test data
     */
    public static TethysBarChartData createTestBarData() {
        final TethysBarChartData myData = new TethysBarChartData("Test BarChart");
        int i = 0;

        final TethysBarChartSeries mySeries1 = myData.createSeries("Base");
        mySeries1.addSection(dateToString(new TethysDate(YEAR_2010, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addSection(dateToString(new TethysDate(YEAR_2011, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addSection(dateToString(new TethysDate(YEAR_2012, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addSection(dateToString(new TethysDate(YEAR_2013, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));

        final TethysBarChartSeries mySeries2 = myData.createSeries("Extra");
        mySeries2.addSection(dateToString(new TethysDate(YEAR_2010, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addSection(dateToString(new TethysDate(YEAR_2011, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addSection(dateToString(new TethysDate(YEAR_2012, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addSection(dateToString(new TethysDate(YEAR_2013, Month.APRIL, DAY_5)), TethysMoney.getWholeUnits(AREA_VALUES[i]));

        return myData;
    }

    /**
     * Create Test Data.
     * @return the test data
     */
    public static TethysPieChartData createTestPieData() {
        final TethysPieChartData myData = new TethysPieChartData("Test PieChart");
        int i = 0;
        myData.addSection("Banking", TethysMoney.getWholeUnits(PIE_VALUES[i++]));
        myData.addSection("Cash", TethysMoney.getWholeUnits(PIE_VALUES[i++]));
        myData.addSection("Portfolios", TethysMoney.getWholeUnits(PIE_VALUES[i++]));
        myData.addSection("Loans", TethysMoney.getWholeUnits(PIE_VALUES[i]));
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
}
