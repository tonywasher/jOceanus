package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TethysFXPieChartExample extends Application {
    /**
     * The chart.
     */
    private PieChart theChart;

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create the panel */
        buildPieChart();
        updatePieChart();

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        myPane.setCenter(theChart);
        pStage.setTitle("JavaFX PieChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    private void buildPieChart() {
        theChart = new PieChart();
        theChart.setLabelsVisible(true);
    }

    private void updatePieChart() {
        final PieChart.Data slice1 = new PieChart.Data("Banking", 213);
        final PieChart.Data slice2 = new PieChart.Data("Cash", 67);
        final PieChart.Data slice3 = new PieChart.Data("Portfolios", 36);
        final PieChart.Data slice4 = new PieChart.Data("Loans", 36);

        theChart.setTitle("Demo PieChart");
        final ObservableList<PieChart.Data> myData = theChart.getData();

        myData.clear();

        myData.add(slice1);
        myData.add(slice2);
        myData.add(slice3);
        myData.add(slice4);

        final double mySum = myData.stream().mapToDouble(PieChart.Data::getPieValue).sum();
        myData.forEach(d -> d.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println(d.getName())));
        myData.forEach(d -> {
            final double value = d.getPieValue();
            final int perCent = (int) (100 * (value / mySum));
            final Tooltip myTip = new Tooltip(d.getName() + " (" + value + ", " + perCent + "%)");
            Tooltip.install(d.getNode(), myTip);
        });
    }
}
