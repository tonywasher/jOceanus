/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.test.ui;

import net.sourceforge.joceanus.tethys.test.ui.TethysTestHelper.TethysIconState;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableCharArrayColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableDateColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableIconColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableIntegerColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableListColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableLongColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableMoneyColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITablePriceColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRateColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRatioColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableScrollColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableShortColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableStringColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableUnitsColumn;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Test Swing Table Cells.
 */
public class TethysTestTable {
    /**
     * Scroll Width.
     */
    private static final int SCROLL_WIDTH = 1000;

    /**
     * Scroll Height.
     */
    private static final int SCROLL_HEIGHT = 120;

    /**
     * The TableView.
     */
    private final TethysUITableManager<TethysTestDataId, TethysTestTableItem> theTable;

    /**
     * The Test helper.
     */
    private final TethysTestHelper theHelper;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysTestTable(final TethysUIFactory<?> pFactory) {
        /* Create helper */
        theHelper = new TethysTestHelper(pFactory);

        /* Create test Data */
        final List<TethysTestTableItem> myData = new ArrayList<>();
        myData.add(new TethysTestTableItem(theHelper, "Damage"));
        myData.add(new TethysTestTableItem(theHelper, "Tony"));
        myData.add(new TethysTestTableItem(theHelper, "Dave"));

        /* Create tableView */
        theTable = pFactory.tableFactory().newTable();
        theTable.setItems(myData);
        theTable.setPreferredHeight(SCROLL_HEIGHT);
        theTable.setPreferredWidth(SCROLL_WIDTH);
        theTable.setRepaintRowOnCommit(true);
        theTable.setComparator(Comparator.comparing(TethysTestTableItem::getName));
        theTable.setOnCommit(TethysTestTableItem::incrementUpdates);

        /* Create the name column */
        final TethysUITableStringColumn<TethysTestDataId, TethysTestTableItem> myNameColumn = theTable.declareStringColumn(TethysTestDataId.NAME);
        myNameColumn.setCellValueFactory(TethysTestTableItem::getName);
        myNameColumn.setOnCommit(TethysTestTableItem::setName);
        myNameColumn.setRepaintColumnOnCommit(true);

        /* Create the date column */
        final TethysUITableDateColumn<TethysTestDataId, TethysTestTableItem> myDateColumn = theTable.declareDateColumn(TethysTestDataId.DATE);
        myDateColumn.setCellValueFactory(TethysTestTableItem::getDate);
        myDateColumn.setOnCommit(TethysTestTableItem::setDate);

        /* Create the short column */
        final TethysUITableShortColumn<TethysTestDataId, TethysTestTableItem> myShortColumn = theTable.declareShortColumn(TethysTestDataId.SHORT);
        myShortColumn.setCellValueFactory(TethysTestTableItem::getShort);
        myShortColumn.setOnCommit(TethysTestTableItem::setShort);
        myShortColumn.setValidator((v, r) -> v < 0
                ? "Must be positive"
                : null);

        /* Create the integer column */
        final TethysUITableIntegerColumn<TethysTestDataId, TethysTestTableItem> myIntColumn = theTable.declareIntegerColumn(TethysTestDataId.INTEGER);
        myIntColumn.setCellValueFactory(TethysTestTableItem::getInteger);
        myIntColumn.setOnCommit(TethysTestTableItem::setInteger);

        /* Create the long column */
        final TethysUITableLongColumn<TethysTestDataId, TethysTestTableItem> myLongColumn = theTable.declareLongColumn(TethysTestDataId.LONG);
        myLongColumn.setCellValueFactory(TethysTestTableItem::getLong);
        myLongColumn.setOnCommit(TethysTestTableItem::setLong);

        /* Create the money column */
        final TethysUITableMoneyColumn<TethysTestDataId, TethysTestTableItem> myMoneyColumn = theTable.declareMoneyColumn(TethysTestDataId.MONEY);
        myMoneyColumn.setCellValueFactory(TethysTestTableItem::getMoney);
        myMoneyColumn.setOnCommit(TethysTestTableItem::setMoney);

        /* Create the price column */
        final TethysUITablePriceColumn<TethysTestDataId, TethysTestTableItem> myPriceColumn = theTable.declarePriceColumn(TethysTestDataId.PRICE);
        myPriceColumn.setCellValueFactory(TethysTestTableItem::getPrice);
        myPriceColumn.setOnCommit(TethysTestTableItem::setPrice);

        /* Create the units column */
        final TethysUITableUnitsColumn<TethysTestDataId, TethysTestTableItem> myUnitsColumn = theTable.declareUnitsColumn(TethysTestDataId.UNITS);
        myUnitsColumn.setCellValueFactory(TethysTestTableItem::getUnits);
        myUnitsColumn.setOnCommit(TethysTestTableItem::setUnits);

        /* Create the rate column */
        final TethysUITableRateColumn<TethysTestDataId, TethysTestTableItem> myRateColumn = theTable.declareRateColumn(TethysTestDataId.RATE);
        myRateColumn.setCellValueFactory(TethysTestTableItem::getRate);
        myRateColumn.setOnCommit(TethysTestTableItem::setRate);

        /* Create the ratio column */
        final TethysUITableRatioColumn<TethysTestDataId, TethysTestTableItem> myRatioColumn = theTable.declareRatioColumn(TethysTestDataId.RATIO);
        myRatioColumn.setCellValueFactory(TethysTestTableItem::getRatio);
        myRatioColumn.setOnCommit(TethysTestTableItem::setRatio);

        /* Create the boolean column */
        final TethysUITableIconColumn<Boolean, TethysTestDataId, TethysTestTableItem> myBoolColumn = theTable.declareIconColumn(TethysTestDataId.BOOLEAN, Boolean.class);
        myBoolColumn.setCellValueFactory(TethysTestTableItem::getBoolean);
        myBoolColumn.setOnCommit(TethysTestTableItem::setBoolean);
        myBoolColumn.setName("B");
        final TethysUIIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysTestIcon.OPENFALSE, TethysTestIcon.OPENTRUE);
        myBoolColumn.setIconMapSet(p -> myMapSet);

