/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.lethe.field.swing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Currency;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldValue;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * Cell editors.
 * @author Tony Washer
 */
public final class MetisSwingFieldCellEditor {
    /**
     * Empty string.
     */
    private static final String STR_EMPTY = "";

    /**
     * private constructor.
     */
    private MetisSwingFieldCellEditor() {
    }

    /**
     * String Cell Editor.
     */
    public static class MetisFieldStringCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2172483058466364800L;

        /**
         * The text field.
         */
        private final JTextField theField;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * Constructor.
         */
        protected MetisFieldStringCellEditor() {
            theField = new JTextField();
            theField.addFocusListener(new StringListener());
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Store location of box */
            int myRow = pTable.convertRowIndexToModel(pRowIndex);
            int myCol = pTable.convertColumnIndexToModel(pColIndex);
            thePoint = new Point(myCol, myRow);

            /* Set the text */
            theField.setText(((pValue == null)
                              || (MetisFieldValue.ERROR.equals(pValue)))
                                                                         ? STR_EMPTY
                                                                         : (String) pValue);
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!STR_EMPTY.equals(s)) {
                return s;
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Object s = getCellEditorValue();
            if (s == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }

        /**
         * Focus Listener.
         */
        private final class StringListener
                implements FocusListener {

            @Override
            public void focusGained(final FocusEvent e) {
                /* Not needed */
            }

            @Override
            public void focusLost(final FocusEvent e) {
                /* Cancel editing on focus lost */
                fireEditingCanceled();
            }
        }
    }

    /**
     * Integer Cell Editor.
     */
    public static class MetisFieldIntegerCellEditor
            extends MetisFieldStringCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2172483058466364800L;

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Access the value */
            Object o = pValue;

            /* If we have an integer value passed */
            if (o instanceof Integer) {
                /* Format it */
                o = Integer.toString((Integer) o);
            }

            /* Pass through to super-class */
            return super.getTableCellEditorComponent(pTable, o, isSelected, pRowIndex, pColIndex);
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return Integer.valueOf((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * IconButton Cell Editor.
     * @param <T> the object type
     */
    public static class MetisFieldIconButtonCellEditor<T>
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6709609365303162431L;

        /**
         * The class of the object.
         */
        private final Class<T> theClazz;

        /**
         * The button.
         */
        private final transient TethysSwingIconButtonManager<T> theButton;

        /**
         * The mouse Listener.
         */
        private final transient MouseListener theMouseListener;

        /**
         * The editor table.
         */
        private transient JTable theTable;

        /**
         * The editor value.
         */
        private transient T theValue;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * Constructor.
         * @param pButton the button
         * @param pClazz the class of the object
         */
        protected MetisFieldIconButtonCellEditor(final TethysSwingIconButtonManager<T> pButton,
                                                 final Class<T> pClazz) {
            /* Store parameters */
            theButton = pButton;
            theClazz = pClazz;

            /* Create the MouseListener */
            theMouseListener = new MouseListener();

            /* Set listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                /* Store value and stop editing */
                theValue = theButton.getValue();
                stopCellEditing();
            });
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         */
        public void setIconMapSet(final Function<Integer, TethysIconMapSet<T>> pSupplier) {
            theButton.setIconMapSet(() -> pSupplier.apply(thePoint.y));
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Store location of button */
            int myRow = pTable.convertRowIndexToModel(pRowIndex);
            int myCol = pTable.convertColumnIndexToModel(pColIndex);
            thePoint = new Point(myCol, myRow);
            theTable = pTable;

            /* Store current value */
            theValue = theClazz.cast(pValue);
            theButton.setValue(theValue);

            /* Declare the mouse listener */
            theTable.addMouseListener(theMouseListener);

            /* Return the button */
            return theButton.getNode();
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }

        @Override
        public boolean stopCellEditing() {
            if (super.stopCellEditing()) {
                theTable.removeMouseListener(theMouseListener);
                thePoint = null;
                return true;
            }
            return false;
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            theTable.removeMouseListener(theMouseListener);
            thePoint = null;
        }

        /**
         * Mouse Adapter class.
         * <p>
         * Required to handle button clicked, dragged, and released in different place
         */
        private class MouseListener
                extends MouseAdapter {
            @Override
            public void mouseReleased(final MouseEvent e) {
                Rectangle myRect = theButton.getNode().getBounds();
                if (!myRect.contains(e.getPoint())) {
                    cancelCellEditing();
                }
            }
        }
    }

    /**
     * ScrollButton Cell Editor.
     * @param <T> the object type
     */
    public static class MetisFieldScrollButtonCellEditor<T>
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 8612779653045598187L;

