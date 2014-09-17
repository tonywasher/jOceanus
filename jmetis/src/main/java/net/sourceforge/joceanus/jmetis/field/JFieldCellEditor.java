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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Currency;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.jdatebutton.JDateDialog.JDateEditor;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayCellEditor;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayConfig;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.event.JEventCellEditor;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Cell editors.
 * @author Tony Washer
 */
public final class JFieldCellEditor {
    /**
     * private constructor.
     */
    private JFieldCellEditor() {
    }

    /**
     * Empty string.
     */
    private static final String STR_EMPTY = "";

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
     * IconButton Cell Editor.
     * @param <T> the object type
     */
    public static class IconButtonCellEditor<T>
            extends JEventCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6709609365303162431L;

        /**
         * The class of the object.
         */
        private final Class<T> theClass;

        /**
         * The button.
         */
        private final JIconButton<T> theButton;

        /**
         * The state.
         */
        private final transient DefaultIconButtonState<T> theState;

        /**
         * The selection Listener.
         */
        private final transient ButtonListener theButtonListener = new ButtonListener();

        /**
         * The mouse Listener.
         */
        private final transient MouseListener theMouseListener = new MouseListener();

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
         * Set the CellEditor value.
         * @param pNewValue the new value
         */
        public void setNewValue(final T pNewValue) {
            theValue = pNewValue;
            theButton.setValue(theValue);
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        /**
         * Constructor.
         * @param pClass the class of the object
         */
        protected IconButtonCellEditor(final Class<T> pClass) {
            /* Use simple setup */
            this(pClass, false);
        }

        /**
         * Constructor.
         * @param pClass the class of the object
         * @param pComplex use complex state true/false
         */
        protected IconButtonCellEditor(final Class<T> pClass,
                                       final boolean pComplex) {
            /* Store parameters */
            theClass = pClass;

            /* Build the button */
            theState = pComplex
                               ? new ComplexIconButtonState<T, Boolean>(Boolean.FALSE)
                               : new DefaultIconButtonState<T>();
            theButton = new JIconButton<T>(theState);
            theButton.setFocusPainted(false);
            theButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, theButtonListener);
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
            theValue = theClass.cast(pValue);
            theButton.storeValue(theValue);

            /* ensure EditingState */
            ensureEditingState();

            /* Declare the mouse listener */
            theTable.addMouseListener(theMouseListener);

            /* Return the button */
            return theButton;
        }

        @Override
        public Object getCellEditorValue() {
            return theValue;
        }

        /**
         * Obtain standard state.
         * @return the state machine
         */
        public DefaultIconButtonState<T> getState() {
            return theState;
        }

        /**
         * Obtain complex state.
         * @return the state machine
         */
        @SuppressWarnings("unchecked")
        public ComplexIconButtonState<T, Boolean> getComplexState() {
            return (theState instanceof ComplexIconButtonState)
                                                               ? (ComplexIconButtonState<T, Boolean>) theState
                                                               : null;
        }