        /* Create the extra boolean column */
        final TethysUITableIconColumn<Boolean, TethysTestDataId, TethysTestTableItem> myXtraBoolColumn = theTable.declareIconColumn(TethysTestDataId.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setCellValueFactory(TethysTestTableItem::getXtraBoolean);
        myXtraBoolColumn.setOnCommit(TethysTestTableItem::setXtraBoolean);
        myXtraBoolColumn.setName("X");
        myXtraBoolColumn.setCellEditable(TethysTestTableItem::getBoolean);
        final Map<TethysIconState, TethysUIIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysTestIcon.OPENFALSE, TethysTestIcon.OPENTRUE, TethysTestIcon.CLOSEDTRUE);
        myXtraBoolColumn.setIconMapSet(p -> myMap.get(Boolean.TRUE.equals(p.getBoolean())
                ? TethysIconState.OPEN
                : TethysIconState.CLOSED));
        myXtraBoolColumn.setRepaintColumnId(TethysTestDataId.BOOLEAN);

        /* Create the scroll column */
        final TethysUITableScrollColumn<String, TethysTestDataId, TethysTestTableItem> myScrollColumn = theTable.declareScrollColumn(TethysTestDataId.SCROLL, String.class);
        myScrollColumn.setCellValueFactory(TethysTestTableItem::getScroll);
        myScrollColumn.setOnCommit(TethysTestTableItem::setScroll);
        myScrollColumn.setMenuConfigurator((r, c) -> theHelper.buildContextMenu(c));

        /* Create the list column */
        final TethysUITableListColumn<TethysTestListId, TethysTestDataId, TethysTestTableItem> myListColumn = theTable.declareListColumn(TethysTestDataId.LIST, TethysTestListId.class);
        myListColumn.setCellValueFactory(TethysTestTableItem::getList);
        myListColumn.setOnCommit(TethysTestTableItem::setList);
        myListColumn.setSelectables(r -> theHelper.buildSelectableList());

        /* Create the password column */
        final TethysUITableCharArrayColumn<TethysTestDataId, TethysTestTableItem> myPassColumn = theTable.declareCharArrayColumn(TethysTestDataId.PASSWORD);
        myPassColumn.setCellValueFactory(TethysTestTableItem::getPassword);
        myPassColumn.setOnCommit(TethysTestTableItem::setPassword);

        /* Create the updates column */
        final TethysUITableIntegerColumn<TethysTestDataId, TethysTestTableItem> myUpdatesColumn = theTable.declareIntegerColumn(TethysTestDataId.UPDATES);
        myUpdatesColumn.setCellValueFactory(TethysTestTableItem::getUpdates);
        myUpdatesColumn.setName("U");
        myUpdatesColumn.setEditable(false);

        /* Set Disabled indicator */
        theTable.setChanged((c, r) -> c == TethysTestDataId.NAME && r.getUpdates() > 0);
        theTable.setDisabled(TethysTestTableItem::getBoolean);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return theTable;
    }
}
