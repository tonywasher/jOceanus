package net.sourceforge.joceanus.jtethys.test.ui.swing;

import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

public class TethysSwingPieChartExample {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingPieChartExample.class);

    /**
     * The dataSet.
     */
    private final DefaultPieDataset theDataSet;

    /**
     * The chart.
     */
    private final JFreeChart theChart;

    /**
     * The panel.
     */
    private final ChartPanel thePanel;

    TethysSwingPieChartExample() {
        theDataSet = new DefaultPieDataset();

        theChart = ChartFactory.createPieChart(
                "PieChart XDemo",   // chart title
                theDataSet,          // data
                true,             // include legend
                true,
                false);
        ((PiePlot) theChart.getPlot()).setStartAngle(0);

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
                if (entity instanceof PieSectionEntity) {
                    final PieSectionEntity section = (PieSectionEntity) entity;
                    System.out.println(section.getSectionKey());
                }
            }
        });
    }

    private void updatePieChart() {
        theDataSet.clear();

        theDataSet.setValue("Banking", 213);
        theDataSet.setValue("Cash", 67);
        theDataSet.setValue("Portfolios", 36);
        theDataSet.setValue("Loans", 36);

        theChart.setTitle("XXX");
        theChart.fireChartChanged();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(TethysSwingPieChartExample::createAndShowGUI);
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("PieChart Demo");

            /* Create the UI */
            final TethysSwingPieChartExample myChart = new TethysSwingPieChartExample();
            myChart.updatePieChart();

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
}
