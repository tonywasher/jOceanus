package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import java.time.LocalDate;
import java.time.Month;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import net.sourceforge.joceanus.jtethys.date.TethysDate;

public class TethysFXBarChartExample extends Application {
    /**
     * The chart.
     */
    private StackedBarChart<String, Number> theChart;

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
        buildBarChart();
        updateBarChart();

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        myPane.setCenter(theChart);
        pStage.setTitle("JavaFX BarChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    private void buildBarChart() {
        final CategoryAxis myXAxis = new CategoryAxis();
        myXAxis.setLabel("Date");
        final NumberAxis myYAxis = new NumberAxis();
        myYAxis.setLabel("Value");
        theChart = new StackedBarChart<>(myXAxis, myYAxis);
    }

    private void updateBarChart() {
        theChart.setTitle("Demo BarChart");
        final ObservableList<XYChart.Series<String, Number>> myData = theChart.getData();
        myData.clear();

        final XYChart.Series<String, Number> mySeries1 = new XYChart.Series<>();
        mySeries1.setName("Base");

        mySeries1.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2010, Month.APRIL, 5)), 567));
        mySeries1.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2011, Month.APRIL, 5)), 612));
        mySeries1.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2012, Month.APRIL, 5)), 800));
        mySeries1.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2013, Month.APRIL, 5)), 780));

        myData.add(mySeries1);

        final XYChart.Series<String, Number> mySeries2 = new XYChart.Series<>();
        mySeries2.setName("Extra");

        mySeries2.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2010, Month.APRIL, 5)), 167));
        mySeries2.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2011, Month.APRIL, 5)), 212));
        mySeries2.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2012, Month.APRIL, 5)), 100));
        mySeries2.getData().add(new XYChart.Data<>(dateToString(new TethysDate(2013, Month.APRIL, 5)), 280));

        myData.add(mySeries2);

        myData.forEach(s -> s.getData().forEach(d -> {
            final Number value = d.getYValue();
            d.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println(s.getName() + " " + d.getXValue()));
            final Tooltip myTip = new Tooltip("(" + s.getName() + " " + d.getXValue() + ") = " + value);
            Tooltip.install(d.getNode(), myTip);
        }));
    }

    private String dateToString(final TethysDate pDate) {
        return pDate.toString();
    }
}

