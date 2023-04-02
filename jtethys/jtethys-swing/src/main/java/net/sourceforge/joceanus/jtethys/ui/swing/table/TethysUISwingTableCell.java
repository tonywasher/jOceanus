/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableCell;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingRawDecimalTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingTextEditField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableUnitsColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableColumn.TethysUISwingTableValidatedColumn;

/**
 * Table Cell.
 *
 * @param <T> the column item class
 * @param <C> the column identity
 * @param <R> the table item class
 */
public abstract class TethysUISwingTableCell<T, C, R>
        implements TethysUITableCell<T, C, R> {
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
    TethysUISwingTableCell(final TethysUISwingTableColumn<T, C, R> pColumn,
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
    TethysUISwingTableCell(final TethysUISwingTableColumn<T, C, R> pColumn,
                           final TethysUISwingDataTextField<T> pControl,
                           final Class<T> pClazz) {
        /* Record the parameters */
        theColumn = pColumn;
        theEditControl = pControl;
        theClazz = pClazz;

        /* Create the editor and renderer */
        theEditor = new TethysUISwingTableCellEditor();
        theRenderer = new TethysUISwingTableCellRenderer();
        theRenderControl = pControl.cloneField(theRenderer);

        /* Apply validator to text field */
        if (theEditControl instanceof TethysUISwingDataTextField.TethysUISwingTextEditField
                && theColumn instanceof TethysUISwingTableColumn.TethysUISwingTableValidatedColumn) {
            final TethysUISwingTextEditField<T, ?> myField = (TethysUISwingTextEditField<T, ?>) theEditControl;
            final TethysUISwingTableValidatedColumn<T, C, R> myColumn = (TethysUISwingTableValidatedColumn<T, C, R>) theColumn;
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
    public TethysUISwingTableColumn<T, C, R> getColumn() {
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

    /**
     * Obtain the editor.
     *
     * @return the editor
     */
    TethysUISwingTableCellEditor getEditor() {
        return theEditor;
    }

    /**
     * Obtain the renderer.
     *
     * @return the renderer
     */
    TethysUISwingTableCellRenderer getRenderer() {
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

    /**
     * Is the cell editable?
     *
     * @param pIndex the row index
     * @return true/false
     */
    protected boolean isCellEditable(final int pIndex) {
        setActiveRow(pIndex);
        return theColumn.getTable().isEditable()
                && theColumn.isEditable()
                && theColumn.getCellEditable().test(theRow);
    }

    /**
     * Obtain the cast value.
     *
     * @param pValue the value
     * @return the cast value
     */
    protected T getCastValue(final Object pValue) {
        return theClazz.cast(pValue);
    }

    /**
     * Set the active row.
     *
     * @param pIndex the row index
     */
    void setActiveRow(final int pIndex) {
        theRowIndex = pIndex;
        theRow = theColumn.getRowForIndex(pIndex);
    }

    @Override
    public void repaintColumnCell(final C pId) {
        /* Note the cell has been updated */
        final TethysUISwingTableManager<C, R> myTable = theColumn.getTable();
        final TethysUISwingTableColumn<?, C, R> myCol = myTable.getColumn(pId);
        myTable.getTableModel().fireTableCellUpdated(theRowIndex, myCol.getColumnIndex());
    }

    @Override
    public void repaintCellRow() {
        /* Note the the cell has been updated */
        final TethysUISwingTableManager<C, R> myTable = theColumn.getTable();
        myTable.getTableModel().fireTableRowUpdated(theRowIndex);
    }

    /**
     * Data Cell Editor.
     */
    private class TethysUISwingTableCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
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
            final TethysEventRegistrar<TethysUIEvent> myRegistrar = getControl().getEventRegistrar();
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
                getTable().rePaintOnCommit(TethysUISwingTableCell.this);
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
    private class TethysUISwingTableCellRenderer
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
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
            extends TethysUISwingTableCell<String, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableStringCell(final TethysUISwingTableStringColumn<C, R> pColumn,
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
            extends TethysUISwingTableCell<char[], C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableCharArrayCell(final TethysUISwingTableCharArrayColumn<C, R> pColumn,
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
            extends TethysUISwingTableCell<Short, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableShortCell(final TethysUISwingTableShortColumn<C, R> pColumn,
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
            extends TethysUISwingTableCell<Integer, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableIntegerCell(final TethysUISwingTableIntegerColumn<C, R> pColumn,
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
            extends TethysUISwingTableCell<Long, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableLongCell(final TethysUISwingTableLongColumn<C, R> pColumn,
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
            extends TethysUISwingTableCell<TethysDecimal, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableRawDecimalCell(final TethysUISwingTableRawDecimalColumn<C, R> pColumn,
                                         final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingRawDecimalTextField) pFactory.fieldFactory().newRawDecimalField(), TethysDecimal.class);
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
            extends TethysUISwingTableCell<TethysMoney, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableMoneyCell(final TethysUISwingTableMoneyColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingMoneyTextField) pFactory.fieldFactory().newMoneyField(), TethysMoney.class);
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
            extends TethysUISwingTableCell<TethysPrice, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTablePriceCell(final TethysUISwingTablePriceColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingPriceTextField) pFactory.fieldFactory().newPriceField(), TethysPrice.class);
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
            extends TethysUISwingTableCell<TethysRate, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableRateCell(final TethysUISwingTableRateColumn<C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingRateTextField) pFactory.fieldFactory().newRateField(), TethysRate.class);
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
            extends TethysUISwingTableCell<TethysUnits, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableUnitsCell(final TethysUISwingTableUnitsColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingUnitsTextField) pFactory.fieldFactory().newUnitsField(), TethysUnits.class);
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
            extends TethysUISwingTableCell<TethysRatio, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableRatioCell(final TethysUISwingTableRatioColumn<C, R> pColumn,
                                    final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingRatioTextField) pFactory.fieldFactory().newRatioField(), TethysRatio.class);
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
            extends TethysUISwingTableCell<TethysDate, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        TethysUISwingTableDateCell(final TethysUISwingTableDateColumn<C, R> pColumn,
                                   final TethysUICoreFactory<?> pFactory) {
            super(pColumn, (TethysUISwingDateButtonField) pFactory.fieldFactory().newDateField(), TethysDate.class);
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
            extends TethysUISwingTableCell<T, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         * @param pClazz   the field class
         */
        @SuppressWarnings("unchecked")
        TethysUISwingTableScrollCell(final TethysUISwingTableScrollColumn<T, C, R> pColumn,
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
        public TethysUISwingTableScrollColumn<T, C, R> getColumn() {
            return (TethysUISwingTableScrollColumn<T, C, R>) super.getColumn();
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
            extends TethysUISwingTableCell<List<T>, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         */
        @SuppressWarnings("unchecked")
        TethysUISwingTableListCell(final TethysUISwingTableListColumn<T, C, R> pColumn,
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
        protected List<T> getCastValue(final Object pValue) {
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
            extends TethysUISwingTableCell<T, C, R> {
        /**
         * Constructor.
         *
         * @param pColumn  the column
         * @param pFactory the GUI Factory
         * @param pClazz   the field class
         */
        @SuppressWarnings("unchecked")
        TethysUISwingTableIconCell(final TethysUISwingTableIconColumn<T, C, R> pColumn,
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
