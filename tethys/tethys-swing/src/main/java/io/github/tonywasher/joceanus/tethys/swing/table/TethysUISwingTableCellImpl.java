/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.swing.table;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIFieldAttribute;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIFieldType;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableCharArrayColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableDateColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableIconColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableIntegerColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableListColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableLongColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableMoneyColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITablePriceColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRateColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRatioColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableRawDecimalColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableScrollColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableShortColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableStringColumn;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn.TethysUITableUnitsColumn;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingCharArrayTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingIntegerTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingLongTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingMoneyTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingPriceTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingRateTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingRatioTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingRawDecimalTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingShortTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingStringTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingTextEditField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingUnitsTextField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingDateButtonField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingIconButtonField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingListButtonField;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingScrollButtonField;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTableDateColumn;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTableIconColumn;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTableListColumn;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTableMoneyColumn;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTablePriceColumn;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTableRawDecimalColumn;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableColumn.TethysUISwingTableValidatedColumn;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serial;
import java.util.List;
import java.util.function.Supplier;

/**
 * Table Cell.
 *
 * @param <T> the column item class
 * @param <C> the column identity
 * @param <R> the table item class
 */
public abstract class TethysUISwingTableCellImpl<T, C, R>
        implements TethysUISwingTableCell<T, C, R> {
    /**
     * The Column.
     */
    private final TethysUISwingTableColumn<T, C, R> theColumn;

    /**
     * The Renderer Control field.
     */
    private final TethysUISwingDataTextField<T> theRenderControl;

    /**
     * The Editor Control field.
     */
    private final TethysUISwingDataTextField<T> theEditControl;

    /**
     * The Data class.
     */
    private final Class<T> theClazz;

    /**
     * The Editor.
     */
    private final TethysUISwingTableCellEditor theEditor;

    /**
     * The Renderer.
     */
    private final TethysUISwingTableCellRenderer theRenderer;

    /**
     * The useDialog.
     */
    private boolean useDialog;

    /**
     * The Active row index.
     */
    private int theRowIndex;

    /**
     * The Active row.
     */
    private R theRow;

    /**
     * Is the Active location selected?
     */
    private boolean isSelected;

    /**
     * Constructor.
     *
     * @param pColumn  the column
     * @param pControl the edit control
     */
    TethysUISwingTableCellImpl(final TethysUITableColumn<T, C, R> pColumn,
                               final TethysUISwingDataTextField<T> pControl) {
        /* Record the parameters */
        this(pColumn, pControl, null);
    }

    /**
     * Constructor.
     *
     * @param pColumn  the column
     * @param pControl the edit control
     * @param pClazz   the field class
     */
    TethysUISwingTableCellImpl(final TethysUITableColumn<T, C, R> pColumn,
                               final TethysUISwingDataTextField<T> pControl,
                               final Class<T> pClazz) {
        /* Record the parameters */
        theColumn = (TethysUISwingTableColumn<T, C, R>) pColumn;
        theEditControl = pControl;
        theClazz = pClazz;

        /* Create the editor and renderer */
        theEditor = new TethysUISwingTableCellEditor();
        theRenderer = new TethysUISwingTableCellRenderer();
        theRenderControl = pControl.cloneField(theRenderer);

        /* Apply validator to text field */
        if (theEditControl instanceof TethysUISwingTextEditField<T, ?> myField
                && theColumn instanceof TethysUISwingTableValidatedColumn<T, C, R> myColumn) {
            myField.setValidator(t -> myColumn.getValidator().apply(t, getActiveRow()));
            myField.setReporter(theColumn.getTable().getOnValidateError());
        }
    }

    /**
     * Use Dialog for edit.
     */
    void useDialogForEdit() {
        useDialog = true;
    }

    @Override
    public R getActiveRow() {
        return theRow;
    }

    @Override
    public TethysUISwingTableManager<C, R> getTable() {
        return theColumn.getTable();
    }

    @Override
    public TethysUITableColumn<T, C, R> getColumn() {
        return theColumn;
    }

    @Override
    public C getColumnId() {
        return theColumn.getId();
    }

    @Override
    public TethysUIFieldType getCellType() {
        return theColumn.getCellType();
    }

    @Override
    public TableCellEditor getEditor() {
        return theEditor;
    }

    @Override
    public TableCellRenderer getRenderer() {
        return theRenderer;
    }

    @Override
    public TethysUISwingDataTextField<T> getControl() {
        return theEditControl;
    }

    /**
     * Obtain the render control.
     *
     * @return the render control
     */
    protected TethysUISwingDataTextField<T> getRenderControl() {
        return theRenderControl;
    }

    /**
     * Should we use dialog for this control?
     *
     * @return true/false
     */
    boolean useDialog() {
        return useDialog;
    }

    /**
     * Is the active item selected?
     *
     * @return true/false
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Set the selected indication.
     *
     * @param pSelected true/false
     */
    void setSelected(final boolean pSelected) {
        isSelected = pSelected;
    }

    @Override
    public boolean isCellEditable(final int pIndex) {
        setActiveRow(pIndex);
        return theColumn.getTable().isEditable()
                && theColumn.isEditable()
                && theColumn.getCellEditable().test(theRow);
    }

    @Override
    public T getCastValue(final Object pValue) {
        return theClazz.cast(pValue);
    }

    @Override
    public void setActiveRow(final int pIndex) {
        theRowIndex = pIndex;
        theRow = theColumn.getRowForIndex(pIndex);
    }

    @Override
    public void repaintColumnCell(final C pId) {
        /* Note the cell has been updated */
        final TethysUISwingTableManager<C, R> myTable = theColumn.getTable();
        final TethysUISwingTableColumn<?, C, R> myCol = (TethysUISwingTableColumn<?, C, R>) myTable.getColumn(pId);
        myTable.getTableModel().fireTableCellUpdated(theRowIndex, myCol.getColumnIndex());
    }

    @Override
    public void repaintCellRow() {
        /* Note that the cell has been updated */
        final TethysUISwingTableManager<C, R> myTable = theColumn.getTable();
        myTable.fireTableRowUpdated(theRowIndex);
    }

    /**
     *
     * Data Cell Editor.
     */
    private class TethysUISwingTableCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        @Serial
        private static final long serialVersionUID = -3591698125380052152L;

        /**
         * the active row index.
         */
        private int theRowIndex;

        /**
         * Are we in the middle of stopping edit?
         */
        private boolean stoppingEdit;

        /**
         * Constructor.
         */
        TethysUISwingTableCellEditor() {
            /* Add listeners */
            final OceanusEventRegistrar<TethysUIEvent> myRegistrar = getControl().getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> stopCellEditing());
            myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
        }

        @Override
        public T getCellEditorValue() {
            return getControl().getValue();
        }

        @Override
        public Component getTableCellEditorComponent(final JTable pTable,
                                                     final Object pValue,
                                                     final boolean pSelected,
                                                     final int pRow,
                                                     final int pCol) {
            /* Determine the active row */
            theRowIndex = pTable.convertRowIndexToModel(pRow);
            setActiveRow(theRowIndex);

            /* Determine cell rectangle */
            final Point myPoint = pTable.getLocationOnScreen();
            Rectangle myCellLoc = pTable.getCellRect(pRow, pCol, false);

            /* Calculate rectangle */
            myCellLoc = new Rectangle(myPoint.x + myCellLoc.x,
                    myPoint.y + myCellLoc.y,
                    myCellLoc.width,
                    myCellLoc.height);

            /* Set field value and start edit */
            getControl().setValue(getCastValue(pValue));
            getControl().startCellEditing(myCellLoc);
            theColumn.getTable().processOnCellEditState(Boolean.TRUE);

            /* Return the field */
            return useDialog()
                    ? getControl().getLabel()
                    : getControl().getEditControl();
        }

        @Override
        public boolean stopCellEditing() {
            /* If we are already stopping edit */
            if (stoppingEdit) {
                return true;
            }

            /* Restore the active row */
            setActiveRow(theRowIndex);

            /* Check the data */
            stoppingEdit = true;
            getControl().parseData();

            /* If there is no error */
            boolean bComplete = false;
            if (!getControl().isAttributeSet(TethysUIFieldAttribute.ERROR)) {
                /* Pass call onwards */
                bComplete = super.stopCellEditing();

                /* Repaint any cells necessary */
                getTable().rePaintOnCommit(TethysUISwingTableCellImpl.this);
            }

            /* Return status */
            stoppingEdit = false;
            return bComplete;
        }

        @Override
        public void cancelCellEditing() {
            theColumn.getTable().processOnCellEditState(Boolean.FALSE);
            getControl().clearTheAttribute(TethysUIFieldAttribute.ERROR);
            getControl().adjustField();
            super.cancelCellEditing();
        }
    }

    /**
     * Data Cell Editor.
     */
    private final class TethysUISwingTableCellRenderer
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        @Serial
        private static final long serialVersionUID = -5226192429038913966L;

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean pSelected,
                                                        final boolean hasFocus,
                                                        final int pRow,
                                                        final int pCol) {
            /* Store the location */
            final int myRowIndex = pTable.convertRowIndexToModel(pRow);
            setActiveRow(myRowIndex);
            setSelected(pSelected);

            /* Access table details */
            final TethysUISwingTableManager<C, R> myTable = getTable();
            final C myId = getColumnId();
            final R myRow = getActiveRow();

            /* Set changed and disabled attributes */
            getRenderControl().setTheAttributeState(TethysUIFieldAttribute.CHANGED, myTable.isChanged(myId, myRow));
            getRenderControl().setTheAttributeState(TethysUIFieldAttribute.DISABLED, myTable.isDisabled(myRow));
            getRenderControl().setTheAttributeState(TethysUIFieldAttribute.ERROR, myTable.isError(myId, myRow));
            getRenderControl().setTheAttributeState(TethysUIFieldAttribute.SELECTED, pSelected);
            getRenderControl().setTheAttributeState(TethysUIFieldAttribute.ALTERNATE, (pRow & 1) == 0);

            /* Set details and stop editing */
            getRenderControl().setValue(getCastValue(pValue));
            getRenderControl().adjustField();

            /* Return the label as the render item */
            return this;
        }
    }

    /**
     * String Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableStringCell<C, R>
            extends TethysUISwingTableCellImpl<String, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableStringCell(final TethysUITableStringColumn<C, R> pColumn,
                                     final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingStringTextField) pFactory.fieldFactory().newStringField(), String.class);
        }

        @Override
        public TethysUISwingStringTextField getControl() {
            return (TethysUISwingStringTextField) super.getControl();
        }
    }

    /**
     * CharArray Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableCharArrayCell<C, R>
            extends TethysUISwingTableCellImpl<char[], C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableCharArrayCell(final TethysUITableCharArrayColumn<C, R> pColumn,
                                        final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingCharArrayTextField) pFactory.fieldFactory().newCharArrayField(), char[].class);
        }

        @Override
        public TethysUISwingCharArrayTextField getControl() {
            return (TethysUISwingCharArrayTextField) super.getControl();
        }
    }

    /**
     * Short Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableShortCell<C, R>
            extends TethysUISwingTableCellImpl<Short, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableShortCell(final TethysUITableShortColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingShortTextField) pFactory.fieldFactory().newShortField(), Short.class);
        }

        @Override
        public TethysUISwingShortTextField getControl() {
            return (TethysUISwingShortTextField) super.getControl();
        }
    }

    /**
     * Integer Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableIntegerCell<C, R>
            extends TethysUISwingTableCellImpl<Integer, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableIntegerCell(final TethysUITableIntegerColumn<C, R> pColumn,
                                      final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingIntegerTextField) pFactory.fieldFactory().newIntegerField(), Integer.class);
        }

        @Override
        public TethysUISwingIntegerTextField getControl() {
            return (TethysUISwingIntegerTextField) super.getControl();
        }
    }

    /**
     * Long Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableLongCell<C, R>
            extends TethysUISwingTableCellImpl<Long, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableLongCell(final TethysUITableLongColumn<C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingLongTextField) pFactory.fieldFactory().newLongField(), Long.class);
        }

        @Override
        public TethysUISwingLongTextField getControl() {
            return (TethysUISwingLongTextField) super.getControl();
        }
    }

    /**
     * RawDecimal Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableRawDecimalCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusDecimal, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableRawDecimalCell(final TethysUITableRawDecimalColumn<C, R> pColumn,
                                         final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingRawDecimalTextField) pFactory.fieldFactory().newRawDecimalField(), OceanusDecimal.class);
            getControl().setNumDecimals(() -> getColumn().getNumDecimals().applyAsInt(getActiveRow()));
        }

        @Override
        public TethysUISwingTableRawDecimalColumn<C, R> getColumn() {
            return (TethysUISwingTableRawDecimalColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysUISwingRawDecimalTextField getControl() {
            return (TethysUISwingRawDecimalTextField) super.getControl();
        }
    }

    /**
     * Money Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableMoneyCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusMoney, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableMoneyCell(final TethysUITableMoneyColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingMoneyTextField) pFactory.fieldFactory().newMoneyField(), OceanusMoney.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysUISwingTableMoneyColumn<C, R> getColumn() {
            return (TethysUISwingTableMoneyColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysUISwingMoneyTextField getControl() {
            return (TethysUISwingMoneyTextField) super.getControl();
        }
    }

    /**
     * Price Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTablePriceCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusPrice, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTablePriceCell(final TethysUITablePriceColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingPriceTextField) pFactory.fieldFactory().newPriceField(), OceanusPrice.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysUISwingTablePriceColumn<C, R> getColumn() {
            return (TethysUISwingTablePriceColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysUISwingPriceTextField getControl() {
            return (TethysUISwingPriceTextField) super.getControl();
        }
    }

    /**
     * Rate Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableRateCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusRate, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableRateCell(final TethysUITableRateColumn<C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingRateTextField) pFactory.fieldFactory().newRateField(), OceanusRate.class);
        }

        @Override
        public TethysUISwingRateTextField getControl() {
            return (TethysUISwingRateTextField) super.getControl();
        }
    }

    /**
     * Units Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableUnitsCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusUnits, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableUnitsCell(final TethysUITableUnitsColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingUnitsTextField) pFactory.fieldFactory().newUnitsField(), OceanusUnits.class);
        }

        @Override
        public TethysUISwingUnitsTextField getControl() {
            return (TethysUISwingUnitsTextField) super.getControl();
        }
    }

    /**
     * Ratio Cell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableRatioCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusRatio, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableRatioCell(final TethysUITableRatioColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingRatioTextField) pFactory.fieldFactory().newRatioField(), OceanusRatio.class);
        }

        @Override
        public TethysUISwingRatioTextField getControl() {
            return (TethysUISwingRatioTextField) super.getControl();
        }
    }

    /**
     * DateCell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableDateCell<C, R>
            extends TethysUISwingTableCellImpl<OceanusDate, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableDateCell(final TethysUITableDateColumn<C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingDateButtonField) pFactory.fieldFactory().newDateField(), OceanusDate.class);
            useDialogForEdit();
            getControl().setDateConfigurator(c -> getColumn().getDateConfigurator().accept(getActiveRow(), c));
        }

        @Override
        public TethysUISwingDateButtonField getControl() {
            return (TethysUISwingDateButtonField) super.getControl();
        }

        @Override
        public TethysUISwingTableDateColumn<C, R> getColumn() {
            return (TethysUISwingTableDateColumn<C, R>) super.getColumn();
        }
    }

    /**
     * ScrollCell.
     *
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysUISwingTableScrollCell<T, C, R>
            extends TethysUISwingTableCellImpl<T, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         * @param pClazz   the field class
         */
        @SuppressWarnings("unchecked")
        TethysUISwingTableScrollCell(final TethysUITableScrollColumn<T, C, R> pColumn,
                                     final TethysUICoreFactory<?> pFactory,
                                     final Class<T> pClazz) {
            super(pColumn, (TethysUISwingScrollButtonField<T>) pFactory.fieldFactory().newScrollField(pClazz), pClazz);
            useDialogForEdit();
            getControl().setMenuConfigurator(c -> getColumn().getMenuConfigurator().accept(getActiveRow(), c));
            getControl().getEventRegistrar().addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> getEditor().cancelCellEditing());
        }

        @Override
        public TethysUISwingScrollButtonField<T> getControl() {
            return (TethysUISwingScrollButtonField<T>) super.getControl();
        }

        @Override
        public TethysUITableScrollColumn<T, C, R> getColumn() {
            return (TethysUITableScrollColumn<T, C, R>) super.getColumn();
        }
    }

    /**
     * ListCell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysUISwingTableListCell<T extends Comparable<? super T>, C, R>
            extends TethysUISwingTableCellImpl<List<T>, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        @SuppressWarnings("unchecked")
        TethysUISwingTableListCell(final TethysUITableListColumn<T, C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingListButtonField<T>) pFactory.fieldFactory().newListField());
            useDialogForEdit();
            getControl().setSelectables(() -> getColumn().getSelectables().apply(getActiveRow()));
            getControl().getEventRegistrar().addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> getEditor().cancelCellEditing());
        }

        @Override
        public TethysUISwingListButtonField<T> getControl() {
            return (TethysUISwingListButtonField<T>) super.getControl();
        }

        @Override
        public TethysUISwingTableListColumn<T, C, R> getColumn() {
            return (TethysUISwingTableListColumn<T, C, R>) super.getColumn();
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<T> getCastValue(final Object pValue) {
            return (List<T>) pValue;
        }
    }

    /**
     * IconCell.
     *
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysUISwingTableIconCell<T, C, R>
            extends TethysUISwingTableCellImpl<T, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         * @param pClazz   the field class
         */
        @SuppressWarnings("unchecked")
        TethysUISwingTableIconCell(final TethysUITableIconColumn<T, C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory,
                                   final Class<T> pClazz) {
            super(pColumn, (TethysUISwingIconButtonField<T>) pFactory.fieldFactory().newIconField(pClazz), pClazz);
            final Supplier<TethysUIIconMapSet<T>> mySupplier = () -> getColumn().getIconMapSet().apply(getActiveRow());
            getControl().setIconMapSet(mySupplier);
            getRenderControl().setIconMapSet(mySupplier);
        }

        @Override
        public TethysUISwingTableIconColumn<T, C, R> getColumn() {
            return (TethysUISwingTableIconColumn<T, C, R>) super.getColumn();
        }

        @Override
        public TethysUISwingIconButtonField<T> getControl() {
            return (TethysUISwingIconButtonField<T>) super.getControl();
        }

        @Override
        public TethysUISwingIconButtonField<T> getRenderControl() {
            return (TethysUISwingIconButtonField<T>) super.getRenderControl();
        }
    }
}
