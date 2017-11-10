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
package net.sourceforge.joceanus.jmetis.atlas.ui.swing;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisEncryptedValue;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableManager;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosTableItem;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableUnitsColumn;

/**
 * Metis swing table manager.
 * @param <R> the item type
 */
public class MetisSwingTableManager<R extends MetisDataEosTableItem>
        extends MetisTableManager<R, JComponent, Icon> {
    /**
     * Table List.
     */
    private final MetisSwingTableListManager<R> theList;

    /**
     * Table Calculator.
     */
    private MetisTableCalculator<R> theCalculator;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldSet the fieldSet
     * @param pList the versioned list
     */
    public MetisSwingTableManager(final TethysSwingGuiFactory pFactory,
                                  final MetisDataEosFieldSet<R> pFieldSet,
                                  final MetisIndexedList<R> pList) {
        /* Initialise underlying class */
        super(pFactory, pFieldSet);

        /* Create the table list */
        theList = new MetisSwingTableListManager<>(this, pList);
        getTable().setItems(theList.getTableList());
    }

    @Override
    protected TethysSwingTableManager<MetisFieldId, R> getTable() {
        return (TethysSwingTableManager<MetisFieldId, R>) super.getTable();
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pField the field
     * @param pClazz the item class
     * @return the value
     */
    private <T> T getItemFieldValue(final R pItem,
                                    final MetisDataEosFieldDef pField,
                                    final Class<T> pClazz) {
        return pField.getStorage().isCalculated()
                                                  ? getCalculatedFieldValue(pItem, pField, pClazz)
                                                  : getStandardFieldValue(pItem, pField, pClazz);
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pField the field
     * @param pClazz the item class
     * @return the value
     */
    private <T> T getStandardFieldValue(final R pItem,
                                        final MetisDataEosFieldDef pField,
                                        final Class<T> pClazz) {
        Object myValue = pField.getFieldValue(pItem);
        if (myValue == MetisDataFieldValue.SKIP) {
            myValue = null;
        }
        if (myValue instanceof MetisEncryptedValue) {
            myValue = ((MetisEncryptedValue) myValue).getValue();
        }
        return pClazz.cast(myValue);
    }

    /**
     * Obtain a value of a specific class.
     * @param <T> the class
     * @param pItem the item
     * @param pField the field
     * @param pClazz the item class
     * @return the value
     */
    private <T> T getCalculatedFieldValue(final R pItem,
                                          final MetisDataEosFieldDef pField,
                                          final Class<T> pClazz) {
        final Object myValue = theCalculator == null
                                                     ? null
                                                     : theCalculator.calculateValue(pItem, pField);
        return pClazz.cast(myValue);
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
    public TethysSwingTableStringColumn<MetisFieldId, R> declareStringColumn(final MetisFieldId pId) {
        final TethysSwingTableStringColumn<MetisFieldId, R> myColumn = getTable().declareStringColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, String.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableCharArrayColumn<MetisFieldId, R> declareCharArrayColumn(final MetisFieldId pId) {
        final TethysSwingTableCharArrayColumn<MetisFieldId, R> myColumn = getTable().declareCharArrayColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, char[].class));
        return myColumn;
    }

    @Override
    public TethysSwingTableShortColumn<MetisFieldId, R> declareShortColumn(final MetisFieldId pId) {
        final TethysSwingTableShortColumn<MetisFieldId, R> myColumn = getTable().declareShortColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, Short.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableIntegerColumn<MetisFieldId, R> declareIntegerColumn(final MetisFieldId pId) {
        final TethysSwingTableIntegerColumn<MetisFieldId, R> myColumn = getTable().declareIntegerColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, Integer.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableLongColumn<MetisFieldId, R> declareLongColumn(final MetisFieldId pId) {
        final TethysSwingTableLongColumn<MetisFieldId, R> myColumn = getTable().declareLongColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, Long.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRawDecimalColumn<MetisFieldId, R> declareRawDecimalColumn(final MetisFieldId pId) {
        final TethysSwingTableRawDecimalColumn<MetisFieldId, R> myColumn = getTable().declareRawDecimalColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysDecimal.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableMoneyColumn<MetisFieldId, R> declareMoneyColumn(final MetisFieldId pId) {
        final TethysSwingTableMoneyColumn<MetisFieldId, R> myColumn = getTable().declareMoneyColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysMoney.class));
        return myColumn;
    }

    @Override
    public TethysSwingTablePriceColumn<MetisFieldId, R> declarePriceColumn(final MetisFieldId pId) {
        final TethysSwingTablePriceColumn<MetisFieldId, R> myColumn = getTable().declarePriceColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysPrice.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRateColumn<MetisFieldId, R> declareRateColumn(final MetisFieldId pId) {
        final TethysSwingTableRateColumn<MetisFieldId, R> myColumn = getTable().declareRateColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysRate.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableUnitsColumn<MetisFieldId, R> declareUnitsColumn(final MetisFieldId pId) {
        final TethysSwingTableUnitsColumn<MetisFieldId, R> myColumn = getTable().declareUnitsColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysUnits.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDilutionColumn<MetisFieldId, R> declareDilutionColumn(final MetisFieldId pId) {
        final TethysSwingTableDilutionColumn<MetisFieldId, R> myColumn = getTable().declareDilutionColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysDilution.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableRatioColumn<MetisFieldId, R> declareRatioColumn(final MetisFieldId pId) {
        final TethysSwingTableRatioColumn<MetisFieldId, R> myColumn = getTable().declareRatioColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysRatio.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDilutedPriceColumn<MetisFieldId, R> declareDilutedPriceColumn(final MetisFieldId pId) {
        final TethysSwingTableDilutedPriceColumn<MetisFieldId, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysDilutedPrice.class));
        return myColumn;
    }

    @Override
    public TethysSwingTableDateColumn<MetisFieldId, R> declareDateColumn(final MetisFieldId pId) {
        final TethysSwingTableDateColumn<MetisFieldId, R> myColumn = getTable().declareDateColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, TethysDate.class));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableScrollColumn<T, MetisFieldId, R> declareScrollColumn(final MetisFieldId pId,
                                                                                    final Class<T> pClazz) {
        final TethysSwingTableScrollColumn<T, MetisFieldId, R> myColumn = getTable().declareScrollColumn(pId, pClazz);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, pClazz));
        return myColumn;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Comparable<T>> TethysSwingTableListColumn<T, MetisFieldId, R> declareListColumn(final MetisFieldId pId) {
        final TethysSwingTableListColumn<T, MetisFieldId, R> myColumn = getTable().declareListColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> (List<T>) getItemFieldValue(p, myField, List.class));
        return myColumn;
    }

    @Override
    public <T> TethysSwingTableIconColumn<T, MetisFieldId, R> declareIconColumn(final MetisFieldId pId,
                                                                                final Class<T> pClazz) {
        final TethysSwingTableIconColumn<T, MetisFieldId, R> myColumn = getTable().declareIconColumn(pId, pClazz);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> getItemFieldValue(p, myField, pClazz));
        return myColumn;
    }
}
