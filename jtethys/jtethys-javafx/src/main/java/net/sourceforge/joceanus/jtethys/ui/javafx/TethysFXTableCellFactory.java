/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
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
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXStateIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButton.TethysFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButton.TethysFXScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTableManager.TethysFXTableColumn;

/**
 * TableCell implementations built on DataEditFields.
 * @param <I> the column identity
 * @param <R> the table row type
 */
public class TethysFXTableCellFactory<I, R>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Data formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * Constructor.
     */
    public TethysFXTableCellFactory() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysFXTableCellFactory(final TethysDataFormatter pFormatter) {
        theEventManager = new TethysEventManager<>();
        theFormatter = pFormatter;
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
    protected Callback<TableColumn<R, String>, TableCell<R, String>> stringCellFactory(final TethysFXTableColumn<I, R, String> pColumn) {
        return e -> listenToCell(new TethysFXTableStringCell<>(pColumn));
    }

    /**
     * Obtain Short Cell Factory.
     * @param pColumn the column
     * @return the short cell factory
     */
    protected Callback<TableColumn<R, Short>, TableCell<R, Short>> shortCellFactory(final TethysFXTableColumn<I, R, Short> pColumn) {
        return e -> listenToCell(new TethysFXTableShortCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Integer Cell Factory.
     * @param pColumn the column
     * @return the integer cell factory
     */
    protected Callback<TableColumn<R, Integer>, TableCell<R, Integer>> integerCellFactory(final TethysFXTableColumn<I, R, Integer> pColumn) {
        return e -> listenToCell(new TethysFXTableIntegerCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Long Cell Factory.
     * @param pColumn the column
     * @return the long cell factory
     */
    protected Callback<TableColumn<R, Long>, TableCell<R, Long>> longCellFactory(final TethysFXTableColumn<I, R, Long> pColumn) {
        return e -> listenToCell(new TethysFXTableLongCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Money Cell Factory.
     * @param pColumn the column
     * @return the money cell factory
     */
    protected Callback<TableColumn<R, TethysMoney>, TableCell<R, TethysMoney>> moneyCellFactory(final TethysFXTableColumn<I, R, TethysMoney> pColumn) {
        return e -> listenToCell(new TethysFXTableMoneyCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Price Cell Factory.
     * @param pColumn the column
     * @return the price cell factory
     */
    protected Callback<TableColumn<R, TethysPrice>, TableCell<R, TethysPrice>> priceCellFactory(final TethysFXTableColumn<I, R, TethysPrice> pColumn) {
        return e -> listenToCell(new TethysFXTablePriceCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Rate Cell Factory.
     * @param pColumn the column
     * @return the rate cell factory
     */
    protected Callback<TableColumn<R, TethysRate>, TableCell<R, TethysRate>> rateCellFactory(final TethysFXTableColumn<I, R, TethysRate> pColumn) {
        return e -> listenToCell(new TethysFXTableRateCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Units Cell Factory.
     * @param pColumn the column
     * @return the units cell factory
     */
    protected Callback<TableColumn<R, TethysUnits>, TableCell<R, TethysUnits>> unitsCellFactory(final TethysFXTableColumn<I, R, TethysUnits> pColumn) {
        return e -> listenToCell(new TethysFXTableUnitsCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Dilution Cell Factory.
     * @param pColumn the column
     * @return the dilution cell factory
     */
    protected Callback<TableColumn<R, TethysDilution>, TableCell<R, TethysDilution>> dilutionCellFactory(final TethysFXTableColumn<I, R, TethysDilution> pColumn) {
        return e -> listenToCell(new TethysFXTableDilutionCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Ratio Cell Factory.
     * @param pColumn the column
     * @return the ratio cell factory
     */
    protected Callback<TableColumn<R, TethysRatio>, TableCell<R, TethysRatio>> ratioCellFactory(final TethysFXTableColumn<I, R, TethysRatio> pColumn) {
        return e -> listenToCell(new TethysFXTableRatioCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain DilutedPrice Cell Factory.
     * @param pColumn the column
     * @return the dilutedPrice cell factory
     */
    protected Callback<TableColumn<R, TethysDilutedPrice>, TableCell<R, TethysDilutedPrice>> dilutedPriceCellFactory(final TethysFXTableColumn<I, R, TethysDilutedPrice> pColumn) {
        return e -> listenToCell(new TethysFXTableDilutedPriceCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Scroll Cell Factory.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    protected <C> Callback<TableColumn<R, C>, TableCell<R, C>> scrollCellFactory(final TethysFXTableColumn<I, R, C> pColumn,
                                                                                 final Class<C> pClass) {
        return e -> listenToCell(new TethysFXTableScrollCell<>(pColumn, pClass));
    }

    /**
     * Obtain List Cell Factory.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    protected <C> Callback<TableColumn<R, C>, TableCell<R, C>> listCellFactory(final TethysFXTableColumn<I, R, C> pColumn, final Class<C> pClass) {
        return e -> listenToCell(new TethysFXTableListCell<>(pColumn, pClass));
    }

    /**
     * Obtain Date Cell Factory.
     * @param pColumn the column
     * @return the date cell factory
     */
    protected Callback<TableColumn<R, TethysDate>, TableCell<R, TethysDate>> dateCellFactory(final TethysFXTableColumn<I, R, TethysDate> pColumn) {
        return e -> listenToCell(new TethysFXTableDateCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Icon Cell Factory.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    protected <C> Callback<TableColumn<R, C>, TableCell<R, C>> iconCellFactory(final TethysFXTableColumn<I, R, C> pColumn,
                                                                               final Class<C> pClass) {
        return e -> listenToCell(new TethysFXTableIconCell<>(pColumn, pClass));
    }

    /**
     * Obtain State Icon Cell Factory.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    protected <C> Callback<TableColumn<R, C>, TableCell<R, C>> stateIconCellFactory(final TethysFXTableColumn<I, R, C> pColumn,
                                                                                    final Class<C> pClass) {
        return e -> listenToCell(new TethysFXTableStateIconCell<>(pColumn, pClass));
    }

    /**
     * Listen to cell.
     * @param <C> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <C> TethysFXTableCell<I, R, C> listenToCell(final TethysFXTableCell<I, R, C> pCell) {
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * DataCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public abstract static class TethysFXTableCell<I, R, C>
            extends TableCell<R, C>
            implements TethysEventProvider<TethysUIEvent> {
        /**
         * The Column.
         */
        private final TethysFXTableColumn<I, R, C> theColumn;

        /**
         * The Text field.
         */
        private final TethysFXDataTextField<C> theField;

        /**
         * The Data class.
         */
        private final Class<C> theClass;

        /**
         * The Event Manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The new value.
         */
        private C theNewValue;

        /**
         * Constructor.
         * @param pColumn the column
         * @param pField the edit field
         * @param pClass the field class
         */
        protected TethysFXTableCell(final TethysFXTableColumn<I, R, C> pColumn,
                                    final TethysFXDataTextField<C> pField,
                                    final Class<C> pClass) {
            /* Record the parameters */
            theColumn = pColumn;
            theField = pField;
            theClass = pClass;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Set the field as the graphic */
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(theField.getNode());

            /* Add listener to the edit field */
            theField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, this::handleCommit);
            theField.getEventRegistrar().addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> handleCancel());
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Obtain the column.
         * @return the column
         */
        public TethysFXTableColumn<I, R, C> getColumn() {
            return theColumn;
        }

        /**
         * Obtain the field.
         * @return the field
         */
        public TethysFXDataTextField<C> getField() {
            return theField;
        }

        /**
         * Obtain the new value.
         * @return the new value
         */
        public C getNewValue() {
            return theNewValue;
        }

        @Override
        public void startEdit() {
            /* Perform preEdit tasks */
            if (preEditHook()) {
                /* Start the edit */
                super.startEdit();

                /* Set the value of the item */
                theField.setValue(getItem());
                theField.startCellEditing();
            }
        }

        /**
         * obtain the current row.
         * @return the row (or null)
         */
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
        public void updateItem(final C pValue,
                               final boolean pEmpty) {
            /* Update correctly */
            super.updateItem(pValue, pEmpty);

            /* Set details and stop editing */
            theField.setValue(pEmpty
                                     ? null
                                     : pValue);
            theField.setEditable(false);

            /* Format the cell */
            theEventManager.fireEvent(TethysUIEvent.CELLFORMAT, this);
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
        private boolean preCommitHook(final C pNewValue) {
            theNewValue = pNewValue;
            return theEventManager.fireEvent(TethysUIEvent.CELLPRECOMMIT, this);
        }

        @Override
        public void commitEdit(final C pNewValue) {
            /* Perform preCommitCheck */
            if (preCommitHook(pNewValue)) {
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
        private void handleCommit(final TethysEvent<TethysUIEvent> pEvent) {
            commitEdit(pEvent.getDetails(theClass));
        }

        /**
         * Handle cancel.
         */
        private void handleCancel() {
            cancelEdit();
        }
    }

    /**
     * String Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableStringCell<I, R>
            extends TethysFXTableCell<I, R, String> {
        /**
         * Constructor.
         * @param pColumn the column
         */
        protected TethysFXTableStringCell(final TethysFXTableColumn<I, R, String> pColumn) {
            super(pColumn, new TethysFXStringTextField(), String.class);
        }

        @Override
        public TethysFXStringTextField getField() {
            return (TethysFXStringTextField) super.getField();
        }
    }

    /**
     * Short Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableShortCell<I, R>
            extends TethysFXTableCell<I, R, Short> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableShortCell(final TethysFXTableColumn<I, R, Short> pColumn,
                                         final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXShortTextField(pFormatter), Short.class);
        }

        @Override
        public TethysFXShortTextField getField() {
            return (TethysFXShortTextField) super.getField();
        }
    }

    /**
     * Integer Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableIntegerCell<I, R>
            extends TethysFXTableCell<I, R, Integer> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableIntegerCell(final TethysFXTableColumn<I, R, Integer> pColumn,
                                           final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXIntegerTextField(pFormatter), Integer.class);
        }

        @Override
        public TethysFXIntegerTextField getField() {
            return (TethysFXIntegerTextField) super.getField();
        }
    }

    /**
     * Long Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableLongCell<I, R>
            extends TethysFXTableCell<I, R, Long> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableLongCell(final TethysFXTableColumn<I, R, Long> pColumn,
                                        final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXLongTextField(pFormatter), Long.class);
        }

        @Override
        public TethysFXLongTextField getField() {
            return (TethysFXLongTextField) super.getField();
        }
    }

    /**
     * Money Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableMoneyCell<I, R>
            extends TethysFXTableCell<I, R, TethysMoney> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableMoneyCell(final TethysFXTableColumn<I, R, TethysMoney> pColumn,
                                         final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXMoneyTextField(pFormatter), TethysMoney.class);
        }

        @Override
        public TethysFXMoneyTextField getField() {
            return (TethysFXMoneyTextField) super.getField();
        }
    }

    /**
     * Price Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTablePriceCell<I, R>
            extends TethysFXTableCell<I, R, TethysPrice> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTablePriceCell(final TethysFXTableColumn<I, R, TethysPrice> pColumn,
                                         final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXPriceTextField(pFormatter), TethysPrice.class);
        }

        @Override
        public TethysFXPriceTextField getField() {
            return (TethysFXPriceTextField) super.getField();
        }
    }

    /**
     * Rate Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRateCell<I, R>
            extends TethysFXTableCell<I, R, TethysRate> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableRateCell(final TethysFXTableColumn<I, R, TethysRate> pColumn,
                                        final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXRateTextField(pFormatter), TethysRate.class);
        }

        @Override
        public TethysFXRateTextField getField() {
            return (TethysFXRateTextField) super.getField();
        }
    }

    /**
     * Units Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableUnitsCell<I, R>
            extends TethysFXTableCell<I, R, TethysUnits> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableUnitsCell(final TethysFXTableColumn<I, R, TethysUnits> pColumn,
                                         final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXUnitsTextField(pFormatter), TethysUnits.class);
        }

        @Override
        public TethysFXUnitsTextField getField() {
            return (TethysFXUnitsTextField) super.getField();
        }
    }

    /**
     * Dilution Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutionCell<I, R>
            extends TethysFXTableCell<I, R, TethysDilution> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableDilutionCell(final TethysFXTableColumn<I, R, TethysDilution> pColumn,
                                            final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXDilutionTextField(pFormatter), TethysDilution.class);
        }

        @Override
        public TethysFXDilutionTextField getField() {
            return (TethysFXDilutionTextField) super.getField();
        }
    }

    /**
     * DilutedPrice Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDilutedPriceCell<I, R>
            extends TethysFXTableCell<I, R, TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableDilutedPriceCell(final TethysFXTableColumn<I, R, TethysDilutedPrice> pColumn,
                                                final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXDilutedPriceTextField(pFormatter), TethysDilutedPrice.class);
        }

        @Override
        public TethysFXDilutedPriceTextField getField() {
            return (TethysFXDilutedPriceTextField) super.getField();
        }
    }

    /**
     * Ratio Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableRatioCell<I, R>
            extends TethysFXTableCell<I, R, TethysRatio> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableRatioCell(final TethysFXTableColumn<I, R, TethysRatio> pColumn,
                                         final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXRatioTextField(pFormatter), TethysRatio.class);
        }

        @Override
        public TethysFXRatioTextField getField() {
            return (TethysFXRatioTextField) super.getField();
        }
    }

    /**
     * DateCell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysFXTableDateCell<I, R>
            extends TethysFXTableCell<I, R, TethysDate> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysFXTableDateCell(final TethysFXTableColumn<I, R, TethysDate> pColumn,
                                        final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysFXDateButtonField(pFormatter), TethysDate.class);
        }

        @Override
        public TethysFXDateButtonField getField() {
            return (TethysFXDateButtonField) super.getField();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXDateButtonManager getDateManager() {
            return getField().getDateManager();
        }
    }

    /**
     * ScrollCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXTableScrollCell<I, R, C>
            extends TethysFXTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysFXTableScrollCell(final TethysFXTableColumn<I, R, C> pColumn,
                                          final Class<C> pClass) {
            super(pColumn, new TethysFXScrollButtonField<>(), pClass);
        }

        @Override
        public TethysFXScrollButtonField<C> getField() {
            return (TethysFXScrollButtonField<C>) super.getField();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXScrollButtonManager<C> getScrollManager() {
            return getField().getScrollManager();
        }
    }

    /**
     * ListCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXTableListCell<I, R, C>
            extends TethysFXTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysFXTableListCell(final TethysFXTableColumn<I, R, C> pColumn,
                                        final Class<C> pClass) {
            super(pColumn, new TethysFXListButtonField<>(), pClass);
        }

        @Override
        public TethysFXListButtonField<C> getField() {
            return (TethysFXListButtonField<C>) super.getField();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXListButtonManager<C> getListManager() {
            return getField().getListManager();
        }
    }

    /**
     * IconCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXTableIconCell<I, R, C>
            extends TethysFXTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysFXTableIconCell(final TethysFXTableColumn<I, R, C> pColumn,
                                        final Class<C> pClass) {
            super(pColumn, new TethysFXIconButtonField<C>(), pClass);
        }

        @Override
        public TethysFXIconButtonField<C> getField() {
            return (TethysFXIconButtonField<C>) super.getField();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysIconButtonManager<C, Node> getIconManager() {
            return getField().getIconManager();
        }
    }

    /**
     * IconStateCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     * @param <S> the state class
     */
    public static class TethysFXTableStateIconCell<I, R, C, S>
            extends TethysFXTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysFXTableStateIconCell(final TethysFXTableColumn<I, R, C> pColumn,
                                             final Class<C> pClass) {
            super(pColumn, new TethysFXStateIconButtonField<C, S>(), pClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysFXStateIconButtonField<C, S> getField() {
            return (TethysFXStateIconButtonField<C, S>) super.getField();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXStateIconButtonManager<C, S> getIconManager() {
            return getField().getIconManager();
        }
    }
}