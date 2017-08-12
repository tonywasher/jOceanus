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
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.ui.TethysDataId;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableUnitsColumn;

/**
 * Test Swing Table Cells.
 */
public class TethysSwingTableExample {
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
        theTable.setRepaintRowOnCommit(true);
        theTable.setComparator((l, r) -> l.getName().compareTo(r.getName()));
        theTable.setOnCommit(r -> r.incrementUpdates());

        /* Create the name column */
        TethysSwingTableStringColumn<TethysDataId, TethysSwingTableItem> myNameColumn = theTable.declareStringColumn(TethysDataId.NAME);
        myNameColumn.setCellValueFactory(p -> p.getName());
        myNameColumn.setOnCommit((r, v) -> r.setName(v));
        myNameColumn.setRepaintColumnOnCommit(true);

        /* Create the date column */
        TethysSwingTableDateColumn<TethysDataId, TethysSwingTableItem> myDateColumn = theTable.declareDateColumn(TethysDataId.DATE);
        myDateColumn.setCellValueFactory(p -> p.getDate());
        myDateColumn.setOnCommit((r, v) -> r.setDate(v));

        /* Create the short column */
        TethysSwingTableShortColumn<TethysDataId, TethysSwingTableItem> myShortColumn = theTable.declareShortColumn(TethysDataId.SHORT);
        myShortColumn.setCellValueFactory(p -> p.getShort());
        myShortColumn.setOnCommit((r, v) -> r.setShort(v));
        myShortColumn.setValidator((v, r) -> v < 0
                                                   ? "Must be positive"
                                                   : null);

        /* Create the integer column */
        TethysSwingTableIntegerColumn<TethysDataId, TethysSwingTableItem> myIntColumn = theTable.declareIntegerColumn(TethysDataId.INTEGER);
        myIntColumn.setCellValueFactory(p -> p.getInteger());
        myIntColumn.setOnCommit((r, v) -> r.setInteger(v));

        /* Create the long column */
        TethysSwingTableLongColumn<TethysDataId, TethysSwingTableItem> myLongColumn = theTable.declareLongColumn(TethysDataId.LONG);
        myLongColumn.setCellValueFactory(p -> p.getLong());
        myLongColumn.setOnCommit((r, v) -> r.setLong(v));

        /* Create the money column */
        TethysSwingTableMoneyColumn<TethysDataId, TethysSwingTableItem> myMoneyColumn = theTable.declareMoneyColumn(TethysDataId.MONEY);
        myMoneyColumn.setCellValueFactory(p -> p.getMoney());
        myMoneyColumn.setOnCommit((r, v) -> r.setMoney(v));

        /* Create the price column */
        TethysSwingTablePriceColumn<TethysDataId, TethysSwingTableItem> myPriceColumn = theTable.declarePriceColumn(TethysDataId.PRICE);
        myPriceColumn.setCellValueFactory(p -> p.getPrice());
        myPriceColumn.setOnCommit((r, v) -> r.setPrice(v));

        /* Create the units column */
        TethysSwingTableUnitsColumn<TethysDataId, TethysSwingTableItem> myUnitsColumn = theTable.declareUnitsColumn(TethysDataId.UNITS);
        myUnitsColumn.setCellValueFactory(p -> p.getUnits());
        myUnitsColumn.setOnCommit((r, v) -> r.setUnits(v));

        /* Create the rate column */
        TethysSwingTableRateColumn<TethysDataId, TethysSwingTableItem> myRateColumn = theTable.declareRateColumn(TethysDataId.RATE);
        myRateColumn.setCellValueFactory(p -> p.getRate());
        myRateColumn.setOnCommit((r, v) -> r.setRate(v));

        /* Create the ratio column */
        TethysSwingTableRatioColumn<TethysDataId, TethysSwingTableItem> myRatioColumn = theTable.declareRatioColumn(TethysDataId.RATIO);
        myRatioColumn.setCellValueFactory(p -> p.getRatio());
        myRatioColumn.setOnCommit((r, v) -> r.setRatio(v));

