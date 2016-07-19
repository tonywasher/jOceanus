/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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

import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingStateIconButtonManager;

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
            implements TethysStateIconField<T, S, JComponent, Icon> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingStateIconButtonField(final TethysSwingGuiFactory pFactory) {
            super(pFactory, pFactory.newStateIconButton(), new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pBase the icon manager
         * @param pLabel the label
         */
        private TethysSwingStateIconButtonField(final TethysSwingGuiFactory pFactory,
                                                final TethysSwingStateIconButtonManager<T, S> pBase,
                                                final JLabel pLabel) {
            super(pFactory, pBase, pLabel);
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysSwingStateIconButtonManager<T, S> getIconManager() {
            return (TethysSwingStateIconButtonManager<T, S>) super.getIconManager();
        }

        @Override
        protected TethysSwingStateIconButtonField<T, S> cloneField(final JLabel pLabel) {
            TethysSwingStateIconButtonManager<T, S> myClone = new TethysSwingStateIconButtonManager<>(getGuiFactory(), getIconManager());
            return new TethysSwingStateIconButtonField<>(getGuiFactory(), myClone, pLabel);
        }

        /**
         * Set the machine state.
         * @param pState the state
         */
        protected void setMachineState(final S pState) {
            getIconManager().setMachineState(pState);
        }
    }

    /**
     * IconButtonField class.
     * @param <T> the data type
     */
    public static class TethysSwingIconButtonField<T>
            extends TethysSwingDataTextField<T>
            implements TethysIconField<T, JComponent, Icon> {
        /**
         * The icon manager.
         */
        private final TethysSimpleIconButtonManager<T, JComponent, Icon> theManager;

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
         * @param pFactory the GUI factory
         */
        protected TethysSwingIconButtonField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingIconButtonField(final TethysSwingGuiFactory pFactory,
                                           final JLabel pLabel) {
            this(pFactory, pFactory.newSimpleIconButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysSwingIconButtonField(final TethysSwingGuiFactory pFactory,
                                           final TethysSimpleIconButtonManager<T, JComponent, Icon> pManager,
                                           final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode(), pLabel);

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set listener on manager */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = pManager.getEventRegistrar();
            myRegistrar.addEventListener(this::handleEvent);
        }

        /**
         * handle Icon Button event.
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
        public TethysSimpleIconButtonManager<T, JComponent, Icon> getIconManager() {
            return theManager;
        }

        @Override
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
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
            isCellEditing = true;
            SwingUtilities.invokeLater(() -> theButton.doClick());
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

        @Override
        protected TethysSwingIconButtonField<T> cloneField(final JLabel pLabel) {
            TethysSwingSimpleIconButtonManager<T> myClone = new TethysSwingSimpleIconButtonManager<>(getGuiFactory(), getIconManager());
            return new TethysSwingIconButtonField<>(getGuiFactory(), myClone, pLabel);
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static class TethysSwingScrollButtonField<T>
            extends TethysSwingDataTextField<T>
            implements TethysScrollField<T, JComponent, Icon> {
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
         * @param pFactory the GUI factory
         */
        protected TethysSwingScrollButtonField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingScrollButtonField(final TethysSwingGuiFactory pFactory,
                                             final JLabel pLabel) {
            this(pFactory, pFactory.newScrollButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysSwingScrollButtonField(final TethysSwingGuiFactory pFactory,
                                             final TethysSwingScrollButtonManager<T> pManager,
                                             final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode(), pLabel);

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

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

        @Override
        protected TethysSwingScrollButtonField<T> cloneField(final JLabel pLabel) {
            return new TethysSwingScrollButtonField<>(getGuiFactory(), pLabel);
        }
    }

    /**
     * DateButtonField class.
     */
    public static class TethysSwingDateButtonField
            extends TethysSwingDataTextField<TethysDate>
            implements TethysDateField<JComponent, Icon> {
        /**
         * The date manager.
         */
        private final TethysSwingDateButtonManager theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingDateButtonField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingDateButtonField(final TethysSwingGuiFactory pFactory,
                                           final JLabel pLabel) {
            this(pFactory, pFactory.newDateButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysSwingDateButtonField(final TethysSwingGuiFactory pFactory,
                                           final TethysSwingDateButtonManager pManager,
                                           final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode(), pLabel);

            /* Store the manager and button */
            theManager = pManager;

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
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
        }

        @Override
        public void setValue(final TethysDate pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            theManager.setSelectedDate(pValue);
            getLabel().setText(theManager.getText());
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            isCellEditing = true;
            TethysSwingDateDialog myDialog = theManager.getDialog();

            /* Show the dialog */
            myDialog.showDialogUnderRectangle(pCell);
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

        @Override
        protected TethysSwingDateButtonField cloneField(final JLabel pLabel) {
            return new TethysSwingDateButtonField(getGuiFactory(), pLabel);
        }
    }

    /**
     * ColorButtonField class.
     */
    public static class TethysSwingColorButtonField
            extends TethysSwingDataTextField<String> {
        /**
         * The colour picker.
         */
        private final TethysSwingColorPicker thePicker;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingColorButtonField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingColorButtonField(final TethysSwingGuiFactory pFactory,
                                            final JLabel pLabel) {
            this(pFactory, pFactory.newColorPicker(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pPicker the picker
         * @param pLabel the label
         */
        private TethysSwingColorButtonField(final TethysSwingGuiFactory pFactory,
                                            final TethysSwingColorPicker pPicker,
                                            final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, pPicker.getNode(), pLabel);

            /* Store the picker */
            thePicker = pPicker;

            /* Set listener on picker */
            pPicker.getEventRegistrar().addEventListener(this::handleEvent);

            /* Configure the label */
            getLabel().setHorizontalTextPosition(SwingConstants.RIGHT);
        }

        /**
         * handle Date Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(thePicker.getValue());
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
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
        }

        @Override
        public void setValue(final String pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            thePicker.setValue(pValue);

            /* Configure the label */
            JLabel myLabel = getLabel();
            myLabel.setText(pValue);
            myLabel.setIcon(thePicker.getSwatch());
        }

        @Override
        public void startCellEditing(final Rectangle pCell) {
            isCellEditing = true;
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

        @Override
        protected TethysSwingColorButtonField cloneField(final JLabel pLabel) {
            return new TethysSwingColorButtonField(getGuiFactory(), pLabel);
        }
    }

    /**
     * ListButtonField class.
     * @param <T> the data type
     */
    public static class TethysSwingListButtonField<T>
            extends TethysSwingDataTextField<TethysItemList<T>>
            implements TethysListField<T, JComponent, Icon> {
        /**
         * The icon manager.
         */
        private final TethysSwingListButtonManager<T> theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysSwingListButtonField(final TethysSwingGuiFactory pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysSwingListButtonField(final TethysSwingGuiFactory pFactory,
                                           final JLabel pLabel) {
            this(pFactory, pFactory.newListButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysSwingListButtonField(final TethysSwingGuiFactory pFactory,
                                           final TethysSwingListButtonManager<T> pManager,
                                           final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode(), pLabel);

            /* Store the manager and button */
            theManager = pManager;

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

        @Override
        protected TethysSwingListButtonField<T> cloneField(final JLabel pLabel) {
            return new TethysSwingListButtonField<>(getGuiFactory(), pLabel);
        }
    }
}
