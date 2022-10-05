/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.table.TethysUICoreTableManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.table.TethysUIFXTableColumn.TethysUIFXTableUnitsColumn;

/**
 * JavaFX Table manager.
 *
 * @param <C> the column identity
 * @param <R> the row type
 */
public class TethysUIFXTableManager<C, R>
        extends TethysUICoreTableManager<C, R> {
    /**
     * Base StyleSheet Class.
     */
    private static final String CSS_STYLE_BASE = TethysUIFXUtils.CSS_STYLE_BASE + "-table";

    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The TableView.
     */
    private final TableView<R> theTable;

    /**
     * The Columns.
     */
    private final ObservableList<TableColumn<R, ?>> theColumns;

    /**
     * The CellFactory.
     */
    private final TethysUIFXTableCellFactory<C, R> theCellFactory;

    /**
     * The Items.
     */
    private ObservableList<R> theObservableItems;

    /**
     * The Active Cell.
     */
    private TethysUIFXTableCell<?, C, R> theActiveCell;

    /**
     * The Sort Comparator.
     */
    private final ObjectProperty<Comparator<R>> theCompValue;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    public TethysUIFXTableManager(final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create fields */
        theTable = new TableView<>();
        theColumns = theTable.getColumns();
        theCellFactory = new TethysUIFXTableCellFactory<>(pFactory);
        theTable.getStyleClass().add(CSS_STYLE_BASE);
        theTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        theNode = new TethysUIFXNode(theTable);

        /* Configure the table */
        theTable.setEditable(true);

        /* Set single Selection and listen for selection changes */
        final TableViewSelectionModel<R> myModel = theTable.getSelectionModel();
        myModel.setSelectionMode(SelectionMode.SINGLE);
        myModel.selectedItemProperty().addListener((v, o, n) -> processOnSelect(n));

        theCompValue = new SimpleObjectProperty<>();
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTable.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theTable.setManaged(pVisible);
        theTable.setVisible(pVisible);
    }

    @Override
    public Iterator<R> itemIterator() {
        return theObservableItems == null
                ? Collections.emptyIterator()
                : theObservableItems.iterator();
    }

    @Override
    public Iterator<R> viewIterator() {
        final List<R> myItems = theTable.getItems();
        return myItems == null
                ? Collections.emptyIterator()
                : myItems.iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TethysUIFXTableColumn<?, C, R> getColumn(final C pId) {
        return (TethysUIFXTableColumn<?, C, R>) super.getColumn(pId);
    }

    @Override
    public void requestFocus() {
        theTable.requestFocus();
    }

    /**
     * Obtain the columns.
     *
     * @return the columns
     */
    ObservableList<TableColumn<R, ?>> getColumns() {
        return theColumns;
    }

    /**
     * Obtain the cell factory.
     *
     * @return the cell factory
     */
    TethysUIFXTableCellFactory<C, R> getCellFactory() {
        return theCellFactory;
    }

    @Override
    public void setItems(final List<R> pItems) {
        super.setItems(pItems);
        setTheItems();
    }

    @Override
    public TethysUIFXTableManager<C, R> setFilter(final Predicate<R> pFilter) {
        super.setFilter(pFilter);
        setTheItems();
        return this;
    }

    @Override
    public TethysUIFXTableManager<C, R> setComparator(final Comparator<R> pComparator) {
        super.setComparator(pComparator);
        setTheItems();
        return this;
    }

    @Override
    public void cancelEditing() {
        theTable.edit(-1, null);
    }

    /**
     * Set the table items.
     */
    private void setTheItems() {
        /* Access the underlying copy */
        final List<R> myItems = super.getItems();
        theObservableItems = myItems == null ? null : FXCollections.observableArrayList(super.getItems());
        ObservableList<R> mySorted = theObservableItems;

        /* If we have any items */
        if (mySorted != null) {
            /* Apply filter if specified */
            final Predicate<R> myFilter = getFilter();
            if (myFilter != null) {
                mySorted = mySorted.filtered(myFilter);
            }

            /* Apply sort if specified */
            final Comparator<R> myComparator = getComparator();
            if (myComparator != null) {
                mySorted = mySorted.sorted(myComparator);
                theCompValue.setValue(myComparator);
                ((SortedList<R>) mySorted).comparatorProperty().bind(theCompValue);
            }
        }

        /* Declare the items */
        theTable.setItems(mySorted);
    }

    @Override
    protected void processOnCommit(final R pRow) throws OceanusException {
        super.processOnCommit(pRow);
        fireTableDataChanged();
    }

    @Override
    public void fireTableDataChanged() {
        setTheItems();
    }

    /**
     * Set the active cell.
     *
     * @param pCell the actively editing cell
     */
    protected void setActiveCell(final TethysUIFXTableCell<?, C, R> pCell) {
        theActiveCell = pCell;
    }

    /**
     * Is the table locked for editing.
     *
     * @return true/false
     */
    protected boolean isEditLocked() {
        return !isEditable()
                || theActiveCell != null && theActiveCell.isCellInError();
    }

    @Override
    public TethysUIFXTableStringColumn<C, R> declareStringColumn(final C pId) {
        return new TethysUIFXTableStringColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableCharArrayColumn<C, R> declareCharArrayColumn(final C pId) {
        return new TethysUIFXTableCharArrayColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableShortColumn<C, R> declareShortColumn(final C pId) {
        return new TethysUIFXTableShortColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableIntegerColumn<C, R> declareIntegerColumn(final C pId) {
        return new TethysUIFXTableIntegerColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableLongColumn<C, R> declareLongColumn(final C pId) {
        return new TethysUIFXTableLongColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableRawDecimalColumn<C, R> declareRawDecimalColumn(final C pId) {
        return new TethysUIFXTableRawDecimalColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableMoneyColumn<C, R> declareMoneyColumn(final C pId) {
        return new TethysUIFXTableMoneyColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTablePriceColumn<C, R> declarePriceColumn(final C pId) {
        return new TethysUIFXTablePriceColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableRateColumn<C, R> declareRateColumn(final C pId) {
        return new TethysUIFXTableRateColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableUnitsColumn<C, R> declareUnitsColumn(final C pId) {
        return new TethysUIFXTableUnitsColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableDilutionColumn<C, R> declareDilutionColumn(final C pId) {
        return new TethysUIFXTableDilutionColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableRatioColumn<C, R> declareRatioColumn(final C pId) {
        return new TethysUIFXTableRatioColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableDilutedPriceColumn<C, R> declareDilutedPriceColumn(final C pId) {
        return new TethysUIFXTableDilutedPriceColumn<>(this, pId);
    }

    @Override
    public TethysUIFXTableDateColumn<C, R> declareDateColumn(final C pId) {
        return new TethysUIFXTableDateColumn<>(this, pId);
    }

    @Override
    public <T> TethysUIFXTableScrollColumn<T, C, R> declareScrollColumn(final C pId,
                                                                        final Class<T> pClazz) {
        return new TethysUIFXTableScrollColumn<>(this, pId, pClazz);
    }

    @Override
    public <T extends Comparable<T>> TethysUIFXTableListColumn<T, C, R> declareListColumn(final C pId,
                                                                                          final Class<T> pClazz) {
        return new TethysUIFXTableListColumn<>(this, pId, pClazz);
    }

    @Override
    public <T> TethysUIFXTableIconColumn<T, C, R> declareIconColumn(final C pId,
                                                                    final Class<T> pClazz) {
        return new TethysUIFXTableIconColumn<>(this, pId, pClazz);
    }

    @Override
    public void selectRow(final R pItem) {
        theTable.getSelectionModel().select(pItem);
    }

    @Override
    public void scrollSelectedToView() {
        final List<R> mySelected = theTable.getSelectionModel().getSelectedItems();
        final Iterator<R> myIterator = mySelected.iterator();
        if (myIterator.hasNext()) {
            theTable.scrollTo(myIterator.next());
        }
    }

    @Override
    public void selectRowWithScroll(final R pItem) {
        selectRow(pItem);
        scrollSelectedToView();
    }
}