        /* Create the dilution column */
        TethysSwingTableDilutionColumn<TethysDataId, TethysSwingTableItem> myDilutionColumn = theTable.declareDilutionColumn(TethysDataId.DILUTION);
        myDilutionColumn.setCellValueFactory(p -> p.getDilution());
        myDilutionColumn.setOnCommit((r, v) -> r.setDilution(v));

        /* Create the dilutedPrice column */
        TethysSwingTableDilutedPriceColumn<TethysDataId, TethysSwingTableItem> myDilutedPriceColumn = theTable.declareDilutedPriceColumn(TethysDataId.DILUTEDPRICE);
        myDilutedPriceColumn.setCellValueFactory(p -> p.getDilutedPrice());
        myDilutedPriceColumn.setOnCommit((r, v) -> r.setDilutedPrice(v));

        /* Create the boolean column */
        TethysSwingTableIconColumn<Boolean, TethysDataId, TethysSwingTableItem> myBoolColumn = theTable.declareIconColumn(TethysDataId.BOOLEAN, Boolean.class);
        myBoolColumn.setCellValueFactory(p -> p.getBoolean());
        myBoolColumn.setOnCommit((r, v) -> r.setBoolean(v));
        myBoolColumn.setName("B");
        TethysIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        myBoolColumn.setIconMapSet(p -> myMapSet);

        /* Create the extra boolean column */
        TethysSwingTableIconColumn<Boolean, TethysDataId, TethysSwingTableItem> myXtraBoolColumn = theTable.declareIconColumn(TethysDataId.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setCellValueFactory(p -> p.getXtraBoolean());
        myXtraBoolColumn.setOnCommit((r, v) -> r.setXtraBoolean(v));
        myXtraBoolColumn.setName("X");
        myXtraBoolColumn.setCellEditable(p -> p.getBoolean());
        Map<IconState, TethysIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE, TethysHelperIcon.CLOSEDTRUE);
        myXtraBoolColumn.setIconMapSet(p -> myMap.get(p.getBoolean()
                                                                     ? IconState.OPEN
                                                                     : IconState.CLOSED));
        myXtraBoolColumn.setRepaintColumnId(TethysDataId.BOOLEAN);

        /* Create the scroll column */
        TethysSwingTableScrollColumn<String, TethysDataId, TethysSwingTableItem> myScrollColumn = theTable.declareScrollColumn(TethysDataId.SCROLL, String.class);
        myScrollColumn.setCellValueFactory(p -> p.getScroll());
        myScrollColumn.setOnCommit((r, v) -> r.setScroll(v));
        myScrollColumn.setMenuConfigurator((r, c) -> theHelper.buildContextMenu(c));

        /* Create the list column */
        TethysSwingTableListColumn<TethysListId, TethysDataId, TethysSwingTableItem> myListColumn = theTable.declareListColumn(TethysDataId.LIST);
        myListColumn.setCellValueFactory(p -> p.getList());
        myListColumn.setOnCommit((r, v) -> r.setList(v));
        myListColumn.setSelectables(r -> theHelper.buildSelectableList());

        /* Create the password column */
        TethysSwingTableCharArrayColumn<TethysDataId, TethysSwingTableItem> myPassColumn = theTable.declareCharArrayColumn(TethysDataId.PASSWORD);
        myPassColumn.setCellValueFactory(p -> p.getPassword());
        myPassColumn.setOnCommit((r, v) -> r.setPassword(v));

        /* Create the updates column */
        TethysSwingTableIntegerColumn<TethysDataId, TethysSwingTableItem> myUpdatesColumn = theTable.declareIntegerColumn(TethysDataId.UPDATES);
        myUpdatesColumn.setCellValueFactory(p -> p.getUpdates());
        myUpdatesColumn.setName("U");
        myUpdatesColumn.setEditable(false);

        /* Set Disabled indicator */
        theTable.setChanged((c, r) -> c == TethysDataId.NAME && r.getUpdates() > 0);
        theTable.setDisabled(r -> r.getBoolean());
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
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
