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
package net.sourceforge.joceanus.tethys.javafx.chart;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.core.chart.TethysUICoreAreaChart;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * javaFX AreaChart.
 */
public class TethysUIFXAreaChart
        extends TethysUICoreAreaChart {
    /**
     * The border factor.
     */
    private static final double BORDER_FACTOR = 0.05;

    /**
     * The border factor.
     */
    private static final double TICK_FACTOR = 0.05;

    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The chart.
     */
    private final StackedAreaChart<Number, Number> theChart;

    /**
     * The series map.
     */
    private final Map<String, Series<Number, Number>> theSeries;

    /**
     * The minimun date.
     */
    private Number theMinimum;

    /**
     * The maximun date.
     */
    private Number theMaximum;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    TethysUIFXAreaChart(final TethysUICoreFactory<?> pFactory) {
        /* initialise underlying class */
        super(pFactory);

        /* Create chart */
        final NumberAxis myXAxis = new NumberAxis();
        myXAxis.setAutoRanging(false);
        myXAxis.setForceZeroInRange(false);
        myXAxis.setTickLabelRotation(LABEL_ANGLE);
        myXAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(final Number pValue) {
                return new OceanusDate(LocalDate.ofEpochDay(pValue.longValue())).toString();
            }

            @Override
            public Number fromString(final String pValue) {
                return null;
            }
        });
        final NumberAxis myYAxis = new NumberAxis();
        myYAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(final Number pValue) {
                return getFormatter().formatMoney(new OceanusMoney(pValue.toString()));
            }

            @Override
            public Number fromString(final String pValue) {
                return null;
            }
        });
        theChart = new StackedAreaChart<>(myXAxis, myYAxis);
        theChart.setHorizontalGridLinesVisible(false);
        theChart.setVerticalGridLinesVisible(false);

        /* Create the map */
        theSeries = new HashMap<>();

        /* Create Node */
        theNode = new TethysUIFXNode(theChart);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theChart.setDisable(!pEnabled);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theChart.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theChart.setPrefHeight(pHeight);
    }

    @Override
    public void updateAreaChart(final TethysUIAreaChartData pData) {
        /* Update underlying data */
        super.updateAreaChart(pData);

        /* Set the chart title and Axis labels */
        theChart.setTitle(pData.getTitle());

        /* Adjust XAxis */
        final NumberAxis myAxis = (NumberAxis) theChart.getXAxis();
        myAxis.setLabel(pData.getXAxisLabel());
        if (theMinimum != null) {
            final double myAdjust = getBorderAdjust();
            myAxis.setLowerBound(theMinimum.doubleValue() - myAdjust);
            myAxis.setUpperBound(theMaximum.doubleValue() + myAdjust);
            myAxis.setTickUnit(getTickCount());
        }

        /* Adjust Y Axis */
        theChart.getYAxis().setLabel(pData.getYAxisLabel());
    }

    @Override
    protected void resetData() {
        /* Clear existing data  */
        final ObservableList<Series<Number, Number>> myData = theChart.getData();
        myData.clear();

        /* Clear max/min dates */
        theMaximum = null;
        theMinimum = null;

        /* Clear underlying data  */
        super.resetData();
    }

    /**
     * Determine border adjustment.
     * @return the border adjustment
     */
    private double getBorderAdjust() {
        final double myRange = theMaximum.doubleValue() - theMinimum.doubleValue();
        return myRange * BORDER_FACTOR;
    }

    /**
     * Determine tick count.
     * @return the tick count
     */
    private long getTickCount() {
        final double myRange = theMaximum.doubleValue() - theMinimum.doubleValue();
        return (long) (myRange * TICK_FACTOR);
    }

    @Override
    protected void createPoint(final String pName,
                               final TethysUIAreaChartDataPoint pPoint) {
        /* Access the series */
        Series<Number, Number> mySeries = theSeries.get(pName);
        if (mySeries == null) {
            /* Create the series */
            mySeries = new Series<>();
            mySeries.setName(pName);
            final ObservableList<Series<Number, Number>> myData = theChart.getData();
            myData.add(mySeries);
            theSeries.put(pName, mySeries);

            /* Install click handler for node */
            mySeries.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> selectSeries(pName));
        }

        /* Add the point */
        final ObservableList<Data<Number, Number>> myPoints = mySeries.getData();
        final Data<Number, Number> myData = new Data<>(dateToEpoch(pPoint.getDate()), pPoint.getValue().doubleValue());
        myPoints.add(myData);
        final Node myNode = myData.getNode();

        /* Create the toolTip */
        final String myTooltip = getToolTip(pName, pPoint.getValue());
        Tooltip.install(myNode, new Tooltip(myTooltip));

        /* Adjust max/min */
        final Number myDate = myData.getXValue();
        if (theMinimum == null
                || theMinimum.longValue() > myDate.longValue()) {
            theMinimum = myDate;
        }
        if (theMaximum == null
                || theMaximum.longValue() < myDate.longValue()) {
            theMaximum = myDate;
        }
    }

    /**
     * Convert date to epoch.
     * @param pDate the date
     * @return the epoch
     */
    private static long dateToEpoch(final OceanusDate pDate) {
        return pDate.getDate().toEpochDay();
    }
}
