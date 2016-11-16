/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.Currency;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysRawDecimalField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableCell;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXStateIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRawDecimalTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableColumn;

/**
 * TableCell implementations built on DataEditFields.
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysFXTableCellFactory<C, R>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The dummy style.
     */
    private static final String STYLE_DUMMY = "DummyStyle";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The GUI Factory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXTableCellFactory(final TethysFXGuiFactory pFactory) {
        theEventManager = new TethysEventManager<>();
        theGuiFactory = pFactory;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain String Cell Factory.
     * @param pColumn the column
     * @return the string cell factory
     */
    protected Callback<TableColumn<R, String>, TableCell<R, String>> stringCellFactory(final TethysFXTableColumn<C, R, String> pColumn) {
        return e -> listenToCell(new TethysFXTableStringCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain CharArray Cell Factory.
     * @param pColumn the column
     * @return the charArray cell factory
     */
    protected Callback<TableColumn<R, char[]>, TableCell<R, char[]>> charArrayCellFactory(final TethysFXTableColumn<C, R, char[]> pColumn) {
        return e -> listenToCell(new TethysFXTableCharArrayCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Short Cell Factory.
     * @param pColumn the column
     * @return the short cell factory
     */
    protected Callback<TableColumn<R, Short>, TableCell<R, Short>> shortCellFactory(final TethysFXTableColumn<C, R, Short> pColumn) {
        return e -> listenToCell(new TethysFXTableShortCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Integer Cell Factory.
     * @param pColumn the column
     * @return the integer cell factory
     */
    protected Callback<TableColumn<R, Integer>, TableCell<R, Integer>> integerCellFactory(final TethysFXTableColumn<C, R, Integer> pColumn) {
        return e -> listenToCell(new TethysFXTableIntegerCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Long Cell Factory.
     * @param pColumn the column
     * @return the long cell factory
     */
    protected Callback<TableColumn<R, Long>, TableCell<R, Long>> longCellFactory(final TethysFXTableColumn<C, R, Long> pColumn) {
        return e -> listenToCell(new TethysFXTableLongCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain RawDecimal Cell Factory.
     * @param pColumn the column
     * @return the rawDecimal cell factory
     */
    protected Callback<TableColumn<R, TethysDecimal>, TableCell<R, TethysDecimal>> rawDecimalCellFactory(final TethysFXTableColumn<C, R, TethysDecimal> pColumn) {
        return e -> listenToCell(new TethysFXTableRawDecimalCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Money Cell Factory.
     * @param pColumn the column
     * @return the money cell factory
     */
    protected Callback<TableColumn<R, TethysMoney>, TableCell<R, TethysMoney>> moneyCellFactory(final TethysFXTableColumn<C, R, TethysMoney> pColumn) {
        return e -> listenToCell(new TethysFXTableMoneyCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Price Cell Factory.
     * @param pColumn the column
     * @return the price cell factory
     */
    protected Callback<TableColumn<R, TethysPrice>, TableCell<R, TethysPrice>> priceCellFactory(final TethysFXTableColumn<C, R, TethysPrice> pColumn) {
        return e -> listenToCell(new TethysFXTablePriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Rate Cell Factory.
     * @param pColumn the column
     * @return the rate cell factory
     */
    protected Callback<TableColumn<R, TethysRate>, TableCell<R, TethysRate>> rateCellFactory(final TethysFXTableColumn<C, R, TethysRate> pColumn) {
        return e -> listenToCell(new TethysFXTableRateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Units Cell Factory.
     * @param pColumn the column
     * @return the units cell factory
     */
    protected Callback<TableColumn<R, TethysUnits>, TableCell<R, TethysUnits>> unitsCellFactory(final TethysFXTableColumn<C, R, TethysUnits> pColumn) {
        return e -> listenToCell(new TethysFXTableUnitsCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Dilution Cell Factory.
     * @param pColumn the column
     * @return the dilution cell factory
     */
    protected Callback<TableColumn<R, TethysDilution>, TableCell<R, TethysDilution>> dilutionCellFactory(final TethysFXTableColumn<C, R, TethysDilution> pColumn) {
        return e -> listenToCell(new TethysFXTableDilutionCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Ratio Cell Factory.
     * @param pColumn the column
     * @return the ratio cell factory
     */
    protected Callback<TableColumn<R, TethysRatio>, TableCell<R, TethysRatio>> ratioCellFactory(final TethysFXTableColumn<C, R, TethysRatio> pColumn) {
        return e -> listenToCell(new TethysFXTableRatioCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain DilutedPrice Cell Factory.
     * @param pColumn the column
     * @return the dilutedPrice cell factory
     */
    protected Callback<TableColumn<R, TethysDilutedPrice>, TableCell<R, TethysDilutedPrice>> dilutedPriceCellFactory(final TethysFXTableColumn<C, R, TethysDilutedPrice> pColumn) {
        return e -> listenToCell(new TethysFXTableDilutedPriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Scroll Cell Factory.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    protected <T> Callback<TableColumn<R, T>, TableCell<R, T>> scrollCellFactory(final TethysFXTableColumn<C, R, T> pColumn,
                                                                                 final Class<T> pClass) {
        return e -> listenToCell(new TethysFXTableScrollCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain List Cell Factory.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    protected <T> Callback<TableColumn<R, TethysItemList<T>>, TableCell<R, TethysItemList<T>>> listCellFactory(final TethysFXTableColumn<C, R, TethysItemList<T>> pColumn,
                                                                                                               final Class<T> pClass) {
        return e -> listenToCell(new TethysFXTableListCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain Date Cell Factory.
     * @param pColumn the column
     * @return the date cell factory
     */
    protected Callback<TableColumn<R, TethysDate>, TableCell<R, TethysDate>> dateCellFactory(final TethysFXTableColumn<C, R, TethysDate> pColumn) {
        return e -> listenToCell(new TethysFXTableDateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Icon Cell Factory.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    protected <T> Callback<TableColumn<R, T>, TableCell<R, T>> iconCellFactory(final TethysFXTableColumn<C, R, T> pColumn,
                                                                               final Class<T> pClass) {
        return e -> listenToCell(new TethysFXTableIconCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain State Icon Cell Factory.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    protected <T> Callback<TableColumn<R, T>, TableCell<R, T>> stateIconCellFactory(final TethysFXTableColumn<C, R, T> pColumn,
                                                                                    final Class<T> pClass) {
        return e -> listenToCell(new TethysFXTableStateIconCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Listen to cell.
     * @param <T> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <T> TethysFXTableCell<C, R, T> listenToCell(final TethysFXTableCell<C, R, T> pCell) {
        theEventManager.fireEvent(TethysUIEvent.CELLCREATE, pCell);
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * DataCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public abstract static class TethysFXTableCell<C, R, T>
            extends TableCell<R, T>
            implements TethysEventProvider<TethysUIEvent>, TethysTableCell<T, C, R, Node, Node> {
        /**
         * The Column.
         */
        private final TethysFXTableColumn<C, R, T> theColumn;

        /**
         * The Control field.
         */
        private final TethysFXDataTextField<T> theControl;

        /**
         * The Data class.
         */
        private final Class<T> theClass;

        /**
         * The Event Manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The new value.
         */
        private T theNewValue;

        /**
         * Constructor.
         * @param pColumn the column
         * @param pField the edit field
         */
        protected TethysFXTableCell(final TethysFXTableColumn<C, R, T> pColumn,
                                    final TethysFXDataTextField<T> pField) {
            /* Record the parameters */
            this(pColumn, pField, null);
        }

        /**
         * Constructor.
         * @param pColumn the column
         * @param pControl the edit control
         * @param pClass the field class
         */
        protected TethysFXTableCell(final TethysFXTableColumn<C, R, T> pColumn,
                                    final TethysFXDataTextField<T> pControl,
                                    final Class<T> pClass) {
            /* Record the parameters */
            theColumn = pColumn;
            theControl = pControl;
            theClass = pClass;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Set the field as the graphic */
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(theControl.getNode());

            /* Add listener to the edit field */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theControl.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, this::handleCommit);
            myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> handleCancel());
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        @Override
        public TethysFXTableManager<C, R> getTable() {
            return theColumn.getTable();
        }

        @Override
        public TethysFXTableColumn<C, R, T> getColumn() {
            return theColumn;
        }

        @Override
        public C getColumnId() {
            return theColumn.getId();
        }

        @Override
        public TethysFXDataTextField<T> getControl() {
            return theControl;
        }

        @Override
        public TethysFieldType getCellType() {
            return theColumn.getCellType();
        }

        @Override
        public T getNewValue() {
            return theNewValue;
        }

        @Override
        public void startEdit() {
            /* Perform preEdit tasks */
            if (preEditHook()) {
                /* Start the edit */
                super.startEdit();

                /* Set the value of the item */
                theControl.setValue(getItem());
                theControl.startCellEditing(theControl.getLabel());
            }
        }

        @Override
        public R getActiveRow() {
            /* Access list and determine size */
            ObservableList<R> myItems = getTableView().getItems();
            int mySize = myItems == null
                                         ? 0
                                         : myItems.size();

            /* Access list and determine size */
            TableRow<?> myRow = getTableRow();
            int myIndex = myRow == null
                                        ? -1
                                        : myRow.getIndex();

            /* Access explicit item */
            return (myIndex < 0) || (myIndex >= mySize)
                                                        ? null
                                                        : myItems.get(myIndex);
        }

        @Override
        public void updateItem(final T pValue,
                               final boolean pEmpty) {
            /* Update correctly */
            super.updateItem(pValue, pEmpty);

            /* Format the cell */
            if (!pEmpty) {
                /* Access table details */
                TethysFXTableManager<C, R> myTable = getTable();
                C myId = getColumnId();
                R myRow = getActiveRow();

                /* Set changed and disabled attributes */
                theControl.setTheAttributeState(TethysFieldAttribute.CHANGED, myTable.isChanged(myId, myRow));
                theControl.setTheAttributeState(TethysFieldAttribute.DISABLED, myTable.isDisabled(myRow));
                theControl.setTheAttributeState(TethysFieldAttribute.ERROR, myTable.isError(myId, myRow));
            }

            /* Set details and stop editing */
            theControl.setValue(pEmpty
                                       ? null
                                       : pValue);
            theControl.setEditable(false);
        }

        /**
         * Method that determines whether a cell is editable and prepares for edit.
         * @return continue edit true/false
         */
        private boolean preEditHook() {
            return theEventManager.fireEvent(TethysUIEvent.CELLPREEDIT, this);
        }

        /**
         * Method that determines whether a value is valid prior to commit.
         * @param pNewValue the new value
         * @return continue edit true/false
         */
        private boolean preCommitHook(final T pNewValue) {
            theNewValue = pNewValue;
            return theEventManager.fireEvent(TethysUIEvent.CELLPRECOMMIT, this);
        }

        @Override
        public void commitEdit(final T pNewValue) {
            /* Perform preCommitCheck */
            if (!theControl.isAttributeSet(TethysFieldAttribute.ERROR)
                && preCommitHook(pNewValue)) {
                /* pass on the call */
                super.commitEdit(pNewValue);

                /* Note that we have made a change */
                postCommitHook();
            }
        }

        /**
         * Method that should be overridden to provide control over postProcessing of a commit.
         */
        protected void postCommitHook() {
            theEventManager.fireEvent(TethysUIEvent.CELLCOMMITTED, this);
        }

        /**
         * handle Commit.
         * @param pEvent the event
         */
        protected void handleCommit(final TethysEvent<TethysUIEvent> pEvent) {
            commitEdit(pEvent.getDetails(theClass));
        }

        /**
         * Handle cancel.
         */
        private void handleCancel() {
            cancelEdit();
        }

        @Override
        public void repaintColumnCell(final C pId) {
            TethysFXTableManager<C, R> myTable = theColumn.getTable();
            myTable.repaintColumn(pId);
        }

        @Override
        public void repaintCellRow() {
            TableRow<?> myRow = getTableRow();
            List<String> myClasses = myRow.getStyleClass();
            myClasses.add(STYLE_DUMMY);
            myClasses.remove(STYLE_DUMMY);
        }
    }

    /**
     * String Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableStringCell<C, R>
            extends TethysFXTableCell<C, R, String> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableStringCell(final TethysFXTableColumn<C, R, String> pColumn,
                                          final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newStringField(), String.class);
        }

        @Override
        public TethysFXStringTextField getControl() {
            return (TethysFXStringTextField) super.getControl();
        }
    }

    /**
     * CharArray Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableCharArrayCell<C, R>
            extends TethysFXTableCell<C, R, char[]> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableCharArrayCell(final TethysFXTableColumn<C, R, char[]> pColumn,
                                             final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newCharArrayField(), char[].class);
        }

        @Override
        public TethysFXCharArrayTextField getControl() {
            return (TethysFXCharArrayTextField) super.getControl();
        }
    }

    /**
     * Short Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableShortCell<C, R>
            extends TethysFXTableCell<C, R, Short> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableShortCell(final TethysFXTableColumn<C, R, Short> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newShortField(), Short.class);
        }

        @Override
        public TethysFXShortTextField getControl() {
            return (TethysFXShortTextField) super.getControl();
        }
    }

    /**
     * Integer Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIntegerCell<C, R>
            extends TethysFXTableCell<C, R, Integer> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableIntegerCell(final TethysFXTableColumn<C, R, Integer> pColumn,
                                           final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newIntegerField(), Integer.class);
        }

        @Override
        public TethysFXIntegerTextField getControl() {
            return (TethysFXIntegerTextField) super.getControl();
        }
    }

    /**
     * Long Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableLongCell<C, R>
            extends TethysFXTableCell<C, R, Long> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableLongCell(final TethysFXTableColumn<C, R, Long> pColumn,
                                        final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newLongField(), Long.class);
        }

        @Override
        public TethysFXLongTextField getControl() {
            return (TethysFXLongTextField) super.getControl();
        }
    }

    /**
     * RawDecimal Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRawDecimalCell<C, R>
            extends TethysFXTableCell<C, R, TethysDecimal>
            implements TethysRawDecimalField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableRawDecimalCell(final TethysFXTableColumn<C, R, TethysDecimal> pColumn,
                                              final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newRawDecimalField(), TethysDecimal.class);
        }

        @Override
        public TethysFXRawDecimalTextField getControl() {
            return (TethysFXRawDecimalTextField) super.getControl();
        }

        @Override
        public void setNumDecimals(final int pNumDecimals) {
            getControl().setNumDecimals(pNumDecimals);
        }
    }

    /**
     * Money Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableMoneyCell<C, R>
            extends TethysFXTableCell<C, R, TethysMoney>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableMoneyCell(final TethysFXTableColumn<C, R, TethysMoney> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newMoneyField(), TethysMoney.class);
        }

        @Override
        public TethysFXMoneyTextField getControl() {
            return (TethysFXMoneyTextField) super.getControl();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getControl().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Price Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTablePriceCell<C, R>
            extends TethysFXTableCell<C, R, TethysPrice>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTablePriceCell(final TethysFXTableColumn<C, R, TethysPrice> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newPriceField(), TethysPrice.class);
        }

        @Override
        public TethysFXPriceTextField getControl() {
            return (TethysFXPriceTextField) super.getControl();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getControl().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Rate Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRateCell<C, R>
            extends TethysFXTableCell<C, R, TethysRate> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableRateCell(final TethysFXTableColumn<C, R, TethysRate> pColumn,
                                        final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newRateField(), TethysRate.class);
        }

        @Override
        public TethysFXRateTextField getControl() {
            return (TethysFXRateTextField) super.getControl();
        }
    }

    /**
     * Units Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableUnitsCell<C, R>
            extends TethysFXTableCell<C, R, TethysUnits> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableUnitsCell(final TethysFXTableColumn<C, R, TethysUnits> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newUnitsField(), TethysUnits.class);
        }

        @Override
        public TethysFXUnitsTextField getControl() {
            return (TethysFXUnitsTextField) super.getControl();
        }
    }

    /**
     * Dilution Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutionCell<C, R>
            extends TethysFXTableCell<C, R, TethysDilution> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableDilutionCell(final TethysFXTableColumn<C, R, TethysDilution> pColumn,
                                            final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutionField(), TethysDilution.class);
        }

        @Override
        public TethysFXDilutionTextField getControl() {
            return (TethysFXDilutionTextField) super.getControl();
        }
    }

    /**
     * DilutedPrice Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutedPriceCell<C, R>
            extends TethysFXTableCell<C, R, TethysDilutedPrice>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableDilutedPriceCell(final TethysFXTableColumn<C, R, TethysDilutedPrice> pColumn,
                                                final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutedPriceField(), TethysDilutedPrice.class);
        }

        @Override
        public TethysFXDilutedPriceTextField getControl() {
            return (TethysFXDilutedPriceTextField) super.getControl();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getControl().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Ratio Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRatioCell<C, R>
            extends TethysFXTableCell<C, R, TethysRatio> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableRatioCell(final TethysFXTableColumn<C, R, TethysRatio> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newRatioField(), TethysRatio.class);
        }

        @Override
        public TethysFXRatioTextField getControl() {
            return (TethysFXRatioTextField) super.getControl();
        }
    }

    /**
     * DateCell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDateCell<C, R>
            extends TethysFXTableCell<C, R, TethysDate>
            implements TethysDateField<Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableDateCell(final TethysFXTableColumn<C, R, TethysDate> pColumn,
                                        final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newDateField(), TethysDate.class);
        }

        @Override
        public TethysFXDateButtonField getControl() {
            return (TethysFXDateButtonField) super.getControl();
        }

        @Override
        public TethysFXDateButtonManager getDateManager() {
            return getControl().getDateManager();
        }
    }

    /**
     * ScrollCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysFXTableScrollCell<C, R, T>
            extends TethysFXTableCell<C, R, T>
            implements TethysScrollField<T, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableScrollCell(final TethysFXTableColumn<C, R, T> pColumn,
                                          final TethysFXGuiFactory pFactory,
                                          final Class<T> pClass) {
            super(pColumn, pFactory.newScrollField(), pClass);
        }

        @Override
        public TethysFXScrollButtonField<T> getControl() {
            return (TethysFXScrollButtonField<T>) super.getControl();
        }

        @Override
        public TethysFXScrollButtonManager<T> getScrollManager() {
            return getControl().getScrollManager();
        }
    }

    /**
     * ListCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysFXTableListCell<C, R, T>
            extends TethysFXTableCell<C, R, TethysItemList<T>>
            implements TethysListField<T, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableListCell(final TethysFXTableColumn<C, R, TethysItemList<T>> pColumn,
                                        final TethysFXGuiFactory pFactory,
                                        final Class<T> pClass) {
            super(pColumn, pFactory.newListField());
        }

        @Override
        public TethysFXListButtonField<T> getControl() {
            return (TethysFXListButtonField<T>) super.getControl();
        }

        @Override
        public TethysFXListButtonManager<T> getListManager() {
            return getControl().getListManager();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void handleCommit(final TethysEvent<TethysUIEvent> pEvent) {
            commitEdit(pEvent.getDetails(TethysItemList.class));
        }
    }

    /**
     * IconCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysFXTableIconCell<C, R, T>
            extends TethysFXTableCell<C, R, T>
            implements TethysIconField<T, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableIconCell(final TethysFXTableColumn<C, R, T> pColumn,
                                        final TethysFXGuiFactory pFactory,
                                        final Class<T> pClass) {
            super(pColumn, pFactory.newSimpleIconField(), pClass);
        }

        @Override
        public TethysFXIconButtonField<T> getControl() {
            return (TethysFXIconButtonField<T>) super.getControl();
        }

        @Override
        public TethysSimpleIconButtonManager<T, Node, Node> getIconManager() {
            return getControl().getIconManager();
        }
    }

    /**
     * IconStateCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     * @param <S> the state class
     */
    public static class TethysFXTableStateIconCell<C, R, T, S>
            extends TethysFXTableCell<C, R, T>
            implements TethysStateIconField<T, S, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableStateIconCell(final TethysFXTableColumn<C, R, T> pColumn,
                                             final TethysFXGuiFactory pFactory,
                                             final Class<T> pClass) {
            super(pColumn, pFactory.newStateIconField(), pClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysFXStateIconButtonField<T, S> getControl() {
            return (TethysFXStateIconButtonField<T, S>) super.getControl();
        }

        @Override
        public TethysFXStateIconButtonManager<T, S> getIconManager() {
            return getControl().getIconManager();
        }
    }
}
