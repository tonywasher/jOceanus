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
package net.sourceforge.joceanus.jtethys.test.ui.swing;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.test.ui.TethysDataId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger LOGGER = LogManager.getLogger(TethysSwingTableExample.class);

    /**
     * The GUI Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory = new TethysSwingGuiFactory();

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
        final List<TethysSwingTableItem> myData = new ArrayList<>();
        myData.add(new TethysSwingTableItem(theHelper, "Damage"));
        myData.add(new TethysSwingTableItem(theHelper, "Tony"));
        myData.add(new TethysSwingTableItem(theHelper, "Dave"));

        /* Create tableView */
        theTable = theGuiFactory.newTable();
        theTable.setItems(myData);
        theTable.getNode().setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
        theTable.setRepaintRowOnCommit(true);
        theTable.setComparator((l, r) -> l.getName().compareTo(r.getName()));
        theTable.setOnCommit(TethysSwingTableItem::incrementUpdates);

        /* Create the name column */
        final TethysSwingTableStringColumn<TethysDataId, TethysSwingTableItem> myNameColumn = theTable.declareStringColumn(TethysDataId.NAME);
        myNameColumn.setCellValueFactory(TethysSwingTableItem::getName);
        myNameColumn.setOnCommit((r, v) -> r.setName(v));
        myNameColumn.setRepaintColumnOnCommit(true);

        /* Create the date column */
        final TethysSwingTableDateColumn<TethysDataId, TethysSwingTableItem> myDateColumn = theTable.declareDateColumn(TethysDataId.DATE);
        myDateColumn.setCellValueFactory(TethysSwingTableItem::getDate);
        myDateColumn.setOnCommit((r, v) -> r.setDate(v));

        /* Create the short column */
        final TethysSwingTableShortColumn<TethysDataId, TethysSwingTableItem> myShortColumn = theTable.declareShortColumn(TethysDataId.SHORT);
        myShortColumn.setCellValueFactory(TethysSwingTableItem::getShort);
        myShortColumn.setOnCommit((r, v) -> r.setShort(v));
        myShortColumn.setValidator((v, r) -> v < 0
                                                   ? "Must be positive"
                                                   : null);

        /* Create the integer column */
        final TethysSwingTableIntegerColumn<TethysDataId, TethysSwingTableItem> myIntColumn = theTable.declareIntegerColumn(TethysDataId.INTEGER);
        myIntColumn.setCellValueFactory(TethysSwingTableItem::getInteger);
        myIntColumn.setOnCommit((r, v) -> r.setInteger(v));

        /* Create the long column */
        final TethysSwingTableLongColumn<TethysDataId, TethysSwingTableItem> myLongColumn = theTable.declareLongColumn(TethysDataId.LONG);
        myLongColumn.setCellValueFactory(TethysSwingTableItem::getLong);
        myLongColumn.setOnCommit((r, v) -> r.setLong(v));

        /* Create the money column */
        final TethysSwingTableMoneyColumn<TethysDataId, TethysSwingTableItem> myMoneyColumn = theTable.declareMoneyColumn(TethysDataId.MONEY);
        myMoneyColumn.setCellValueFactory(TethysSwingTableItem::getMoney);
        myMoneyColumn.setOnCommit((r, v) -> r.setMoney(v));

        /* Create the price column */
        final TethysSwingTablePriceColumn<TethysDataId, TethysSwingTableItem> myPriceColumn = theTable.declarePriceColumn(TethysDataId.PRICE);
        myPriceColumn.setCellValueFactory(TethysSwingTableItem::getPrice);
        myPriceColumn.setOnCommit((r, v) -> r.setPrice(v));

        /* Create the units column */
        final TethysSwingTableUnitsColumn<TethysDataId, TethysSwingTableItem> myUnitsColumn = theTable.declareUnitsColumn(TethysDataId.UNITS);
        myUnitsColumn.setCellValueFactory(TethysSwingTableItem::getUnits);
        myUnitsColumn.setOnCommit((r, v) -> r.setUnits(v));

        /* Create the rate column */
        final TethysSwingTableRateColumn<TethysDataId, TethysSwingTableItem> myRateColumn = theTable.declareRateColumn(TethysDataId.RATE);
        myRateColumn.setCellValueFactory(TethysSwingTableItem::getRate);
        myRateColumn.setOnCommit((r, v) -> r.setRate(v));

        /* Create the ratio column */
        final TethysSwingTableRatioColumn<TethysDataId, TethysSwingTableItem> myRatioColumn = theTable.declareRatioColumn(TethysDataId.RATIO);
        myRatioColumn.setCellValueFactory(TethysSwingTableItem::getRatio);
        myRatioColumn.setOnCommit((r, v) -> r.setRatio(v));

        /* Create the dilution column */
        final TethysSwingTableDilutionColumn<TethysDataId, TethysSwingTableItem> myDilutionColumn = theTable.declareDilutionColumn(TethysDataId.DILUTION);
        myDilutionColumn.setCellValueFactory(TethysSwingTableItem::getDilution);
        myDilutionColumn.setOnCommit((r, v) -> r.setDilution(v));

        /* Create the dilutedPrice column */
        final TethysSwingTableDilutedPriceColumn<TethysDataId, TethysSwingTableItem> myDilutedPriceColumn = theTable.declareDilutedPriceColumn(TethysDataId.DILUTEDPRICE);
        myDilutedPriceColumn.setCellValueFactory(TethysSwingTableItem::getDilutedPrice);
        myDilutedPriceColumn.setOnCommit((r, v) -> r.setDilutedPrice(v));

        /* Create the boolean column */
        final TethysSwingTableIconColumn<Boolean, TethysDataId, TethysSwingTableItem> myBoolColumn = theTable.declareIconColumn(TethysDataId.BOOLEAN, Boolean.class);
        myBoolColumn.setCellValueFactory(TethysSwingTableItem::getBoolean);
        myBoolColumn.setOnCommit((r, v) -> r.setBoolean(v));
        myBoolColumn.setName("B");
        final TethysIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        myBoolColumn.setIconMapSet(p -> myMapSet);

        /* Create the extra boolean column */
        final TethysSwingTableIconColumn<Boolean, TethysDataId, TethysSwingTableItem> myXtraBoolColumn = theTable.declareIconColumn(TethysDataId.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setCellValueFactory(TethysSwingTableItem::getXtraBoolean);
        myXtraBoolColumn.setOnCommit((r, v) -> r.setXtraBoolean(v));
        myXtraBoolColumn.setName("X");
        myXtraBoolColumn.setCellEditable(TethysSwingTableItem::getBoolean);
        final Map<IconState, TethysIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE, TethysHelperIcon.CLOSEDTRUE);
        myXtraBoolColumn.setIconMapSet(p -> myMap.get(p.getBoolean()
                                                                     ? IconState.OPEN
                                                                     : IconState.CLOSED));
        myXtraBoolColumn.setRepaintColumnId(TethysDataId.BOOLEAN);

        /* Create the scroll column */
        final TethysSwingTableScrollColumn<String, TethysDataId, TethysSwingTableItem> myScrollColumn = theTable.declareScrollColumn(TethysDataId.SCROLL, String.class);
        myScrollColumn.setCellValueFactory(TethysSwingTableItem::getScroll);
        myScrollColumn.setOnCommit((r, v) -> r.setScroll(v));
        myScrollColumn.setMenuConfigurator((r, c) -> theHelper.buildContextMenu(c));

        /* Create the list column */
        final TethysSwingTableListColumn<TethysListId, TethysDataId, TethysSwingTableItem> myListColumn = theTable.declareListColumn(TethysDataId.LIST);
        myListColumn.setCellValueFactory(TethysSwingTableItem::getList);
        myListColumn.setOnCommit((r, v) -> r.setList(v));
        myListColumn.setSelectables(r -> theHelper.buildSelectableList());

        /* Create the password column */
        final TethysSwingTableCharArrayColumn<TethysDataId, TethysSwingTableItem> myPassColumn = theTable.declareCharArrayColumn(TethysDataId.PASSWORD);
        myPassColumn.setCellValueFactory(TethysSwingTableItem::getPassword);
        myPassColumn.setOnCommit((r, v) -> r.setPassword(v));

        /* Create the updates column */
        final TethysSwingTableIntegerColumn<TethysDataId, TethysSwingTableItem> myUpdatesColumn = theTable.declareIntegerColumn(TethysDataId.UPDATES);
        myUpdatesColumn.setCellValueFactory(TethysSwingTableItem::getUpdates);
        myUpdatesColumn.setName("U");
        myUpdatesColumn.setEditable(false);

        /* Set Disabled indicator */
        theTable.setChanged((c, r) -> c == TethysDataId.NAME && r.getUpdates() > 0);
        theTable.setDisabled(TethysSwingTableItem::getBoolean);
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
            final JFrame myFrame = new JFrame("SwingTable Demo");

            /* Configure log4j */
            TethysLogConfig.configureLog4j();

            /* Create the UI */
            final TethysSwingTableExample myExample = new TethysSwingTableExample();

            /* Build the panel */
            final JComponent myPanel = myExample.theTable.getNode();

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
