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
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisIndexedList;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableManager;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosTableItem;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableUnitsColumn;

/**
 * Metis javaFX table manager.
 * @param <R> the item type
 */
public class MetisFXTableManager<R extends MetisDataEosTableItem>
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
     * Constructor.
     * @param pFactory the GUI Factory
     * @param pFieldSet the fieldSet
     * @param pList the versioned list
     */
    public MetisFXTableManager(final TethysFXGuiFactory pFactory,
                               final MetisDataEosFieldSet<R> pFieldSet,
                               final MetisIndexedList<R> pList) {
        /* Initialise underlying class */
        super(pFactory, pFieldSet);

        /* Create the table list */
        theList = new MetisFXTableList<>(pList);
        theItemFields = theList.getListFields();
        getTable().setItems(theList.getUnderlyingList());
    }

    @Override
    protected TethysFXTableManager<MetisFieldId, R> getTable() {
        return (TethysFXTableManager<MetisFieldId, R>) super.getTable();
    }

    @Override
    public void setCalculator(final MetisTableCalculator<R> pCalculator) {
        theItemFields.setCalculator(pCalculator);
    }

    @Override
    public TethysFXTableStringColumn<MetisFieldId, R> declareStringColumn(final MetisFieldId pId) {
        final TethysFXTableStringColumn<MetisFieldId, R> myColumn = getTable().declareStringColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableCharArrayColumn<MetisFieldId, R> declareCharArrayColumn(final MetisFieldId pId) {
        final TethysFXTableCharArrayColumn<MetisFieldId, R> myColumn = getTable().declareCharArrayColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableShortColumn<MetisFieldId, R> declareShortColumn(final MetisFieldId pId) {
        final TethysFXTableShortColumn<MetisFieldId, R> myColumn = getTable().declareShortColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableIntegerColumn<MetisFieldId, R> declareIntegerColumn(final MetisFieldId pId) {
        final TethysFXTableIntegerColumn<MetisFieldId, R> myColumn = getTable().declareIntegerColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableLongColumn<MetisFieldId, R> declareLongColumn(final MetisFieldId pId) {
        final TethysFXTableLongColumn<MetisFieldId, R> myColumn = getTable().declareLongColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableRawDecimalColumn<MetisFieldId, R> declareRawDecimalColumn(final MetisFieldId pId) {
        final TethysFXTableRawDecimalColumn<MetisFieldId, R> myColumn = getTable().declareRawDecimalColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableMoneyColumn<MetisFieldId, R> declareMoneyColumn(final MetisFieldId pId) {
        final TethysFXTableMoneyColumn<MetisFieldId, R> myColumn = getTable().declareMoneyColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTablePriceColumn<MetisFieldId, R> declarePriceColumn(final MetisFieldId pId) {
        final TethysFXTablePriceColumn<MetisFieldId, R> myColumn = getTable().declarePriceColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableRateColumn<MetisFieldId, R> declareRateColumn(final MetisFieldId pId) {
        final TethysFXTableRateColumn<MetisFieldId, R> myColumn = getTable().declareRateColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableUnitsColumn<MetisFieldId, R> declareUnitsColumn(final MetisFieldId pId) {
        final TethysFXTableUnitsColumn<MetisFieldId, R> myColumn = getTable().declareUnitsColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableDilutionColumn<MetisFieldId, R> declareDilutionColumn(final MetisFieldId pId) {
        final TethysFXTableDilutionColumn<MetisFieldId, R> myColumn = getTable().declareDilutionColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableRatioColumn<MetisFieldId, R> declareRatioColumn(final MetisFieldId pId) {
        final TethysFXTableRatioColumn<MetisFieldId, R> myColumn = getTable().declareRatioColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableDilutedPriceColumn<MetisFieldId, R> declareDilutedPriceColumn(final MetisFieldId pId) {
        final TethysFXTableDilutedPriceColumn<MetisFieldId, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public TethysFXTableDateColumn<MetisFieldId, R> declareDateColumn(final MetisFieldId pId) {
        final TethysFXTableDateColumn<MetisFieldId, R> myColumn = getTable().declareDateColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public <T> TethysFXTableScrollColumn<T, MetisFieldId, R> declareScrollColumn(final MetisFieldId pId,
                                                                                 final Class<T> pClazz) {
        final TethysFXTableScrollColumn<T, MetisFieldId, R> myColumn = getTable().declareScrollColumn(pId, pClazz);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public <T extends Comparable<T>> TethysFXTableListColumn<T, MetisFieldId, R> declareListColumn(final MetisFieldId pId) {
        final TethysFXTableListColumn<T, MetisFieldId, R> myColumn = getTable().declareListColumn(pId);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }

    @Override
    public <T> TethysFXTableIconColumn<T, MetisFieldId, R> declareIconColumn(final MetisFieldId pId,
                                                                             final Class<T> pClazz) {
        final TethysFXTableIconColumn<T, MetisFieldId, R> myColumn = getTable().declareIconColumn(pId, pClazz);
        final MetisDataEosFieldDef myField = getFieldForId(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), myField));
        theItemFields.declareField(myField);
        return myColumn;
    }
}
