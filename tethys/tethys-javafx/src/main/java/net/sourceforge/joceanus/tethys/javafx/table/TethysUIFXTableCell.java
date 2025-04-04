/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.table;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableCell;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXCharArrayTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXIntegerTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXLongTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXMoneyTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXPriceTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXRateTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXRatioTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXRawDecimalTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXShortTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXStringTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXTextEditField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXUnitsTextField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXDateButtonField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXIconButtonField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXListButtonField;
import net.sourceforge.joceanus.tethys.javafx.field.TethysUIFXScrollButtonField;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableCharArrayColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableDateColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableIconColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableIntegerColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableListColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableLongColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableMoneyColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTablePriceColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRateColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRatioColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableRawDecimalColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableScrollColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableShortColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableStringColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableUnitsColumn;
import net.sourceforge.joceanus.tethys.javafx.table.TethysUIFXTableColumn.TethysUIFXTableValidatedColumn;

/**
 * DataCell.
 *
 * @param <T> the column item class
 * @param <C> the column identity
 * @param <R> the table item class
 */
public abstract class TethysUIFXTableCell<T, C, R>
        extends TableCell<R, T>
        implements OceanusEventProvider<TethysUIEvent>, TethysUITableCell<T, C, R> {
    /**
     * The dummy style.
     */
    private static final String STYLE_DUMMY = "DummyStyle";

    /**
     * The Column.
     */
    private final TethysUIFXTableColumn<T, C, R> theColumn;

    /**
     * The Control field.
     */
    private final TethysUIFXDataTextField<T> theControl;

    /**
     * The Data class.
     */
    private final Class<T> theClass;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * Constructor.
     *
     * @param pColumn the column
     * @param pField  the edit field
     */
    TethysUIFXTableCell(final TethysUIFXTableColumn<T, C, R> pColumn,
                        final TethysUIFXDataTextField<T> pField) {
        /* Record the parameters */
        this(pColumn, pField, null);
    }

    /**
     * Constructor.
     *
     * @param pColumn  the column
     * @param pControl the edit control
     * @param pClazz   the field class
     */
    TethysUIFXTableCell(final TethysUIFXTableColumn<T, C, R> pColumn,
                        final TethysUIFXDataTextField<T> pControl,
                        final Class<T> pClazz) {
        /* Record the parameters */
        theColumn = pColumn;
        theControl = pControl;
        theClass = pClazz;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Set the field as the graphic */
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setGraphic(TethysUIFXNode.getNode(theControl));

        /* Add listener to the edit field */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theControl.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, this::handleCommit);
        myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> handleCancel());

        /* Apply validator to a text field */
        if (theControl instanceof TethysUIFXDataTextField.TethysUIFXTextEditField
                && theColumn instanceof TethysUIFXTableColumn.TethysUIFXTableValidatedColumn) {
            final TethysUIFXTextEditField<T, ?> myField = (TethysUIFXTextEditField<T, ?>) theControl;
            final TethysUIFXTableValidatedColumn<T, C, R> myColumn = (TethysUIFXTableValidatedColumn<T, C, R>) theColumn;
            myField.setValidator(t -> myColumn.getValidator().apply(t, getActiveRow()));
            myField.setReporter(theColumn.getTable().getOnValidateError());
        }
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIFXTableManager<C, R> getTable() {
        return theColumn.getTable();
    }

    @Override
    public TethysUIFXTableColumn<T, C, R> getColumn() {
        return theColumn;
    }

    @Override
    public C getColumnId() {
        return theColumn.getId();
    }

    @Override
    public TethysUIFXDataTextField<T> getControl() {
        return theControl;
    }

    @Override
    public TethysUIFieldType getCellType() {
        return theColumn.getCellType();
    }

    /**
     * Is the cell in error?
     *
     * @return true/false
     */
    boolean isCellInError() {
        return theControl.isAttributeSet(TethysUIFieldAttribute.ERROR);
    }

    /**
     * Is the cell editable?
     *
     * @return true/false
     */
    boolean isCellEditable() {
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
            theColumn.getTable().processOnCellEditState(Boolean.TRUE);
        }
    }

    @Override
    public R getActiveRow() {
        /* Access list and determine size */
        final ObservableList<R> myItems = getTableView().getItems();
        final int mySize = myItems == null
                ? 0
                : myItems.size();

        /* Access list and determine size */
        final TableRow<?> myRow = getTableRow();
        final int myIndex = myRow == null
                ? -1
                : myRow.getIndex();

        /* Access explicit item */
        return myIndex < 0 || myIndex >= mySize
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
            final TethysUIFXTableManager<C, R> myTable = getTable();
            final C myId = getColumnId();
            final R myRow = getActiveRow();

            /* Set changed and disabled attributes */
            theControl.setTheAttributeState(TethysUIFieldAttribute.CHANGED, myTable.isChanged(myId, myRow));
            theControl.setTheAttributeState(TethysUIFieldAttribute.DISABLED, myTable.isDisabled(myRow));
            theControl.setTheAttributeState(TethysUIFieldAttribute.ERROR, myTable.isError(myId, myRow));
        }

        /* Set details and stop editing */
        theControl.setValue(pEmpty
                ? null
                : pValue);
        theControl.setEditable(false);
    }

    @Override
    public void commitEdit(final T pNewValue) {
        /* If we have no error */
        if (!isCellInError()) {
            /* Protect against exceptions */
            try {
                /* Call the commit hook */
                theColumn.processOnCommit(getActiveRow(), pNewValue);

                /* Repaint any cells necessary */
                getTable().rePaintOnCommit(this);

                /* Note that we are no longer editing */
                cancelEdit();
                theColumn.getTable().processOnCellEditState(Boolean.TRUE);

                /* If we had an exception, report it */
            } catch (OceanusException e) {
                getTable().processOnCommitError(e);
            }
        }
    }

    /**
     * handle Commit.
     *
     * @param pEvent the event
     */
    protected void handleCommit(final OceanusEvent<TethysUIEvent> pEvent) {
        commitEdit(pEvent.getDetails(theClass));
    }

    /**
     * Handle cancel.
     */
    private void handleCancel() {
        cancelEdit();
        theColumn.getTable().processOnCellEditState(Boolean.FALSE);
        getTable().setActiveCell(null);
    }

    @Override
    public void repaintColumnCell(final C pId) {
        final TethysUIFXTableManager<C, R> myTable = theColumn.getTable();
        myTable.repaintColumn(pId);
    }

    @Override
    public void repaintCellRow() {
        final TableRow<?> myRow = getTableRow();
        final List<String> myClasses = myRow.getStyleClass();
        myClasses.add(STYLE_DUMMY);
        myClasses.remove(STYLE_DUMMY);
    }

    /**
     * String Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableStringCell<C, R>
            extends TethysUIFXTableCell<String, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableStringCell(final TethysUIFXTableStringColumn<C, R> pColumn,
                                  final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXStringTextField) pFactory.fieldFactory().newStringField(), String.class);
        }

        @Override
        public TethysUIFXStringTextField getControl() {
            return (TethysUIFXStringTextField) super.getControl();
        }
    }

    /**
     * CharArray Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableCharArrayCell<C, R>
            extends TethysUIFXTableCell<char[], C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableCharArrayCell(final TethysUIFXTableCharArrayColumn<C, R> pColumn,
                                     final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXCharArrayTextField) pFactory.fieldFactory().newCharArrayField(), char[].class);
        }

        @Override
        public TethysUIFXCharArrayTextField getControl() {
            return (TethysUIFXCharArrayTextField) super.getControl();
        }
    }

    /**
     * Short Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableShortCell<C, R>
            extends TethysUIFXTableCell<Short, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableShortCell(final TethysUIFXTableShortColumn<C, R> pColumn,
                                 final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXShortTextField) pFactory.fieldFactory().newShortField(), Short.class);
        }

        @Override
        public TethysUIFXShortTextField getControl() {
            return (TethysUIFXShortTextField) super.getControl();
        }
    }

    /**
     * Integer Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableIntegerCell<C, R>
            extends TethysUIFXTableCell<Integer, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableIntegerCell(final TethysUIFXTableIntegerColumn<C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXIntegerTextField) pFactory.fieldFactory().newIntegerField(), Integer.class);
        }

        @Override
        public TethysUIFXIntegerTextField getControl() {
            return (TethysUIFXIntegerTextField) super.getControl();
        }
    }

    /**
     * Long Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableLongCell<C, R>
            extends TethysUIFXTableCell<Long, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableLongCell(final TethysUIFXTableLongColumn<C, R> pColumn,
                                final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXLongTextField) pFactory.fieldFactory().newLongField(), Long.class);
        }

        @Override
        public TethysUIFXLongTextField getControl() {
            return (TethysUIFXLongTextField) super.getControl();
        }
    }

    /**
     * RawDecimal Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableRawDecimalCell<C, R>
            extends TethysUIFXTableCell<OceanusDecimal, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableRawDecimalCell(final TethysUIFXTableRawDecimalColumn<C, R> pColumn,
                                      final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXRawDecimalTextField) pFactory.fieldFactory().newRawDecimalField(), OceanusDecimal.class);
            getControl().setNumDecimals(() -> getColumn().getNumDecimals().applyAsInt(getActiveRow()));
        }

        @Override
        public TethysUIFXTableRawDecimalColumn<C, R> getColumn() {
            return (TethysUIFXTableRawDecimalColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysUIFXRawDecimalTextField getControl() {
            return (TethysUIFXRawDecimalTextField) super.getControl();
        }
    }

    /**
     * Money Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableMoneyCell<C, R>
            extends TethysUIFXTableCell<OceanusMoney, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableMoneyCell(final TethysUIFXTableMoneyColumn<C, R> pColumn,
                                 final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXMoneyTextField) pFactory.fieldFactory().newMoneyField(), OceanusMoney.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysUIFXTableMoneyColumn<C, R> getColumn() {
            return (TethysUIFXTableMoneyColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysUIFXMoneyTextField getControl() {
            return (TethysUIFXMoneyTextField) super.getControl();
        }
    }

    /**
     * Price Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTablePriceCell<C, R>
            extends TethysUIFXTableCell<OceanusPrice, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTablePriceCell(final TethysUIFXTablePriceColumn<C, R> pColumn,
                                 final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXPriceTextField) pFactory.fieldFactory().newPriceField(), OceanusPrice.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysUIFXTablePriceColumn<C, R> getColumn() {
            return (TethysUIFXTablePriceColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysUIFXPriceTextField getControl() {
            return (TethysUIFXPriceTextField) super.getControl();
        }
    }

    /**
     * Rate Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableRateCell<C, R>
            extends TethysUIFXTableCell<OceanusRate, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableRateCell(final TethysUIFXTableRateColumn<C, R> pColumn,
                                final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXRateTextField) pFactory.fieldFactory().newRateField(), OceanusRate.class);
        }

        @Override
        public TethysUIFXRateTextField getControl() {
            return (TethysUIFXRateTextField) super.getControl();
        }
    }

    /**
     * Units Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableUnitsCell<C, R>
            extends TethysUIFXTableCell<OceanusUnits, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableUnitsCell(final TethysUIFXTableUnitsColumn<C, R> pColumn,
                                 final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXUnitsTextField) pFactory.fieldFactory().newUnitsField(), OceanusUnits.class);
        }

        @Override
        public TethysUIFXUnitsTextField getControl() {
            return (TethysUIFXUnitsTextField) super.getControl();
        }
    }

    /**
     * Ratio Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableRatioCell<C, R>
            extends TethysUIFXTableCell<OceanusRatio, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableRatioCell(final TethysUIFXTableRatioColumn<C, R> pColumn,
                                 final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXRatioTextField) pFactory.fieldFactory().newRatioField(), OceanusRatio.class);
        }

        @Override
        public TethysUIFXRatioTextField getControl() {
            return (TethysUIFXRatioTextField) super.getControl();
        }
    }

    /**
     * DateCell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableDateCell<C, R>
            extends TethysUIFXTableCell<OceanusDate, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUIFXTableDateCell(final TethysUIFXTableDateColumn<C, R> pColumn,
                                final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXDateButtonField) pFactory.fieldFactory().newDateField(), OceanusDate.class);
            getControl().setDateConfigurator(c -> getColumn().getDateConfigurator().accept(getActiveRow(), c));
        }

        @Override
        public TethysUIFXDateButtonField getControl() {
            return (TethysUIFXDateButtonField) super.getControl();
        }

        @Override
        public TethysUIFXTableDateColumn<C, R> getColumn() {
            return (TethysUIFXTableDateColumn<C, R>) super.getColumn();
        }
    }

    /**
     * ScrollCell.
     *
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableScrollCell<T, C, R>
            extends TethysUIFXTableCell<T, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         * @param pClazz   the field class
         */
        @SuppressWarnings("unchecked")
        TethysUIFXTableScrollCell(final TethysUIFXTableScrollColumn<T, C, R> pColumn,
                                  final TethysUICoreFactory<?> pFactory,
                                  final Class<T> pClazz) {
            super(pColumn, (TethysUIFXScrollButtonField<T>) pFactory.fieldFactory().newScrollField(pClazz), pClazz);
            getControl().setMenuConfigurator(c -> getColumn().getMenuConfigurator().accept(getActiveRow(), c));
        }

        @Override
        public TethysUIFXScrollButtonField<T> getControl() {
            return (TethysUIFXScrollButtonField<T>) super.getControl();
        }

        @Override
        public TethysUIFXTableScrollColumn<T, C, R> getColumn() {
            return (TethysUIFXTableScrollColumn<T, C, R>) super.getColumn();
        }
    }

    /**
     * ListCell.
     *
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableListCell<T extends Comparable<? super T>, C, R>
            extends TethysUIFXTableCell<List<T>, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        @SuppressWarnings("unchecked")
        TethysUIFXTableListCell(final TethysUIFXTableListColumn<T, C, R> pColumn,
                                final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUIFXListButtonField<T>) pFactory.fieldFactory().newListField());
            getControl().setSelectables(() -> getColumn().getSelectables().apply(getActiveRow()));
        }

        @Override
        public TethysUIFXListButtonField<T> getControl() {
            return (TethysUIFXListButtonField<T>) super.getControl();
        }

        @Override
        public TethysUIFXTableListColumn<T, C, R> getColumn() {
            return (TethysUIFXTableListColumn<T, C, R>) super.getColumn();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void handleCommit(final OceanusEvent<TethysUIEvent> pEvent) {
            commitEdit(pEvent.getDetails(List.class));
        }
    }

    /**
     * IconCell.
     *
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUIFXTableIconCell<T, C, R>
            extends TethysUIFXTableCell<T, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         * @param pClazz   the field class
         */
        @SuppressWarnings("unchecked")
        TethysUIFXTableIconCell(final TethysUIFXTableIconColumn<T, C, R> pColumn,
                                final TethysUICoreFactory<?> pFactory,
                                final Class<T> pClazz) {
            super(pColumn, (TethysUIFXIconButtonField<T>) pFactory.fieldFactory().newIconField(pClazz), pClazz);
            getControl().setIconMapSet(this::determineMapSet);
        }

        /**
         * Determine the mapSet.
         *
         * @return the mapSet
         */
        private TethysUIIconMapSet<T> determineMapSet() {
            final R myRow = getActiveRow();
            return myRow == null
                    ? null
                    : getColumn().getIconMapSet().apply(myRow);
        }

        @Override
        public TethysUIFXTableIconColumn<T, C, R> getColumn() {
            return (TethysUIFXTableIconColumn<T, C, R>) super.getColumn();
        }

        @Override
        public TethysUIFXIconButtonField<T> getControl() {
            return (TethysUIFXIconButtonField<T>) super.getControl();
        }
    }
}
