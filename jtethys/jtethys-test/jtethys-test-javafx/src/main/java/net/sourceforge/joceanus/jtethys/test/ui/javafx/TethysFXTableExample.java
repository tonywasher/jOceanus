/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.test.ui.TethysDataId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableUnitsColumn;

import java.util.Map;

/**
 * Test JavaFX Table Cells.
 */
public class TethysFXTableExample
        extends Application {
    /**
     * The GUI Factory.
     */
    private final TethysFXGuiFactory theGuiFactory = new TethysFXGuiFactory();

    /**
     * The TableView.
     */
    private final TethysFXTableManager<TethysDataId, TethysFXTableItem> theTable;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<?, ?> theHelper;
    
    /**
     * Constructor.
     */
    public TethysFXTableExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create test Data */
        final ObservableList<TethysFXTableItem> myData = FXCollections.observableArrayList(p -> new Observable[]
        { p.nameProperty() });
        myData.add(new TethysFXTableItem(theHelper, "Damage"));
        myData.add(new TethysFXTableItem(theHelper, "Tony"));
        myData.add(new TethysFXTableItem(theHelper, "Dave"));

        /* Create tableView */
        theTable = theGuiFactory.newTable();
        theTable.setItems(myData);
        theTable.setRepaintRowOnCommit(true);
        theTable.setComparator((l, r) -> l.nameProperty().getValue().compareTo(r.nameProperty().getValue()));
        theTable.setOnCommit(r -> r.incrementUpdates());

        /* Create the name column */
        final TethysFXTableStringColumn<TethysDataId, TethysFXTableItem> myNameColumn = theTable.declareStringColumn(TethysDataId.NAME);
        myNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
        myNameColumn.setOnCommit((r, v) -> r.nameProperty().setValue(v));
        myNameColumn.setRepaintColumnOnCommit(true);

        /* Create the date column */
        final TethysFXTableDateColumn<TethysDataId, TethysFXTableItem> myDateColumn = theTable.declareDateColumn(TethysDataId.DATE);
        myDateColumn.setCellValueFactory(p -> p.getValue().dateProperty());
        myDateColumn.setOnCommit((r, v) -> r.dateProperty().setValue(v));

        /* Create the short column */
        final TethysFXTableShortColumn<TethysDataId, TethysFXTableItem> myShortColumn = theTable.declareShortColumn(TethysDataId.SHORT);
        myShortColumn.setCellValueFactory(p -> p.getValue().shortProperty());
        myShortColumn.setOnCommit((r, v) -> r.shortProperty().setValue(v));
        myShortColumn.setValidator((v, r) -> v < 0
                                                   ? "Must be positive"
                                                   : null);

        /* Create the integer column */
        final TethysFXTableIntegerColumn<TethysDataId, TethysFXTableItem> myIntColumn = theTable.declareIntegerColumn(TethysDataId.INTEGER);
        myIntColumn.setCellValueFactory(p -> p.getValue().integerProperty());
        myIntColumn.setOnCommit((r, v) -> r.integerProperty().setValue(v));

        /* Create the long column */
        final TethysFXTableLongColumn<TethysDataId, TethysFXTableItem> myLongColumn = theTable.declareLongColumn(TethysDataId.LONG);
        myLongColumn.setCellValueFactory(p -> p.getValue().longProperty());
        myLongColumn.setOnCommit((r, v) -> r.longProperty().setValue(v));

        /* Create the money column */
        final TethysFXTableMoneyColumn<TethysDataId, TethysFXTableItem> myMoneyColumn = theTable.declareMoneyColumn(TethysDataId.MONEY);
        myMoneyColumn.setCellValueFactory(p -> p.getValue().moneyProperty());
        myMoneyColumn.setOnCommit((r, v) -> r.moneyProperty().setValue(v));

        /* Create the price column */
        final TethysFXTablePriceColumn<TethysDataId, TethysFXTableItem> myPriceColumn = theTable.declarePriceColumn(TethysDataId.PRICE);
        myPriceColumn.setCellValueFactory(p -> p.getValue().priceProperty());
        myPriceColumn.setOnCommit((r, v) -> r.priceProperty().setValue(v));

        /* Create the units column */
        final TethysFXTableUnitsColumn<TethysDataId, TethysFXTableItem> myUnitsColumn = theTable.declareUnitsColumn(TethysDataId.UNITS);
        myUnitsColumn.setCellValueFactory(p -> p.getValue().unitsProperty());
        myUnitsColumn.setOnCommit((r, v) -> r.unitsProperty().setValue(v));

        /* Create the rate column */
        final TethysFXTableRateColumn<TethysDataId, TethysFXTableItem> myRateColumn = theTable.declareRateColumn(TethysDataId.RATE);
        myRateColumn.setCellValueFactory(p -> p.getValue().rateProperty());
        myRateColumn.setOnCommit((r, v) -> r.rateProperty().setValue(v));

        /* Create the ratio column */
        final TethysFXTableRatioColumn<TethysDataId, TethysFXTableItem> myRatioColumn = theTable.declareRatioColumn(TethysDataId.RATIO);
        myRatioColumn.setCellValueFactory(p -> p.getValue().ratioProperty());
        myRatioColumn.setOnCommit((r, v) -> r.ratioProperty().setValue(v));

        /* Create the dilution column */
        final TethysFXTableDilutionColumn<TethysDataId, TethysFXTableItem> myDilutionColumn = theTable.declareDilutionColumn(TethysDataId.DILUTION);
        myDilutionColumn.setCellValueFactory(p -> p.getValue().dilutionProperty());
        myDilutionColumn.setOnCommit((r, v) -> r.dilutionProperty().setValue(v));

        /* Create the dilutedPrice column */
        final TethysFXTableDilutedPriceColumn<TethysDataId, TethysFXTableItem> myDilutedPriceColumn = theTable.declareDilutedPriceColumn(TethysDataId.DILUTEDPRICE);
        myDilutedPriceColumn.setCellValueFactory(p -> p.getValue().dilutedPriceProperty());
        myDilutedPriceColumn.setOnCommit((r, v) -> r.dilutedPriceProperty().setValue(v));

        /* Create the boolean column */
        final TethysFXTableIconColumn<Boolean, TethysDataId, TethysFXTableItem> myBoolColumn = theTable.declareIconColumn(TethysDataId.BOOLEAN, Boolean.class);
        myBoolColumn.setCellValueFactory(p -> p.getValue().booleanProperty());
        myBoolColumn.setOnCommit((r, v) -> r.booleanProperty().setValue(v));
        myBoolColumn.setName("B");
        final TethysIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        myBoolColumn.setIconMapSet(p -> myMapSet);

        /* Create the extra boolean column */
        final TethysFXTableIconColumn<Boolean, TethysDataId, TethysFXTableItem> myXtraBoolColumn = theTable.declareIconColumn(TethysDataId.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setCellValueFactory(p -> p.getValue().xtraBooleanProperty());
        myXtraBoolColumn.setOnCommit((r, v) -> r.xtraBooleanProperty().setValue(v));
        myXtraBoolColumn.setName("X");
        myXtraBoolColumn.setCellEditable(p -> p.booleanProperty().getValue());
        final Map<IconState, TethysIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE, TethysHelperIcon.CLOSEDTRUE);
        myXtraBoolColumn.setIconMapSet(p -> myMap.get(p.booleanProperty().getValue()
                                                                                     ? IconState.OPEN
                                                                                     : IconState.CLOSED));
        myXtraBoolColumn.setRepaintColumnId(TethysDataId.BOOLEAN);

        /* Create the scroll column */
        final TethysFXTableScrollColumn<String, TethysDataId, TethysFXTableItem> myScrollColumn = theTable.declareScrollColumn(TethysDataId.SCROLL, String.class);
        myScrollColumn.setCellValueFactory(p -> p.getValue().scrollProperty());
        myScrollColumn.setOnCommit((r, v) -> r.scrollProperty().setValue(v));
        myScrollColumn.setMenuConfigurator((r, c) -> theHelper.buildContextMenu(c));

        /* Create the list column */
        final TethysFXTableListColumn<TethysListId, TethysDataId, TethysFXTableItem> myListColumn = theTable.declareListColumn(TethysDataId.LIST);
        myListColumn.setCellValueFactory(p -> p.getValue().listProperty());
        myListColumn.setOnCommit((r, v) -> r.listProperty().setValue(v));
        myListColumn.setSelectables(r -> theHelper.buildSelectableList());

        /* Create the password column */
        final TethysFXTableCharArrayColumn<TethysDataId, TethysFXTableItem> myPassColumn = theTable.declareCharArrayColumn(TethysDataId.PASSWORD);
        myPassColumn.setCellValueFactory(p -> p.getValue().passwordProperty());
        myPassColumn.setOnCommit((r, v) -> r.passwordProperty().setValue(v));

        /* Create the updates column */
        final TethysFXTableIntegerColumn<TethysDataId, TethysFXTableItem> myUpdatesColumn = theTable.declareIntegerColumn(TethysDataId.UPDATES);
        myUpdatesColumn.setCellValueFactory(p -> p.getValue().updatesProperty());
        myUpdatesColumn.setName("U");
        myUpdatesColumn.setEditable(false);

        /* Set Disabled indicator */
        theTable.setChanged((c, r) -> c == TethysDataId.NAME && r.updatesProperty().getValue() > 0);
        theTable.setDisabled(r -> r.booleanProperty().getValue());
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
        /* Configure log4j */
        TethysLogConfig.configureLog4j();

        /* Create scene */
        final BorderPane myPane = new BorderPane();
        final Scene myScene = new Scene(myPane);
        theGuiFactory.registerScene(myScene);
        myPane.setCenter(theTable.getNode());
        pStage.setTitle("JavaFXTable Demo");
        pStage.setScene(myScene);
        pStage.show();
    }
}
