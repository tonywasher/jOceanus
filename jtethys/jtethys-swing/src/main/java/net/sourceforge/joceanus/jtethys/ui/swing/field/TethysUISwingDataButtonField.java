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
package net.sourceforge.joceanus.jtethys.ui.swing.field;

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
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.jtethys.ui.swing.button.TethysUISwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.button.TethysUISwingDateDialog;
import net.sourceforge.joceanus.jtethys.ui.swing.button.TethysUISwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.button.TethysUISwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.dialog.TethysUISwingColorPicker;
import net.sourceforge.joceanus.jtethys.ui.swing.menu.TethysUISwingScrollMenu;

/**
 * Generic class for displaying and editing a button data field.
 */
public final class TethysUISwingDataButtonField {
    /**
     * Private constructor.
     */
    private TethysUISwingDataButtonField() {
    }

    /**
     * IconButtonField class.
     * @param <T> the data type
     */
    public static final class TethysUISwingIconButtonField<T>
            extends TethysUISwingDataTextField<T>
            implements TethysUIIconButtonField<T> {
        /**
         * The icon manager.
         */
        private final TethysUIIconButtonManager<T> theManager;

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
        TethysUISwingIconButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingIconButtonField(final TethysUICoreFactory<?> pFactory,
                                             final JLabel pLabel) {
            this(pFactory, pFactory.buttonFactory().newIconButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysUISwingIconButtonField(final TethysUICoreFactory<?> pFactory,
                                             final TethysUIIconButtonManager<T> pManager,
                                             final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();
            pLabel.setHorizontalAlignment(SwingConstants.CENTER);

            /* Set listener on manager */
            final TethysEventRegistrar<TethysUIXEvent> myRegistrar = pManager.getEventRegistrar();
            myRegistrar.addEventListener(this::handleEvent);
        }

        /**
         * handle Icon Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIXEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getValue());
                    fireEvent(TethysUIXEvent.NEWVALUE, pEvent.getDetails());
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
                fireEvent(TethysUIXEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }

        @Override
        public void setIconMapSet(final Supplier<TethysUIIconMapSet<T>> pSupplier) {
            theManager.setIconMapSet(pSupplier);
        }

        @Override
        protected TethysUISwingIconButtonField<T> cloneField(final JLabel pLabel) {
            final TethysUIIconButtonManager<T> myClone = getGuiFactory().buttonFactory().newIconButton();
            return new TethysUISwingIconButtonField<>(getGuiFactory(), myClone, pLabel);
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static final class TethysUISwingScrollButtonField<T>
            extends TethysUISwingDataTextField<T>
            implements TethysUIScrollButtonField<T> {
        /**
         * The scroll manager.
         */
        private final TethysUISwingScrollButtonManager<T> theManager;

        /**
         * The configurator.
         */
        private Consumer<TethysUIScrollMenu<T>> theConfigurator;

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
        TethysUISwingScrollButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                               final JLabel pLabel) {
            this(pFactory, pFactory.buttonFactory().newScrollButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysUISwingScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                               final TethysUIScrollButtonManager<T> pManager,
                                               final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = (TethysUISwingScrollButtonManager<T>) pManager;
            theButton = getEditControl();

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
            theManager.getMenu().getEventRegistrar().addEventListener(TethysUIXEvent.WINDOWCLOSED, e -> haltCellEditing());

            /* Set configurator */
            theConfigurator = p -> {
            };
        }

        /**
         * handle Scroll Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIXEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getValue());
                    fireEvent(TethysUIXEvent.NEWVALUE, pEvent.getDetails());
                    break;
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void setMenuConfigurator(final Consumer<TethysUIScrollMenu<T>> pConfigurator) {
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
            final TethysUISwingScrollMenu<T> myMenu = theManager.getMenu();
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
                fireEvent(TethysUIXEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }

        @Override
        protected TethysUISwingScrollButtonField<T> cloneField(final JLabel pLabel) {
            return new TethysUISwingScrollButtonField<>(getGuiFactory(), pLabel);
        }
    }

    /**
     * DateButtonField class.
     */
    public static class TethysUISwingDateButtonField
            extends TethysUISwingDataTextField<TethysDate>
            implements TethysUIDateButtonField {
        /**
         * The date manager.
         */
        private final TethysUISwingDateButtonManager theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingDateButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingDateButtonField(final TethysUICoreFactory<?> pFactory,
                                             final JLabel pLabel) {
            this(pFactory, pFactory.buttonFactory().newDateButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysUISwingDateButtonField(final TethysUICoreFactory<?> pFactory,
                                             final TethysUIDateButtonManager pManager,
                                             final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = (TethysUISwingDateButtonManager) pManager;

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
        }

        /**
         * handle Date Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIXEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getSelectedDate());
                    fireEvent(TethysUIXEvent.NEWVALUE, pEvent.getDetails());
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
            final TethysUISwingDateDialog myDialog = theManager.getDialog();

            /* Show the dialog */
            myDialog.showDialogUnderRectangle(pCell);
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                fireEvent(TethysUIXEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }

        @Override
        protected TethysUISwingDateButtonField cloneField(final JLabel pLabel) {
            return new TethysUISwingDateButtonField(getGuiFactory(), pLabel);
        }
    }

    /**
     * ColorButtonField class.
     */
    public static class TethysUISwingColorButtonField
            extends TethysUISwingDataTextField<String> {
        /**
         * The colour picker.
         */
        private final TethysUISwingColorPicker thePicker;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingColorButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingColorButtonField(final TethysUICoreFactory<?> pFactory,
                                              final JLabel pLabel) {
            this(pFactory, (TethysUISwingColorPicker) pFactory.dialogFactory().newColorPicker(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pPicker the picker
         * @param pLabel the label
         */
        private TethysUISwingColorButtonField(final TethysUICoreFactory<?> pFactory,
                                              final TethysUISwingColorPicker pPicker,
                                              final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, TethysUISwingNode.getComponent(pPicker), pLabel);

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
        private void handleEvent(final TethysEvent<TethysUIXEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(thePicker.getValue());
                    fireEvent(TethysUIXEvent.NEWVALUE, pEvent.getDetails());
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
                fireEvent(TethysUIXEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }

        @Override
        protected TethysUISwingColorButtonField cloneField(final JLabel pLabel) {
            return new TethysUISwingColorButtonField(getGuiFactory(), pLabel);
        }
    }

    /**
     * ListButtonField class.
     * @param <T> the data type
     */
    public static class TethysUISwingListButtonField<T extends Comparable<T>>
            extends TethysUISwingDataTextField<List<T>>
            implements TethysUIListButtonField<T> {
        /**
         * The icon manager.
         */
        private final TethysUISwingListButtonManager<T> theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        TethysUISwingListButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, new JLabel());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pLabel the label
         */
        private TethysUISwingListButtonField(final TethysUICoreFactory<?> pFactory,
                                           final JLabel pLabel) {
            this(pFactory, pFactory.buttonFactory().newListButton(), pLabel);
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         * @param pLabel the label
         */
        private TethysUISwingListButtonField(final TethysUICoreFactory<?> pFactory,
                                             final TethysUIListButtonManager<T> pManager,
                                             final JLabel pLabel) {
            /* Initialise underlying class */
            super(pFactory, TethysUISwingNode.getComponent(pManager), pLabel);

            /* Store the manager and button */
            theManager = (TethysUISwingListButtonManager<T>) pManager;

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
            theManager.getMenu().getEventRegistrar().addEventListener(TethysUIXEvent.WINDOWCLOSED, e -> haltCellEditing());
        }

        /**
         * handle List Button event.
         * @param pEvent the even
         */
        private void handleEvent(final TethysEvent<TethysUIXEvent> pEvent) {
            switch (pEvent.getEventId()) {
                case NEWVALUE:
                    setValue(theManager.getValue());
                    fireEvent(TethysUIXEvent.NEWVALUE, pEvent.getDetails());
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
            final TethysUISwingScrollMenu<T> myMenu = theManager.getMenu();
            myMenu.showMenuAtPosition(pCell, SwingConstants.BOTTOM);
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                fireEvent(TethysUIXEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }

        @Override
        protected TethysUISwingListButtonField<T> cloneField(final JLabel pLabel) {
            return new TethysUISwingListButtonField<>(getGuiFactory(), pLabel);
        }

        @Override
        public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
            theManager.setSelectables(pSelectables);
        }
    }
}
