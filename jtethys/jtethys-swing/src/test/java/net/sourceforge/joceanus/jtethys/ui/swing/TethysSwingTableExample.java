/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableCell;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableStateIconCell;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStateIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableUnitsColumn;

/**
 * Test Swing Table Cells.
 */
public class TethysSwingTableExample {
    /**
     * Default icon width.
     */
    private static final int DEFAULT_ICONWIDTH = 16;

    /**
     * Scroll Width.
     */
    private static final int SCROLL_WIDTH = 1000;

    /**
     * Scroll Height.
     */
    private static final int SCROLL_HEIGHT = 120;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingTableExample.class);

    /**
     * The GUI Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory = new TethysSwingGuiFactory();

    /**
     * The TestData.
     */
    private final List<TethysSwingTableItem> theData;

    /**
     * The TableView.
     */
    private final TethysSwingTableManager<TethysDataId, TethysSwingTableItem> theTable;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<?, ?> theHelper;

    /**
     * Constructor.
     */
    public TethysSwingTableExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create test Data */
        theData = new ArrayList<>();
        theData.add(new TethysSwingTableItem(theHelper, "Damage"));
        theData.add(new TethysSwingTableItem(theHelper, "Tony"));
        theData.add(new TethysSwingTableItem(theHelper, "Dave"));

        /* Create tableView */
        theTable = theGuiFactory.newTable();
        theTable.setItems(theData);
        theTable.getNode().setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));

        /* Listen to the table requests */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theTable.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.CELLCREATE, this::handleCreate);
        myRegistrar.addEventListener(TethysUIEvent.CELLFORMAT, this::handleFormat);
        myRegistrar.addEventListener(TethysUIEvent.CELLPREEDIT, this::handlePreEdit);
        myRegistrar.addEventListener(TethysUIEvent.CELLPRECOMMIT, this::handlePreCommit);
        myRegistrar.addEventListener(TethysUIEvent.CELLCOMMITTED, this::handleCommit);

        /* Create the name column */
        TethysSwingTableStringColumn<TethysDataId, TethysSwingTableItem> myNameColumn = theTable.declareStringColumn(TethysDataId.NAME);
        myNameColumn.setCellValueFactory(p -> p.getName());
        myNameColumn.setCellCommitFactory((p, v) -> p.setName(v));

        /* Create the date column */
        TethysSwingTableDateColumn<TethysDataId, TethysSwingTableItem> myDateColumn = theTable.declareDateColumn(TethysDataId.DATE);
        myDateColumn.setCellValueFactory(p -> p.getDate());
        myDateColumn.setCellCommitFactory((p, v) -> p.setDate(v));

        /* Create the short column */
        TethysSwingTableShortColumn<TethysDataId, TethysSwingTableItem> myShortColumn = theTable.declareShortColumn(TethysDataId.SHORT);
        myShortColumn.setCellValueFactory(p -> p.getShort());
        myShortColumn.setCellCommitFactory((p, v) -> p.setShort(v));

        /* Create the integer column */
        TethysSwingTableIntegerColumn<TethysDataId, TethysSwingTableItem> myIntColumn = theTable.declareIntegerColumn(TethysDataId.INTEGER);
        myIntColumn.setCellValueFactory(p -> p.getInteger());
        myIntColumn.setCellCommitFactory((p, v) -> p.setInteger(v));

        /* Create the long column */
        TethysSwingTableLongColumn<TethysDataId, TethysSwingTableItem> myLongColumn = theTable.declareLongColumn(TethysDataId.LONG);
        myLongColumn.setCellValueFactory(p -> p.getLong());
        myLongColumn.setCellCommitFactory((p, v) -> p.setLong(v));

        /* Create the money column */
        TethysSwingTableMoneyColumn<TethysDataId, TethysSwingTableItem> myMoneyColumn = theTable.declareMoneyColumn(TethysDataId.MONEY);
        myMoneyColumn.setCellValueFactory(p -> p.getMoney());
        myMoneyColumn.setCellCommitFactory((p, v) -> p.setMoney(v));

        /* Create the price column */
        TethysSwingTablePriceColumn<TethysDataId, TethysSwingTableItem> myPriceColumn = theTable.declarePriceColumn(TethysDataId.PRICE);
        myPriceColumn.setCellValueFactory(p -> p.getPrice());
        myPriceColumn.setCellCommitFactory((p, v) -> p.setPrice(v));

        /* Create the units column */
        TethysSwingTableUnitsColumn<TethysDataId, TethysSwingTableItem> myUnitsColumn = theTable.declareUnitsColumn(TethysDataId.UNITS);
        myUnitsColumn.setCellValueFactory(p -> p.getUnits());
        myUnitsColumn.setCellCommitFactory((p, v) -> p.setUnits(v));

        /* Create the rate column */
        TethysSwingTableRateColumn<TethysDataId, TethysSwingTableItem> myRateColumn = theTable.declareRateColumn(TethysDataId.RATE);
        myRateColumn.setCellValueFactory(p -> p.getRate());
        myRateColumn.setCellCommitFactory((p, v) -> p.setRate(v));

        /* Create the ratio column */
        TethysSwingTableRatioColumn<TethysDataId, TethysSwingTableItem> myRatioColumn = theTable.declareRatioColumn(TethysDataId.RATIO);
        myRatioColumn.setCellValueFactory(p -> p.getRatio());
        myRatioColumn.setCellCommitFactory((p, v) -> p.setRatio(v));

        /* Create the dilution column */
        TethysSwingTableDilutionColumn<TethysDataId, TethysSwingTableItem> myDilutionColumn = theTable.declareDilutionColumn(TethysDataId.DILUTION);
        myDilutionColumn.setCellValueFactory(p -> p.getDilution());
        myDilutionColumn.setCellCommitFactory((p, v) -> p.setDilution(v));

        /* Create the dilutedPrice column */
        TethysSwingTableDilutedPriceColumn<TethysDataId, TethysSwingTableItem> myDilutedPriceColumn = theTable.declareDilutedPriceColumn(TethysDataId.DILUTEDPRICE);
        myDilutedPriceColumn.setCellValueFactory(p -> p.getDilutedPrice());
        myDilutedPriceColumn.setCellCommitFactory((p, v) -> p.setDilutedPrice(v));

        /* Create the boolean column */
        TethysSwingTableIconColumn<TethysDataId, TethysSwingTableItem, Boolean> myBoolColumn = theTable.declareIconColumn(TethysDataId.BOOLEAN, Boolean.class);
        myBoolColumn.setCellValueFactory(p -> p.getBoolean());
        myBoolColumn.setCellCommitFactory((p, v) -> p.setBoolean(v));
        myBoolColumn.setName("B");

        /* Create the extra boolean column */
        TethysSwingTableStateIconColumn<TethysDataId, TethysSwingTableItem, Boolean> myXtraBoolColumn = theTable.declareStateIconColumn(TethysDataId.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setCellValueFactory(p -> p.getXtraBoolean());
        myXtraBoolColumn.setCellCommitFactory((p, v) -> p.setXtraBoolean(v));
        myXtraBoolColumn.setName("X");

        /* Create the scroll column */
        TethysSwingTableScrollColumn<TethysDataId, TethysSwingTableItem, String> myScrollColumn = theTable.declareScrollColumn(TethysDataId.SCROLL, String.class);
        myScrollColumn.setCellValueFactory(p -> p.getScroll());
        myScrollColumn.setCellCommitFactory((p, v) -> p.setScroll(v));

        /* Create the list column */
        TethysSwingTableListColumn<TethysDataId, TethysSwingTableItem, TethysListId> myListColumn = theTable.declareListColumn(TethysDataId.LIST, TethysListId.class);
        myListColumn.setCellValueFactory(p -> p.getList());
        myListColumn.setCellCommitFactory((p, v) -> p.setList(v));

        /* Create the password column */
        TethysSwingTableCharArrayColumn<TethysDataId, TethysSwingTableItem> myPassColumn = theTable.declareCharArrayColumn(TethysDataId.PASSWORD);
        myPassColumn.setCellValueFactory(p -> p.getPassword());
        myPassColumn.setCellCommitFactory((p, v) -> p.setPassword(v));

        /* Create the updates column */
        TethysSwingTableIntegerColumn<TethysDataId, TethysSwingTableItem> myUpdatesColumn = theTable.declareIntegerColumn(TethysDataId.UPDATES);
        myUpdatesColumn.setCellValueFactory(p -> p.getUpdates());
        myUpdatesColumn.setName("U");

        /* Set Disabled indicator */
        theTable.setChanged((c, r) -> c == TethysDataId.NAME && r.getUpdates() > 0);
        theTable.setDisabled(r -> r.getBoolean());
    }

    /**
     * Handle create event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handleCreate(final TethysEvent<TethysUIEvent> pEvent) {
        /* Obtain the Cell */
        TethysSwingTableCell<TethysDataId, TethysSwingTableItem, ?> myCell = pEvent.getDetails(TethysSwingTableCell.class);

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
     * Handle format event=
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handleFormat(final TethysEvent<TethysUIEvent> pEvent) {
        /* Obtain the Cell */
        TethysSwingTableCell<TethysDataId, TethysSwingTableItem, ?> myCell = pEvent.getDetails(TethysSwingTableCell.class);

        /* If this is the extra Boolean field */
        if (TethysDataId.XTRABOOL.equals(myCell.getColumnId())) {
            /* Set correct state for extra Boolean */
            TethysSwingTableStateIconCell<?, ?, Boolean, IconState> myStateCell = (TethysSwingTableStateIconCell<?, ?, Boolean, IconState>) myCell;
            TethysSwingTableItem myRow = myCell.getActiveRow();
            myStateCell.setRenderMachineState(myRow.getBoolean()
                                                                 ? IconState.OPEN
                                                                 : IconState.CLOSED);
        }
    }

    /**
     * Handle preEdit event.
     * @param pEvent the event
     */
    @SuppressWarnings("unchecked")
    private void handlePreEdit(final TethysEvent<TethysUIEvent> pEvent) {
        /* Access column id */
        TethysDataId myId = getColumnId(pEvent);

        /* Make the updates column read-only */
        if (TethysDataId.UPDATES.equals(myId)) {
            pEvent.consume();
        }

        /* If this is the extra Boolean field */
        if (TethysDataId.XTRABOOL.equals(myId)) {
            /* Access details */
            TethysSwingTableCell<TethysDataId, TethysSwingTableItem, ?> myCell = pEvent.getDetails(TethysSwingTableCell.class);
            TethysSwingTableStateIconCell<?, ?, Boolean, IconState> myStateCell = (TethysSwingTableStateIconCell<?, ?, Boolean, IconState>) myCell;
            TethysSwingTableItem myRow = myCell.getActiveRow();

            /* Not editable if boolean is false */
            if (!myRow.getBoolean()) {
                pEvent.consume();

                /* else update the state */
            } else {
                /* Set correct state for extra Boolean */
                myStateCell.setEditMachineState(myRow.getBoolean()
                                                                   ? IconState.OPEN
                                                                   : IconState.CLOSED);
            }
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
        TethysSwingTableCell<TethysDataId, TethysSwingTableItem, ?> myCell = pEvent.getDetails(TethysSwingTableCell.class);
        TethysSwingTableItem myRow = myCell.getActiveRow();
        myRow.incrementUpdates();
        myCell.repaintColumnCell(TethysDataId.UPDATES);
        myCell.repaintColumnCell(TethysDataId.NAME);
        if (myCell.getColumnId().equals(TethysDataId.BOOLEAN)) {
            myCell.repaintColumnCell(TethysDataId.XTRABOOL);
        }
    }

    /**
     * Obtain the cell id.
     * @param pEvent the event
     * @return the Id
     */
    @SuppressWarnings("unchecked")
    private TethysDataId getColumnId(final TethysEvent<TethysUIEvent> pEvent) {
        TethysSwingTableCell<TethysDataId, ?, ?> myCell = pEvent.getDetails(TethysSwingTableCell.class);
        return myCell.getColumnId();
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("SwingTable Demo");

            /* Create the UI */
            TethysSwingTableExample myExample = new TethysSwingTableExample();

            /* Build the panel */
            JComponent myPanel = myExample.theTable.getNode();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
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
