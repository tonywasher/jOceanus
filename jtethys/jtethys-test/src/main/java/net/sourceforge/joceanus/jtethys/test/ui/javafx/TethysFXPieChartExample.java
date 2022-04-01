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
package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import net.sourceforge.joceanus.jtethys.ui.TethysPieChart.TethysPieChartData;
import net.sourceforge.joceanus.jtethys.ui.TethysPieChart.TethysPieChartSection;
import net.sourceforge.joceanus.jtethys.test.ui.TethysTestChartData;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXPieChart;

/**
 * JavaFX PieChart Example.
 */
public class TethysFXPieChartExample extends Application {
    /**
     * The Gui Factory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * The chart.
     */
    private final TethysFXPieChart theChart;

    /**
     * Constructor.
     */
    public TethysFXPieChartExample() {
        /* Create GUI Factory */
        theGuiFactory = new TethysFXGuiFactory();

        /* Create chart */
        theChart = theGuiFactory.newPieChart();
        final TethysPieChartData myData = TethysTestChartData.createTestPieData();
        theChart.updatePieChart(myData);

        /* Add listener */
        theChart.getEventRegistrar().addEventListener(e ->  System.out.println(((TethysPieChartSection) e.getDetails()).getSource()));
    }

    @Override
    public void start(final Stage pStage) {
        /* Create a Pane */
        final TethysFXBorderPaneManager myPane = theGuiFactory.newBorderPane();
        myPane.setCentre(theChart);

        /* Create scene */
        final Scene myScene = new Scene((Region) TethysFXNode.getNode(myPane));
        theGuiFactory.registerScene(myScene);
        pStage.setTitle("JavaFXPieChart Demo");
        pStage.setScene(myScene);
        pStage.show();
    }
}
