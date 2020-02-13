package net.sourceforge.joceanus.jtethys.test.ui.swing;

import java.awt.HeadlessException;
import java.time.Month;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeTableXYDataset;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

public class TethysSwingXYChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingXYChartExample.class);

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

    TethysSwingXYChartExample() {
        theDataSet = new TimeTableXYDataset();

        theChart = ChartFactory.createTimeSeriesChart(
                "XYChart XDemo",   // chart title
                "Date",
                "Value",
                theDataSet);
        final XYPlot myPlot = (XYPlot) theChart.getPlot();
        final StackedXYAreaRenderer2 myRenderer = new StackedXYAreaRenderer2();
        myRenderer.setDefaultToolTipGenerator((pDataset, pSeries, pItem) -> Double.toString(pDataset.getYValue(pSeries, pItem)));
        myPlot.setRenderer(0, myRenderer);

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
                    System.out.println(theDataSet.getSeriesKey(section.getSeriesIndex()));
                }
            }
        });
    }

    private void updateXYChart() {
        theDataSet.clear();

        theDataSet.add(dateToDay(new TethysDate(2010, Month.APRIL, 5)), 567, "Total");
        theDataSet.add(dateToDay(new TethysDate(2011, Month.APRIL, 5)), 612, "Total");
        theDataSet.add(dateToDay(new TethysDate(2012, Month.APRIL, 5)), 800, "Total");
        theDataSet.add(dateToDay(new TethysDate(2013, Month.APRIL, 5)), 780, "Total");

        theDataSet.add(dateToDay(new TethysDate(2010, Month.APRIL, 5)), 167, "Extra");
        theDataSet.add(dateToDay(new TethysDate(2011, Month.APRIL, 5)), 212, "Extra");
        theDataSet.add(dateToDay(new TethysDate(2012, Month.APRIL, 5)), 100, "Extra");
        theDataSet.add(dateToDay(new TethysDate(2013, Month.APRIL, 5)), 280, "Extra");

        theChart.setTitle("XXX");
        theChart.fireChartChanged();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TethysSwingXYChartExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("PieChart Demo");

            /* Create the UI */
            final TethysSwingXYChartExample myChart = new TethysSwingXYChartExample();
            myChart.updateXYChart();

            /* Attach the panel to the frame */
            myFrame.setContentPane(myChart.thePanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    private Day dateToDay(final TethysDate pDate) {
        return new Day(pDate.getDay(), pDate.getMonth(), pDate.getYear());
    }
}

