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
package net.sourceforge.joceanus.jmetis.atlas.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisEncryptedValue;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisBaseList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisEditList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisVersionedList;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableManager;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStateIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableUnitsColumn;

/**
 * Metis swing table manager.
 * @param <R> the item type
 */
public class MetisSwingTableManager<R extends MetisDataVersionedItem>
        extends MetisTableManager<R, JComponent, Icon> {
    /**
     * Table List.
     */
    private final MetisSwingTableListManager<R> theList;

    /**
     * is the table readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Table Calculator.
     */
    private MetisTableCalculator<R> theCalculator;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pList the edit list
     */
    public MetisSwingTableManager(final TethysSwingGuiFactory pFactory,
                                  final MetisEditList<R> pList) {
        this(pFactory, pList, false);
    }

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pList the base list
     */
    public MetisSwingTableManager(final TethysSwingGuiFactory pFactory,
                                  final MetisBaseList<R> pList) {
        this(pFactory, pList, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pList the versioned list
     * @param pReadOnly is the table readOnly?
     */
    protected MetisSwingTableManager(final TethysSwingGuiFactory pFactory,
                                     final MetisVersionedList<R> pList,
                                     final boolean pReadOnly) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the table list */
        isReadOnly = pReadOnly;
        theList = new MetisSwingTableListManager<>(this, pList);
        getTable().setItems(theList.getTableList());
    }

    @Override
    protected TethysSwingTableManager<MetisDataField, R> getTable() {
        return (TethysSwingTableManager<MetisDataField, R>) super.getTable();
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pId the field id
     * @param pClass the item class
     * @return the value
     */
    private <T> T getItemFieldValue(final R pItem,
                                    final MetisDataField pId,
                                    final Class<T> pClass) {
        return pId.getStorage().isCalculated()
                                               ? getCalculatedFieldValue(pItem, pId, pClass)
                                               : getStandardFieldValue(pItem, pId, pClass);
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pId the field id
     * @param pClass the item class
     * @return the value
     */
    private <T> T getStandardFieldValue(final R pItem,
                                        final MetisDataField pId,
                                        final Class<T> pClass) {
        Object myValue = pItem.getFieldValue(pId);
        if (myValue == MetisDataFieldValue.SKIP) {
            myValue = null;
        }
        if (myValue instanceof MetisEncryptedValue) {
            myValue = ((MetisEncryptedValue) myValue).getValue();
        }
        return pClass.cast(myValue);
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pId the field id
     * @param pClass the item class
     * @return the value
     */
    private <T> T getCalculatedFieldValue(final R pItem,
                                          final MetisDataField pId,
                                          final Class<T> pClass) {
        Object myValue = theCalculator == null
                                               ? null
                                               : theCalculator.calculateValue(pItem, pId);
        return pClass.cast(myValue);
    }

    /**
     * Fire TableData changed.
     */
    protected void fireTableDataChanged() {
        getTable().fireTableDataChanged();
    }

    /**
     * Fire TableRow deleted.
     * @param pRowIndex the row index
     */
    protected void fireTableRowDeleted(final int pRowIndex) {
        getTable().fireTableRowDeleted(pRowIndex);
    }

    /**
     * Fire TableRow changed.
     * @param pRowIndex the row index
     */
    protected void fireTableRowChanged(final int pRowIndex) {
        getTable().fireTableRowChanged(pRowIndex);
    }

    /**
     * Fire TableRow added.
     * @param pRowIndex the row index
     */
    protected void fireTableRowAdded(final int pRowIndex) {
        getTable().fireTableRowAdded(pRowIndex);
    }

    @Override
    public void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theCalculator = pCalculator;
        getTable().fireTableDataChanged();
    }

    @Override
    public TethysSwingTableStringColumn<MetisDataField, R> declareStringColumn(final MetisDataField pId) {
        TethysSwingTableStringColumn<MetisDataField, R> myColumn = getTable().declareStringColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, String.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableCharArrayColumn<MetisDataField, R> declareCharArrayColumn(final MetisDataField pId) {
        TethysSwingTableCharArrayColumn<MetisDataField, R> myColumn = getTable().declareCharArrayColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, char[].class));
        return myColumn;
    }

    @Override
    public TethysSwingTableShortColumn<MetisDataField, R> declareShortColumn(final MetisDataField pId) {
        TethysSwingTableShortColumn<MetisDataField, R> myColumn = getTable().declareShortColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, Short.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableIntegerColumn<MetisDataField, R> declareIntegerColumn(final MetisDataField pId) {
        TethysSwingTableIntegerColumn<MetisDataField, R> myColumn = getTable().declareIntegerColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, Integer.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableLongColumn<MetisDataField, R> declareLongColumn(final MetisDataField pId) {
        TethysSwingTableLongColumn<MetisDataField, R> myColumn = getTable().declareLongColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, Long.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRawDecimalColumn<MetisDataField, R> declareRawDecimalColumn(final MetisDataField pId) {
        TethysSwingTableRawDecimalColumn<MetisDataField, R> myColumn = getTable().declareRawDecimalColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDecimal.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableMoneyColumn<MetisDataField, R> declareMoneyColumn(final MetisDataField pId) {
        TethysSwingTableMoneyColumn<MetisDataField, R> myColumn = getTable().declareMoneyColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysMoney.class));
        return myColumn;
    }

    @Override
    public TethysSwingTablePriceColumn<MetisDataField, R> declarePriceColumn(final MetisDataField pId) {
        TethysSwingTablePriceColumn<MetisDataField, R> myColumn = getTable().declarePriceColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysPrice.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRateColumn<MetisDataField, R> declareRateColumn(final MetisDataField pId) {
        TethysSwingTableRateColumn<MetisDataField, R> myColumn = getTable().declareRateColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysRate.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableUnitsColumn<MetisDataField, R> declareUnitsColumn(final MetisDataField pId) {
        TethysSwingTableUnitsColumn<MetisDataField, R> myColumn = getTable().declareUnitsColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysUnits.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDilutionColumn<MetisDataField, R> declareDilutionColumn(final MetisDataField pId) {
        TethysSwingTableDilutionColumn<MetisDataField, R> myColumn = getTable().declareDilutionColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDilution.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRatioColumn<MetisDataField, R> declareRatioColumn(final MetisDataField pId) {
        TethysSwingTableRatioColumn<MetisDataField, R> myColumn = getTable().declareRatioColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysRatio.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDilutedPriceColumn<MetisDataField, R> declareDilutedPriceColumn(final MetisDataField pId) {
        TethysSwingTableDilutedPriceColumn<MetisDataField, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDilutedPrice.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDateColumn<MetisDataField, R> declareDateColumn(final MetisDataField pId) {
        TethysSwingTableDateColumn<MetisDataField, R> myColumn = getTable().declareDateColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDate.class));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableScrollColumn<MetisDataField, R, T> declareScrollColumn(final MetisDataField pId,
                                                                                      final Class<T> pClass) {
        TethysSwingTableScrollColumn<MetisDataField, R, T> myColumn = getTable().declareScrollColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, pClass));
        return myColumn;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TethysSwingTableListColumn<MetisDataField, R, T> declareListColumn(final MetisDataField pId,
                                                                                  final Class<T> pClass) {
        TethysSwingTableListColumn<MetisDataField, R, T> myColumn = getTable().declareListColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> (TethysItemList<T>) getItemFieldValue(p, pId, TethysItemList.class));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableIconColumn<MetisDataField, R, T> declareIconColumn(final MetisDataField pId,
                                                                                  final Class<T> pClass) {
        TethysSwingTableIconColumn<MetisDataField, R, T> myColumn = getTable().declareIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, pClass));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableStateIconColumn<MetisDataField, R, T> declareStateIconColumn(final MetisDataField pId,
                                                                                            final Class<T> pClass) {
        TethysSwingTableStateIconColumn<MetisDataField, R, T> myColumn = getTable().declareStateIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, pClass));
        return myColumn;
    }
}
