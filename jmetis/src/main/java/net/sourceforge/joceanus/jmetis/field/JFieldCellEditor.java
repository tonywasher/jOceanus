/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.field;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import net.sourceforge.joceanus.jmetis.field.JFieldManager.PopulateFieldData;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayCellEditor;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;

/**
 * Cell editors.
 * @author Tony Washer
 */
public class JFieldCellEditor {
    /**
     * Empty string.
     */
    private static final String STR_EMPTY = "";

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
     * PopUpMenuSelector interface.
     */
    public interface PopUpMenuSelector {
        /**
         * Get the popUpMenu for the item at row and column.
         * @param pEditor the cell editor
         * @param pRowIndex the row
         * @param pColIndex the column
         * @return the popUpMenu
         */
        JPopupMenu getPopUpMenu(PopUpMenuCellEditor pEditor,
                                final int pRowIndex,
                                final int pColIndex);
    }

    /**
     * String Cell Editor.
     */
    public static class StringCellEditor
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
         * Constructor.
         */
        protected StringCellEditor() {
            theField = new JTextField();
            theField.addFocusListener(new StringListener());
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            theField.setText(((pValue == null) || (JFieldValue.ERROR.equals(pValue)))
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
    public static class IntegerCellEditor
            extends StringCellEditor {
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
     * Boolean Cell Editor.
     */
    public static class BooleanCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
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
        protected BooleanCellEditor() {
            theField = new JCheckBox();
            theField.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            theField.setSelected(((pValue == null) || (JFieldValue.ERROR.equals(pValue)))
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
        private class BooleanListener
                implements ItemListener {
            @Override
            public void itemStateChanged(final ItemEvent pEvent) {
                stopCellEditing();
            }
        }
    }

    /**
     * Icon Cell Editor.
     */
    public static class IconCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 838279262363600243L;

        /**
         * The button.
         */
        private final JButton theButton;

        /**
         * The selection Listener.
         */
        private final transient ButtonListener theListener = new ButtonListener();

        /**
         * The editor table.
         */
        private transient JTable theTable;

        /**
         * The editor value.
         */
        private transient Object theValue;

        /**
         * Is the editor active?
         */
        private transient boolean isActive;

        /**
         * Constructor.
         * @param pTable the table
         */
        protected IconCellEditor(final JTable pTable) {
            theButton = new JButton();
            theButton.setFocusPainted(false);
            theButton.addActionListener(theListener);
            pTable.addMouseListener(new MouseListener());
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Save table and value */
            theTable = pTable;
            theValue = pValue;
            isActive = true;

            /* Set the icon into the button */
            theButton.setIcon((pValue instanceof Icon)
                                                      ? (Icon) pValue
                                                      : null);

            /* Return the button */
            return theButton;
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }

        @Override
        public boolean stopCellEditing() {
            if (super.stopCellEditing()) {
                isActive = false;
                return true;
            }
            return false;
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            isActive = false;
        }

        /**
         * Button Listener class.
         */
        private class ButtonListener
                implements ActionListener {
            @Override
            public void actionPerformed(final ActionEvent pEvent) {
                /* If we can notify regarding the click */
                TableModel myModel = theTable.getModel();
                if (myModel instanceof PopulateFieldData) {
                    /* Determine the row that this has been invoked in */
                    int myRow = theTable.getEditingRow();
                    myRow = theTable.convertRowIndexToModel(myRow);

                    /* Determine the column that this has been invoked in */
                    int myCol = theTable.getEditingColumn();
                    myCol = theTable.convertColumnIndexToModel(myCol);

                    /* Notify the model regarding the click */
                    Point myPoint = new Point(myCol, myRow);
                    theValue = ((PopulateFieldData) myModel).buttonClick(myPoint);
                }

                /* Stop editing */
                stopCellEditing();
            }
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
                if (isActive) {
                    stopCellEditing();
                }
            }
        }
    }

    /**
     * PopUpMenu Cell Editor.
     */
    public static class PopUpMenuCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6815861197403796996L;

        /**
         * The button.
         */
        private final JButton theButton;

        /**
         * The selection Listener.
         */
        private final transient ButtonListener theButtonListener = new ButtonListener();

        /**
         * The popUp Listener.
         */
        private final transient PopUpListener thePopUpListener = new PopUpListener();

        /**
         * The mouse Listener.
         */
        private final transient MouseListener theMouseListener = new MouseListener();

        /**
         * The popUp Menu.
         */
        private transient JPopupMenu theMenu;

        /**
         * The table.
         */
        private transient JTable theTable;

        /**
         * The editor value.
         */
        private transient Object theValue;

        /**
         * Is the button active?
         */
        private transient boolean isActive;

        /**
         * Constructor.
         */
        protected PopUpMenuCellEditor() {
            theButton = new JButton();
            theButton.setHorizontalAlignment(SwingConstants.LEFT);
            theButton.setFocusPainted(false);
            theButton.addActionListener(theButtonListener);
        }

        @Override
        public JComponent getTableCellEditorComponent(final JTable pTable,
                                                      final Object pValue,
                                                      final boolean isSelected,
                                                      final int pRowIndex,
                                                      final int pColIndex) {
            /* Determine the menu to display */
            if (!(pTable instanceof PopUpMenuSelector)) {
                return null;
            }
            PopUpMenuSelector myTable = (PopUpMenuSelector) pTable;
            int myRow = pTable.convertRowIndexToModel(pRowIndex);
            int myCol = pTable.convertColumnIndexToModel(pColIndex);
            theMenu = myTable.getPopUpMenu(this, myRow, myCol);
            theTable = pTable;

            /* Set value */
            theValue = pValue;
            isActive = true;

            /* Set button text */
            if (pValue instanceof String) {
                theButton.setText((String) pValue);
            } else if (pValue != null) {
                theButton.setText(pValue.toString());
            } else {
                theButton.setText(null);
            }

            /* Declare the mouse listener */
            pTable.addMouseListener(theMouseListener);

            /* Return the button */
            return theButton;
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }

        @Override
        public boolean stopCellEditing() {
            if (super.stopCellEditing()) {
                theMenu.removePopupMenuListener(thePopUpListener);
                theTable.removeMouseListener(theMouseListener);
                isActive = false;
                return true;
            }
            return false;
        }

        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            theMenu.removePopupMenuListener(thePopUpListener);
            theTable.removeMouseListener(theMouseListener);
            isActive = false;
        }

        /**
         * Button Listener class.
         */
        private class ButtonListener
                implements ActionListener {
            @Override
            public void actionPerformed(final ActionEvent pEvent) {
                /* Stop mouse listener */
                isActive = false;

                /* Add the listener to the menu */
                theMenu.addPopupMenuListener(thePopUpListener);

                /* Show the popUp menu in the correct place */
                Rectangle myLoc = theButton.getBounds();
                theMenu.show(theButton, 0, myLoc.height);
            }
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
                if (isActive) {
                    stopCellEditing();
                }
            }
        }