        /**
         * The class of the object.
         */
        private final Class<T> theClazz;

        /**
         * The button.
         */
        private final transient TethysSwingScrollButtonManager<T> theButton;

        /**
         * The mouse Listener.
         */
        private final transient MouseListener theMouseListener;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * The editor value.
         */
        private transient T theValue;

        /**
         * The active table.
         */
        private JTable theTable;

        /**
         * Constructor.
         * @param pButton the button
         * @param pClazz the class of the object
         */
        protected MetisFieldScrollButtonCellEditor(final TethysSwingScrollButtonManager<T> pButton,
                                                   final Class<T> pClazz) {
            /* Store parameters */
            theClazz = pClazz;
            theButton = pButton;

            /* Create the MouseListener */
            theMouseListener = new MouseListener();

            /* Set listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                /* Store value and stop editing */
                theValue = theButton.getValue();
                stopCellEditing();
            });
        }

        /**
         * Set Menu configurator.
         * @param pConfigurator the configurator
         */
        public void setMenuConfigurator(final BiConsumer<Integer, TethysScrollMenu<T, Icon>> pConfigurator) {
            theButton.setMenuConfigurator(c -> pConfigurator.accept(thePoint.y, c));
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Store location of button */
            int myRow = pTable.convertRowIndexToModel(pRowIndex);
            int myCol = pTable.convertColumnIndexToModel(pColIndex);
            thePoint = new Point(myCol, myRow);
            theTable = pTable;

            /* Store current value */
            theValue = theClazz.cast(pValue);
            theButton.setValue(theValue);

            /* Declare the mouse listener */
            theTable.addMouseListener(theMouseListener);

            /* Return the button */
            return theButton.getNode();
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }

        @Override
        public boolean stopCellEditing() {
            if (super.stopCellEditing()) {
                theTable.removeMouseListener(theMouseListener);
                return true;
            }
            return false;
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            theTable.removeMouseListener(theMouseListener);
        }

        /**
         * Mouse Adapter class.
         * <p>
         * Required to handle button clicked, dragged, and released in different place
         */
        private class MouseListener
                extends MouseAdapter {
            @Override
            public void mouseReleased(final MouseEvent e) {
                Rectangle myRect = theButton.getNode().getBounds();
                if (!myRect.contains(e.getPoint())) {
                    cancelCellEditing();
                }
            }
        }
    }

    /**
     * ListButton Cell Editor.
     * @param <T> the object type
     */
    public static class MetisFieldListButtonCellEditor<T>
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -154317476142054387L;

        /**
         * The button.
         */
        private final transient TethysSwingListButtonManager<T> theButton;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * The mouse Listener.
         */
        private final transient MouseListener theMouseListener;

        /**
         * The editor value.
         */
        private transient TethysItemList<T> theValue;

        /**
         * The active table.
         */
        private JTable theTable;

        /**
         * Constructor.
         * @param pButton the button
         */
        protected MetisFieldListButtonCellEditor(final TethysSwingListButtonManager<T> pButton) {
            /* Store parameters */
            theButton = pButton;

            /* Create the MouseListener */
            theMouseListener = new MouseListener();

            /* Set listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                /* Store value and stop editing */
                theValue = theButton.getValue();
                stopCellEditing();
            });
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        @SuppressWarnings("unchecked")
        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Store location of button */
            int myRow = pTable.convertRowIndexToModel(pRowIndex);
            int myCol = pTable.convertColumnIndexToModel(pColIndex);
            thePoint = new Point(myCol, myRow);
            theTable = pTable;

            /* Store current value */
            theValue = (TethysItemList<T>) pValue;
            theButton.setValue(theValue);

            /* Declare the mouse listener */
            theTable.addMouseListener(theMouseListener);

            /* Return the button */
            return theButton.getNode();
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }

        @Override
        public boolean stopCellEditing() {
            if (super.stopCellEditing()) {
                theTable.removeMouseListener(theMouseListener);
                return true;
            }
            return false;
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            theTable.removeMouseListener(theMouseListener);
        }

        /**
         * Mouse Adapter class.
         * <p>
         * Required to handle button clicked, dragged, and released in different place
         */
        private class MouseListener
                extends MouseAdapter {
            @Override
            public void mouseReleased(final MouseEvent e) {
                Rectangle myRect = theButton.getNode().getBounds();
                if (!myRect.contains(e.getPoint())) {
                    cancelCellEditing();
                }
            }
        }
    }

    /**
     * Calendar Cell Editor.
     */
    public static class MetisFieldCalendarCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2421257860258168379L;

        /**
         * The button manager.
         */
        private final transient TethysSwingDateButtonManager theButton;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * The value.
         */
        private transient TethysDate theValue;

        /**
         * Constructor.
         * @param pButton the button
         */
        protected MetisFieldCalendarCellEditor(final TethysSwingDateButtonManager pButton) {
            /* Create a new button */
            theButton = pButton;

            /* Set listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                /* Store value and stop editing */
                theValue = theButton.getSelectedDate();
                stopCellEditing();
            });
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        /**
         * Set Date configurator.
         * @param pConfigurator the configurator
         */
        public void setDateConfigurator(final BiConsumer<Integer, TethysDateConfig> pConfigurator) {
            theButton.setDateConfigurator(c -> pConfigurator.accept(thePoint.y, c));
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Store location of box */
            int myRow = pTable.convertRowIndexToModel(pRowIndex);
            int myCol = pTable.convertColumnIndexToModel(pColIndex);
            thePoint = new Point(myCol, myRow);

            /* Store current value */
            theValue = (TethysDate) pValue;
            theButton.setSelectedDate(theValue);

            /* Return the button */
            return theButton.getNode();
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }
    }

    /**
     * Decimal Cell Editor.
     */
    private abstract static class MetisFieldDecimalCellEditor
            extends MetisFieldStringCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2636603780411978911L;

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Access the value */
            Object o = pValue;

            /* If we have a decimal value passed */
            if (o instanceof TethysDecimal) {
                /* Format it */
                o = ((TethysDecimal) o).toString();
            }

            /* Pass through to super-class */
            return super.getTableCellEditorComponent(pTable, o, isSelected, pRowIndex, pColIndex);
        }
    }

    /**
     * Rate Cell Editor.
     */
    public static class MetisFieldRateCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2636603780411978911L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldRateCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parseRateValue((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * Money Cell Editor.
     */
    public static class MetisFieldMoneyCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2748644075720076417L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * The currency function.
         */
        private transient Function<Integer, Currency> theCurrency;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldMoneyCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
            theCurrency = r -> theParser.getDefaultCurrency();
        }

        /**
         * Set the deemed currency.
         * @param pCurrency the deemed currency function
         */
        public void setDeemedCurrency(final Function<Integer, Currency> pCurrency) {
            theCurrency = pCurrency;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    Currency myCurrency = theCurrency.apply((int) getPoint().getY());
                    if (myCurrency == null) {
                        myCurrency = theParser.getDefaultCurrency();
                    }
                    return theParser.parseMoneyValue((String) o, myCurrency);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * Units Cell Editor.
     */
    public static class MetisFieldUnitsCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5924761972037405523L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldUnitsCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parseUnitsValue((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * Ratio Cell Editor.
     */
    public static class MetisFieldRatioCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2407894758574879133L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldRatioCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parseRatioValue((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * Dilutions Cell Editor.
     */
    public static class MetisFieldDilutionCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4764410922782962134L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldDilutionCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parseDilutionValue((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * Price Cell Editor.
     */
    public static class MetisFieldPriceCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7215554993415708775L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * The currency function.
         */
        private transient Function<Integer, Currency> theCurrency;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldPriceCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
            theCurrency = r -> theParser.getDefaultCurrency();
        }

        /**
         * Set the deemed currency.
         * @param pCurrency the deemed currency function
         */
        public void setDeemedCurrency(final Function<Integer, Currency> pCurrency) {
            theCurrency = pCurrency;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    Currency myCurrency = theCurrency.apply((int) getPoint().getY());
                    if (myCurrency == null) {
                        myCurrency = theParser.getDefaultCurrency();
                    }
                    return theParser.parsePriceValue((String) o, myCurrency);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }

    /**
     * DilutedPrice Cell Editor.
     */
    public static class MetisFieldDilutedPriceCellEditor
            extends MetisFieldDecimalCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 3930787232807465136L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalParser theParser;

        /**
         * The currency function.
         */
        private transient Function<Integer, Currency> theCurrency;

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MetisFieldDilutedPriceCellEditor(final TethysDecimalParser pParser) {
            theParser = pParser;
            theCurrency = r -> theParser.getDefaultCurrency();
        }

        /**
         * Set the deemed currency.
         * @param pCurrency the deemed currency function
         */
        public void setDeemedCurrency(final Function<Integer, Currency> pCurrency) {
            theCurrency = pCurrency;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    Currency myCurrency = theCurrency.apply((int) getPoint().getY());
                    if (myCurrency == null) {
                        myCurrency = theParser.getDefaultCurrency();
                    }
                    return theParser.parseDilutedPriceValue((String) o, myCurrency);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
