/*******************************************************************************
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
import net.sourceforge.JDecimal.Decimal.DilutedPrice;
import net.sourceforge.JDecimal.Decimal.Dilution;
import net.sourceforge.JDecimal.Decimal.Money;
import net.sourceforge.JDecimal.Decimal.Price;
import net.sourceforge.JDecimal.Decimal.Rate;
import net.sourceforge.JDecimal.Decimal.Units;

public class Editor {
    /* String Cell Editor */
    public static class StringCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 2172483058466364800L;
        private JTextField theField;

        public StringCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : (String) value);
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
            String s = (String) getCellEditorValue();
            if (s == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* Integer Cell Editor */
    public static class IntegerCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 2172483058466364800L;
        private JTextField theField;

        public IntegerCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : Integer
                    .toString((Integer) value));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    Integer myInt = new Integer(s);
                    return myInt;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            String s = (String) getCellEditorValue();
            if (s == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* String Cell Editor */
    public static class ComboBoxCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 6107290027015360230L;
        private JComboBox theCombo;
        private ComboAction theActionListener = new ComboAction();
        private ComboPopup thePopupListener = new ComboPopup();

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            DataTable<?> myTable = (DataTable<?>) table;
            theCombo = myTable.getComboBox(row, col);
            theCombo.setSelectedIndex(-1);
            if (value != null)
                theCombo.setSelectedItem((String) value);
            theCombo.addActionListener(theActionListener);
            theCombo.addPopupMenuListener(thePopupListener);
            return theCombo;
        }

        private class ComboAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        }

        private class ComboPopup implements PopupMenuListener {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                cancelCellEditing();
            }
        }

        @Override
        public Object getCellEditorValue() {
            String s = (String) theCombo.getSelectedItem();
            if ((s != null) && (s.equals("")))
                s = null;
            return s;
        }

        @Override
        public void cancelCellEditing() {
            theCombo.removePopupMenuListener(thePopupListener);
            theCombo.removeActionListener(theActionListener);
            super.cancelCellEditing();
        }

        @Override
        public boolean stopCellEditing() {
            theCombo.removePopupMenuListener(thePopupListener);
            theCombo.removeActionListener(theActionListener);
            return super.stopCellEditing();
        }
    }

    /* Calendar Cell Editor */
    public static class CalendarCell extends DateDayCellEditor {
        private static final long serialVersionUID = -5463480186940634327L;
        private DateDayRange theRange = null;

        public void setRange(DateDayRange pRange) {
            theRange = pRange;
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            /* Access the range */
            DateDay myStart = (theRange == null) ? null : theRange.getStart();
            DateDay myEnd = (theRange == null) ? null : theRange.getEnd();
            DateDay myCurr;

            /* If the value is null */
            if ((value == null) || (value == Renderer.theError)) {
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

    /* Rates Cell Editor */
    public static class RateCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 2636603780411978911L;
        private JTextField theField;

        public RateCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : ((Rate) value)
                    .format(false));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    Rate myRate = new Rate(s);
                    return myRate;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Rate myRate = (Rate) getCellEditorValue();
            if (myRate == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* Money Cell Editor */
    public static class MoneyCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 2748644075720076417L;
        private JTextField theField;

        public MoneyCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : ((Money) value)
                    .format(false));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    Money myMoney = new Money(s);
                    return myMoney;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Money myMoney = (Money) getCellEditorValue();
            if (myMoney == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* Units Cell Editor */
    public static class UnitCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = -5924761972037405523L;
        private JTextField theField;

        public UnitCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : ((Units) value)
                    .format(false));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    Units myUnits = new Units(s);
                    return myUnits;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Units myUnits = (Units) getCellEditorValue();
            if ((Object) myUnits == this) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* Dilutions Cell Editor */
    public static class DilutionCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = -4764410922782962134L;
        private JTextField theField;

        public DilutionCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : ((Dilution) value)
                    .format(false));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    Dilution myDilution = new Dilution(s);
                    return myDilution;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Units myUnits = (Units) getCellEditorValue();
            if ((Object) myUnits == this) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* Price Cell Editor */
    public static class PriceCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 7215554993415708775L;
        private JTextField theField;

        public PriceCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : ((Price) value)
                    .format(false));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    Price myPrice = new Price(s);
                    return myPrice;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Price myPrice = (Price) getCellEditorValue();
            if (myPrice == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }

    /* DilutedPrice Cell Editor */
    public static class DilutedPriceCell extends AbstractCellEditor implements TableCellEditor {
        private static final long serialVersionUID = 3930787232807465136L;
        private JTextField theField;

        public DilutedPriceCell() {
            theField = new JTextField();
        }

        @Override
        public JComponent getTableCellEditorComponent(JTable table,
                                                      Object value,
                                                      boolean isSelected,
                                                      int row,
                                                      int col) {
            theField.setText(((value == null) || (value == Renderer.theError)) ? "" : ((DilutedPrice) value)
                    .format(false));
            return theField;
        }

        @Override
        public Object getCellEditorValue() {
            String s = theField.getText();
            if (!s.equals("")) {
                try {
                    DilutedPrice myPrice = new DilutedPrice(s);
                    return myPrice;
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public boolean stopCellEditing() {
            Price myPrice = (Price) getCellEditorValue();
            if (myPrice == null) {
                fireEditingCanceled();
                return false;
            }
            return super.stopCellEditing();
        }
    }
}
