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
package net.sourceforge.joceanus.jmetis.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData.MetisEncryptedField;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisBaseList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisEditList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisVersionedList;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.ui.MetisTableManager;
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
public class MetisSwingTableManager<R extends MetisIndexedItem>
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
    protected TethysSwingTableManager<MetisField, R> getTable() {
        return (TethysSwingTableManager<MetisField, R>) super.getTable();
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
                                    final MetisField pId,
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
                                        final MetisField pId,
                                        final Class<T> pClass) {
        Object myValue = pItem.getFieldValue(pId);
        if (myValue == MetisFieldValue.SKIP) {
            myValue = null;
        }
        if (myValue instanceof MetisEncryptedField) {
            myValue = ((MetisEncryptedField<?>) myValue).getValue();
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
                                          final MetisField pId,
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
    public TethysSwingTableStringColumn<MetisField, R> declareStringColumn(final MetisField pId) {
        TethysSwingTableStringColumn<MetisField, R> myColumn = getTable().declareStringColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, String.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableCharArrayColumn<MetisField, R> declareCharArrayColumn(final MetisField pId) {
        TethysSwingTableCharArrayColumn<MetisField, R> myColumn = getTable().declareCharArrayColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, char[].class));
        return myColumn;
    }

    @Override
    public TethysSwingTableShortColumn<MetisField, R> declareShortColumn(final MetisField pId) {
        TethysSwingTableShortColumn<MetisField, R> myColumn = getTable().declareShortColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, Short.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableIntegerColumn<MetisField, R> declareIntegerColumn(final MetisField pId) {
        TethysSwingTableIntegerColumn<MetisField, R> myColumn = getTable().declareIntegerColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, Integer.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableLongColumn<MetisField, R> declareLongColumn(final MetisField pId) {
        TethysSwingTableLongColumn<MetisField, R> myColumn = getTable().declareLongColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, Long.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRawDecimalColumn<MetisField, R> declareRawDecimalColumn(final MetisField pId) {
        TethysSwingTableRawDecimalColumn<MetisField, R> myColumn = getTable().declareRawDecimalColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDecimal.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableMoneyColumn<MetisField, R> declareMoneyColumn(final MetisField pId) {
        TethysSwingTableMoneyColumn<MetisField, R> myColumn = getTable().declareMoneyColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysMoney.class));
        return myColumn;
    }

    @Override
    public TethysSwingTablePriceColumn<MetisField, R> declarePriceColumn(final MetisField pId) {
        TethysSwingTablePriceColumn<MetisField, R> myColumn = getTable().declarePriceColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysPrice.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRateColumn<MetisField, R> declareRateColumn(final MetisField pId) {
        TethysSwingTableRateColumn<MetisField, R> myColumn = getTable().declareRateColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysRate.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableUnitsColumn<MetisField, R> declareUnitsColumn(final MetisField pId) {
        TethysSwingTableUnitsColumn<MetisField, R> myColumn = getTable().declareUnitsColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysUnits.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDilutionColumn<MetisField, R> declareDilutionColumn(final MetisField pId) {
        TethysSwingTableDilutionColumn<MetisField, R> myColumn = getTable().declareDilutionColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDilution.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRatioColumn<MetisField, R> declareRatioColumn(final MetisField pId) {
        TethysSwingTableRatioColumn<MetisField, R> myColumn = getTable().declareRatioColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysRatio.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDilutedPriceColumn<MetisField, R> declareDilutedPriceColumn(final MetisField pId) {
        TethysSwingTableDilutedPriceColumn<MetisField, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDilutedPrice.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDateColumn<MetisField, R> declareDateColumn(final MetisField pId) {
        TethysSwingTableDateColumn<MetisField, R> myColumn = getTable().declareDateColumn(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, TethysDate.class));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableScrollColumn<MetisField, R, T> declareScrollColumn(final MetisField pId,
                                                                                  final Class<T> pClass) {
        TethysSwingTableScrollColumn<MetisField, R, T> myColumn = getTable().declareScrollColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, pClass));
        return myColumn;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TethysSwingTableListColumn<MetisField, R, T> declareListColumn(final MetisField pId,
                                                                              final Class<T> pClass) {
        TethysSwingTableListColumn<MetisField, R, T> myColumn = getTable().declareListColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> (TethysItemList<T>) getItemFieldValue(p, pId, TethysItemList.class));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableIconColumn<MetisField, R, T> declareIconColumn(final MetisField pId,
                                                                              final Class<T> pClass) {
        TethysSwingTableIconColumn<MetisField, R, T> myColumn = getTable().declareIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, pClass));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableStateIconColumn<MetisField, R, T> declareStateIconColumn(final MetisField pId,
                                                                                        final Class<T> pClass) {
        TethysSwingTableStateIconColumn<MetisField, R, T> myColumn = getTable().declareStateIconColumn(pId, pClass);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, pId, pClass));
        return myColumn;
    }
}
