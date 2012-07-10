/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayCellEditor;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Decimal;
import net.sourceforge.JDecimal.DilutedPrice;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.models.ui.Renderer.RendererFieldValue;

/**
 * Cell editors.
 * @author Tony Washer
 */
public class Editor {
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
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable table,
                                                      final Object value,
                                                      final boolean isSelected,
                                                      final int row,
                                                      final int col) {
            theField.setText(((value == null) || (RendererFieldValue.Error.equals(value)))
                                                                                          ? ""
                                                                                          : (String) value);
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
        public JComponent getTableCellEditorComponent(final JTable table,
                                                      final Object value,
                                                      final boolean isSelected,
                                                      final int row,
                                                      final int col) {
            /* Access the value */
            Object o = value;

            /* If we have an integer value passed */
            if (o instanceof Integer) {
                /* Format it */
                o = Integer.toString((Integer) o);
            }

            /* Pass through to super-class */
            return super.getTableCellEditorComponent(table, o, isSelected, row, col);
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
        private JComboBox theCombo = null;

        /**
         * The action Listener.
         */
        private final transient ComboAction theActionListener = new ComboAction();

        /**
         * The popUp listener.
         */
        private final transient ComboPopup thePopupListener = new ComboPopup();

        @Override
        public JComponent getTableCellEditorComponent(final JTable table,
                                                      final Object value,
                                                      final boolean isSelected,
                                                      final int row,
                                                      final int col) {
            if (!(table instanceof DataTable)) {
                return null;
            }
            DataTable<?> myTable = (DataTable<?>) table;
            theCombo = myTable.getComboBox(row, col);
            if (value != null) {
                theCombo.setSelectedItem(value);
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
    public static class CalendarEditor extends DateDayCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5463480186940634327L;

        /**
         * The Selectable range.
         */
        private transient DateDayRange theRange = null;

        /**
         * Set the selectable range.
         * @param pRange the range
         */
        public void setRange(final DateDayRange pRange) {
            theRange = pRange;
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable table,
                                                      final Object value,
                                                      final boolean isSelected,
                                                      final int row,
                                                      final int col) {
            /* Access the range */
            DateDay myStart = (theRange == null) ? null : theRange.getStart();
            DateDay myEnd = (theRange == null) ? null : theRange.getEnd();
            DateDay myCurr;

            /* If the value is null */
            if ((value == null) || (RendererFieldValue.Error.equals(value))) {
                myCurr = new DateDay();
            } else {
                myCurr = (DateDay) value;
            }

            /* Set up initial values and range */
            setEarliestDateDay(myStart);
            setLatestDateDay(myEnd);

            /* Pass onwards */
            return super.getTableCellEditorComponent(table, myCurr, isSelected, row, col);
        }

        @Override
        public Object getCellEditorValue() {
            return getSelectedDateDay();
        }

        @Override
        public boolean stopCellEditing() {
            DateDay myDate = (DateDay) getCellEditorValue();
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
        public JComponent getTableCellEditorComponent(final JTable table,
                                                      final Object value,
                                                      final boolean isSelected,
                                                      final int row,
                                                      final int col) {
            /* Access the value */
            Object o = value;

            /* If we have a decimal value passed */
            if (o instanceof Decimal) {
                /* Format it */
                o = ((Decimal) o).format(false);
            }

            /* Pass through to super-class */
            return super.getTableCellEditorComponent(table, o, isSelected, row, col);
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

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return new Rate((String) o);
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

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return new Money((String) o);
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

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return new Units((String) o);
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

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return new Dilution((String) o);
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

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return new Price((String) o);
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

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String) && (!o.equals(""))) {
                try {
                    return new DilutedPrice((String) o);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
