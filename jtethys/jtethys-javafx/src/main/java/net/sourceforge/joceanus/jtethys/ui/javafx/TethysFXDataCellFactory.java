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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

/**
 * TableCell implementations built on DataEditFields.
 */
public class TethysFXDataCellFactory
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
    public TethysFXDataCellFactory() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysFXDataCellFactory(final TethysDataFormatter pFormatter) {
        theEventManager = new TethysEventManager<>();
        theFormatter = pFormatter;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain String Cell Factory.
     * @param <T> the table row type
     * @return the string cell factory
     */
    public <T> Callback<TableColumn<T, String>, TableCell<T, String>> stringCellFactory() {
        return e -> listenToCell(new TethysFXDataStringCell<>());
    }

    /**
     * Obtain Short Cell Factory.
     * @param <T> the table row type
     * @return the short cell factory
     */
    public <T> Callback<TableColumn<T, Short>, TableCell<T, Short>> shortCellFactory() {
        return e -> listenToCell(new TethysFXDataShortCell<>(theFormatter));
    }

    /**
     * Obtain Integer Cell Factory.
     * @param <T> the table row type
     * @return the integer cell factory
     */
    public <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>> integerCellFactory() {
        return e -> listenToCell(new TethysFXDataIntegerCell<>(theFormatter));
    }

    /**
     * Obtain Long Cell Factory.
     * @param <T> the table row type
     * @return the long cell factory
     */
    public <T> Callback<TableColumn<T, Long>, TableCell<T, Long>> longCellFactory() {
        return e -> listenToCell(new TethysFXDataLongCell<>(theFormatter));
    }

    /**
     * Obtain Money Cell Factory.
     * @param <T> the table row type
     * @return the money cell factory
     */
    public <T> Callback<TableColumn<T, TethysMoney>, TableCell<T, TethysMoney>> moneyCellFactory() {
        return e -> listenToCell(new TethysFXDataMoneyCell<>(theFormatter));
    }

    /**
     * Obtain Price Cell Factory.
     * @param <T> the table row type
     * @return the price cell factory
     */
    public <T> Callback<TableColumn<T, TethysPrice>, TableCell<T, TethysPrice>> priceCellFactory() {
        return e -> listenToCell(new TethysFXDataPriceCell<>(theFormatter));
    }

    /**
     * Obtain Rate Cell Factory.
     * @param <T> the table row type
     * @return the rate cell factory
     */
    public <T> Callback<TableColumn<T, TethysRate>, TableCell<T, TethysRate>> rateCellFactory() {
        return e -> listenToCell(new TethysFXDataRateCell<>(theFormatter));
    }

    /**
     * Obtain Units Cell Factory.
     * @param <T> the table row type
     * @return the units cell factory
     */
    public <T> Callback<TableColumn<T, TethysUnits>, TableCell<T, TethysUnits>> unitsCellFactory() {
        return e -> listenToCell(new TethysFXDataUnitsCell<>(theFormatter));
    }

    /**
     * Obtain Dilution Cell Factory.
     * @param <T> the table row type
     * @return the dilution cell factory
     */
    public <T> Callback<TableColumn<T, TethysDilution>, TableCell<T, TethysDilution>> dilutionCellFactory() {
        return e -> listenToCell(new TethysFXDataDilutionCell<>(theFormatter));
    }

    /**
     * Obtain Ratio Cell Factory.
     * @param <T> the table row type
     * @return the ratio cell factory
     */
    public <T> Callback<TableColumn<T, TethysRatio>, TableCell<T, TethysRatio>> ratioCellFactory() {
        return e -> listenToCell(new TethysFXDataRatioCell<>(theFormatter));
    }

    /**
     * Obtain DilutedPrice Cell Factory.
     * @param <T> the table row type
     * @return the dilutedPrice cell factory
     */
    public <T> Callback<TableColumn<T, TethysDilutedPrice>, TableCell<T, TethysDilutedPrice>> dilutedPriceCellFactory() {
        return e -> listenToCell(new TethysFXDataDilutedPriceCell<>(theFormatter));
    }

    /**
     * Obtain Scroll Cell Factory.
     * @param <T> the table row type
     * @param <C> the column type
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    public <T, C> Callback<TableColumn<T, C>, TableCell<T, C>> scrollCellFactory(final Class<C> pClass) {
        return e -> listenToCell(new TethysFXDataScrollCell<>(pClass));
    }

    /**
     * Obtain List Cell Factory.
     * @param <T> the table row type
     * @param <C> the column type
     * @param pClass the class of the item
     * @return the scroll cell factory
     */
    public <T, C> Callback<TableColumn<T, C>, TableCell<T, C>> listCellFactory(final Class<C> pClass) {
        return e -> listenToCell(new TethysFXDataListCell<>(pClass));
    }

    /**
     * Obtain Date Cell Factory.
     * @param <T> the table row type
     * @return the date cell factory
     */
    public <T> Callback<TableColumn<T, TethysDate>, TableCell<T, TethysDate>> dateCellFactory() {
        return e -> listenToCell(new TethysFXDataDateCell<>(theFormatter));
    }

    /**
     * Obtain Icon Cell Factory.
     * @param <T> the table row type
     * @param <C> the column type
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    public <T, C> Callback<TableColumn<T, C>, TableCell<T, C>> iconCellFactory(final Class<C> pClass) {
        return e -> listenToCell(new TethysFXDataIconCell<>(pClass));
    }

    /**
     * Obtain State Icon Cell Factory.
     * @param <T> the table row type
     * @param <C> the column type
     * @param pClass the class of the item
     * @return the icon cell factory
     */
    public <T, C> Callback<TableColumn<T, C>, TableCell<T, C>> stateIconCellFactory(final Class<C> pClass) {
        return e -> listenToCell(new TethysFXDataStateIconCell<>(pClass));
    }

    /**
     * Listen to cell.
     * @param <T> the table row type
     * @param <C> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <T, C> TethysFXDataTextCell<T, C> listenToCell(final TethysFXDataTextCell<T, C> pCell) {
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * TextCell.
     * @param <T> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXDataTextCell<T, C>
            extends TableCell<T, C>
            implements TethysEventProvider<TethysUIEvent> {
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
         * Constructor.
         * @param pField the edit field
         * @param pClass the field class
         */
        protected TethysFXDataTextCell(final TethysFXDataTextField<C> pField,
                                       final Class<C> pClass) {
            /* Record the parameters */
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
         * Obtain the field.
         * @return the field
         */
        protected TethysFXDataTextField<C> getField() {
            return theField;
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
        public T getCurrentRow() {
            /* Access list and determine size */
            ObservableList<T> myItems = getTableView().getItems();
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
         * Set the font.
         * @param pFont the font
         */
        public void setTheFont(final Font pFont) {
            theField.setFont(pFont);
        }

        /**
         * Set the text colour.
         * @param pColor the colour
         */
        public void setTextFill(final Color pColor) {
            theField.setTextFill(pColor);
        }

        /**
         * Method that determines whether a cell is editable and prepares for edit.
         * @return continue edit true/false
         */
        private boolean preEditHook() {
            return !theEventManager.fireEvent(TethysUIEvent.CELLPREEDIT, this);
        }

        /**
         * Method that determines whether a value is valid prior to commit.
         * @param pNewValue the new value
         * @return continue edit true/false
         */
        private boolean preCommitHook(final C pNewValue) {
            return !theEventManager.fireEvent(TethysUIEvent.CELLPRECOMMIT, this);
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
     * @param <T> the table item class
     */
    public static class TethysFXDataStringCell<T>
            extends TethysFXDataTextCell<T, String> {
        /**
         * Constructor.
         */
        protected TethysFXDataStringCell() {
            super(new TethysFXStringTextField(), String.class);
        }

        @Override
        public TethysFXStringTextField getField() {
            return (TethysFXStringTextField) super.getField();
        }
    }

    /**
     * Short Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataShortCell<T>
            extends TethysFXDataTextCell<T, Short> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataShortCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXShortTextField(pFormatter), Short.class);
        }

        @Override
        public TethysFXShortTextField getField() {
            return (TethysFXShortTextField) super.getField();
        }
    }

    /**
     * Integer Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataIntegerCell<T>
            extends TethysFXDataTextCell<T, Integer> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataIntegerCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXIntegerTextField(pFormatter), Integer.class);
        }

        @Override
        public TethysFXIntegerTextField getField() {
            return (TethysFXIntegerTextField) super.getField();
        }
    }

    /**
     * Long Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataLongCell<T>
            extends TethysFXDataTextCell<T, Long> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataLongCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXLongTextField(pFormatter), Long.class);
        }

        @Override
        public TethysFXLongTextField getField() {
            return (TethysFXLongTextField) super.getField();
        }
    }

    /**
     * Money Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataMoneyCell<T>
            extends TethysFXDataTextCell<T, TethysMoney> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataMoneyCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXMoneyTextField(pFormatter), TethysMoney.class);
        }

        @Override
        public TethysFXMoneyTextField getField() {
            return (TethysFXMoneyTextField) super.getField();
        }
    }

    /**
     * Price Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataPriceCell<T>
            extends TethysFXDataTextCell<T, TethysPrice> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataPriceCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXPriceTextField(pFormatter), TethysPrice.class);
        }

        @Override
        public TethysFXPriceTextField getField() {
            return (TethysFXPriceTextField) super.getField();
        }
    }

    /**
     * Rate Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataRateCell<T>
            extends TethysFXDataTextCell<T, TethysRate> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataRateCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXRateTextField(pFormatter), TethysRate.class);
        }

        @Override
        public TethysFXRateTextField getField() {
            return (TethysFXRateTextField) super.getField();
        }
    }

    /**
     * Units Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataUnitsCell<T>
            extends TethysFXDataTextCell<T, TethysUnits> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataUnitsCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXUnitsTextField(pFormatter), TethysUnits.class);
        }

        @Override
        public TethysFXUnitsTextField getField() {
            return (TethysFXUnitsTextField) super.getField();
        }
    }

    /**
     * Dilution Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataDilutionCell<T>
            extends TethysFXDataTextCell<T, TethysDilution> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataDilutionCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXDilutionTextField(pFormatter), TethysDilution.class);
        }

        @Override
        public TethysFXDilutionTextField getField() {
            return (TethysFXDilutionTextField) super.getField();
        }
    }

    /**
     * DilutedPrice Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataDilutedPriceCell<T>
            extends TethysFXDataTextCell<T, TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataDilutedPriceCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXDilutedPriceTextField(pFormatter), TethysDilutedPrice.class);
        }

        @Override
        public TethysFXDilutedPriceTextField getField() {
            return (TethysFXDilutedPriceTextField) super.getField();
        }
    }

    /**
     * Ratio Cell.
     * @param <T> the table item class
     */
    public static class TethysFXDataRatioCell<T>
            extends TethysFXDataTextCell<T, TethysRatio> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataRatioCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXRatioTextField(pFormatter), TethysRatio.class);
        }

        @Override
        public TethysFXRatioTextField getField() {
            return (TethysFXRatioTextField) super.getField();
        }
    }

    /**
     * DateCell.
     * @param <T> the table item class
     */
    public static class TethysFXDataDateCell<T>
            extends TethysFXDataTextCell<T, TethysDate> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysFXDataDateCell(final TethysDataFormatter pFormatter) {
            super(new TethysFXDateButtonField(pFormatter), TethysDate.class);
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
     * @param <T> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXDataScrollCell<T, C>
            extends TethysFXDataTextCell<T, C> {
        /**
         * Constructor.
         * @param pClass the field class
         */
        protected TethysFXDataScrollCell(final Class<C> pClass) {
            super(new TethysFXScrollButtonField<>(), pClass);
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
     * @param <T> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXDataListCell<T, C>
            extends TethysFXDataTextCell<T, C> {
        /**
         * Constructor.
         * @param pClass the field class
         */
        protected TethysFXDataListCell(final Class<C> pClass) {
            super(new TethysFXListButtonField<>(), pClass);
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
     * @param <T> the table item class
     * @param <C> the column item class
     */
    public static class TethysFXDataIconCell<T, C>
            extends TethysFXDataTextCell<T, C> {
        /**
         * Constructor.
         * @param pClass the field class
         */
        protected TethysFXDataIconCell(final Class<C> pClass) {
            super(new TethysFXIconButtonField<C>(), pClass);
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
     * @param <T> the table item class
     * @param <C> the column item class
     * @param <S> the state class
     */
    public static class TethysFXDataStateIconCell<T, C, S>
            extends TethysFXDataTextCell<T, C> {
        /**
         * Constructor.
         * @param pClass the field class
         */
        protected TethysFXDataStateIconCell(final Class<C> pClass) {
            super(new TethysFXStateIconButtonField<C, S>(), pClass);
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