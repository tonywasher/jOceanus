/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;

/**
 * Test JavaFX Table Cells.
 */
public class TethysFXDataTableExample
        extends Application {
    /**
     * The TestData.
     */
    private final ObservableList<TestDataItem> theData;

    /**
     * The TableView.
     */
    private final TethysFXTableManager<String, TestDataItem> theTable;

    /**
     * Constructor.
     */
    public TethysFXDataTableExample() {
        /* Create test Data */
        theData = FXCollections.observableArrayList(new TestDataItem("Damage"),
                new TestDataItem("Tony"), new TestDataItem("Dave"));

        /* Create tableView */
        theTable = new TethysFXTableManager<>();
        theTable.setItems(theData);

        /* Create the name column */
        TethysFXTableStringColumn<String, TestDataItem> myStrColumn = theTable.declareStringColumn("Name");
        myStrColumn.setCellValueFactory(p -> p.getValue().nameProperty());

        /* Create the date column */
        TethysFXTableDateColumn<String, TestDataItem> myDateColumn = theTable.declareDateColumn("Date");
        myDateColumn.setCellValueFactory(p -> p.getValue().dateProperty());
    }

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
        // Node myMain = buildPanel();

        /* Create scene */
        Scene myScene = new Scene(new Group());
        ((Group) myScene.getRoot()).getChildren().addAll(theTable.getNode());
        pStage.setTitle("JavaFXTable Demo");
        // TethysFXGuiUtils.addStyleSheet(myScene);
        pStage.setScene(myScene);
        pStage.show();
    }

    /**
     * The Data Item.
     */
    public static class TestDataItem {
        /**
         * Name property.
         */
        private final StringProperty theName = new SimpleStringProperty(this, "Name");

        /**
         * Date property.
         */
        private final ObjectProperty<TethysDate> theDate = new SimpleObjectProperty<TethysDate>(this, "Date");

        /**
         * Constructor.
         * @param pName the Name
         */
        TestDataItem(final String pName) {
            theName.set(pName);
            theDate.set(new TethysDate());
        }

        /**
         * Obtain the name property.
         * @return the name property
         */
        public StringProperty nameProperty() {
            return theName;
        }

        /**
         * Obtain the date property.
         * @return the date property
         */
        public ObjectProperty<TethysDate> dateProperty() {
            return theDate;
        }
    }
}
