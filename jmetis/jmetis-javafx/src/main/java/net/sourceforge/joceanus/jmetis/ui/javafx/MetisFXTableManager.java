/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.ui.javafx;

import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisBaseList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisEditList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisVersionedList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.ui.MetisTableManager;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStateIconColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableUnitsColumn;

/**
 * Metis javaFX table manager.
 * @param <R> the item type
 */
public class MetisFXTableManager<R extends MetisIndexedItem>
        extends MetisTableManager<R, Node, Node> {
    /**
     * Table List.
     */
    private final MetisFXTableList<R> theList;

    /**
     * Table ListFields.
     */
    private final MetisFXTableListFields<R> theItemFields;

    /**
     * is the table readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pList the edit list
     */
    public MetisFXTableManager(final TethysFXGuiFactory pFactory,
                               final MetisEditList<R> pList) {
        this(pFactory, pList, false);
    }

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pList the base list
     */
    public MetisFXTableManager(final TethysFXGuiFactory pFactory,
                               final MetisBaseList<R> pList) {
        this(pFactory, pList, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pList the versioned list
     * @param pReadOnly is the table readOnly?
     */
    private MetisFXTableManager(final TethysFXGuiFactory pFactory,
                                final MetisVersionedList<R> pList,
                                final boolean pReadOnly) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the table list */
        isReadOnly = pReadOnly;
        theList = new MetisFXTableList<>(pList);
        theItemFields = theList.getListFields();
        getTable().setItems(theList.getUnderlyingList());
    }

    @Override
    protected TethysFXTableManager<MetisField, R> getTable() {
        return (TethysFXTableManager<MetisField, R>) super.getTable();
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theItemFields.setCalculator(pCalculator);
    }

    @Override
    public TethysFXTableStringColumn<MetisField, R> declareStringColumn(final MetisField pId) {
        TethysFXTableStringColumn<MetisField, R> myColumn = getTable().declareStringColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableCharArrayColumn<MetisField, R> declareCharArrayColumn(final MetisField pId) {
        TethysFXTableCharArrayColumn<MetisField, R> myColumn = getTable().declareCharArrayColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableShortColumn<MetisField, R> declareShortColumn(final MetisField pId) {
        TethysFXTableShortColumn<MetisField, R> myColumn = getTable().declareShortColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableIntegerColumn<MetisField, R> declareIntegerColumn(final MetisField pId) {
        TethysFXTableIntegerColumn<MetisField, R> myColumn = getTable().declareIntegerColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableLongColumn<MetisField, R> declareLongColumn(final MetisField pId) {
        TethysFXTableLongColumn<MetisField, R> myColumn = getTable().declareLongColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRawDecimalColumn<MetisField, R> declareRawDecimalColumn(final MetisField pId) {
        TethysFXTableRawDecimalColumn<MetisField, R> myColumn = getTable().declareRawDecimalColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableMoneyColumn<MetisField, R> declareMoneyColumn(final MetisField pId) {
        TethysFXTableMoneyColumn<MetisField, R> myColumn = getTable().declareMoneyColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTablePriceColumn<MetisField, R> declarePriceColumn(final MetisField pId) {
        TethysFXTablePriceColumn<MetisField, R> myColumn = getTable().declarePriceColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRateColumn<MetisField, R> declareRateColumn(final MetisField pId) {
        TethysFXTableRateColumn<MetisField, R> myColumn = getTable().declareRateColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableUnitsColumn<MetisField, R> declareUnitsColumn(final MetisField pId) {
        TethysFXTableUnitsColumn<MetisField, R> myColumn = getTable().declareUnitsColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDilutionColumn<MetisField, R> declareDilutionColumn(final MetisField pId) {
        TethysFXTableDilutionColumn<MetisField, R> myColumn = getTable().declareDilutionColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRatioColumn<MetisField, R> declareRatioColumn(final MetisField pId) {
        TethysFXTableRatioColumn<MetisField, R> myColumn = getTable().declareRatioColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDilutedPriceColumn<MetisField, R> declareDilutedPriceColumn(final MetisField pId) {
        TethysFXTableDilutedPriceColumn<MetisField, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDateColumn<MetisField, R> declareDateColumn(final MetisField pId) {
        TethysFXTableDateColumn<MetisField, R> myColumn = getTable().declareDateColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableScrollColumn<MetisField, R, T> declareScrollColumn(final MetisField pId,
                                                                               final Class<T> pClass) {
        TethysFXTableScrollColumn<MetisField, R, T> myColumn = getTable().declareScrollColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableListColumn<MetisField, R, T> declareListColumn(final MetisField pId,
                                                                           final Class<T> pClass) {
        TethysFXTableListColumn<MetisField, R, T> myColumn = getTable().declareListColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableIconColumn<MetisField, R, T> declareIconColumn(final MetisField pId,
                                                                           final Class<T> pClass) {
        TethysFXTableIconColumn<MetisField, R, T> myColumn = getTable().declareIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableStateIconColumn<MetisField, R, T> declareStateIconColumn(final MetisField pId,
                                                                                     final Class<T> pClass) {
        TethysFXTableStateIconColumn<MetisField, R, T> myColumn = getTable().declareStateIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }
}