        /**
         * Obtain new action element for given value.
         * @param pValue the value
         * @return the new action
         */
        public PopUpAction getNewAction(final Object pValue) {
            return new PopUpAction(pValue);
        }

        /**
         * Obtain new action element for given name and value.
         * @param pName the name
         * @param pValue the value
         * @return the new action
         */
        public PopUpAction getNewAction(final String pName,
                                        final Object pValue) {
            return new PopUpAction(pName, pValue);
        }

        /**
         * PopUp action class.
         */
        public final class PopUpAction
                extends AbstractAction {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -8465776918544737464L;

            /**
             * Action value.
             */
            private final Object theActionValue;

            /**
             * Constructor.
             * @param pValue the value
             */
            private PopUpAction(final Object pValue) {
                super(pValue.toString());
                theActionValue = pValue;
            }

            /**
             * Constructor.
             * @param pName the name
             * @param pValue the value
             */
            private PopUpAction(final String pName,
                                final Object pValue) {
                super(pName);
                theActionValue = pValue;
            }

            @Override
            public void actionPerformed(final ActionEvent e) {
                /* Record the value */
                theValue = theActionValue;

                /* Remove listener and stop editing */
                theMenu.removePopupMenuListener(thePopUpListener);
                stopCellEditing();
            }
        }

        /**
         * PopUp listener class.
         * <p>
         * Required to handle button clicked, dragged, and released in different place
         */
        private class PopUpListener
                implements PopupMenuListener {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                /* Ignore */
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
                cancelCellEditing();
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                /* Ignore */
            }
        }
    }

    /**
     * ComboBox Cell Editor.
     */
    public static class ComboBoxCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {
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

        /**
         * Constructor.
         */
        protected ComboBoxCellEditor() {
        }

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
            theCombo = myTable.getComboBox(pTable.convertRowIndexToModel(pRowIndex), pTable.convertColumnIndexToModel(pColIndex));
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
        private class ComboAction
                implements ActionListener {
            @Override
            public void actionPerformed(final ActionEvent e) {
                stopCellEditing();
            }
        }

        /**
         * Combo Popup class.
         */
        private class ComboPopup
                implements PopupMenuListener {
            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                /* Not needed */
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                /* Not needed */
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
                cancelCellEditing();
            }
        }

        @Override
        public Object getCellEditorValue() {
            return (theCombo != null)
                                     ? theCombo.getSelectedItem()
                                     : null;
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
    public static class CalendarCellEditor
            extends JDateDayCellEditor {
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
        protected CalendarCellEditor(final JDateDayFormatter pFormatter) {
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
            JDateDay myStart = (theRange == null)
                                                 ? null
                                                 : theRange.getStart();
            JDateDay myEnd = (theRange == null)
                                               ? null
                                               : theRange.getEnd();
            JDateDay myCurr;

            /* If the value is null */
            if ((pValue == null)
                || (JFieldValue.ERROR.equals(pValue))) {
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
    private abstract static class DecimalCellEditor
            extends StringCellEditor {
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
    public static class RateCellEditor
            extends DecimalCellEditor {
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
         * @param pParser the parser
         */
        protected RateCellEditor(final JDecimalParser pParser) {
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
    public static class MoneyCellEditor
            extends DecimalCellEditor {
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
         * @param pParser the parser
         */
        protected MoneyCellEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
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
    public static class UnitsCellEditor
            extends DecimalCellEditor {
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
         * @param pParser the parser
         */
        protected UnitsCellEditor(final JDecimalParser pParser) {
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
     * Dilutions Cell Editor.
     */
    public static class DilutionCellEditor
            extends DecimalCellEditor {
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
         * @param pParser the parser
         */
        protected DilutionCellEditor(final JDecimalParser pParser) {
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
    public static class PriceCellEditor
            extends DecimalCellEditor {
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
         * @param pParser the parser
         */
        protected PriceCellEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
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
    public static class DilutedPriceCellEditor
            extends DecimalCellEditor {
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
         * @param pParser the parser
         */
        protected DilutedPriceCellEditor(final JDecimalParser pParser) {
            theParser = pParser;
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
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
