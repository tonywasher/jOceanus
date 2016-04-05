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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Currency;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
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
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableColumn;

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
    protected TethysSwingTableCell<C, R, String> stringCell(final TethysSwingTableColumn<C, R, String> pColumn) {
        return listenToCell(new TethysSwingTableStringCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Short Cell.
     * @param pColumn the column
     * @return the short cell
     */
    protected TethysSwingTableCell<C, R, Short> shortCell(final TethysSwingTableColumn<C, R, Short> pColumn) {
        return listenToCell(new TethysSwingTableShortCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Integer Cell.
     * @param pColumn the column
     * @return the integer cell
     */
    protected TethysSwingTableCell<C, R, Integer> integerCell(final TethysSwingTableColumn<C, R, Integer> pColumn) {
        return listenToCell(new TethysSwingTableIntegerCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Long Cell.
     * @param pColumn the column
     * @return the long cell
     */
    protected TethysSwingTableCell<C, R, Long> longCell(final TethysSwingTableColumn<C, R, Long> pColumn) {
        return listenToCell(new TethysSwingTableLongCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Money Cell.
     * @param pColumn the column
     * @return the money cell
     */
    protected TethysSwingTableCell<C, R, TethysMoney> moneyCell(final TethysSwingTableColumn<C, R, TethysMoney> pColumn) {
        return listenToCell(new TethysSwingTableMoneyCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Price Cell.
     * @param pColumn the column
     * @return the price cell
     */
    protected TethysSwingTableCell<C, R, TethysPrice> priceCell(final TethysSwingTableColumn<C, R, TethysPrice> pColumn) {
        return listenToCell(new TethysSwingTablePriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Rate Cell.
     * @param pColumn the column
     * @return the rate cell
     */
    protected TethysSwingTableCell<C, R, TethysRate> rateCell(final TethysSwingTableColumn<C, R, TethysRate> pColumn) {
        return listenToCell(new TethysSwingTableRateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Units Cell.
     * @param pColumn the column
     * @return the units cell
     */
    protected TethysSwingTableCell<C, R, TethysUnits> unitsCell(final TethysSwingTableColumn<C, R, TethysUnits> pColumn) {
        return listenToCell(new TethysSwingTableUnitsCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Dilution Cell.
     * @param pColumn the column
     * @return the dilution cell
     */
    protected TethysSwingTableCell<C, R, TethysDilution> dilutionCell(final TethysSwingTableColumn<C, R, TethysDilution> pColumn) {
        return listenToCell(new TethysSwingTableDilutionCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Ratio Cell.
     * @param pColumn the column
     * @return the ratio cell
     */
    protected TethysSwingTableCell<C, R, TethysRatio> ratioCell(final TethysSwingTableColumn<C, R, TethysRatio> pColumn) {
        return listenToCell(new TethysSwingTableRatioCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain DilutedPrice Cell.
     * @param pColumn the column
     * @return the dilutedPrice cell
     */
    protected TethysSwingTableCell<C, R, TethysDilutedPrice> dilutedPriceCell(final TethysSwingTableColumn<C, R, TethysDilutedPrice> pColumn) {
        return listenToCell(new TethysSwingTableDilutedPriceCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Scroll Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell
     */
    protected <T> TethysSwingTableCell<C, R, T> scrollCell(final TethysSwingTableColumn<C, R, T> pColumn,
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
    protected <T> TethysSwingTableCell<C, R, TethysItemList<T>> listCell(final TethysSwingTableColumn<C, R, TethysItemList<T>> pColumn,
                                                                         final Class<T> pClass) {
        return listenToCell(new TethysSwingTableListCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Obtain Date Cell.
     * @param pColumn the column
     * @return the date cell
     */
    protected TethysSwingTableCell<C, R, TethysDate> dateCell(final TethysSwingTableColumn<C, R, TethysDate> pColumn) {
        return listenToCell(new TethysSwingTableDateCell<>(pColumn, theGuiFactory));
    }

    /**
     * Obtain Icon Cell.
     * @param <T> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell
     */
    protected <T> TethysSwingTableCell<C, R, T> iconCell(final TethysSwingTableColumn<C, R, T> pColumn,
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
    protected <T> TethysSwingTableCell<C, R, T> stateIconCell(final TethysSwingTableColumn<C, R, T> pColumn,
                                                              final Class<T> pClass) {
        return listenToCell(new TethysSwingTableStateIconCell<>(pColumn, theGuiFactory, pClass));
    }

    /**
     * Listen to cell.
     * @param <T> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <T> TethysSwingTableCell<C, R, T> listenToCell(final TethysSwingTableCell<C, R, T> pCell) {
        theEventManager.fireEvent(TethysUIEvent.CELLCREATE, pCell);
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * Table Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public abstract static class TethysSwingTableCell<C, R, T>
            implements TethysEventProvider<TethysUIEvent>, TethysTableCell<T, C, R, JComponent, Icon> {
        /**
         * The Column.
         */
        private final TethysSwingTableColumn<C, R, T> theColumn;

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
         * @param pRenderer the render control
         * @param pEditor the edit control
         */
        protected TethysSwingTableCell(final TethysSwingTableColumn<C, R, T> pColumn,
                                       final TethysSwingDataTextField<T> pRenderer,
                                       final TethysSwingDataTextField<T> pEditor) {
            /* Record the parameters */
            this(pColumn, pRenderer, pEditor, null);
        }

        /**
         * Constructor.
         * @param pColumn the column
         * @param pRenderer the render control
         * @param pEditor the edit control
         * @param pClass the field class
         */
        protected TethysSwingTableCell(final TethysSwingTableColumn<C, R, T> pColumn,
                                       final TethysSwingDataTextField<T> pRenderer,
                                       final TethysSwingDataTextField<T> pEditor,
                                       final Class<T> pClass) {
            /* Record the parameters */
            theColumn = pColumn;
            theRenderControl = pRenderer;
            theEditControl = pEditor;
            theClass = pClass;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Create the editor and renderer */
            theEditor = new TethysSwingTableCellEditor();
            theRenderer = new TethysSwingTableCellRenderer();
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
        public TethysSwingTableColumn<C, R, T> getColumn() {
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
            return theRenderControl;
        }

        /**
         * Obtain the edit control.
         * @return the edit control
         */
        public TethysSwingDataTextField<T> getEditControl() {
            return theEditControl;
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
            return theEventManager.fireEvent(TethysUIEvent.CELLPREEDIT, this);
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
            TethysSwingTableColumn<C, R, ?> myCol = myTable.getColumn(pId);
            myTable.getTableModel().fireTableCellUpdated(theRowIndex, myCol.getColumnIndex());
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
                if (theEventManager.fireEvent(TethysUIEvent.CELLPRECOMMIT, TethysSwingTableCell.this)) {
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
                int myRow = pTable.convertRowIndexToModel(pRow);
                setActiveRow(myRow);
                isSelected = pSelected;

                /* Format the cell */
                theEventManager.fireEvent(TethysUIEvent.CELLFORMAT, TethysSwingTableCell.this);

                /* Set details and stop editing */
                theRenderControl.setValue(getCastValue(pValue));

                /* Return the label as the render item */
                return theRenderControl.getLabel();
            }
        }
    }

    /**
     * String Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableStringCell<C, R>
            extends TethysSwingTableCell<C, R, String> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableStringCell(final TethysSwingTableColumn<C, R, String> pColumn,
                                             final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newStringField(), pFactory.newStringField(), String.class);
        }

        @Override
        public TethysSwingStringTextField getControl() {
            return (TethysSwingStringTextField) super.getControl();
        }

        @Override
        public TethysSwingStringTextField getEditControl() {
            return (TethysSwingStringTextField) super.getEditControl();
        }
    }

    /**
     * Short Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableShortCell<C, R>
            extends TethysSwingTableCell<C, R, Short> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableShortCell(final TethysSwingTableColumn<C, R, Short> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newShortField(), pFactory.newShortField(), Short.class);
        }

        @Override
        public TethysSwingShortTextField getControl() {
            return (TethysSwingShortTextField) super.getControl();
        }

        @Override
        public TethysSwingShortTextField getEditControl() {
            return (TethysSwingShortTextField) super.getEditControl();
        }
    }

    /**
     * Integer Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIntegerCell<C, R>
            extends TethysSwingTableCell<C, R, Integer> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableIntegerCell(final TethysSwingTableColumn<C, R, Integer> pColumn,
                                              final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newIntegerField(), pFactory.newIntegerField(), Integer.class);
        }

        @Override
        public TethysSwingIntegerTextField getControl() {
            return (TethysSwingIntegerTextField) super.getControl();
        }

        @Override
        public TethysSwingIntegerTextField getEditControl() {
            return (TethysSwingIntegerTextField) super.getEditControl();
        }
    }

    /**
     * Long Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableLongCell<C, R>
            extends TethysSwingTableCell<C, R, Long> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableLongCell(final TethysSwingTableColumn<C, R, Long> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newLongField(), pFactory.newLongField(), Long.class);
        }

        @Override
        public TethysSwingLongTextField getControl() {
            return (TethysSwingLongTextField) super.getControl();
        }

        @Override
        public TethysSwingLongTextField getEditControl() {
            return (TethysSwingLongTextField) super.getEditControl();
        }
    }

    /**
     * Money Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableMoneyCell<C, R>
            extends TethysSwingTableCell<C, R, TethysMoney>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableMoneyCell(final TethysSwingTableColumn<C, R, TethysMoney> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newMoneyField(), pFactory.newMoneyField(), TethysMoney.class);
        }

        @Override
        public TethysSwingMoneyTextField getControl() {
            return (TethysSwingMoneyTextField) super.getControl();
        }

        @Override
        public TethysSwingMoneyTextField getEditControl() {
            return (TethysSwingMoneyTextField) super.getEditControl();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditControl().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Price Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTablePriceCell<C, R>
            extends TethysSwingTableCell<C, R, TethysPrice>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTablePriceCell(final TethysSwingTableColumn<C, R, TethysPrice> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newPriceField(), pFactory.newPriceField(), TethysPrice.class);
        }

        @Override
        public TethysSwingPriceTextField getControl() {
            return (TethysSwingPriceTextField) super.getControl();
        }

        @Override
        public TethysSwingPriceTextField getEditControl() {
            return (TethysSwingPriceTextField) super.getEditControl();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditControl().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Rate Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRateCell<C, R>
            extends TethysSwingTableCell<C, R, TethysRate> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableRateCell(final TethysSwingTableColumn<C, R, TethysRate> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newRateField(), pFactory.newRateField(), TethysRate.class);
        }

        @Override
        public TethysSwingRateTextField getControl() {
            return (TethysSwingRateTextField) super.getControl();
        }

        @Override
        public TethysSwingRateTextField getEditControl() {
            return (TethysSwingRateTextField) super.getEditControl();
        }
    }

    /**
     * Units Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableUnitsCell<C, R>
            extends TethysSwingTableCell<C, R, TethysUnits> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableUnitsCell(final TethysSwingTableColumn<C, R, TethysUnits> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newUnitsField(), pFactory.newUnitsField(), TethysUnits.class);
        }

        @Override
        public TethysSwingUnitsTextField getControl() {
            return (TethysSwingUnitsTextField) super.getControl();
        }

        @Override
        public TethysSwingUnitsTextField getEditControl() {
            return (TethysSwingUnitsTextField) super.getEditControl();
        }
    }

    /**
     * Dilution Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutionCell<C, R>
            extends TethysSwingTableCell<C, R, TethysDilution> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDilutionCell(final TethysSwingTableColumn<C, R, TethysDilution> pColumn,
                                               final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutionField(), pFactory.newDilutionField(), TethysDilution.class);
        }

        @Override
        public TethysSwingDilutionTextField getControl() {
            return (TethysSwingDilutionTextField) super.getControl();
        }

        @Override
        public TethysSwingDilutionTextField getEditControl() {
            return (TethysSwingDilutionTextField) super.getEditControl();
        }
    }

    /**
     * DilutedPrice Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutedPriceCell<C, R>
            extends TethysSwingTableCell<C, R, TethysDilutedPrice>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDilutedPriceCell(final TethysSwingTableColumn<C, R, TethysDilutedPrice> pColumn,
                                                   final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDilutedPriceField(), pFactory.newDilutedPriceField(), TethysDilutedPrice.class);
        }

        @Override
        public TethysSwingDilutedPriceTextField getControl() {
            return (TethysSwingDilutedPriceTextField) super.getControl();
        }

        @Override
        public TethysSwingDilutedPriceTextField getEditControl() {
            return (TethysSwingDilutedPriceTextField) super.getEditControl();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditControl().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Ratio Cell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRatioCell<C, R>
            extends TethysSwingTableCell<C, R, TethysRatio> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableRatioCell(final TethysSwingTableColumn<C, R, TethysRatio> pColumn,
                                            final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newRatioField(), pFactory.newRatioField(), TethysRatio.class);
        }

        @Override
        public TethysSwingRatioTextField getControl() {
            return (TethysSwingRatioTextField) super.getControl();
        }

        @Override
        public TethysSwingRatioTextField getEditControl() {
            return (TethysSwingRatioTextField) super.getEditControl();
        }
    }

    /**
     * DateCell.
     * @param <C> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDateCell<C, R>
            extends TethysSwingTableCell<C, R, TethysDate>
            implements TethysDateField<JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         */
        protected TethysSwingTableDateCell(final TethysSwingTableColumn<C, R, TethysDate> pColumn,
                                           final TethysSwingGuiFactory pFactory) {
            super(pColumn, pFactory.newDateField(), pFactory.newDateField(), TethysDate.class);
            useDialog();
        }

        @Override
        public TethysSwingDateButtonField getControl() {
            return (TethysSwingDateButtonField) super.getControl();
        }

        @Override
        public TethysSwingDateButtonField getEditControl() {
            return (TethysSwingDateButtonField) super.getEditControl();
        }

        @Override
        public TethysSwingDateButtonManager getDateManager() {
            return getEditControl().getDateManager();
        }
    }

    /**
     * ScrollCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysSwingTableScrollCell<C, R, T>
            extends TethysSwingTableCell<C, R, T>
            implements TethysScrollField<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableScrollCell(final TethysSwingTableColumn<C, R, T> pColumn,
                                             final TethysSwingGuiFactory pFactory,
                                             final Class<T> pClass) {
            super(pColumn, pFactory.newScrollField(), pFactory.newScrollField(), pClass);
            useDialog();
        }

        @Override
        public TethysSwingScrollButtonField<T> getControl() {
            return (TethysSwingScrollButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingScrollButtonField<T> getEditControl() {
            return (TethysSwingScrollButtonField<T>) super.getEditControl();
        }

        @Override
        public TethysSwingScrollButtonManager<T> getScrollManager() {
            return getEditControl().getScrollManager();
        }
    }

    /**
     * ListCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     */
    public static class TethysSwingTableListCell<C, R, T>
            extends TethysSwingTableCell<C, R, TethysItemList<T>>
            implements TethysListField<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableListCell(final TethysSwingTableColumn<C, R, TethysItemList<T>> pColumn,
                                           final TethysSwingGuiFactory pFactory,
                                           final Class<T> pClass) {
            super(pColumn, pFactory.newListField(), pFactory.newListField());
            useDialog();
        }

        @Override
        public TethysSwingListButtonField<T> getControl() {
            return (TethysSwingListButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingListButtonField<T> getEditControl() {
            return (TethysSwingListButtonField<T>) super.getEditControl();
        }

        @Override
        public TethysSwingListButtonManager<T> getListManager() {
            return getEditControl().getListManager();
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
    public static class TethysSwingTableIconCell<C, R, T>
            extends TethysSwingTableCell<C, R, T>
            implements TethysIconField<T, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableIconCell(final TethysSwingTableColumn<C, R, T> pColumn,
                                           final TethysSwingGuiFactory pFactory,
                                           final Class<T> pClass) {
            super(pColumn, pFactory.newSimpleIconField(), pFactory.newSimpleIconField(), pClass);
        }

        @Override
        public TethysSwingIconButtonField<T> getControl() {
            return (TethysSwingIconButtonField<T>) super.getControl();
        }

        @Override
        public TethysSwingIconButtonField<T> getEditControl() {
            return (TethysSwingIconButtonField<T>) super.getEditControl();
        }

        @Override
        public TethysSimpleIconButtonManager<T, JComponent, Icon> getIconManager() {
            return getEditControl().getIconManager();
        }
    }

    /**
     * IconStateCell.
     * @param <C> the column identity
     * @param <R> the table item class
     * @param <T> the column item class
     * @param <S> the state class
     */
    public static class TethysSwingTableStateIconCell<C, R, T, S>
            extends TethysSwingTableCell<C, R, T>
            implements TethysStateIconField<T, S, JComponent, Icon> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFactory the GUI Factory
         * @param pClass the field class
         */
        protected TethysSwingTableStateIconCell(final TethysSwingTableColumn<C, R, T> pColumn,
                                                final TethysSwingGuiFactory pFactory,
                                                final Class<T> pClass) {
            super(pColumn, pFactory.newStateIconField(), pFactory.newStateIconField(), pClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysSwingStateIconButtonField<T, S> getControl() {
            return (TethysSwingStateIconButtonField<T, S>) super.getControl();
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysSwingStateIconButtonField<T, S> getEditControl() {
            return (TethysSwingStateIconButtonField<T, S>) super.getEditControl();
        }

        @Override
        public TethysSwingStateIconButtonManager<T, S> getIconManager() {
            return getControl().getIconManager();
        }
    }
}
