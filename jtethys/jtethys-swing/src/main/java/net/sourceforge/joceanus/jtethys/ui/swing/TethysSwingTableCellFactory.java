/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableCell;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRawDecimalTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingTextEditField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableCharArrayColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutionColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIconColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableIntegerColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableListColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableLongColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRateColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRatioColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRawDecimalColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableScrollColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableShortColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableStringColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableUnitsColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableValidatedColumn;

/**
 * Swing Table Cell Factory.
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysSwingTableCellFactory<C, R> {
    /**
     * The GUI Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingTableCellFactory(final TethysSwingGuiFactory pFactory) {
        theGuiFactory = pFactory;
    }

    /**
     * Obtain String Cell.
     * @param pColumn the column
     * @return the string cell
     */
    protected TethysSwingTableCell<String, C, R> stringCell(final TethysSwingTableStringColumn<C, R> pColumn) {
        return new TethysSwingTableStringCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain charArray Cell.
     * @param pColumn the column
     * @return the charArray cell
     */
    protected TethysSwingTableCell<char[], C, R> charArrayCell(final TethysSwingTableCharArrayColumn<C, R> pColumn) {
        return new TethysSwingTableCharArrayCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Short Cell.
     * @param pColumn the column
     * @return the short cell
     */
    protected TethysSwingTableCell<Short, C, R> shortCell(final TethysSwingTableShortColumn<C, R> pColumn) {
        return new TethysSwingTableShortCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Integer Cell.
     * @param pColumn the column
     * @return the integer cell
     */
    protected TethysSwingTableCell<Integer, C, R> integerCell(final TethysSwingTableIntegerColumn<C, R> pColumn) {
        return new TethysSwingTableIntegerCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Long Cell.
     * @param pColumn the column
     * @return the long cell
     */
    protected TethysSwingTableCell<Long, C, R> longCell(final TethysSwingTableLongColumn<C, R> pColumn) {
        return new TethysSwingTableLongCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell.
     * @param pColumn the column
     * @return the money cell
     */
    protected TethysSwingTableCell<TethysDecimal, C, R> rawDecimalCell(final TethysSwingTableRawDecimalColumn<C, R> pColumn) {
        return new TethysSwingTableRawDecimalCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Money Cell.
     * @param pColumn the column
     * @return the money cell
     */
    protected TethysSwingTableCell<TethysMoney, C, R> moneyCell(final TethysSwingTableMoneyColumn<C, R> pColumn) {
        return new TethysSwingTableMoneyCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Price Cell.
     * @param pColumn the column
     * @return the price cell
     */
    protected TethysSwingTableCell<TethysPrice, C, R> priceCell(final TethysSwingTablePriceColumn<C, R> pColumn) {
        return new TethysSwingTablePriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Rate Cell.
     * @param pColumn the column
     * @return the rate cell
     */
    protected TethysSwingTableCell<TethysRate, C, R> rateCell(final TethysSwingTableRateColumn<C, R> pColumn) {
        return new TethysSwingTableRateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Units Cell.
     * @param pColumn the column
     * @return the units cell
     */
    protected TethysSwingTableCell<TethysUnits, C, R> unitsCell(final TethysSwingTableUnitsColumn<C, R> pColumn) {
        return new TethysSwingTableUnitsCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Dilution Cell.
     * @param pColumn the column
     * @return the dilution cell
     */
    protected TethysSwingTableCell<TethysDilution, C, R> dilutionCell(final TethysSwingTableDilutionColumn<C, R> pColumn) {
        return new TethysSwingTableDilutionCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Ratio Cell.
     * @param pColumn the column
     * @return the ratio cell
     */
    protected TethysSwingTableCell<TethysRatio, C, R> ratioCell(final TethysSwingTableRatioColumn<C, R> pColumn) {
        return new TethysSwingTableRatioCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain DilutedPrice Cell.
     * @param pColumn the column
     * @return the dilutedPrice cell
     */
    protected TethysSwingTableCell<TethysDilutedPrice, C, R> dilutedPriceCell(final TethysSwingTableDilutedPriceColumn<C, R> pColumn) {
        return new TethysSwingTableDilutedPriceCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Scroll Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClazz the class of the item
     * @return the scroll cell
     */
    protected <T> TethysSwingTableCell<T, C, R> scrollCell(final TethysSwingTableScrollColumn<T, C, R> pColumn,
                                                           final Class<T> pClazz) {
        return new TethysSwingTableScrollCell<>(pColumn, theGuiFactory, pClazz);
    }

    /**
     * Obtain List Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @return the list cell
     */
    protected <T extends Comparable<T>> TethysSwingTableCell<List<T>, C, R> listCell(final TethysSwingTableListColumn<T, C, R> pColumn) {
        return new TethysSwingTableListCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Date Cell.
     * @param pColumn the column
     * @return the date cell
     */
    protected TethysSwingTableCell<TethysDate, C, R> dateCell(final TethysSwingTableDateColumn<C, R> pColumn) {
        return new TethysSwingTableDateCell<>(pColumn, theGuiFactory);
    }

    /**
     * Obtain Icon Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClazz the class of the item
     * @return the icon cell
     */
    protected <T> TethysSwingTableCell<T, C, R> iconCell(final TethysSwingTableIconColumn<T, C, R> pColumn,
                                                         final Class<T> pClazz) {
        return new TethysSwingTableIconCell<>(pColumn, theGuiFactory, pClazz);
    }

    /**
     * Table Cell.
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public abstract static class TethysSwingTableCell<T, C, R>
            implements TethysTableCell<T, C, R, JComponent, Icon> {
        /**
         * The Column.
         */
        private final TethysSwingTableColumn<T, C, R> theColumn;

        /**
         * The Renderer Control field.
         */
        private final TethysSwingDataTextField<T> theRenderControl;

        /**
         * The Editor Control field.
         */
        private final TethysSwingDataTextField<T> theEditControl;

        /**
         * The Data class.
         */
        private final Class<T> theClazz;

        /**
         * The Editor.
         */
        private final TethysSwingTableCellEditor theEditor;

        /**
         * The Renderer.
         */
        private final TethysSwingTableCellRenderer theRenderer;

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
         * @param pColumn the column
         * @param pControl the edit control
         */
        protected TethysSwingTableCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                       final TethysSwingDataTextField<T> pControl) {
            /* Record the parameters */
            this(pColumn, pControl, null);
        }

        /**
         * Constructor.
         * @param pColumn the column
         * @param pControl the edit control
         * @param pClazz the field class
         */
        protected TethysSwingTableCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                       final TethysSwingDataTextField<T> pControl,
                                       final Class<T> pClazz) {
            /* Record the parameters */
            theColumn = pColumn;
            theEditControl = pControl;
            theClazz = pClazz;

            /* Create the editor and renderer */
            theEditor = new TethysSwingTableCellEditor();
            theRenderer = new TethysSwingTableCellRenderer();
            theRenderControl = pControl.cloneField(theRenderer);

            /* Apply validator to text field */
            if (theEditControl instanceof TethysSwingTextEditField
                && theColumn instanceof TethysSwingTableValidatedColumn) {
                final TethysSwingTextEditField<T> myField = (TethysSwingTextEditField<T>) theEditControl;
                final TethysSwingTableValidatedColumn<T, C, R> myColumn = (TethysSwingTableValidatedColumn<T, C, R>) theColumn;
                myField.setValidator(t -> myColumn.getValidator().apply(t, getActiveRow()));
            }
        }

        /**
         * Use Dialog for edit.
         */
        protected void useDialogForEdit() {
            useDialog = true;
        }

        @Override
        public R getActiveRow() {
            return theRow;
        }

        @Override
        public TethysSwingTableManager<C, R> getTable() {
            return theColumn.getTable();
        }

        @Override
        public TethysSwingTableColumn<T, C, R> getColumn() {
            return theColumn;
        }

        @Override
        public C getColumnId() {
            return theColumn.getId();
        }

        @Override
        public TethysFieldType getCellType() {
            return theColumn.getCellType();
        }

        /**
         * Obtain the editor.
         * @return the editor
         */
        public TethysSwingTableCellEditor getEditor() {
            return theEditor;
        }

        /**
         * Obtain the renderer.
         * @return the renderer
         */
        public TethysSwingTableCellRenderer getRenderer() {
            return theRenderer;
        }

        @Override
        public TethysSwingDataTextField<T> getControl() {
            return theEditControl;
        }

        /**
         * Obtain the render control.
         * @return the render control
         */
        protected TethysSwingDataTextField<T> getRenderControl() {
            return theRenderControl;
        }

        /**
         * Should we use dialog for this control?
         * @return true/false
         */
        boolean useDialog() {
            return useDialog;
        }

        /**
         * Is the active item selected?
         * @return true/false
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Set the selected indication.
         * @param pSelected true/false
         */
        void setSelected(final boolean pSelected) {
            isSelected = pSelected;
        }

        /**
         * Is the cell editable?
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
         * @param pValue the value
         * @return the cast value
         */
        protected T getCastValue(final Object pValue) {
            return theClazz.cast(pValue);
        }

        /**
         * Set the active row.
         * @param pIndex the row index
         */
        protected void setActiveRow(final int pIndex) {
            theRowIndex = pIndex;
            theRow = theColumn.getRowForIndex(pIndex);
        }

        @Override
        public void repaintColumnCell(final C pId) {
            /* Note the the cell has been updated */
            final TethysSwingTableManager<C, R> myTable = theColumn.getTable();
            final TethysSwingTableColumn<?, C, R> myCol = myTable.getColumn(pId);
            myTable.getTableModel().fireTableCellUpdated(theRowIndex, myCol.getColumnIndex());
        }

        @Override
        public void repaintCellRow() {
            /* Note the the cell has been updated */
            final TethysSwingTableManager<C, R> myTable = theColumn.getTable();
            myTable.getTableModel().fireTableRowUpdated(theRowIndex);
        }

        /**
         * Data Cell Editor.
         */
        private class TethysSwingTableCellEditor
                extends AbstractCellEditor
                implements TableCellEditor {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -3591698125380052152L;

            /**
             * Are we in the middle of stopping edit?
             */
            private boolean stoppingEdit;

            /**
             * The point at which we are editing.
             */
            private Point thePoint;

            /**
             * Constructor.
             */
            protected TethysSwingTableCellEditor() {
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
                final int myRow = pTable.convertRowIndexToModel(pRow);
                setActiveRow(myRow);

                /* Determine cell rectangle */
                thePoint = pTable.getLocationOnScreen();
                Rectangle myCellLoc = pTable.getCellRect(pRow, pCol, false);

                /* Calculate rectangle */
                myCellLoc = new Rectangle(thePoint.x + myCellLoc.x,
                        thePoint.y + myCellLoc.y,
                        myCellLoc.width,
                        myCellLoc.height);

                /* Set field value and start edit */
                getControl().setValue(getCastValue(pValue));
                getControl().startCellEditing(myCellLoc);

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

                /* Check the data */
                stoppingEdit = true;
                getControl().parseData();

                /* If there is no error */
                boolean bComplete = false;
                if (!getControl().isAttributeSet(TethysFieldAttribute.ERROR)) {
                    /* Pass call onwards */
                    bComplete = super.stopCellEditing();

                    /* Repaint any cells necessary */
                    getTable().rePaintOnCommit(TethysSwingTableCell.this);
                }

                /* Return status */
                stoppingEdit = false;
                return bComplete;
            }
        }

        /**
         * Data Cell Editor.
         */
        private class TethysSwingTableCellRenderer
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
                final TethysSwingTableManager<C, R> myTable = getTable();
                final C myId = getColumnId();
                final R myRow = getActiveRow();

                /* Set changed and disabled attributes */
                getRenderControl().setTheAttributeState(TethysFieldAttribute.CHANGED, myTable.isChanged(myId, myRow));
                getRenderControl().setTheAttributeState(TethysFieldAttribute.DISABLED, myTable.isDisabled(myRow));
                getRenderControl().setTheAttributeState(TethysFieldAttribute.ERROR, myTable.isError(myId, myRow));
                getRenderControl().setTheAttributeState(TethysFieldAttribute.SELECTED, pSelected);
                getRenderControl().setTheAttributeState(TethysFieldAttribute.ALTERNATE, (pRow & 1) == 0);

                /* Set details and stop editing */
                getRenderControl().setValue(getCastValue(pValue));
                getRenderControl().adjustField();

                /* Return the label as the render item */
                return this;
            }
        }
    }

    /**
     * String Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableStringCell<C, R>
            extends TethysSwingTableCell<String, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableStringCell(final TethysSwingTableStringColumn<C, R> pColumn,
                                             final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newStringField(), String.class);
        }

        @Override
        public TethysSwingStringTextField getControl() {
            return (TethysSwingStringTextField) super.getControl();
        }
    }

    /**
     * CharArray Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableCharArrayCell<C, R>
            extends TethysSwingTableCell<char[], C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableCharArrayCell(final TethysSwingTableCharArrayColumn<C, R> pColumn,
                                                final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newCharArrayField(), char[].class);
        }

        @Override
        public TethysSwingCharArrayTextField getControl() {
            return (TethysSwingCharArrayTextField) super.getControl();
        }
    }

    /**
     * Short Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableShortCell<C, R>
            extends TethysSwingTableCell<Short, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableShortCell(final TethysSwingTableShortColumn<C, R> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newShortField(), Short.class);
        }

        @Override
        public TethysSwingShortTextField getControl() {
            return (TethysSwingShortTextField) super.getControl();
        }
    }

    /**
     * Integer Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIntegerCell<C, R>
            extends TethysSwingTableCell<Integer, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableIntegerCell(final TethysSwingTableIntegerColumn<C, R> pColumn,
                                              final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newIntegerField(), Integer.class);
        }

        @Override
        public TethysSwingIntegerTextField getControl() {
            return (TethysSwingIntegerTextField) super.getControl();
        }
    }

    /**
     * Long Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableLongCell<C, R>
            extends TethysSwingTableCell<Long, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableLongCell(final TethysSwingTableLongColumn<C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newLongField(), Long.class);
        }

        @Override
        public TethysSwingLongTextField getControl() {
            return (TethysSwingLongTextField) super.getControl();
        }
    }

    /**
     * RawDecimal Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRawDecimalCell<C, R>
            extends TethysSwingTableCell<TethysDecimal, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableRawDecimalCell(final TethysSwingTableRawDecimalColumn<C, R> pColumn,
                                                 final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newRawDecimalField(), TethysDecimal.class);
            getControl().setNumDecimals(() -> getColumn().getNumDecimals().apply(getActiveRow()));
        }

        @Override
        public TethysSwingTableRawDecimalColumn<C, R> getColumn() {
            return (TethysSwingTableRawDecimalColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysSwingRawDecimalTextField getControl() {
            return (TethysSwingRawDecimalTextField) super.getControl();
        }
    }

    /**
     * Money Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableMoneyCell<C, R>
            extends TethysSwingTableCell<TethysMoney, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableMoneyCell(final TethysSwingTableMoneyColumn<C, R> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newMoneyField(), TethysMoney.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysSwingTableMoneyColumn<C, R> getColumn() {
            return (TethysSwingTableMoneyColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysSwingMoneyTextField getControl() {
            return (TethysSwingMoneyTextField) super.getControl();
        }
    }

    /**
     * Price Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTablePriceCell<C, R>
            extends TethysSwingTableCell<TethysPrice, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTablePriceCell(final TethysSwingTablePriceColumn<C, R> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newPriceField(), TethysPrice.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysSwingTablePriceColumn<C, R> getColumn() {
            return (TethysSwingTablePriceColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysSwingPriceTextField getControl() {
            return (TethysSwingPriceTextField) super.getControl();
        }
    }

    /**
     * Rate Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRateCell<C, R>
            extends TethysSwingTableCell<TethysRate, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableRateCell(final TethysSwingTableRateColumn<C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newRateField(), TethysRate.class);
        }

        @Override
        public TethysSwingRateTextField getControl() {
            return (TethysSwingRateTextField) super.getControl();
        }
    }

    /**
     * Units Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableUnitsCell<C, R>
            extends TethysSwingTableCell<TethysUnits, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableUnitsCell(final TethysSwingTableUnitsColumn<C, R> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newUnitsField(), TethysUnits.class);
        }

        @Override
        public TethysSwingUnitsTextField getControl() {
            return (TethysSwingUnitsTextField) super.getControl();
        }
    }

    /**
     * Dilution Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutionCell<C, R>
            extends TethysSwingTableCell<TethysDilution, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDilutionCell(final TethysSwingTableDilutionColumn<C, R> pColumn,
                                               final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutionField(), TethysDilution.class);
        }

        @Override
        public TethysSwingDilutionTextField getControl() {
            return (TethysSwingDilutionTextField) super.getControl();
        }
    }

    /**
     * DilutedPrice Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutedPriceCell<C, R>
            extends TethysSwingTableCell<TethysDilutedPrice, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDilutedPriceCell(final TethysSwingTableDilutedPriceColumn<C, R> pColumn,
                                                   final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutedPriceField(), TethysDilutedPrice.class);
            getControl().setDeemedCurrency(() -> getColumn().getDeemedCurrency().apply(getActiveRow()));
        }

        @Override
        public TethysSwingTableDilutedPriceColumn<C, R> getColumn() {
            return (TethysSwingTableDilutedPriceColumn<C, R>) super.getColumn();
        }

        @Override
        public TethysSwingDilutedPriceTextField getControl() {
            return (TethysSwingDilutedPriceTextField) super.getControl();
        }
    }

    /**
     * Ratio Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRatioCell<C, R>
            extends TethysSwingTableCell<TethysRatio, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableRatioCell(final TethysSwingTableRatioColumn<C, R> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newRatioField(), TethysRatio.class);
        }

        @Override
        public TethysSwingRatioTextField getControl() {
            return (TethysSwingRatioTextField) super.getControl();
        }
    }

    /**
     * DateCell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDateCell<C, R>
            extends TethysSwingTableCell<TethysDate, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDateCell(final TethysSwingTableDateColumn<C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDateField(), TethysDate.class);
            useDialogForEdit();
            getControl().setDateConfigurator(c -> getColumn().getDateConfigurator().accept(getActiveRow(), c));
        }

        @Override
        public TethysSwingDateButtonField getControl() {
            return (TethysSwingDateButtonField) super.getControl();
        }

        @Override
        public TethysSwingTableDateColumn<C, R> getColumn() {
            return (TethysSwingTableDateColumn<C, R>) super.getColumn();
        }
    }

    /**
     * ScrollCell.
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableScrollCell<T, C, R>
            extends TethysSwingTableCell<T, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClazz the field class
         */
        protected TethysSwingTableScrollCell(final TethysSwingTableScrollColumn<T, C, R> pColumn,
                                             final TethysSwingGuiFactory pFactory,
                                             final Class<T> pClazz) {
            super(pColumn, pFactory.newScrollField(), pClazz);
            useDialogForEdit();
            getControl().setMenuConfigurator(c -> getColumn().getMenuConfigurator().accept(getActiveRow(), c));
        }

        @Override
        public TethysSwingScrollButtonField<T> getControl() {
            return (TethysSwingScrollButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingTableScrollColumn<T, C, R> getColumn() {
            return (TethysSwingTableScrollColumn<T, C, R>) super.getColumn();
        }
    }

    /**
     * ListCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysSwingTableListCell<T extends Comparable<T>, C, R>
            extends TethysSwingTableCell<List<T>, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableListCell(final TethysSwingTableListColumn<T, C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newListField());
            useDialogForEdit();
            getControl().setSelectables(() -> getColumn().getSelectables().apply(getActiveRow()));
        }

        @Override
        public TethysSwingListButtonField<T> getControl() {
            return (TethysSwingListButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingTableListColumn<T, C, R> getColumn() {
            return (TethysSwingTableListColumn<T, C, R>) super.getColumn();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<T> getCastValue(final Object pValue) {
            return (List<T>) pValue;
        }
    }

    /**
     * IconCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysSwingTableIconCell<T, C, R>
            extends TethysSwingTableCell<T, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClazz the field class
         */
        protected TethysSwingTableIconCell(final TethysSwingTableIconColumn<T, C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory,
                                           final Class<T> pClazz) {
            super(pColumn, pFactory.newIconField(), pClazz);
            final Supplier<TethysIconMapSet<T>> mySupplier = () -> getColumn().getIconMapSet().apply(getActiveRow());
            getControl().setIconMapSet(mySupplier);
            getRenderControl().setIconMapSet(mySupplier);
        }

        @Override
        public TethysSwingTableIconColumn<T, C, R> getColumn() {
            return (TethysSwingTableIconColumn<T, C, R>) super.getColumn();
        }

        @Override
        public TethysSwingIconButtonField<T> getControl() {
            return (TethysSwingIconButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingIconButtonField<T> getRenderControl() {
            return (TethysSwingIconButtonField<T>) super.getRenderControl();
        }
    }
}
