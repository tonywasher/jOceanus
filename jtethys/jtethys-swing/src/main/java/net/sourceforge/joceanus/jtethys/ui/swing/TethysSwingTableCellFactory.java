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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingStateIconButtonField;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableDilutedPriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableMoneyColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTablePriceColumn;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableRawDecimalColumn;

/**
 * Swing Table Cell Factory.
 * @param <C> the column identity
 * @param <R> the table row type
 */
public class TethysSwingTableCellFactory<C, R>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The GUI Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingTableCellFactory(final TethysSwingGuiFactory pFactory) {
        theEventManager = new TethysEventManager<>();
        theGuiFactory = pFactory;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain String Cell.
     * @param pColumn the column
     * @return the string cell
     */
    protected TethysSwingTableCell<String, C, R> stringCell(final TethysSwingTableColumn<String, C, R> pColumn) {
        return listenToCell(new TethysSwingTableStringCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain charArray Cell.
     * @param pColumn the column
     * @return the charArray cell
     */
    protected TethysSwingTableCell<char[], C, R> charArrayCell(final TethysSwingTableColumn<char[], C, R> pColumn) {
        return listenToCell(new TethysSwingTableCharArrayCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Short Cell.
     * @param pColumn the column
     * @return the short cell
     */
    protected TethysSwingTableCell<Short, C, R> shortCell(final TethysSwingTableColumn<Short, C, R> pColumn) {
        return listenToCell(new TethysSwingTableShortCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Integer Cell.
     * @param pColumn the column
     * @return the integer cell
     */
    protected TethysSwingTableCell<Integer, C, R> integerCell(final TethysSwingTableColumn<Integer, C, R> pColumn) {
        return listenToCell(new TethysSwingTableIntegerCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Long Cell.
     * @param pColumn the column
     * @return the long cell
     */
    protected TethysSwingTableCell<Long, C, R> longCell(final TethysSwingTableColumn<Long, C, R> pColumn) {
        return listenToCell(new TethysSwingTableLongCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Money Cell.
     * @param pColumn the column
     * @return the money cell
     */
    protected TethysSwingTableCell<TethysDecimal, C, R> rawDecimalCell(final TethysSwingTableColumn<TethysDecimal, C, R> pColumn) {
        return listenToCell(new TethysSwingTableRawDecimalCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Money Cell.
     * @param pColumn the column
     * @return the money cell
     */
    protected TethysSwingTableCell<TethysMoney, C, R> moneyCell(final TethysSwingTableColumn<TethysMoney, C, R> pColumn) {
        return listenToCell(new TethysSwingTableMoneyCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Price Cell.
     * @param pColumn the column
     * @return the price cell
     */
    protected TethysSwingTableCell<TethysPrice, C, R> priceCell(final TethysSwingTableColumn<TethysPrice, C, R> pColumn) {
        return listenToCell(new TethysSwingTablePriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Rate Cell.
     * @param pColumn the column
     * @return the rate cell
     */
    protected TethysSwingTableCell<TethysRate, C, R> rateCell(final TethysSwingTableColumn<TethysRate, C, R> pColumn) {
        return listenToCell(new TethysSwingTableRateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Units Cell.
     * @param pColumn the column
     * @return the units cell
     */
    protected TethysSwingTableCell<TethysUnits, C, R> unitsCell(final TethysSwingTableColumn<TethysUnits, C, R> pColumn) {
        return listenToCell(new TethysSwingTableUnitsCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Dilution Cell.
     * @param pColumn the column
     * @return the dilution cell
     */
    protected TethysSwingTableCell<TethysDilution, C, R> dilutionCell(final TethysSwingTableColumn<TethysDilution, C, R> pColumn) {
        return listenToCell(new TethysSwingTableDilutionCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Ratio Cell.
     * @param pColumn the column
     * @return the ratio cell
     */
    protected TethysSwingTableCell<TethysRatio, C, R> ratioCell(final TethysSwingTableColumn<TethysRatio, C, R> pColumn) {
        return listenToCell(new TethysSwingTableRatioCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain DilutedPrice Cell.
     * @param pColumn the column
     * @return the dilutedPrice cell
     */
    protected TethysSwingTableCell<TethysDilutedPrice, C, R> dilutedPriceCell(final TethysSwingTableColumn<TethysDilutedPrice, C, R> pColumn) {
        return listenToCell(new TethysSwingTableDilutedPriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Scroll Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell
     */
    protected <T> TethysSwingTableCell<T, C, R> scrollCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                                           final Class<T> pClass) {
        return listenToCell(new TethysSwingTableScrollCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain List Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the list cell
     */
    protected <T> TethysSwingTableCell<TethysItemList<T>, C, R> listCell(final TethysSwingTableColumn<TethysItemList<T>, C, R> pColumn,
                                                                         final Class<T> pClass) {
        return listenToCell(new TethysSwingTableListCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain Date Cell.
     * @param pColumn the column
     * @return the date cell
     */
    protected TethysSwingTableCell<TethysDate, C, R> dateCell(final TethysSwingTableColumn<TethysDate, C, R> pColumn) {
        return listenToCell(new TethysSwingTableDateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Icon Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell
     */
    protected <T> TethysSwingTableCell<T, C, R> iconCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                                         final Class<T> pClass) {
        return listenToCell(new TethysSwingTableIconCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain State Icon Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell
     */
    protected <T> TethysSwingTableCell<T, C, R> stateIconCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                                              final Class<T> pClass) {
        return listenToCell(new TethysSwingTableStateIconCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Listen to cell.
     * @param <T> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <T> TethysSwingTableCell<T, C, R> listenToCell(final TethysSwingTableCell<T, C, R> pCell) {
        theEventManager.fireEvent(TethysUIEvent.CELLCREATE, pCell);
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * Table Cell.
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public abstract static class TethysSwingTableCell<T, C, R>
            implements TethysEventProvider<TethysUIEvent>, TethysTableCell<T, C, R, JComponent, Icon> {
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
        private final Class<T> theClass;

        /**
         * The Event Manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

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
         * @param pClass the field class
         */
        protected TethysSwingTableCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                       final TethysSwingDataTextField<T> pControl,
                                       final Class<T> pClass) {
            /* Record the parameters */
            theColumn = pColumn;
            theEditControl = pControl;
            theClass = pClass;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Create the editor and renderer */
            theEditor = new TethysSwingTableCellEditor();
            theRenderer = new TethysSwingTableCellRenderer();
            theRenderControl = pControl.cloneField(theRenderer);

            /* Apply validator to text field */
            if (theEditControl instanceof TethysSwingTextEditField) {
                TethysSwingTextEditField<T> myField = (TethysSwingTextEditField<T>) theEditControl;
                myField.setValidator(t -> theColumn.getValidator().apply(t, theRow));
            }
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Use Dialog for edit.
         */
        protected void useDialog() {
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
        public T getNewValue() {
            return theEditor.getCellEditorValue();
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
         * Is the active item selected?
         * @return true/false
         */
        public boolean isSelected() {
            return isSelected;
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
            return theClass.cast(pValue);
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
            TethysSwingTableManager<C, R> myTable = theColumn.getTable();
            TethysSwingTableColumn<?, C, R> myCol = myTable.getColumn(pId);
            myTable.getTableModel().fireTableCellUpdated(theRowIndex, myCol.getColumnIndex());
        }

        @Override
        public void repaintCellRow() {
            /* Note the the cell has been updated */
            TethysSwingTableManager<C, R> myTable = theColumn.getTable();
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
             * Constructor.
             */
            protected TethysSwingTableCellEditor() {
                /* Add listeners */
                TethysEventRegistrar<TethysUIEvent> myRegistrar = theEditControl.getEventRegistrar();
                myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> stopCellEditing());
                myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
            }

            @Override
            public T getCellEditorValue() {
                return theEditControl.getValue();
            }

            @Override
            public Component getTableCellEditorComponent(final JTable pTable,
                                                         final Object pValue,
                                                         final boolean pSelected,
                                                         final int pRow,
                                                         final int pCol) {
                /* Determine the active row */
                int myRow = pTable.convertRowIndexToModel(pRow);
                setActiveRow(myRow);

                /* Determine cell rectangle */
                Point myTableLoc = pTable.getLocationOnScreen();
                Rectangle myCellLoc = pTable.getCellRect(pRow, pCol, false);

                /* Calculate rectangle */
                myCellLoc = new Rectangle(myTableLoc.x + myCellLoc.x,
                        myTableLoc.y + myCellLoc.y,
                        myCellLoc.width,
                        myCellLoc.height);

                /* Set field value and start edit */
                theEditControl.setValue(getCastValue(pValue));
                theEditControl.startCellEditing(myCellLoc);

                /* Return the field */
                return useDialog
                                 ? theEditControl.getLabel()
                                 : theEditControl.getEditControl();
            }

            @Override
            public boolean stopCellEditing() {
                /* If we are OK with the value */
                theEditControl.parseData();
                if (!theEditControl.isAttributeSet(TethysFieldAttribute.ERROR)
                    && theEventManager.fireEvent(TethysUIEvent.CELLPRECOMMIT, TethysSwingTableCell.this)) {
                    /* Pass call onwards */
                    boolean bComplete = super.stopCellEditing();

                    /* Notify of commit */
                    if (bComplete) {
                        theEventManager.fireEvent(TethysUIEvent.CELLCOMMITTED, TethysSwingTableCell.this);
                    }

                    /* Return success */
                    return bComplete;
                }

                /* Return failure */
                return false;
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
                int myRowIndex = pTable.convertRowIndexToModel(pRow);
                setActiveRow(myRowIndex);
                isSelected = pSelected;

                /* Access table details */
                TethysSwingTableManager<C, R> myTable = getTable();
                C myId = getColumnId();
                R myRow = getActiveRow();

                /* Set changed and disabled attributes */
                theRenderControl.setTheAttributeState(TethysFieldAttribute.CHANGED, myTable.isChanged(myId, myRow));
                theRenderControl.setTheAttributeState(TethysFieldAttribute.DISABLED, myTable.isDisabled(myRow));
                theRenderControl.setTheAttributeState(TethysFieldAttribute.ERROR, myTable.isError(myId, myRow));
                theRenderControl.setTheAttributeState(TethysFieldAttribute.SELECTED, pSelected);
                theRenderControl.setTheAttributeState(TethysFieldAttribute.ALTERNATE, (pRow & 1) == 0);

                /* Format the cell */
                theEventManager.fireEvent(TethysUIEvent.CELLFORMAT, TethysSwingTableCell.this);

                /* Set details and stop editing */
                theRenderControl.setValue(getCastValue(pValue));
                theRenderControl.adjustField();

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
        protected TethysSwingTableStringCell(final TethysSwingTableColumn<String, C, R> pColumn,
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
        protected TethysSwingTableCharArrayCell(final TethysSwingTableColumn<char[], C, R> pColumn,
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
        protected TethysSwingTableShortCell(final TethysSwingTableColumn<Short, C, R> pColumn,
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
        protected TethysSwingTableIntegerCell(final TethysSwingTableColumn<Integer, C, R> pColumn,
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
        protected TethysSwingTableLongCell(final TethysSwingTableColumn<Long, C, R> pColumn,
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
        protected TethysSwingTableRawDecimalCell(final TethysSwingTableColumn<TethysDecimal, C, R> pColumn,
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
        protected TethysSwingTableMoneyCell(final TethysSwingTableColumn<TethysMoney, C, R> pColumn,
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
        protected TethysSwingTablePriceCell(final TethysSwingTableColumn<TethysPrice, C, R> pColumn,
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
        protected TethysSwingTableRateCell(final TethysSwingTableColumn<TethysRate, C, R> pColumn,
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
        protected TethysSwingTableUnitsCell(final TethysSwingTableColumn<TethysUnits, C, R> pColumn,
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
        protected TethysSwingTableDilutionCell(final TethysSwingTableColumn<TethysDilution, C, R> pColumn,
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
        protected TethysSwingTableDilutedPriceCell(final TethysSwingTableColumn<TethysDilutedPrice, C, R> pColumn,
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
        protected TethysSwingTableRatioCell(final TethysSwingTableColumn<TethysRatio, C, R> pColumn,
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
            extends TethysSwingTableCell<TethysDate, C, R>
            implements TethysDateField<JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDateCell(final TethysSwingTableColumn<TethysDate, C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDateField(), TethysDate.class);
            useDialog();
        }

        @Override
        public TethysSwingDateButtonField getControl() {
            return (TethysSwingDateButtonField) super.getControl();
        }

        @Override
        public TethysSwingDateButtonManager getDateManager() {
            return getControl().getDateManager();
        }
    }

    /**
     * ScrollCell.
     * @param <T> the column item class
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableScrollCell<T, C, R>
            extends TethysSwingTableCell<T, C, R>
            implements TethysScrollField<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableScrollCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                             final TethysSwingGuiFactory pFactory,
                                             final Class<T> pClass) {
            super(pColumn, pFactory.newScrollField(), pClass);
            useDialog();
        }

        @Override
        public TethysSwingScrollButtonField<T> getControl() {
            return (TethysSwingScrollButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingScrollButtonManager<T> getScrollManager() {
            return getControl().getScrollManager();
        }
    }

    /**
     * ListCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysSwingTableListCell<T, C, R>
            extends TethysSwingTableCell<TethysItemList<T>, C, R> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableListCell(final TethysSwingTableColumn<TethysItemList<T>, C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory,
                                           final Class<T> pClass) {
            super(pColumn, pFactory.newListField());
            useDialog();
        }

        @Override
        public TethysSwingListButtonField<T> getControl() {
            return (TethysSwingListButtonField<T>) super.getControl();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected TethysItemList<T> getCastValue(final Object pValue) {
            return (TethysItemList<T>) pValue;
        }
    }

    /**
     * IconCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysSwingTableIconCell<T, C, R>
            extends TethysSwingTableCell<T, C, R>
            implements TethysIconField<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableIconCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                           final TethysSwingGuiFactory pFactory,
                                           final Class<T> pClass) {
            super(pColumn, pFactory.newSimpleIconField(), pClass);
        }

        @Override
        public TethysSwingIconButtonField<T> getControl() {
            return (TethysSwingIconButtonField<T>) super.getControl();
        }

        @Override
        public TethysSimpleIconButtonManager<T, JComponent, Icon> getIconManager() {
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
    public static class TethysSwingTableStateIconCell<T, C, R, S>
            extends TethysSwingTableCell<T, C, R>
            implements TethysStateIconField<T, S, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableStateIconCell(final TethysSwingTableColumn<T, C, R> pColumn,
                                                final TethysSwingGuiFactory pFactory,
                                                final Class<T> pClass) {
            super(pColumn, pFactory.newStateIconField(), pClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysSwingStateIconButtonField<T, S> getControl() {
            return (TethysSwingStateIconButtonField<T, S>) super.getControl();
        }

        @Override
        public TethysSwingStateIconButtonManager<T, S> getIconManager() {
            return getControl().getIconManager();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected TethysSwingStateIconButtonField<T, S> getRenderControl() {
            return (TethysSwingStateIconButtonField<T, S>) super.getRenderControl();
        }

        /**
         * Obtain the render icon manager.
         * @return the icon manager
         */
        private TethysSwingStateIconButtonManager<T, S> getRenderIconManager() {
            return getRenderControl().getIconManager();
        }

        /**
         * Set the edit machine state.
         * @param pState the state
         */
        public void setEditMachineState(final S pState) {
            getIconManager().setMachineState(pState);
        }

        /**
         * Set the render machine state.
         * @param pState the state
         */
        public void setRenderMachineState(final S pState) {
            getRenderIconManager().setMachineState(pState);
        }
    }
}
