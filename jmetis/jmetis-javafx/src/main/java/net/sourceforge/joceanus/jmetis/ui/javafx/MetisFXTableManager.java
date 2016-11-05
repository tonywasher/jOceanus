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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newlist.MetisEditList;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedItem;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedList;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStateIconColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableUnitsColumn;

/**
 * Metis javaFX table manager.
 * @param <R> the item type
 */
public class MetisFXTableManager<R extends MetisVersionedItem>
        extends MetisTableManager<R, Node, Node> {
    /**
     * Table Fields.
     */
    private final List<MetisField> theFields;

    /**
     * Table FieldSets.
     */
    private final MetisFXTableListFields<R> theItemFields;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected MetisFXTableManager(final TethysFXGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the field list */
        theFields = new ArrayList<>();
        theItemFields = new MetisFXTableListFields<>(theFields);
    }

    @Override
    protected TethysFXTableManager<MetisField, R> getTable() {
        return (TethysFXTableManager<MetisField, R>) super.getTable();
    }

    @Override
    public void setItems(final MetisVersionedList<R> pItems) {
        /* Do nothing */
    }

    @Override
    public MetisEditList<R> getItems() {
        return null;
    }

    @Override
    public TethysFXTableStringColumn<MetisField, R> declareStringColumn(final MetisField pId) {
        TethysFXTableStringColumn<MetisField, R> myColumn = getTable().declareStringColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableCharArrayColumn<MetisField, R> declareCharArrayColumn(final MetisField pId) {
        TethysFXTableCharArrayColumn<MetisField, R> myColumn = getTable().declareCharArrayColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableShortColumn<MetisField, R> declareShortColumn(final MetisField pId) {
        TethysFXTableShortColumn<MetisField, R> myColumn = getTable().declareShortColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableIntegerColumn<MetisField, R> declareIntegerColumn(final MetisField pId) {
        TethysFXTableIntegerColumn<MetisField, R> myColumn = getTable().declareIntegerColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableLongColumn<MetisField, R> declareLongColumn(final MetisField pId) {
        TethysFXTableLongColumn<MetisField, R> myColumn = getTable().declareLongColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableMoneyColumn<MetisField, R> declareMoneyColumn(final MetisField pId) {
        TethysFXTableMoneyColumn<MetisField, R> myColumn = getTable().declareMoneyColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTablePriceColumn<MetisField, R> declarePriceColumn(final MetisField pId) {
        TethysFXTablePriceColumn<MetisField, R> myColumn = getTable().declarePriceColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRateColumn<MetisField, R> declareRateColumn(final MetisField pId) {
        TethysFXTableRateColumn<MetisField, R> myColumn = getTable().declareRateColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableUnitsColumn<MetisField, R> declareUnitsColumn(final MetisField pId) {
        TethysFXTableUnitsColumn<MetisField, R> myColumn = getTable().declareUnitsColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDilutionColumn<MetisField, R> declareDilutionColumn(final MetisField pId) {
        TethysFXTableDilutionColumn<MetisField, R> myColumn = getTable().declareDilutionColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableRatioColumn<MetisField, R> declareRatioColumn(final MetisField pId) {
        TethysFXTableRatioColumn<MetisField, R> myColumn = getTable().declareRatioColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDilutedPriceColumn<MetisField, R> declareDilutedPriceColumn(final MetisField pId) {
        TethysFXTableDilutedPriceColumn<MetisField, R> myColumn = getTable().declareDilutedPriceColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public TethysFXTableDateColumn<MetisField, R> declareDateColumn(final MetisField pId) {
        TethysFXTableDateColumn<MetisField, R> myColumn = getTable().declareDateColumn(pId);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableScrollColumn<MetisField, R, T> declareScrollColumn(final MetisField pId,
                                                                               final Class<T> pClass) {
        TethysFXTableScrollColumn<MetisField, R, T> myColumn = getTable().declareScrollColumn(pId, pClass);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableListColumn<MetisField, R, T> declareListColumn(final MetisField pId,
                                                                           final Class<T> pClass) {
        TethysFXTableListColumn<MetisField, R, T> myColumn = getTable().declareListColumn(pId, pClass);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableIconColumn<MetisField, R, T> declareIconColumn(final MetisField pId,
                                                                           final Class<T> pClass) {
        TethysFXTableIconColumn<MetisField, R, T> myColumn = getTable().declareIconColumn(pId, pClass);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }

    @Override
    public <T> TethysFXTableStateIconColumn<MetisField, R, T> declareStateIconColumn(final MetisField pId,
                                                                                     final Class<T> pClass) {
        TethysFXTableStateIconColumn<MetisField, R, T> myColumn = getTable().declareStateIconColumn(pId, pClass);
        theFields.add(pId);
        myColumn.setCellValueFactory(p -> theItemFields.getObjectProperty(p.getValue(), pId));
        return myColumn;
    }
}
