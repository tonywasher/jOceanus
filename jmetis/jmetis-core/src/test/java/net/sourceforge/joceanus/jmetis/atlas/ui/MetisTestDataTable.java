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
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.list.MetisListBaseManager;
import net.sourceforge.joceanus.jmetis.list.MetisListEditManager;
import net.sourceforge.joceanus.jmetis.list.MetisListEditSession;
import net.sourceforge.joceanus.jmetis.list.MetisListKey;
import net.sourceforge.joceanus.jmetis.list.MetisListSetVersioned;
import net.sourceforge.joceanus.jmetis.list.MetisListUpdateManager;
import net.sourceforge.joceanus.jmetis.list.MetisListVersioned;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;

/**
 * Test Table.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisTestDataTable<N, I>
        implements TethysNode<N> {
    /**
     * The TableManager.
     */
    private final MetisTableManager<MetisTestTableItem, N, I> theTable;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<?, ?> theHelper;

    /**
     * The ListSet.
     */
    private final MetisListSetVersioned theListSet;

    /**
     * The EditListSet.
     */
    private final MetisListSetVersioned theEditSet;

    /**
     * The UpdateListSet.
     */
    private final MetisListSetVersioned theUpdateSet;

    /**
     * The Session.
     */
    private final MetisListEditSession theSession;

    /**
     * The Session Control.
     */
    private final MetisEditSessionControl<N, I> theSessionControl;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public MetisTestDataTable(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create listSet */
        theListSet = MetisListBaseManager.newListSet();
        final MetisListVersioned<MetisTestTableItem> myList = theListSet.declareList(MetisTestItemKey.TEST);

        /* Create update listSet */
        theUpdateSet = MetisListUpdateManager.deriveUpdateListSet(theListSet);

        /* Create edit listSet */
        theEditSet = MetisListEditManager.deriveEditListSet(theListSet);

        /* Create the session */
        theSession = new MetisListEditSession(theEditSet);

        /* Create tableView */
        theTable = pToolkit.newTableManager(MetisTestItemKey.TEST, theSession);
        theTable.setRepaintRowOnCommit(true);
        theTable.setComparator((l, r) -> l.getName().compareTo(r.getName()));
        theTable.setOnCommit(r -> r.incrementUpdates());
        theTable.setEditable(false);

        /* Configure the table */
        configureTable();

        /* Add elements */
        myList.add(createItem(myList, "Damage"));
        myList.add(createItem(myList, "Tony"));
        myList.add(createItem(myList, "Dave"));
        MetisListBaseManager.refresh(theListSet);

        /* Create the session control */
        theSessionControl = new MetisEditSessionControl<>(pToolkit, theSession, theUpdateSet, theTable);
        theSessionControl.setActiveKey(MetisTestItemKey.TEST);
        theSessionControl.getEventRegistrar().addEventListener(e -> theTable.setEditable(theSessionControl.isEditing()));

        /* Access the viewer manager */
        final MetisViewerManager myViewer = pToolkit.getViewerManager();
        final MetisViewerEntry myData = myViewer.getStandardEntry(MetisViewerStandardEntry.DATA);
        myData.setTreeObject(theListSet);
        final MetisViewerEntry myUpdates = myViewer.getStandardEntry(MetisViewerStandardEntry.UPDATES);
        myUpdates.setTreeObject(theUpdateSet);
        final MetisViewerEntry myEdit = myViewer.getStandardEntry(MetisViewerStandardEntry.VIEW);
        myEdit.setTreeObject(theEditSet);
    }

    /**
     * Create new item.
     * @param pList the list
     * @param pName the Name
     * @return the new item
     * @throws OceanusException on error
     */
    private MetisTestTableItem createItem(final MetisListVersioned<MetisTestTableItem> pList,
                                          final String pName) throws OceanusException {
        MetisTestTableItem myItem = pList.newListItem(null);
        myItem.initValues(theHelper, pName);
        return myItem;
    }

    @Override
    public N getNode() {
        return theSessionControl.getNode();
    }

    @Override
    public void setEnabled(boolean pEnabled) {
        theSessionControl.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(boolean pVisible) {
        theSessionControl.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return theSessionControl.getId();
    }

    /**
     * Configure the table.
     */
    private void configureTable() {
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
     * The ItemKey.
     */
    private enum MetisTestItemKey
            implements MetisListKey {
        /**
         * TestItem.
         */
        TEST(1, MetisTestTableItem.class);

        /**
         * The id.
         */
        private Integer theId;

        /**
         * The Class.
         */
        private Class<? extends MetisFieldVersionedItem> theClazz;

        /**
         * Constructor.
         * @param pId the Id.
         */
        MetisTestItemKey(final int pId,
                         Class<MetisTestTableItem> pClazz) {
            theId = pId;
            theClazz = pClazz;
        }

        @Override
        public String getItemName() {
            return getClazz().getSimpleName();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<? extends MetisFieldVersionedItem> getClazz() {
            return theClazz;
        }

        @Override
        public Integer getItemId() {
            return theId;
        }

        @Override
        public String getListName() {
            return getItemName() + "s";
        }

        @SuppressWarnings("unchecked")
        @Override
        public MetisFieldVersionedItem newItem(MetisListSetVersioned pListSet) {
            try {
                return theClazz.newInstance();
            } catch (InstantiationException
                    | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
