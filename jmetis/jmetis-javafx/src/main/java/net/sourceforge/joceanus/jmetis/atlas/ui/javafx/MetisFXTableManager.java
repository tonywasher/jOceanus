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
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisBaseList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisEditList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisVersionedList;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableManager;
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
public class MetisFXTableManager<R extends MetisDataVersionedItem>
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
    protected TethysFXTableManager<MetisDataField, R> getTable() {
        return (TethysFXTableManager<MetisDataField, R>) super.getTable();
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
    public TethysFXTableStringColumn<MetisDataField, R> declareStringColumn(final MetisDataField pId) {
        TethysFXTableStringColumn<MetisDataField, R> myColumn = getTable().declareStringColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableCharArrayColumn<MetisDataField, R> declareCharArrayColumn(final MetisDataField pId) {
        TethysFXTableCharArrayColumn<MetisDataField, R> myColumn = getTable().declareCharArrayColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableShortColumn<MetisDataField, R> declareShortColumn(final MetisDataField pId) {
        TethysFXTableShortColumn<MetisDataField, R> myColumn = getTable().declareShortColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableIntegerColumn<MetisDataField, R> declareIntegerColumn(final MetisDataField pId) {
        TethysFXTableIntegerColumn<MetisDataField, R> myColumn = getTable().declareIntegerColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableLongColumn<MetisDataField, R> declareLongColumn(final MetisDataField pId) {
        TethysFXTableLongColumn<MetisDataField, R> myColumn = getTable().declareLongColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRawDecimalColumn<MetisDataField, R> declareRawDecimalColumn(final MetisDataField pId) {
        TethysFXTableRawDecimalColumn<MetisDataField, R> myColumn = getTable().declareRawDecimalColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableMoneyColumn<MetisDataField, R> declareMoneyColumn(final MetisDataField pId) {
        TethysFXTableMoneyColumn<MetisDataField, R> myColumn = getTable().declareMoneyColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTablePriceColumn<MetisDataField, R> declarePriceColumn(final MetisDataField pId) {
        TethysFXTablePriceColumn<MetisDataField, R> myColumn = getTable().declarePriceColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRateColumn<MetisDataField, R> declareRateColumn(final MetisDataField pId) {
        TethysFXTableRateColumn<MetisDataField, R> myColumn = getTable().declareRateColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableUnitsColumn<MetisDataField, R> declareUnitsColumn(final MetisDataField pId) {
        TethysFXTableUnitsColumn<MetisDataField, R> myColumn = getTable().declareUnitsColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDilutionColumn<MetisDataField, R> declareDilutionColumn(final MetisDataField pId) {
        TethysFXTableDilutionColumn<MetisDataField, R> myColumn = getTable().declareDilutionColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRatioColumn<MetisDataField, R> declareRatioColumn(final MetisDataField pId) {
        TethysFXTableRatioColumn<MetisDataField, R> myColumn = getTable().declareRatioColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDilutedPriceColumn<MetisDataField, R> declareDilutedPriceColumn(final MetisDataField pId) {
        TethysFXTableDilutedPriceColumn<MetisDataField, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDateColumn<MetisDataField, R> declareDateColumn(final MetisDataField pId) {
        TethysFXTableDateColumn<MetisDataField, R> myColumn = getTable().declareDateColumn(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableScrollColumn<MetisDataField, R, T> declareScrollColumn(final MetisDataField pId,
                                                                                   final Class<T> pClass) {
        TethysFXTableScrollColumn<MetisDataField, R, T> myColumn = getTable().declareScrollColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableListColumn<MetisDataField, R, T> declareListColumn(final MetisDataField pId,
                                                                               final Class<T> pClass) {
        TethysFXTableListColumn<MetisDataField, R, T> myColumn = getTable().declareListColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableIconColumn<MetisDataField, R, T> declareIconColumn(final MetisDataField pId,
                                                                               final Class<T> pClass) {
        TethysFXTableIconColumn<MetisDataField, R, T> myColumn = getTable().declareIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableStateIconColumn<MetisDataField, R, T> declareStateIconColumn(final MetisDataField pId,
                                                                                         final Class<T> pClass) {
        TethysFXTableStateIconColumn<MetisDataField, R, T> myColumn = getTable().declareStateIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }
}
