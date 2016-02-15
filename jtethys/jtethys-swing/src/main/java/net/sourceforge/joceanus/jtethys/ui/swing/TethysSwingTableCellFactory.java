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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;

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
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButton.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableManager.TethysSwingTableColumn;

/**
 * Swing Table Cell Factory.
 * @param <I> the column identity
 * @param <R> the table row type
 */
public class TethysSwingTableCellFactory<I, R>
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
    public TethysSwingTableCellFactory() {
        this(new TethysDataFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public TethysSwingTableCellFactory(final TethysDataFormatter pFormatter) {
        theEventManager = new TethysEventManager<>();
        theFormatter = pFormatter;
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
    protected TethysSwingTableCell<I, R, String> stringCell(final TethysSwingTableColumn<I, R, String> pColumn) {
        return listenToCell(new TethysSwingTableStringCell<>(pColumn));
    }

    /**
     * Obtain Short Cell.
     * @param pColumn the column
     * @return the short cell
     */
    protected TethysSwingTableCell<I, R, Short> shortCell(final TethysSwingTableColumn<I, R, Short> pColumn) {
        return listenToCell(new TethysSwingTableShortCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Integer Cell.
     * @param pColumn the column
     * @return the integer cell
     */
    protected TethysSwingTableCell<I, R, Integer> integerCell(final TethysSwingTableColumn<I, R, Integer> pColumn) {
        return listenToCell(new TethysSwingTableIntegerCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Long Cell.
     * @param pColumn the column
     * @return the long cell
     */
    protected TethysSwingTableCell<I, R, Long> longCell(final TethysSwingTableColumn<I, R, Long> pColumn) {
        return listenToCell(new TethysSwingTableLongCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Money Cell.
     * @param pColumn the column
     * @return the money cell
     */
    protected TethysSwingTableCell<I, R, TethysMoney> moneyCell(final TethysSwingTableColumn<I, R, TethysMoney> pColumn) {
        return listenToCell(new TethysSwingTableMoneyCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Price Cell.
     * @param pColumn the column
     * @return the price cell
     */
    protected TethysSwingTableCell<I, R, TethysPrice> priceCell(final TethysSwingTableColumn<I, R, TethysPrice> pColumn) {
        return listenToCell(new TethysSwingTablePriceCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Rate Cell.
     * @param pColumn the column
     * @return the rate cell
     */
    protected TethysSwingTableCell<I, R, TethysRate> rateCell(final TethysSwingTableColumn<I, R, TethysRate> pColumn) {
        return listenToCell(new TethysSwingTableRateCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Units Cell.
     * @param pColumn the column
     * @return the units cell
     */
    protected TethysSwingTableCell<I, R, TethysUnits> unitsCell(final TethysSwingTableColumn<I, R, TethysUnits> pColumn) {
        return listenToCell(new TethysSwingTableUnitsCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Dilution Cell.
     * @param pColumn the column
     * @return the dilution cell
     */
    protected TethysSwingTableCell<I, R, TethysDilution> dilutionCell(final TethysSwingTableColumn<I, R, TethysDilution> pColumn) {
        return listenToCell(new TethysSwingTableDilutionCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Ratio Cell.
     * @param pColumn the column
     * @return the ratio cell
     */
    protected TethysSwingTableCell<I, R, TethysRatio> ratioCell(final TethysSwingTableColumn<I, R, TethysRatio> pColumn) {
        return listenToCell(new TethysSwingTableRatioCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain DilutedPrice Cell.
     * @param pColumn the column
     * @return the dilutedPrice cell
     */
    protected TethysSwingTableCell<I, R, TethysDilutedPrice> dilutedPriceCell(final TethysSwingTableColumn<I, R, TethysDilutedPrice> pColumn) {
        return listenToCell(new TethysSwingTableDilutedPriceCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Scroll Cell.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the scroll cell
     */
    protected <C> TethysSwingTableCell<I, R, C> scrollCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                                           final Class<C> pClass) {
        return listenToCell(new TethysSwingTableScrollCell<>(pColumn, pClass));
    }

    /**
     * Obtain List Cell.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the list cell
     */
    protected <C> TethysSwingTableCell<I, R, C> listCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                                         final Class<C> pClass) {
        return listenToCell(new TethysSwingTableListCell<>(pColumn, pClass));
    }

    /**
     * Obtain Date Cell.
     * @param pColumn the column
     * @return the date cell
     */
    protected TethysSwingTableCell<I, R, TethysDate> dateCell(final TethysSwingTableColumn<I, R, TethysDate> pColumn) {
        return listenToCell(new TethysSwingTableDateCell<>(pColumn, theFormatter));
    }

    /**
     * Obtain Icon Cell.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell
     */
    protected <C> TethysSwingTableCell<I, R, C> iconCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                                         final Class<C> pClass) {
        return listenToCell(new TethysSwingTableIconCell<>(pColumn, pClass));
    }

    /**
     * Obtain State Icon Cell.
     * @param <C> the column type
     * @param pColumn the column
     * @param pClass the class of the item
     * @return the icon cell
     */
    protected <C> TethysSwingTableCell<I, R, C> stateIconCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                                              final Class<C> pClass) {
        return listenToCell(new TethysSwingTableStateIconCell<>(pColumn, pClass));
    }

    /**
     * Listen to cell.
     * @param <C> the column type
     * @param pCell the cell
     * @return the cell
     */
    private <C> TethysSwingTableCell<I, R, C> listenToCell(final TethysSwingTableCell<I, R, C> pCell) {
        pCell.getEventRegistrar().addEventListener(theEventManager::cascadeEvent);
        return pCell;
    }

    /**
     * Table Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public abstract static class TethysSwingTableCell<I, R, C>
            implements TethysEventProvider<TethysUIEvent> {
        /**
         * The Column.
         */
        private final TethysSwingTableColumn<I, R, C> theColumn;

        /**
         * The Control field.
         */
        private final TethysSwingDataTextField<C> theControl;

        /**
         * The Data class.
         */
        private final Class<C> theClass;

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
         * @param pClass the field class
         */
        protected TethysSwingTableCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                       final TethysSwingDataTextField<C> pControl,
                                       final Class<C> pClass) {
            /* Record the parameters */
            theColumn = pColumn;
            theControl = pControl;
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
         * Obtain the active row.
         * @return the row
         */
        public R getActiveRow() {
            return theRow;
        }

        /**
         * Obtain the column.
         * @return the column
         */
        public TethysSwingTableColumn<I, R, C> getColumn() {
            return theColumn;
        }

        /**
         * Obtain the new value.
         * @return the new value
         */
        public C getNewValue() {
            return theEditor.getCellEditorValue();
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

        /**
         * Obtain the control.
         * @return the field
         */
        protected TethysSwingDataTextField<C> getControl() {
            return theControl;
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
         * Set the active row.
         * @param pIndex the row index
         */
        protected void setActiveRow(final int pIndex) {
            theRow = theColumn.getRowForIndex(pIndex);
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
                theControl.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> stopCellEditing());
                theControl.getEventRegistrar().addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
            }

            @Override
            public C getCellEditorValue() {
                return theControl.getValue();
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

                /* Set field value and start edit */
                theControl.setValue(theClass.cast(pValue));
                theControl.startCellEditing();

                /* Return the field */
                return theControl.getNode();
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

                /* Set details and stop editing */
                theControl.setValue(theClass.cast(pValue));
                theControl.setEditable(false);

                /* Format the cell */
                theEventManager.fireEvent(TethysUIEvent.CELLFORMAT, TethysSwingTableCell.this);

                /* Return this as the render item */
                return theControl.getNode();
            }
        }
    }

    /**
     * String Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableStringCell<I, R>
            extends TethysSwingTableCell<I, R, String> {
        /**
         * Constructor.
         * @param pColumn the column
         */
        protected TethysSwingTableStringCell(final TethysSwingTableColumn<I, R, String> pColumn) {
            super(pColumn, new TethysSwingStringTextField(), String.class);
        }

        @Override
        public TethysSwingStringTextField getControl() {
            return (TethysSwingStringTextField) super.getControl();
        }
    }

    /**
     * Short Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableShortCell<I, R>
            extends TethysSwingTableCell<I, R, Short> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableShortCell(final TethysSwingTableColumn<I, R, Short> pColumn,
                                            final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingShortTextField(pFormatter), Short.class);
        }

        @Override
        public TethysSwingShortTextField getControl() {
            return (TethysSwingShortTextField) super.getControl();
        }
    }

    /**
     * Integer Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableIntegerCell<I, R>
            extends TethysSwingTableCell<I, R, Integer> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableIntegerCell(final TethysSwingTableColumn<I, R, Integer> pColumn,
                                              final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingIntegerTextField(pFormatter), Integer.class);
        }

        @Override
        public TethysSwingIntegerTextField getControl() {
            return (TethysSwingIntegerTextField) super.getControl();
        }
    }

    /**
     * Long Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableLongCell<I, R>
            extends TethysSwingTableCell<I, R, Long> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableLongCell(final TethysSwingTableColumn<I, R, Long> pColumn,
                                           final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingLongTextField(pFormatter), Long.class);
        }

        @Override
        public TethysSwingLongTextField getControl() {
            return (TethysSwingLongTextField) super.getControl();
        }
    }

    /**
     * Money Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableMoneyCell<I, R>
            extends TethysSwingTableCell<I, R, TethysMoney> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableMoneyCell(final TethysSwingTableColumn<I, R, TethysMoney> pColumn,
                                            final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingMoneyTextField(pFormatter), TethysMoney.class);
        }

        @Override
        public TethysSwingMoneyTextField getControl() {
            return (TethysSwingMoneyTextField) super.getControl();
        }
    }

    /**
     * Price Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTablePriceCell<I, R>
            extends TethysSwingTableCell<I, R, TethysPrice> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTablePriceCell(final TethysSwingTableColumn<I, R, TethysPrice> pColumn,
                                            final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingPriceTextField(pFormatter), TethysPrice.class);
        }

        @Override
        public TethysSwingPriceTextField getControl() {
            return (TethysSwingPriceTextField) super.getControl();
        }
    }

    /**
     * Rate Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRateCell<I, R>
            extends TethysSwingTableCell<I, R, TethysRate> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableRateCell(final TethysSwingTableColumn<I, R, TethysRate> pColumn,
                                           final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingRateTextField(pFormatter), TethysRate.class);
        }

        @Override
        public TethysSwingRateTextField getControl() {
            return (TethysSwingRateTextField) super.getControl();
        }
    }

    /**
     * Units Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableUnitsCell<I, R>
            extends TethysSwingTableCell<I, R, TethysUnits> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableUnitsCell(final TethysSwingTableColumn<I, R, TethysUnits> pColumn,
                                            final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingUnitsTextField(pFormatter), TethysUnits.class);
        }

        @Override
        public TethysSwingUnitsTextField getControl() {
            return (TethysSwingUnitsTextField) super.getControl();
        }
    }

    /**
     * Dilution Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutionCell<I, R>
            extends TethysSwingTableCell<I, R, TethysDilution> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableDilutionCell(final TethysSwingTableColumn<I, R, TethysDilution> pColumn,
                                               final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingDilutionTextField(pFormatter), TethysDilution.class);
        }

        @Override
        public TethysSwingDilutionTextField getControl() {
            return (TethysSwingDilutionTextField) super.getControl();
        }
    }

    /**
     * DilutedPrice Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDilutedPriceCell<I, R>
            extends TethysSwingTableCell<I, R, TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableDilutedPriceCell(final TethysSwingTableColumn<I, R, TethysDilutedPrice> pColumn,
                                                   final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingDilutedPriceTextField(pFormatter), TethysDilutedPrice.class);
        }

        @Override
        public TethysSwingDilutedPriceTextField getControl() {
            return (TethysSwingDilutedPriceTextField) super.getControl();
        }
    }

    /**
     * Ratio Cell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableRatioCell<I, R>
            extends TethysSwingTableCell<I, R, TethysRatio> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableRatioCell(final TethysSwingTableColumn<I, R, TethysRatio> pColumn,
                                            final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingRatioTextField(pFormatter), TethysRatio.class);
        }

        @Override
        public TethysSwingRatioTextField getControl() {
            return (TethysSwingRatioTextField) super.getControl();
        }
    }

    /**
     * DateCell.
     * @param <I> the column identity
     * @param <R> the table item class
     */
    public static class TethysSwingTableDateCell<I, R>
            extends TethysSwingTableCell<I, R, TethysDate> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pFormatter the data formatter
         */
        protected TethysSwingTableDateCell(final TethysSwingTableColumn<I, R, TethysDate> pColumn,
                                           final TethysDataFormatter pFormatter) {
            super(pColumn, new TethysSwingDateButtonField(pFormatter), TethysDate.class);
        }

        @Override
        public TethysSwingDateButtonField getControl() {
            return (TethysSwingDateButtonField) super.getControl();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysSwingDateButtonManager getDateManager() {
            return getControl().getDateManager();
        }
    }

    /**
     * ScrollCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public static class TethysSwingTableScrollCell<I, R, C>
            extends TethysSwingTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysSwingTableScrollCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                             final Class<C> pClass) {
            super(pColumn, new TethysSwingScrollButtonField<>(), pClass);
        }

        @Override
        public TethysSwingScrollButtonField<C> getControl() {
            return (TethysSwingScrollButtonField<C>) super.getControl();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysSwingScrollButtonManager<C> getScrollManager() {
            return getControl().getScrollManager();
        }
    }

    /**
     * ListCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public static class TethysSwingTableListCell<I, R, C>
            extends TethysSwingTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysSwingTableListCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                           final Class<C> pClass) {
            super(pColumn, new TethysSwingListButtonField<>(), pClass);
        }

        @Override
        public TethysSwingListButtonField<C> getControl() {
            return (TethysSwingListButtonField<C>) super.getControl();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysSwingListButtonManager<C> getListManager() {
            return getControl().getListManager();
        }
    }

    /**
     * IconCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     */
    public static class TethysSwingTableIconCell<I, R, C>
            extends TethysSwingTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysSwingTableIconCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                           final Class<C> pClass) {
            super(pColumn, new TethysSwingIconButtonField<C>(), pClass);
        }

        @Override
        public TethysSwingIconButtonField<C> getControl() {
            return (TethysSwingIconButtonField<C>) super.getControl();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysIconButtonManager<C, JComponent, Icon> getIconManager() {
            return getControl().getIconManager();
        }
    }

    /**
     * IconStateCell.
     * @param <I> the column identity
     * @param <R> the table item class
     * @param <C> the column item class
     * @param <S> the state class
     */
    public static class TethysSwingTableStateIconCell<I, R, C, S>
            extends TethysSwingTableCell<I, R, C> {
        /**
         * Constructor.
         * @param pColumn the column
         * @param pClass the field class
         */
        protected TethysSwingTableStateIconCell(final TethysSwingTableColumn<I, R, C> pColumn,
                                                final Class<C> pClass) {
            super(pColumn, new TethysSwingStateIconButtonField<C, S>(), pClass);
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysSwingStateIconButtonField<C, S> getControl() {
            return (TethysSwingStateIconButtonField<C, S>) super.getControl();
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysSwingStateIconButtonManager<C, S> getIconManager() {
            return getControl().getIconManager();
        }
    }
}
