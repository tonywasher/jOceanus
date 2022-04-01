/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

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
     */
    public static final class TethysSwingIconButtonField<T>
            extends TethysSwingDataTextField<T>
            implements TethysIconButtonField<T> {
        /**
         * The icon manager.
         */
        private final TethysIconButtonManager<T> theManager;

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
            this(pFactory, pFactory.newIconButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysSwingIconButtonField(final TethysSwingGuiFactory pFactory,
                                           final TethysIconButtonManager<T> pManager,
                                           final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, TethysSwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();
            pLabel.setHorizontalAlignment(SwingConstants.CENTER);

            /* Set listener on manager */
            final TethysEventRegistrar<TethysUIEvent> myRegistrar = pManager.getEventRegistrar();
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
            SwingUtilities.invokeLater(theButton::doClick);
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
        public void setIconMapSet(final Supplier<TethysIconMapSet<T>> pSupplier) {
            theManager.setIconMapSet(pSupplier);
        }

        @Override
        protected TethysSwingIconButtonField<T> cloneField(final JLabel pLabel) {
            final TethysSwingIconButtonManager<T> myClone = new TethysSwingIconButtonManager<>(getGuiFactory());
            return new TethysSwingIconButtonField<>(getGuiFactory(), myClone, pLabel);
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static final class TethysSwingScrollButtonField<T>
            extends TethysSwingDataTextField<T>
            implements TethysScrollButtonField<T> {
        /**
         * The scroll manager.
         */
        private final TethysSwingScrollButtonManager<T> theManager;

        /**
         * The configurator.
         */
        private Consumer<TethysScrollMenu<T>> theConfigurator;

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
        TethysSwingScrollButtonField(final TethysSwingGuiFactory pFactory) {
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
            super(pFactory, TethysSwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
            theManager.getMenu().getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> haltCellEditing());

            /* Set configurator */
            theConfigurator = p -> {
            };
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
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void setMenuConfigurator(final Consumer<TethysScrollMenu<T>> pConfigurator) {
            theConfigurator = pConfigurator;
            theManager.setMenuConfigurator(theConfigurator);
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
            final TethysSwingScrollContextMenu<T> myMenu = theManager.getMenu();
            theConfigurator.accept(myMenu);
            if (!myMenu.isEmpty()) {
                myMenu.showMenuAtPosition(pCell, SwingConstants.BOTTOM);
            } else {
                haltCellEditing();
            }
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
            implements TethysDateButtonField {
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
        TethysSwingDateButtonField(final TethysSwingGuiFactory pFactory) {
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
            super(pFactory, TethysSwingNode.getComponent(pManager), pLabel);

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
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void setDateConfigurator(final Consumer<TethysDateConfig> pConfigurator) {
            theManager.setDateConfigurator(pConfigurator);
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
            final TethysSwingDateDialog myDialog = theManager.getDialog();

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
        TethysSwingColorButtonField(final TethysSwingGuiFactory pFactory) {
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
            super(pFactory, TethysSwingNode.getComponent(pPicker), pLabel);

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
            final JLabel myLabel = getLabel();
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
    public static class TethysSwingListButtonField<T extends Comparable<T>>
            extends TethysSwingDataTextField<List<T>>
            implements TethysListButtonField<T> {
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
        TethysSwingListButtonField(final TethysSwingGuiFactory pFactory) {
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
            super(pFactory, TethysSwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = pManager;

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
            theManager.getMenu().getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> haltCellEditing());
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
        public JButton getEditControl() {
            return (JButton) super.getEditControl();
        }

        @Override
        public void setValue(final List<T> pValue) {
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
            final TethysSwingScrollContextMenu<T> myMenu = theManager.getMenu();
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

        @Override
        public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
            theManager.setSelectables(pSelectables);
        }
    }
}
