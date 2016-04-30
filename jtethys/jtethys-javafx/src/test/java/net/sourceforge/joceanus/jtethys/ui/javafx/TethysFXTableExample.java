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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataId;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableCellFactory.TethysFXTableCell;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStateIconColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableUnitsColumn;

/**
 * Test JavaFX Table Cells.
 */
public class TethysFXTableExample
        extends Application {
    /**
     * Default icon width.
     */
    private static final int DEFAULT_ICONWIDTH = 16;

    /**
     * The GUI Factory.
     */
    private final TethysFXGuiFactory theGuiFactory = new TethysFXGuiFactory();

    /**
     * The TestData.
     */
    private final ObservableList<TethysFXTableItem> theData;

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
        theData = FXCollections.observableArrayList(p -> new Observable[]
        { p.nameProperty(), p.xtraBooleanProperty() });
        theData.add(new TethysFXTableItem(theHelper, "Damage"));
        theData.add(new TethysFXTableItem(theHelper, "Tony"));
        theData.add(new TethysFXTableItem(theHelper, "Dave"));

        /* Create tableView */
        theTable = theGuiFactory.newTable();
        theTable.setItems(theData);

        /* Listen to preCommit requests */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theTable.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.CELLCREATE, this::handleCreate);
        myRegistrar.addEventListener(TethysUIEvent.CELLFORMAT, this::handleFormat);
        myRegistrar.addEventListener(TethysUIEvent.CELLPREEDIT, this::handlePreEdit);
        myRegistrar.addEventListener(TethysUIEvent.CELLPRECOMMIT, this::handlePreCommit);
        myRegistrar.addEventListener(TethysUIEvent.CELLCOMMITTED, this::handleCommit);

        /* Create the name column */
        TethysFXTableStringColumn<TethysDataId, TethysFXTableItem> myNameColumn = theTable.declareStringColumn(TethysDataId.NAME);
        myNameColumn.setCellValueFactory(p -> p.getValue().nameProperty());

        /* Create the date column */
        TethysFXTableDateColumn<TethysDataId, TethysFXTableItem> myDateColumn = theTable.declareDateColumn(TethysDataId.DATE);
        myDateColumn.setCellValueFactory(p -> p.getValue().dateProperty());

        /* Create the short column */
        TethysFXTableShortColumn<TethysDataId, TethysFXTableItem> myShortColumn = theTable.declareShortColumn(TethysDataId.SHORT);
        myShortColumn.setCellValueFactory(p -> p.getValue().shortProperty());

        /* Create the integer column */
        TethysFXTableIntegerColumn<TethysDataId, TethysFXTableItem> myIntColumn = theTable.declareIntegerColumn(TethysDataId.INTEGER);
        myIntColumn.setCellValueFactory(p -> p.getValue().integerProperty());

        /* Create the long column */
        TethysFXTableLongColumn<TethysDataId, TethysFXTableItem> myLongColumn = theTable.declareLongColumn(TethysDataId.LONG);
        myLongColumn.setCellValueFactory(p -> p.getValue().longProperty());

        /* Create the money column */
        TethysFXTableMoneyColumn<TethysDataId, TethysFXTableItem> myMoneyColumn = theTable.declareMoneyColumn(TethysDataId.MONEY);
        myMoneyColumn.setCellValueFactory(p -> p.getValue().moneyProperty());

        /* Create the price column */
        TethysFXTablePriceColumn<TethysDataId, TethysFXTableItem> myPriceColumn = theTable.declarePriceColumn(TethysDataId.PRICE);
        myPriceColumn.setCellValueFactory(p -> p.getValue().priceProperty());

        /* Create the units column */
        TethysFXTableUnitsColumn<TethysDataId, TethysFXTableItem> myUnitsColumn = theTable.declareUnitsColumn(TethysDataId.UNITS);
        myUnitsColumn.setCellValueFactory(p -> p.getValue().unitsProperty());

        /* Create the rate column */
        TethysFXTableRateColumn<TethysDataId, TethysFXTableItem> myRateColumn = theTable.declareRateColumn(TethysDataId.RATE);
        myRateColumn.setCellValueFactory(p -> p.getValue().rateProperty());

        /* Create the ratio column */
        TethysFXTableRatioColumn<TethysDataId, TethysFXTableItem> myRatioColumn = theTable.declareRatioColumn(TethysDataId.RATIO);
        myRatioColumn.setCellValueFactory(p -> p.getValue().ratioProperty());

        /* Create the dilution column */
        TethysFXTableDilutionColumn<TethysDataId, TethysFXTableItem> myDilutionColumn = theTable.declareDilutionColumn(TethysDataId.DILUTION);
        myDilutionColumn.setCellValueFactory(p -> p.getValue().dilutionProperty());

        /* Create the dilutedPrice column */
        TethysFXTableDilutedPriceColumn<TethysDataId, TethysFXTableItem> myDilutedPriceColumn = theTable.declareDilutedPriceColumn(TethysDataId.DILUTEDPRICE);
        myDilutedPriceColumn.setCellValueFactory(p -> p.getValue().dilutedPriceProperty());

        /* Create the boolean column */
        TethysFXTableIconColumn<TethysDataId, TethysFXTableItem, Boolean> myBoolColumn = theTable.declareIconColumn(TethysDataId.BOOLEAN, Boolean.class);
        myBoolColumn.setCellValueFactory(p -> p.getValue().booleanProperty());
        myBoolColumn.setName("B");

        /* Create the extra boolean column */
        TethysFXTableStateIconColumn<TethysDataId, TethysFXTableItem, Boolean> myXtraBoolColumn = theTable.declareStateIconColumn(TethysDataId.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setCellValueFactory(p -> p.getValue().xtraBooleanProperty());
        myXtraBoolColumn.setName("X");

        /* Create the scroll column */
        TethysFXTableScrollColumn<TethysDataId, TethysFXTableItem, String> myScrollColumn = theTable.declareScrollColumn(TethysDataId.SCROLL, String.class);
        myScrollColumn.setCellValueFactory(p -> p.getValue().scrollProperty());

        /* Create the list column */
        TethysFXTableListColumn<TethysDataId, TethysFXTableItem, TethysListId> myListColumn = theTable.declareListColumn(TethysDataId.LIST, TethysListId.class);
        myListColumn.setCellValueFactory(p -> p.getValue().listProperty());

        /* Create the password column */
        TethysFXTableCharArrayColumn<TethysDataId, TethysFXTableItem> myPassColumn = theTable.declareCharArrayColumn(TethysDataId.PASSWORD);
        myPassColumn.setCellValueFactory(p -> p.getValue().passwordProperty());

        /* Create the updates column */
        TethysFXTableIntegerColumn<TethysDataId, TethysFXTableItem> myUpdatesColumn = theTable.declareIntegerColumn(TethysDataId.UPDATES);
        myUpdatesColumn.setCellValueFactory(p -> p.getValue().updatesProperty());
        myUpdatesColumn.setName("U");

        /* Set Disabled indicator */
        theTable.setChanged((c, r) -> c == TethysDataId.NAME && r.updatesProperty().getValue() > 0);
        theTable.setDisabled(r -> r.booleanProperty().getValue());
    }

    /**
     * Handle create event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handleCreate(final TethysEvent<TethysUIEvent> pEvent) {
        /* Obtain the Cell */
        TethysFXTableCell<TethysDataId, TethysFXTableItem, ?> myCell = pEvent.getDetails(TethysFXTableCell.class);

        /* Configure static configuration for cells */
        switch (myCell.getColumnId()) {
            case BOOLEAN:
                TethysIconField<Boolean, ?, ?> myBoolField = (TethysIconField<Boolean, ?, ?>) myCell;
                TethysSimpleIconButtonManager<Boolean, ?, ?> myBoolMgr = myBoolField.getIconManager();
                myBoolMgr.setWidth(DEFAULT_ICONWIDTH);
                theHelper.buildSimpleIconState(myBoolMgr, TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
                break;
            case XTRABOOL:
                TethysStateIconField<Boolean, IconState, ?, ?> myStateField = (TethysStateIconField<Boolean, IconState, ?, ?>) myCell;
                TethysStateIconButtonManager<Boolean, IconState, ?, ?> myStateMgr = myStateField.getIconManager();
                myStateMgr.setWidth(DEFAULT_ICONWIDTH);
                theHelper.buildStateIconState(myStateMgr, TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE, TethysHelperIcon.CLOSEDTRUE);
                break;
            case SCROLL:
                TethysScrollField<String, ?, ?> myScrollField = (TethysScrollField<String, ?, ?>) myCell;
                theHelper.buildContextMenu(myScrollField.getScrollManager().getMenu());
                break;
            default:
                break;
        }
    }

    /**
     * Handle format event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handleFormat(final TethysEvent<TethysUIEvent> pEvent) {
        /* Obtain the Cell */
        TethysFXTableCell<TethysDataId, TethysFXTableItem, ?> myCell = pEvent.getDetails(TethysFXTableCell.class);

        /* If this is the extra Boolean field */
        if (TethysDataId.XTRABOOL.equals(myCell.getColumnId())) {
            /* Set correct state for extra Boolean */
            TethysStateIconField<Boolean, IconState, ?, ?> myStateField = (TethysStateIconField<Boolean, IconState, ?, ?>) myCell;
            TethysFXTableItem myRow = myCell.getActiveRow();
            myStateField.getIconManager().setMachineState(myRow.booleanProperty().get()
                                                                                        ? IconState.OPEN
                                                                                        : IconState.CLOSED);
        }
    }

    /**
     * Handle preEdit event.
     * @param pEvent the event
     */
    private void handlePreEdit(final TethysEvent<TethysUIEvent> pEvent) {
        /* Make the updates column read-only */
        if (TethysDataId.UPDATES.equals(getColumnId(pEvent))) {
            pEvent.consume();
        }
    }

    /**
     * Handle preCommit event.
     * @param pEvent the event
     */
    private void handlePreCommit(final TethysEvent<TethysUIEvent> pEvent) {
        /* Consume the event if value is invalid */
    }

    /**
     * Handle Commit event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handleCommit(final TethysEvent<TethysUIEvent> pEvent) {
        TethysFXTableCell<TethysDataId, TethysFXTableItem, ?> myCell = pEvent.getDetails(TethysFXTableCell.class);
        TethysFXTableItem myRow = myCell.getActiveRow();
        myRow.incrementUpdates();
        myCell.repaintColumnCell(TethysDataId.NAME);
        if (myCell.getColumnId().equals(TethysDataId.BOOLEAN)) {
            myCell.repaintCellRow();
        }
    }

    /**
     * Obtain the cell id.
     * @param pEvent the event
     * @return the Id
     */
    @SuppressWarnings("unchecked")
    private TethysDataId getColumnId(final TethysEvent<TethysUIEvent> pEvent) {
        TethysFXTableCell<TethysDataId, ?, ?> myCell = pEvent.getDetails(TethysFXTableCell.class);
        return myCell.getColumnId();
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
        /* Create scene */
        BorderPane myPane = new BorderPane();
        Scene myScene = new Scene(myPane);
        theGuiFactory.registerScene(myScene);
        myPane.setCenter(theTable.getNode());
        pStage.setTitle("JavaFXTable Demo");
        pStage.setScene(myScene);
        pStage.show();
    }
}
