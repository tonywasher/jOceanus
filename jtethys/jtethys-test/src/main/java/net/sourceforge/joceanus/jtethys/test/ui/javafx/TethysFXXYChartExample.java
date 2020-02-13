package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import java.time.LocalDate;
import java.time.Month;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import net.sourceforge.joceanus.jtethys.date.TethysDate;

public class TethysFXXYChartExample extends Application {
    /**
     * The chart.
     */
    private StackedAreaChart<Number, Number> theChart;

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
        buildXYChart();
        updateXYChart();

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        myPane.setCenter(theChart);
        pStage.setTitle("JavaFX AreaChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }

    private void buildXYChart() {
        final NumberAxis myXAxis = new NumberAxis();
        myXAxis.setLabel("Date");
        myXAxis.setAutoRanging(true);
        myXAxis.setForceZeroInRange(false);
        myXAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(final Number pValue) {
                return LocalDate.ofEpochDay(((Double) pValue).longValue()).toString();
            }

            @Override
            public Number fromString(final String pValue) {
                return null;
            }
        });
        final NumberAxis myYAxis = new NumberAxis();
        myYAxis.setLabel("Value");
        theChart = new StackedAreaChart<>(myXAxis, myYAxis);
    }

    private void updateXYChart() {
        theChart.setTitle("Demo AreaChart");
        final ObservableList<XYChart.Series<Number, Number>> myData = theChart.getData();
        myData.clear();

        final XYChart.Series<Number, Number> mySeries1 = new XYChart.Series<>();
        mySeries1.setName("Total");

        mySeries1.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2010, Month.APRIL, 5)), 567));
        mySeries1.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2011, Month.APRIL, 5)), 612));
        mySeries1.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2012, Month.APRIL, 5)), 800));
        mySeries1.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2013, Month.APRIL, 5)), 780));

        myData.add(mySeries1);

        final XYChart.Series<Number, Number> mySeries2 = new XYChart.Series<>();
        mySeries2.setName("Extra");

        mySeries2.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2010, Month.APRIL, 5)), 167));
        mySeries2.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2011, Month.APRIL, 5)), 212));
        mySeries2.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2012, Month.APRIL, 5)), 100));
        mySeries2.getData().add(new XYChart.Data<>(dateToEpoch(new TethysDate(2013, Month.APRIL, 5)), 280));

        myData.add(mySeries2);

        myData.forEach(s -> s.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println(s.getName())));
        myData.forEach(s -> s.getData().forEach(d -> {
            final Number value = d.getYValue();
            final Tooltip myTip = new Tooltip("" + value);
            Tooltip.install(d.getNode(), myTip);
        }));
    }

    private long dateToEpoch(final TethysDate pDate) {
        return pDate.getDate().toEpochDay();
    }
}
