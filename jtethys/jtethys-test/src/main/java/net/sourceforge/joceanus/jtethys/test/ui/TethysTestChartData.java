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
     * Create Test Bar Data.
     * @return the test data
     */
    public static TethysBarChartData createTestBarData() {
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
     * Create Test Data.
     * @return the test data
     */
    public static TethysPieChartData createTestPieData() {
        final TethysPieChartData myData = new TethysPieChartData("Test PieChart");
        myData.addSection("Banking", TethysMoney.getWholeUnits(213));
        myData.addSection("Cash", TethysMoney.getWholeUnits(67));
        myData.addSection("Portfolios", TethysMoney.getWholeUnits(42));
        myData.addSection("Loans", TethysMoney.getWholeUnits(36));
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
