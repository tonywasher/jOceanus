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

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.jdatebutton.swing.JDateButton;
import net.sourceforge.jdatebutton.swing.JDateDialog;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButton.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Generic class for displaying and editing a button data field.
 */
public final class TethysSwingDataButtonField {
    /**
     * Private constructor.
     */
    private TethysSwingDataButtonField() {
    }

    /**
     * IconButtonField class.
     * @param <T> the data type
     * @param <S> the state class
     */
    public static class TethysSwingStateIconButtonField<T, S>
            extends TethysSwingIconButtonField<T>
            implements TethysStateIconField<T, S> {
        /**
         * Constructor.
         */
        public TethysSwingStateIconButtonField() {
            super(new TethysSwingStateIconButtonManager<>());
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysSwingStateIconButtonManager<T, S> getIconManager() {
            return (TethysSwingStateIconButtonManager<T, S>) super.getIconManager();
        }
    }

    /**
     * IconButtonField class.
     * @param <T> the data type
     */
    public static class TethysSwingIconButtonField<T>
            extends TethysSwingDataTextField<T>
            implements TethysIconField<T> {
        /**
         * The icon manager.
         */
        private final TethysSimpleIconButtonManager<T, JComponent, Icon> theManager;

        /**
         * The button.
         */
        private final JButton theButton;

        /**
         * Constructor.
         */
        public TethysSwingIconButtonField() {
            this(new TethysSwingSimpleIconButtonManager<>());
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        protected TethysSwingIconButtonField(final TethysSimpleIconButtonManager<T, JComponent, Icon> pManager) {
            /* Initialise underlying class */
            super(pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(e -> {
                setValue(theManager.getValue());
                fireEvent(TethysUIEvent.NEWVALUE, e.getDetails());
            });
        }

        @Override
        public TethysSimpleIconButtonManager<T, JComponent, Icon> getIconManager() {
            return theManager;
        }

        @Override
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
        }

        @Override
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            getLabel().setFont(pFont);
            theButton.setFont(pFont);
        }

        @Override
        public void setValue(final T pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            theManager.setValue(pValue);
            getLabel().setIcon(theButton.getIcon());
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            SwingUtilities.invokeLater(() -> theButton.doClick());
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static class TethysSwingScrollButtonField<T>
            extends TethysSwingDataTextField<T>
            implements TethysScrollField<T> {
        /**
         * The scroll manager.
         */
        private final TethysSwingScrollButtonManager<T> theManager;

        /**
         * The button.
         */
        private final JButton theButton;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         */
        public TethysSwingScrollButtonField() {
            this(new TethysSwingScrollButtonManager<>());
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        private TethysSwingScrollButtonField(final TethysSwingScrollButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = pManager.getNode();

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
        }

        /**
         * handle Scroll Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getValue());
                    fireEvent(TethysUIEvent.NEWVALUE, pEvent.getDetails());
                    break;
                case PREPAREDIALOG:
                    fireEvent(TethysUIEvent.PREPAREDIALOG, this);
                    break;
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        @Override
        public TethysSwingScrollButtonManager<T> getScrollManager() {
            return theManager;
        }

        @Override
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
        }

        @Override
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            getLabel().setFont(pFont);
            theButton.setFont(pFont);
        }

        @Override
        public void setValue(final T pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            theManager.setValue(pValue);
            getLabel().setText(theButton.getText());
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            isCellEditing = true;
            TethysSwingScrollContextMenu<T> myMenu = theManager.getMenu();
            myMenu.showMenuAtPosition(pCell, SwingConstants.BOTTOM);
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }

    /**
     * DateButtonField class.
     */
    public static class TethysSwingDateButtonField
            extends TethysSwingDataTextField<TethysDate>
            implements TethysDateField {
        /**
         * The date manager.
         */
        private final TethysSwingDateButtonManager theManager;

        /**
         * The button.
         */
        private final JDateButton theButton;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         */
        public TethysSwingDateButtonField() {
            this(new TethysDataFormatter());
        }

        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysSwingDateButtonField(final TethysDataFormatter pFormatter) {
            this(new TethysSwingDateButtonManager(pFormatter));
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        private TethysSwingDateButtonField(final TethysSwingDateButtonManager pManager) {
            /* Initialise underlying class */
            super((JDateButton) pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
        }

        /**
         * handle Date Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getSelectedDate());
                    fireEvent(TethysUIEvent.NEWVALUE, pEvent.getDetails());
                    break;
                case PREPAREDIALOG:
                    fireEvent(TethysUIEvent.PREPAREDIALOG, this);
                    break;
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        @Override
        public TethysSwingDateButtonManager getDateManager() {
            return theManager;
        }

        @Override
        public JDateButton getEditControl() {
            return (JDateButton) super.getEditControl();
        }

        @Override
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            getLabel().setFont(pFont);
            theButton.setFont(pFont);
        }

        @Override
        public void setValue(final TethysDate pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            theManager.setSelectedDate(pValue);
            getLabel().setText(theManager.getNode().getText());
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            isCellEditing = true;
            JDateDialog myDialog = theManager.getDialog();

            /* Position the dialog just below the cell */
            myDialog.setLocation(pCell.x, pCell.y + pCell.height);

            /* Show the dialog */
            myDialog.setVisible(true);
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }

    /**
     * ListButtonField class.
     * @param <T> the data type
     */
    public static class TethysSwingListButtonField<T>
            extends TethysSwingDataTextField<TethysItemList<T>>
            implements TethysListField<T> {
        /**
         * The icon manager.
         */
        private final TethysSwingListButtonManager<T> theManager;

        /**
         * The button.
         */
        private final JButton theButton;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         */
        public TethysSwingListButtonField() {
            this(new TethysSwingListButtonManager<>());
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        private TethysSwingListButtonField(final TethysSwingListButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
        }

        /**
         * handle List Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getValue());
                    fireEvent(TethysUIEvent.NEWVALUE, pEvent.getDetails());
                    break;
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        @Override
        public TethysSwingListButtonManager<T> getListManager() {
            return theManager;
        }

        @Override
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
        }

        @Override
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            getLabel().setFont(pFont);
            theButton.setFont(pFont);
        }

        @Override
        public void setValue(final TethysItemList<T> pValue) {
            super.setValue(pValue);
            theManager.setValue(pValue);
            updateText();
        }

        /**
         * Update the button text.
         */
        private void updateText() {
            getLabel().setText(theManager.getText());
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            isCellEditing = true;
            theManager.buildMenu();
            TethysSwingScrollContextMenu<T> myMenu = theManager.getMenu();
            myMenu.showMenuAtPosition(pCell, SwingConstants.BOTTOM);
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }
}
