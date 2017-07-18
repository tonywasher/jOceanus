/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

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
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXTextEditField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableRawDecimalColumn;

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
    protected Callback<TableColumn<R, String>, TableCell<R, String>> stringCellFactory(final TethysFXTableColumn<String, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableStringCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain CharArray Cell Factory.
     * @param pColumn the column
     * @return the charArray cell factory
     */
    protected Callback<TableColumn<R, char[]>, TableCell<R, char[]>> charArrayCellFactory(final TethysFXTableColumn<char[], C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableCharArrayCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Short Cell Factory.
     * @param pColumn the column
     * @return the short cell factory
     */
    protected Callback<TableColumn<R, Short>, TableCell<R, Short>> shortCellFactory(final TethysFXTableColumn<Short, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableShortCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Integer Cell Factory.
     * @param pColumn the column
     * @return the integer cell factory
     */
    protected Callback<TableColumn<R, Integer>, TableCell<R, Integer>> integerCellFactory(final TethysFXTableColumn<Integer, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableIntegerCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Long Cell Factory.
     * @param pColumn the column
     * @return the long cell factory
     */
    protected Callback<TableColumn<R, Long>, TableCell<R, Long>> longCellFactory(final TethysFXTableColumn<Long, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableLongCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain RawDecimal Cell Factory.
     * @param pColumn the column
     * @return the rawDecimal cell factory
     */
    protected Callback<TableColumn<R, TethysDecimal>, TableCell<R, TethysDecimal>> rawDecimalCellFactory(final TethysFXTableColumn<TethysDecimal, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableRawDecimalCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Money Cell Factory.
     * @param pColumn the column
     * @return the money cell factory
     */
    protected Callback<TableColumn<R, TethysMoney>, TableCell<R, TethysMoney>> moneyCellFactory(final TethysFXTableColumn<TethysMoney, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableMoneyCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Price Cell Factory.
     * @param pColumn the column
     * @return the price cell factory
     */
    protected Callback<TableColumn<R, TethysPrice>, TableCell<R, TethysPrice>> priceCellFactory(final TethysFXTableColumn<TethysPrice, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTablePriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Rate Cell Factory.
     * @param pColumn the column
     * @return the rate cell factory
     */
    protected Callback<TableColumn<R, TethysRate>, TableCell<R, TethysRate>> rateCellFactory(final TethysFXTableColumn<TethysRate, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableRateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Units Cell Factory.
     * @param pColumn the column
     * @return the units cell factory
     */
    protected Callback<TableColumn<R, TethysUnits>, TableCell<R, TethysUnits>> unitsCellFactory(final TethysFXTableColumn<TethysUnits, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableUnitsCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Dilution Cell Factory.
     * @param pColumn the column
     * @return the dilution cell factory
     */
    protected Callback<TableColumn<R, TethysDilution>, TableCell<R, TethysDilution>> dilutionCellFactory(final TethysFXTableColumn<TethysDilution, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableDilutionCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Ratio Cell Factory.
     * @param pColumn the column
     * @return the ratio cell factory
     */
    protected Callback<TableColumn<R, TethysRatio>, TableCell<R, TethysRatio>> ratioCellFactory(final TethysFXTableColumn<TethysRatio, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableRatioCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain DilutedPrice Cell Factory.
     * @param pColumn the column
     * @return the dilutedPrice cell factory
     */
    protected Callback<TableColumn<R, TethysDilutedPrice>, TableCell<R, TethysDilutedPrice>> dilutedPriceCellFactory(final TethysFXTableColumn<TethysDilutedPrice, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableDilutedPriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Scroll Cell Factory.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    protected <T> Callback<TableColumn<R, T>, TableCell<R, T>> scrollCellFactory(final TethysFXTableColumn<T, C, R> pColumn,
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
    protected <T> Callback<TableColumn<R, TethysItemList<T>>, TableCell<R, TethysItemList<T>>> listCellFactory(final TethysFXTableColumn<TethysItemList<T>, C, R> pColumn,
                                                                                                               final Class<T> pClass) {
        return e -> listenToCell(new TethysFXTableListCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain Date Cell Factory.
     * @param pColumn the column
     * @return the date cell factory
     */
    protected Callback<TableColumn<R, TethysDate>, TableCell<R, TethysDate>> dateCellFactory(final TethysFXTableColumn<TethysDate, C, R> pColumn) {
        return e -> listenToCell(new TethysFXTableDateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Icon Cell Factory.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    protected <T> Callback<TableColumn<R, T>, TableCell<R, T>> iconCellFactory(final TethysFXTableColumn<T, C, R> pColumn,
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
    protected <T> Callback<TableColumn<R, T>, TableCell<R, T>> stateIconCellFactory(final TethysFXTableColumn<T, C, R> pColumn,
                                                                                    final Class<T> pClass) {
        return e -> listenToCell(new TethysFXTableStateIconCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Listen to cell.
     * @param <T> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <T> TethysFXTableCell<T, C, R> listenToCell(final TethysFXTableCell<T, C, R> pCell) {
        theEventManager.fireEvent(TethysUIEvent.CELLCREATE, pCell);
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * DataCell.
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public abstract static class TethysFXTableCell<T, C, R>
            extends TableCell<R, T>
            implements TethysEventProvider<TethysUIEvent>, TethysTableCell<T, C, R, Node, Node> {
        /**
         * The Column.
         */
        private final TethysFXTableColumn<T, C, R> theColumn;

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
        protected TethysFXTableCell(final TethysFXTableColumn<T, C, R> pColumn,
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
        protected TethysFXTableCell(final TethysFXTableColumn<T, C, R> pColumn,
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

            /* Apply validator to a text field */
            if (theControl instanceof TethysFXTextEditField) {
                TethysFXTextEditField<T> myField = (TethysFXTextEditField<T>) theControl;
                myField.setValidator(t -> theColumn.getValidator().apply(t, getActiveRow()));
            }
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
        public TethysFXTableColumn<T, C, R> getColumn() {
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

        /**
         * Is the cell in error?
         * @return true/false
         */
        protected boolean isCellInError() {
            return theControl.isAttributeSet(TethysFieldAttribute.ERROR);
        }

        /**
         * Is the cell editable?
         * @return true/false
         */
        protected boolean isCellEditable() {
            /* Check for table, column and cell locks */
            return !getTable().isEditLocked()
                   && theColumn.isEditable()
                   && theColumn.getCellEditable().test(getActiveRow());
        }

        @Override
        public void startEdit() {
            /* Perform preEdit tasks */
            if (isCellEditable()) {
                /* Record the active cell */
                getTable().setActiveCell(this);

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
            if (!isCellInError()
                && preCommitHook(pNewValue)) {
                /* pass on the call */
                super.commitEdit(pNewValue);

                /* Note that we have made a change */
                postCommitHook();
                getTable().setActiveCell(null);
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
            getTable().setActiveCell(null);
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
            extends TethysFXTableCell<String, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableStringCell(final TethysFXTableColumn<String, C, R> pColumn,
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
            extends TethysFXTableCell<char[], C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableCharArrayCell(final TethysFXTableColumn<char[], C, R> pColumn,
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
            extends TethysFXTableCell<Short, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableShortCell(final TethysFXTableColumn<Short, C, R> pColumn,
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
            extends TethysFXTableCell<Integer, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableIntegerCell(final TethysFXTableColumn<Integer, C, R> pColumn,
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
            extends TethysFXTableCell<Long, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableLongCell(final TethysFXTableColumn<Long, C, R> pColumn,
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
            extends TethysFXTableCell<TethysDecimal, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableRawDecimalCell(final TethysFXTableColumn<TethysDecimal, C, R> pColumn,
                                              final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newRawDecimalField(), TethysDecimal.class);
            getControl().setNumDecimals(() -> getColumn().getNumDecimals().apply(getActiveRow()));
        }

        @Override
        public TethysFXTableRawDecimalColumn<C, R> getColumn() {
            return (TethysFXTableRawDecimalColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysFXRawDecimalTextField getControl() {
            return (TethysFXRawDecimalTextField) super.getControl();
        }
    }

    /**
     * Money Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableMoneyCell<C, R>
            extends TethysFXTableCell<TethysMoney, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableMoneyCell(final TethysFXTableColumn<TethysMoney, C, R> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newMoneyField(), TethysMoney.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysFXTableMoneyColumn<C, R> getColumn() {
            return (TethysFXTableMoneyColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysFXMoneyTextField getControl() {
            return (TethysFXMoneyTextField) super.getControl();
        }
    }

    /**
     * Price Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTablePriceCell<C, R>
            extends TethysFXTableCell<TethysPrice, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTablePriceCell(final TethysFXTableColumn<TethysPrice, C, R> pColumn,
                                         final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newPriceField(), TethysPrice.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysFXTablePriceColumn<C, R> getColumn() {
            return (TethysFXTablePriceColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysFXPriceTextField getControl() {
            return (TethysFXPriceTextField) super.getControl();
        }
    }

    /**
     * Rate Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRateCell<C, R>
            extends TethysFXTableCell<TethysRate, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableRateCell(final TethysFXTableColumn<TethysRate, C, R> pColumn,
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
            extends TethysFXTableCell<TethysUnits, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableUnitsCell(final TethysFXTableColumn<TethysUnits, C, R> pColumn,
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
            extends TethysFXTableCell<TethysDilution, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableDilutionCell(final TethysFXTableColumn<TethysDilution, C, R> pColumn,
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
            extends TethysFXTableCell<TethysDilutedPrice, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableDilutedPriceCell(final TethysFXTableColumn<TethysDilutedPrice, C, R> pColumn,
                                                final TethysFXGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutedPriceField(), TethysDilutedPrice.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysFXTableDilutedPriceColumn<C, R> getColumn() {
            return (TethysFXTableDilutedPriceColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysFXDilutedPriceTextField getControl() {
            return (TethysFXDilutedPriceTextField) super.getControl();
        }
    }

    /**
     * Ratio Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRatioCell<C, R>
            extends TethysFXTableCell<TethysRatio, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableRatioCell(final TethysFXTableColumn<TethysRatio, C, R> pColumn,
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
            extends TethysFXTableCell<TethysDate, C, R>
            implements TethysDateField<Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysFXTableDateCell(final TethysFXTableColumn<TethysDate, C, R> pColumn,
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
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableScrollCell<T, C, R>
            extends TethysFXTableCell<T, C, R>
            implements TethysScrollField<T, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableScrollCell(final TethysFXTableColumn<T, C, R> pColumn,
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
    public static class TethysFXTableListCell<T, C, R>
            extends TethysFXTableCell<TethysItemList<T>, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableListCell(final TethysFXTableColumn<TethysItemList<T>, C, R> pColumn,
                                        final TethysFXGuiFactory pFactory,
                                        final Class<T> pClass) {
            super(pColumn, pFactory.newListField());
        }

        @Override
        public TethysFXListButtonField<T> getControl() {
            return (TethysFXListButtonField<T>) super.getControl();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void handleCommit(final TethysEvent<TethysUIEvent> pEvent) {
            commitEdit(pEvent.getDetails(TethysItemList.class));
        }
    }

    /**
     * IconCell.
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIconCell<T, C, R>
            extends TethysFXTableCell<T, C, R>
            implements TethysIconField<T, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableIconCell(final TethysFXTableColumn<T, C, R> pColumn,
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
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <S> the state class
     */
    public static class TethysFXTableStateIconCell<T, C, R, S>
            extends TethysFXTableCell<T, C, R>
            implements TethysStateIconField<T, S, Node, Node> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysFXTableStateIconCell(final TethysFXTableColumn<T, C, R> pColumn,
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
