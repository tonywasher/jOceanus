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
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

public class TethysSwingBarChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingBarChartExample.class);

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

    TethysSwingBarChartExample() {
        theDataSet = new DefaultCategoryDataset();

        theChart = ChartFactory.createStackedBarChart(
                "BarChart XDemo",   // chart title
                "Date",
                "Value",
                theDataSet,          // data
                PlotOrientation.VERTICAL,
                true,             // include legend
                true,
                false);

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
                if (entity instanceof CategoryItemEntity) {
                    final CategoryItemEntity section = (CategoryItemEntity) entity;
                    System.out.println(section.getRowKey() + " " + section.getColumnKey());
                }
            }
        });
    }

    private void updateBarChart() {
        theDataSet.clear();

        theDataSet.addValue(567, "Base", dateToString(new TethysDate(2010, Month.APRIL, 5)));
        theDataSet.addValue(612, "Base", dateToString(new TethysDate(2011, Month.APRIL, 5)));
        theDataSet.addValue(800, "Base", dateToString(new TethysDate(2012, Month.APRIL, 5)));
        theDataSet.addValue(780, "Base", dateToString(new TethysDate(2013, Month.APRIL, 5)));

        theDataSet.addValue(167, "Extra", dateToString(new TethysDate(2010, Month.APRIL, 5)));
        theDataSet.addValue(212, "Extra", dateToString(new TethysDate(2011, Month.APRIL, 5)));
        theDataSet.addValue(100, "Extra", dateToString(new TethysDate(2012, Month.APRIL, 5)));
        theDataSet.addValue(280, "Extra", dateToString(new TethysDate(2013, Month.APRIL, 5)));

        theChart.setTitle("XXX");
        theChart.fireChartChanged();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TethysSwingBarChartExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("BarChart Demo");

            /* Create the UI */
            final TethysSwingBarChartExample myChart = new TethysSwingBarChartExample();
            myChart.updateBarChart();

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

    private String dateToString(final TethysDate pDate) {
        return pDate.toString();
    }
}
