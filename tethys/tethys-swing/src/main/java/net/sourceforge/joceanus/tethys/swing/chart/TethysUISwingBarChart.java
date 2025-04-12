/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.core.chart.TethysUICoreBarChart;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.util.HexNumberFormat;
import org.jfree.data.category.DefaultCategoryDataset;

import java.text.FieldPosition;

/**
 * Swing BarChart.
 */
public class TethysUISwingBarChart
        extends TethysUICoreBarChart {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The dataSet.
     */
    private final DefaultCategoryDataset theDataSet;

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
    TethysUISwingBarChart(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the dataSet */
        theDataSet = new DefaultCategoryDataset();

        /* Create the chart */
        theChart = ChartFactory.createStackedBarChart(
                null,
                null,
                null,
                theDataSet,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        final CategoryPlot myPlot = (CategoryPlot) theChart.getPlot();
        final CategoryItemRenderer myRenderer = myPlot.getRenderer();
        myRenderer.setDefaultToolTipGenerator((pDataset, pSeries, pItem) -> {
            final String myKey = pDataset.getRowKey(pSeries) + ":" + pDataset.getColumnKey(pItem);
            return getToolTip(myKey);
        });
        final NumberAxis myYAxis = (NumberAxis) myPlot.getRangeAxis();
        myYAxis.setNumberFormatOverride(new MoneyFormat());

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
                if (entity instanceof CategoryItemEntity section) {
                    selectSection(section.getRowKey() + ":" + section.getColumnKey());
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
    public void updateBarChart(final TethysUIBarChartData pData) {
        /* Update underlying data */
        super.updateBarChart(pData);

        /* Set the chart title and Axis labels */
        theChart.setTitle(pData.getTitle());
        final CategoryPlot myPlot = (CategoryPlot) theChart.getPlot();
        final CategoryAxis myXAxis = myPlot.getDomainAxis();
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
    protected void createSection(final String pName,
                                 final TethysUIBarChartDataSection pSection) {
        /* Create section */
        theDataSet.addValue(pSection.getValue().doubleValue(), pName, pSection.getReference());

        /* Add to underlying data  */
        super.createSection(pName, pSection);
    }

    /**
     * Money Format class.
     */
    private final class MoneyFormat extends HexNumberFormat {
        private static final long serialVersionUID = -1151614975043008941L;

        @Override
        public StringBuffer format(final double pValue,
                                   final StringBuffer pBuffer,
                                   final FieldPosition pLoc) {
            final OceanusMoney myMoney = new OceanusMoney(Double.toString(pValue));
            return new StringBuffer(getFormatter().formatMoney(myMoney));
        }
    }
}
