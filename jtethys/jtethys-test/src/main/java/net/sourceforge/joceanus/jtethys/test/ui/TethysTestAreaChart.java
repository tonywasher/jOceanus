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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.time.Month;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIAreaChart;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIAreaChart.TethysUIAreaChartData;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIAreaChart.TethysUIAreaChartSeries;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUITextArea;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * AreaChart Test Example.
 */
public class TethysTestAreaChart {
    /**
     * 2010 Year.
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
    private static final int[] AREA_VALUES = {
        567, 612, 800, 780, 167, 212, 100, 280
    };

    /**
     * The chart.
     */
    private final TethysUIAreaChart theChart;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pTextArea the text area
     */
    TethysTestAreaChart(final TethysUIFactory<?> pFactory,
                        final TethysUITextArea pTextArea) {
        /* Create chart */
        final TethysUIChartFactory myFactory = pFactory.chartFactory();
        theChart = myFactory.newAreaChart();
        final TethysUIAreaChartData myData = myFactory.newAreaData("Test AreaChart");
        createTestAreaData(myData);
        theChart.updateAreaChart(myData);

        /* Add listener */
        theChart.getEventRegistrar().addEventListener(e -> {
            pTextArea.appendText((String) ((TethysUIAreaChartSeries) e.getDetails()).getSource());
            pTextArea.appendText("\n");
        });
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return theChart;
    }

    /**
     * Create Test Area Data.
     * @param pData the dataSet
     */
    private static void createTestAreaData(final TethysUIAreaChartData pData) {
        int i = 0;

        final TethysUIAreaChartSeries mySeries1 = pData.createSeries("Base");
        mySeries1.addPoint(new TethysDate(YEAR_2010, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addPoint(new TethysDate(YEAR_2011, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addPoint(new TethysDate(YEAR_2012, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries1.addPoint(new TethysDate(YEAR_2013, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));

        final TethysUIAreaChartSeries mySeries2 = pData.createSeries("Extra");
        mySeries2.addPoint(new TethysDate(YEAR_2010, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addPoint(new TethysDate(YEAR_2011, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addPoint(new TethysDate(YEAR_2012, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i++]));
        mySeries2.addPoint(new TethysDate(YEAR_2013, Month.APRIL, DAY_5), TethysMoney.getWholeUnits(AREA_VALUES[i]));
    }
}