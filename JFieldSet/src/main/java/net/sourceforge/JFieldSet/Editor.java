/*******************************************************************************
 * JFieldSet: Java Swing Field Set
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFieldSet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDateDay.JDateDayCellEditor;
import net.sourceforge.JDateDay.JDateDayFormatter;
import net.sourceforge.JDateDay.JDateDayRange;
import net.sourceforge.JDecimal.JDecimal;
import net.sourceforge.JDecimal.JDecimalParser;
import net.sourceforge.JFieldSet.Renderer.RendererFieldValue;

/**
 * Cell editors.
 * @author Tony Washer
 */
public class Editor {
    /**
     * ComboBoxSelector interface.
     */
    public interface ComboBoxSelector {
        /**
         * Get the combo box for the item at row and column.
         * @param pRowIndex the row
         * @param pColIndex the column
         * @return the combo box
         */
        JComboBox<?> getComboBox(final int pRowIndex,
                                 final int pColIndex);
    }

    /**
     * String Cell Editor.
     */
    public static class StringEditor extends AbstractCellEditor implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2172483058466364800L;

        /**
         * The text field.
         */
        private final JTextField theField;

        /**
         * Constructor.
         */
        public StringEditor() {
            theField = new JTextField();
            theField.addFocusListener(new StringListener());
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            theField.setText(((pValue == null) || (RendererFieldValue.Error.equals(pValue)))
                                                                                            ? ""
                                                                                            : (String) pValue);
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
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
        private final class StringListener implements FocusListener {

            @Override
            public void focusGained(final FocusEvent e) {
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
    public static class IntegerEditor extends StringEditor {
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
            if ((o instanceof String) && (!o.equals(""))) {
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
     * Boolean Cell Editor.
     */
    public static class BooleanEditor extends AbstractCellEditor implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2880132860782195694L;

        /**
         * The checkBox field.
         */
        private final JCheckBox theField;

        /**
         * The selection Listener.
         */
        private final transient BooleanListener theListener = new BooleanListener();

        /**
         * Constructor.
         */
        public BooleanEditor() {
            theField = new JCheckBox();
            theField.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            theField.setSelected(((pValue == null) || (RendererFieldValue.Error.equals(pValue)))
                                                                                                ? Boolean.FALSE
                                                                                                : (Boolean) pValue);
            theField.addItemListener(theListener);
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            return theField.isSelected();
        }

        @Override
        public boolean stopCellEditing() {
            Object s = getCellEditorValue();
            if (s == null) {
                fireEditingCanceled();
                return false;
            }
            theField.removeItemListener(theListener);
            return super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            theField.removeItemListener(theListener);
            super.cancelCellEditing();
        }

        /**
         * Boolean Action class.
         */
        private class BooleanListener implements ItemListener {
            @Override
            public void itemStateChanged(final ItemEvent arg0) {
                stopCellEditing();
            }
        }
    }

    /**
     * ComboBox Cell Editor.
     */
    public static class ComboBoxEditor extends AbstractCellEditor implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6107290027015360230L;

        /**
         * The ComboBox.
         */
        private JComboBox<?> theCombo = null;

        /**
         * The action Listener.
         */
        private final transient ComboAction theActionListener = new ComboAction();

        /**
         * The popUp listener.
         */
        private final transient ComboPopup thePopupListener = new ComboPopup();

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            if (!(pTable instanceof ComboBoxSelector)) {
                return null;
            }
            ComboBoxSelector myTable = (ComboBoxSelector) pTable;
            theCombo = myTable.getComboBox(pTable.convertRowIndexToModel(pRowIndex),
                                           pTable.convertColumnIndexToModel(pColIndex));
            if (pValue != null) {
                theCombo.setSelectedItem(pValue);
            } else {
                theCombo.setSelectedIndex(-1);
            }
            theCombo.addActionListener(theActionListener);
            theCombo.addPopupMenuListener(thePopupListener);
            return theCombo;
        }

        /**
         * Combo Action class.
         */
        private class ComboAction implements ActionListener {
            @Override
            public void actionPerformed(final ActionEvent e) {
                stopCellEditing();
            }
        }

        /**
         * Combo Popup class.
         */
        private class ComboPopup implements PopupMenuListener {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
                cancelCellEditing();
            }
        }

        @Override
        public Object getCellEditorValue() {
            return (theCombo != null) ? theCombo.getSelectedItem() : null;
        }

        @Override
        public void cancelCellEditing() {
            if (theCombo != null) {
                theCombo.removePopupMenuListener(thePopupListener);
                theCombo.removeActionListener(theActionListener);
                theCombo = null;
            }
            super.cancelCellEditing();
        }

        @Override
        public boolean stopCellEditing() {
            if (theCombo != null) {
                theCombo.removePopupMenuListener(thePopupListener);
                theCombo.removeActionListener(theActionListener);
            }
            return super.stopCellEditing();
        }
    }

    /**
     * Calendar Cell Editor.
     */
    public static class CalendarEditor extends JDateDayCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5463480186940634327L;

        /**
         * The Selectable range.
         */
        private transient JDateDayRange theRange = null;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public CalendarEditor(final JDateDayFormatter pFormatter) {
            /* Create a new configuration */
            super(pFormatter);
        }

        /**
         * Set the selectable range.
         * @param pRange the range
         */
        public void setRange(final JDateDayRange pRange) {
            theRange = pRange;
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Access the range */
            JDateDay myStart = (theRange == null) ? null : theRange.getStart();
            JDateDay myEnd = (theRange == null) ? null : theRange.getEnd();
            JDateDay myCurr;

            /* If the value is null */
            if ((pValue == null) || (RendererFieldValue.Error.equals(pValue))) {
                myCurr = new JDateDay();
            } else {
                myCurr = (JDateDay) pValue;
            }

            /* Set up initial values and range */
            setEarliestDateDay(myStart);
            setLatestDateDay(myEnd);

            /* Pass onwards */
            return super.getTableCellEditorComponent(pTable, myCurr, isSelected, pRowIndex, pColIndex);
        }

        @Override
        public Object getCellEditorValue() {
            return getSelectedDateDay();
        }

        @Override
        public boolean stopCellEditing() {
            JDateDay myDate = (JDateDay) getCellEditorValue();
            if ((Object) myDate == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /**
     * Decimal Cell Editor.
     */
    private abstract static class DecimalEditor extends StringEditor {
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
            if (o instanceof JDecimal) {
                /* Format it */
                o = ((JDecimal) o).toString();
            }

            /* Pass through to super-class */
            return super.getTableCellEditorComponent(pTable, o, isSelected, pRowIndex, pColIndex);
        }
    }

    /**
     * Rate Cell Editor.
     */
    public static class RateEditor extends DecimalEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2636603780411978911L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * Constructor.
         */
        public RateEditor() {
            this(new JDecimalParser());
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        public RateEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
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
    public static class MoneyEditor extends DecimalEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2748644075720076417L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * Constructor.
         */
        public MoneyEditor() {
            this(new JDecimalParser());
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        public MoneyEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return theParser.parseMoneyValue((String) o);
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
    public static class UnitsEditor extends DecimalEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5924761972037405523L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * Constructor.
         */
        public UnitsEditor() {
            this(new JDecimalParser());
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        public UnitsEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
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
     * Dilutions Cell Editor.
     */
    public static class DilutionEditor extends DecimalEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4764410922782962134L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * Constructor.
         */
        public DilutionEditor() {
            this(new JDecimalParser());
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        public DilutionEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
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
    public static class PriceEditor extends DecimalEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7215554993415708775L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * Constructor.
         */
        public PriceEditor() {
            this(new JDecimalParser());
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        public PriceEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return theParser.parsePriceValue((String) o);
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
    public static class DilutedPriceEditor extends DecimalEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 3930787232807465136L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * Constructor.
         */
        public DilutedPriceEditor() {
            this(new JDecimalParser());
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        public DilutedPriceEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return theParser.parseDilutedPriceValue((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
