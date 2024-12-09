/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.swing.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTick;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.HexNumberFormat;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeTableXYDataset;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.core.chart.TethysUICoreAreaChart;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingNode;

/**
 * Swing areaChart.
 */
public class TethysUISwingAreaChart
        extends TethysUICoreAreaChart {
    /**
     * The angle for labels.
     */
    protected static final double LABEL_RADIANS = Math.PI * ((double) LABEL_ANGLE / 180);

    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The dataSet.
     */
    private final TimeTableXYDataset theDataSet;

    /**
     * The chart.
     */
    private final JFreeChart theChart;

    /**
     * The panel.
     */
    private final ChartPanel thePanel;

    /**
     * Constructor.
     * @param pFactory the Gui Factory
     */
    TethysUISwingAreaChart(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the dataSet */
        theDataSet = new TimeTableXYDataset();

        /* Create the chart */
        theChart = ChartFactory.createTimeSeriesChart(null, null, null, theDataSet);
        final XYPlot myPlot = (XYPlot) theChart.getPlot();
        final StackedXYAreaRenderer2 myRenderer = new StackedXYAreaRenderer2();
        myRenderer.setDefaultToolTipGenerator((pDataset, pSeries, pItem) -> {
            final OceanusMoney myValue = new OceanusMoney(pDataset.getY(pSeries, pItem).toString());
            return getToolTip(pDataset.getSeriesKey(pSeries).toString(), myValue);
        });
        myPlot.setRenderer(0, myRenderer);
        final NumberAxis myYAxis = (NumberAxis) myPlot.getRangeAxis();
        myYAxis.setNumberFormatOverride(new MoneyFormat());
        final TethysDateAxis myXAxis = new TethysDateAxis();
        myXAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));
        myXAxis.setVerticalTickLabels(true);
        myPlot.setDomainAxis(myXAxis);

        /* Create the panel */
        thePanel = new ChartPanel(theChart);
        thePanel.setOpaque(true);
        thePanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseMoved(final ChartMouseEvent e) {
                /* NoOp */
            }

            @Override
            public void chartMouseClicked(final ChartMouseEvent e) {
                final ChartEntity entity = e.getEntity();
                if (entity instanceof XYItemEntity) {
                    final XYItemEntity section = (XYItemEntity) entity;
                    selectSeries((String) theDataSet.getSeriesKey(section.getSeriesIndex()));
                }
            }
        });

        /* Create the node */
        theNode = new TethysUISwingNode(thePanel);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    @Override
    public void updateAreaChart(final TethysUIAreaChartData pData) {
        /* Update underlying data */
        super.updateAreaChart(pData);

        /* Set the chart title and Axis labels */
        theChart.setTitle(pData.getTitle());
        final XYPlot myPlot = (XYPlot) theChart.getPlot();
        final DateAxis myXAxis = (DateAxis) myPlot.getDomainAxis();
        myXAxis.setLabel(pData.getXAxisLabel());
        final NumberAxis myYAxis = (NumberAxis) myPlot.getRangeAxis();
        myYAxis.setLabel(pData.getYAxisLabel());

        /* Declare changes */
        theChart.fireChartChanged();
    }

    @Override
    protected void resetData() {
        /* Clear existing data  */
        theDataSet.clear();

        /* Clear underlying data  */
        super.resetData();
    }

    @Override
    protected void createPoint(final String pName,
                               final TethysUIAreaChartDataPoint pPoint) {
        /* Add the point */
        theDataSet.add(dateToDay(pPoint.getDate()), pPoint.getValue().doubleValue(), pName);
    }

    private static Day dateToDay(final OceanusDate pDate) {
        return new Day(pDate.getDay(), pDate.getMonth(), pDate.getYear());
    }

    /**
     * Money Format class.
     */
    private final class MoneyFormat extends HexNumberFormat {
        private static final long serialVersionUID = 1200795726700321267L;

        @Override
        public StringBuffer format(final double pValue,
                                   final StringBuffer pBuffer,
                                   final FieldPosition pLoc) {
            final OceanusMoney myMoney = new OceanusMoney(Double.toString(pValue));
            return new StringBuffer(getFormatter().formatMoney(myMoney));
        }
    }

    /**
     * Extended DateAxis.
     */
    private static final class TethysDateAxis extends DateAxis {
        private static final long serialVersionUID = 1976939393800292546L;

        @SuppressWarnings("unchecked")
        @Override
        protected List<Tick> refreshTicksHorizontal(final Graphics2D g2,
                                                    final Rectangle2D dataArea,
                                                    final RectangleEdge edge) {
            final List<Tick> ticks = super.refreshTicksHorizontal(g2, dataArea, edge);
            final List<Tick> ret = new ArrayList<>();
            for (Tick tick : ticks) {
                if (tick instanceof DateTick) {
                    final DateTick dateTick = (DateTick) tick;
                    ret.add(new DateTick(dateTick.getDate(), dateTick.getText(), dateTick.getTextAnchor(),
                            dateTick.getRotationAnchor(), LABEL_RADIANS));
                } else {
                    ret.add(tick);
                }
            }
            return ret;
        }

        @Override
        protected double findMaximumTickLabelHeight(final List ticks,
                                                    final Graphics2D g2,
                                                    final Rectangle2D drawArea,
                                                    final boolean vertical) {
            return super.findMaximumTickLabelHeight(ticks, g2, drawArea, vertical) * Math.sin(-LABEL_RADIANS);
        }
    }
}
