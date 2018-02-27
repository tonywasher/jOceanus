/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableIconColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableIntegerColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableListColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableScrollColumn;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableColumn.MetisTableShortColumn;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;

/**
 * Test Table.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisTestDataTable<N, I> {
    /**
     * The TableManager.
     */
    private final MetisTableManager<MetisTestTableItem, N, I> theTable;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<?, ?> theHelper;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public MetisTestDataTable(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create test Data */
        final MetisListIndexed<MetisTestTableItem> myData = new MetisListIndexed<>();
        myData.add(createItem("Damage"));
        myData.add(createItem("Tony"));
        myData.add(createItem("Dave"));

        /* Create tableView */
        theTable = pToolkit.newTableManager(MetisTestTableItem.class, myData);
        theTable.setRepaintRowOnCommit(true);
        theTable.setComparator((l, r) -> l.getName().compareTo(r.getName()));
        theTable.setOnCommit(r -> r.incrementUpdates());

        /* Create the name column */
        theTable.declareStringColumn(MetisTestDataField.NAME);

        /* Create the date column */
        theTable.declareDateColumn(MetisTestDataField.DATE);

        /* Create the short column */
        final MetisTableShortColumn<MetisTestTableItem, N, I> myShortColumn = theTable.declareShortColumn(MetisTestDataField.SHORT);
        myShortColumn.setValidator((v, r) -> v < 0
                                                   ? "Must be positive"
                                                   : null);

        /* Create the integer column */
        theTable.declareIntegerColumn(MetisTestDataField.INTEGER);

        /* Create the long column */
        theTable.declareLongColumn(MetisTestDataField.LONG);

        /* Create the money column */
        theTable.declareMoneyColumn(MetisTestDataField.MONEY);

        /* Create the price column */
        theTable.declarePriceColumn(MetisTestDataField.PRICE);

        /* Create the units column */
        theTable.declareUnitsColumn(MetisTestDataField.UNITS);

        /* Create the rate column */
        theTable.declareRateColumn(MetisTestDataField.RATE);

        /* Create the ratio column */
        theTable.declareRatioColumn(MetisTestDataField.RATIO);

        /* Create the dilution column */
        theTable.declareDilutionColumn(MetisTestDataField.DILUTION);

        /* Create the dilutedPrice column */
        theTable.declareDilutedPriceColumn(MetisTestDataField.DILUTEDPRICE);

        /* Create the boolean column */
        final MetisTableIconColumn<Boolean, MetisTestTableItem, N, I> myBoolColumn = theTable.declareIconColumn(MetisTestDataField.BOOLEAN, Boolean.class);
        myBoolColumn.setName("B");
        final TethysIconMapSet<Boolean> myMapSet = theHelper.buildSimpleIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);
        myBoolColumn.setIconMapSet(p -> myMapSet);

        /* Create the extra boolean column */
        final MetisTableIconColumn<Boolean, MetisTestTableItem, N, I> myXtraBoolColumn = theTable.declareIconColumn(MetisTestDataField.XTRABOOL, Boolean.class);
        myXtraBoolColumn.setName("X");
        myXtraBoolColumn.setCellEditable(p -> p.getBoolean());
        final Map<IconState, TethysIconMapSet<Boolean>> myMap = theHelper.buildStateIconState(TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE, TethysHelperIcon.CLOSEDTRUE);
        myXtraBoolColumn.setIconMapSet(p -> myMap.get(p.getBoolean()
                                                                     ? IconState.OPEN
                                                                     : IconState.CLOSED));
        myXtraBoolColumn.setRepaintColumnId(MetisTestDataField.BOOLEAN);

        /* Create the scroll column */
        final MetisTableScrollColumn<String, MetisTestTableItem, N, I> myScrollColumn = theTable.declareScrollColumn(MetisTestDataField.SCROLL, String.class);
        myScrollColumn.setMenuConfigurator((r, c) -> theHelper.buildContextMenu(c));

        /* Create the list column */
        final MetisTableListColumn<TethysListId, MetisTestTableItem, N, I> myListColumn = theTable.declareListColumn(MetisTestDataField.LIST);
        myListColumn.setSelectables(r -> theHelper.buildSelectableList());

        /* Create the password column */
        theTable.declareCharArrayColumn(MetisTestDataField.PASSWORD);

        /* Create the updates column */
        final MetisTableIntegerColumn<MetisTestTableItem, N, I> myUpdatesColumn = theTable.declareIntegerColumn(MetisTestDataField.UPDATES);
        myUpdatesColumn.setName("U");
        myUpdatesColumn.setEditable(false);

        /* Set Disabled indicator */
        theTable.setDisabled(r -> r.getBoolean());
    }

    /**
     * Create new item.
     * @param pName the Name
     * @return the new item
     * @throws OceanusException on error
     */
    private MetisTestTableItem createItem(final String pName) throws OceanusException {
        MetisTestTableItem myItem = new MetisTestTableItem();
        myItem.initValues(theHelper, pName);
        return myItem;
    }
}