        /**
         * Ensure editing state.
         */
        private void ensureEditingState() {
            ComplexIconButtonState<T, Boolean> myState = getComplexState();
            if (myState != null) {
                myState.setState(Boolean.TRUE);
            }
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
         * Button Listener class.
         */
        private class ButtonListener
                implements PropertyChangeListener {
            @Override
            public void propertyChange(final PropertyChangeEvent pEvent) {
                /* Access the new value */
                theValue = theButton.getValue();

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
                Rectangle myRect = theButton.getBounds();
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
    public static class ScrollButtonCellEditor<T>
            extends JEventCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 8612779653045598187L;

        /**
         * The class of the object.
         */
        private final Class<T> theClass;

        /**
         * The button.
         */
        private final JScrollButton<T> theButton;

        /**
         * The menu Builder.
         */
        private final transient JScrollMenuBuilder<T> theMenuBuilder;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

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
         * The editor value.
         */
        private transient T theValue;

        /**
         * The active table.
         */
        private JTable theTable;

        /**
         * Obtain the menu Builder.
         * @return the menuBuilder
         */
        public JScrollMenuBuilder<T> getMenuBuilder() {
            return theMenuBuilder;
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        /**
         * Constructor.
         * @param pClass the class of the object
         */
        protected ScrollButtonCellEditor(final Class<T> pClass) {
            /* Store parameters */
            theClass = pClass;

            /* Create button and menu builder */
            theButton = new JScrollButton<T>();
            theMenuBuilder = theButton.getMenuBuilder();

            /* Add popupListener */
            theMenuBuilder.addPopupMenuListener(thePopUpListener);
            theButton.fireOnClose();

            /* sort out listeners */
            theButton.setFocusPainted(false);
            theButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, theButtonListener);
            theMenuBuilder.addChangeListener(theButtonListener);
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
            theValue = theClass.cast(pValue);
            theButton.storeValue(theValue);

            /* Declare the mouse listener */
            theTable.addMouseListener(theMouseListener);

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
         * Button Listener class.
         */
        private class ButtonListener
                implements PropertyChangeListener, ChangeListener {
            @Override
            public void propertyChange(final PropertyChangeEvent pEvent) {
                /* Store value and stop editing */
                theValue = theButton.getValue();
                stopCellEditing();
            }

            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                if (theMenuBuilder.buildingMenu()) {
                    fireStateChanged();
                } else {
                    cancelCellEditing();
                }
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
                Rectangle myRect = theButton.getBounds();
                if (!myRect.contains(e.getPoint())) {
                    cancelCellEditing();
                }
            }
        }

        /**
         * PopUp listener class.
         * <p>
         * Required to handle popUp menu cancelled without selection
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
     * @param <T> the object type
     */
    public static class ComboBoxCellEditor<T>
            extends JEventCellEditor
            implements TableCellEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6107290027015360230L;

        /**
         * The ComboBox.
         */
        private final JComboBox<T> theCombo;

        /**
         * The class of the object.
         */
        private final Class<T> theClass;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * The action Listener.
         */
        private final transient ComboAction theActionListener = new ComboAction();

        /**
         * The popUp listener.
         */
        private final transient ComboPopup thePopupListener = new ComboPopup();

        /**
         * Obtain the comboBox.
         * @return the comboBox
         */
        public JComboBox<T> getComboBox() {
            return theCombo;
        }

        /**
         * Obtain the location of the CellEditor.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        /**
         * Constructor.
         * @param pClass the class of the object
         */
        protected ComboBoxCellEditor(final Class<T> pClass) {
            /* Store parameters */
            theClass = pClass;

            /* Create button and menu builder */
            theCombo = new JComboBox<T>();
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

            /* Enable updates to comboBox */
            fireStateChanged();

            /* Store current value */
            T myValue = theClass.cast(pValue);
            if (myValue != null) {
                theCombo.setSelectedItem(myValue);
            } else {
                theCombo.setSelectedIndex(-1);
            }

            /* Set listeners */
            theCombo.addActionListener(theActionListener);
            theCombo.addPopupMenuListener(thePopupListener);

            /* Return the component */
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
         * Combo PopUp class.
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
            return theCombo.getSelectedItem();
        }

        @Override
        public void cancelCellEditing() {
            if (theCombo != null) {
                theCombo.removePopupMenuListener(thePopupListener);
                theCombo.removeActionListener(theActionListener);
                thePoint = null;
            }
            super.cancelCellEditing();
        }

        @Override
        public boolean stopCellEditing() {
            if (theCombo != null) {
                theCombo.removePopupMenuListener(thePopupListener);
                theCombo.removeActionListener(theActionListener);
                thePoint = null;
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
         * The Select-able range.
         */
        private transient JDateDayRange theRange = null;

        /**
         * Is null date allowed?
         */
        private transient boolean isNullDateAllowed = false;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        protected CalendarCellEditor(final JDateDayFormatter pFormatter) {
            /* Create a new configuration */
            super(pFormatter);
        }

        /**
         * Set the select-able range.
         * @param pRange the range
         */
        public void setRange(final JDateDayRange pRange) {
            theRange = pRange;
        }

        /**
         * Set whether null dates are allowed.
         * @param pNullDateAllowed true/false
         */
        public void setNullDateAllowed(final boolean pNullDateAllowed) {
            isNullDateAllowed = pNullDateAllowed;
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
            setNullDateAllowed(isNullDateAllowed);

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
     * DateDay Cell Editor.
     */
    public static class DateDayCellEditor
            extends JEventCellEditor
            implements TableCellEditor, JDateEditor {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2421257860258168379L;

        /**
         * The Button.
         */
        private transient JDateDayButton theButton;

        /**
         * The point at which the editor is active.
         */
        private transient Point thePoint;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        protected DateDayCellEditor(final JDateDayFormatter pFormatter) {
            /* Create a new button */
            theButton = new JDateDayButton(pFormatter);
            theButton.setEditor(this);
        }

        /**
         * Obtain the DateConfig.
         * @return the configuration
         */
        public JDateDayConfig getDateConfig() {
            return theButton.getDateConfig();
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

            /* Enable updates to comboBox */
            fireStateChanged();

            /* If the value is the date */
            if (pValue instanceof JDateDay) {
                JDateDay myDate = (JDateDay) pValue;
                /* Set the selected date */
                theButton.setSelectedDateDay(myDate);

                /* else set the selected date to null */
            } else {
                theButton.setSelectedDate(null);
            }

            /* Return the button as the component */
            return theButton;
        }

        @Override
        public void fireEditingCanceledEvent() {
            fireEditingCanceled();
        }

        @Override
        public void fireEditingStoppedEvent() {
            fireEditingStopped();
        }

        @Override
        public Object getCellEditorValue() {
            return theButton.getSelectedDateDay();
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
         * The assumed currency.
         */
        private transient Currency theCurrency;

        /**
         * Set the assumed currency.
         * @param pCurrency the assumed currency
         */
        public void setAssumedCurrency(final Currency pCurrency) {
            theCurrency = pCurrency;
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected MoneyCellEditor(final JDecimalParser pParser) {
            theParser = pParser;
            theCurrency = theParser.getDefaultCurrency();
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parseMoneyValue((String) o, theCurrency);
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
         * The assumed currency.
         */
        private transient Currency theCurrency;

        /**
         * Set the assumed currency.
         * @param pCurrency the assumed currency
         */
        public void setAssumedCurrency(final Currency pCurrency) {
            theCurrency = pCurrency;
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected PriceCellEditor(final JDecimalParser pParser) {
            theParser = pParser;
            theCurrency = theParser.getDefaultCurrency();
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parsePriceValue((String) o, theCurrency);
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
         * The assumed currency.
         */
        private transient Currency theCurrency;

        /**
         * Set the assumed currency.
         * @param pCurrency the assumed currency
         */
        public void setAssumedCurrency(final Currency pCurrency) {
            theCurrency = pCurrency;
        }

        /**
         * Constructor.
         * @param pParser the parser
         */
        protected DilutedPriceCellEditor(final JDecimalParser pParser) {
            theParser = pParser;
            theCurrency = theParser.getDefaultCurrency();
        }

        @Override
        public Object getCellEditorValue() {
            Object o = super.getCellEditorValue();
            if ((o instanceof String)
                && (!STR_EMPTY.equals(o))) {
                try {
                    return theParser.parseDilutedPriceValue((String) o, theCurrency);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
            return null;
        }
    }
}
