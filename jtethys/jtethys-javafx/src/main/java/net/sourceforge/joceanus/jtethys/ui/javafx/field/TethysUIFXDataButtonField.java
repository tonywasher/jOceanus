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
package net.sourceforge.joceanus.jtethys.ui.javafx.field;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
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
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.button.TethysUIFXDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.button.TethysUIFXDateDialog;
import net.sourceforge.joceanus.jtethys.ui.javafx.button.TethysUIFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.button.TethysUIFXScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.dialog.TethysUIFXColorPicker;
import net.sourceforge.joceanus.jtethys.ui.javafx.menu.TethysUIFXScrollMenu;

/**
 * Generic class for displaying and editing a button data field.
 */
public final class TethysUIFXDataButtonField {
    /**
     * Private constructor.
     */
    private TethysUIFXDataButtonField() {
    }

    /**
     * IconButtonField class.
     *
     * @param <T> the data type
     */
    public static final class TethysUIFXIconButtonField<T>
            extends TethysUIFXDataTextField<T>
            implements TethysUIIconButtonField<T> {
        /**
         * The icon manager.
         */
        private final TethysUIIconButtonManager<T> theManager;

        /**
         * The button.
         */
        private final Button theButton;

        /**
         * The icon.
         */
        private Node theIcon;

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXIconButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, pFactory.buttonFactory().newIconButton());
        }

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        TethysUIFXIconButtonField(final TethysUICoreFactory<?> pFactory,
                                  final TethysUIIconButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pFactory, TethysUIFXNode.getNode(pManager));

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
            getLabel().setAlignment(Pos.CENTER);

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(e -> {
                setValue(theManager.getValue());
                fireEvent(TethysUIXEvent.NEWVALUE, e.getDetails());
            });
        }

        @Override
        protected Button getEditControl() {
            return (Button) super.getEditControl();
        }

        @Override
        public void setValue(final T pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            theManager.setValue(pValue);
            theIcon = theButton.getGraphic();

            /* If we are not currently editable */
            if (!isEditable()) {
                /* Switch the icon into the label */
                theButton.setGraphic(null);
                getLabel().setGraphic(theIcon);
            }
        }

        @Override
        public void setEditable(final boolean pEditable) {
            /* Obtain current setting */
            final boolean isEditable = isEditable();

            /* If we are changing */
            if (pEditable != isEditable) {
                /* If we are setting editable */
                if (pEditable) {
                    /* Switch the icon into the button */
                    getLabel().setGraphic(null);
                    theButton.setGraphic(theIcon);
                } else {
                    /* Switch the icon into the label */
                    theButton.setGraphic(null);
                    getLabel().setGraphic(theIcon);
                }

                /* Pass call on */
                super.setEditable(pEditable);
            }
        }

        @Override
        public void startCellEditing(final Node pCell) {
            Platform.runLater(theButton::fire);
        }

        @Override
        public void setIconMapSet(final Supplier<TethysUIIconMapSet<T>> pSupplier) {
            theManager.setIconMapSet(pSupplier);
        }
    }

    /**
     * ScrollButtonField class.
     *
     * @param <T> the data type
     */
    public static final class TethysUIFXScrollButtonField<T>
            extends TethysUIFXDataTextField<T>
            implements TethysUIScrollButtonField<T> {
        /**
         * The scroll manager.
         */
        private final TethysUIFXScrollButtonManager<T> theManager;

        /**
         * The configurator.
         */
        private Consumer<TethysUIScrollMenu<T>> theConfigurator;

        /**
         * The button.
         */
        private final Button theButton;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXScrollButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, pFactory.buttonFactory().newScrollButton());
        }

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        private TethysUIFXScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                            final TethysUIScrollButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pFactory, TethysUIFXNode.getNode(pManager));

            /* Store the manager and button */
            theManager = (TethysUIFXScrollButtonManager<T>) pManager;
            theButton = getEditControl();

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);

            /* Set configurator */
            theConfigurator = p -> {
            };
        }

        /**
         * handle Scroll Button event.
         *
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
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing(final Node pCell) {
            isCellEditing = true;
            final TethysUIFXScrollMenu<T> myMenu = theManager.getMenu();
            theConfigurator.accept(myMenu);
            if (!myMenu.isEmpty()) {
                myMenu.showMenuAtPosition(pCell, Side.BOTTOM);
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
    }

    /**
     * DateButtonField class.
     */
    public static class TethysUIFXDateButtonField
            extends TethysUIFXDataTextField<TethysDate>
            implements TethysUIDateButtonField {
        /**
         * The date manager.
         */
        private final TethysUIFXDateButtonManager theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXDateButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, pFactory.buttonFactory().newDateButton());
        }

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        private TethysUIFXDateButtonField(final TethysUICoreFactory<?> pFactory,
                                          final TethysUIDateButtonManager pManager) {
            /* Initialise underlying class */
            super(pFactory, TethysUIFXNode.getNode(pManager));

            /* Store the manager */
            theManager = (TethysUIFXDateButtonManager) pManager;

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
        }

        /**
         * handle Date Button event.
         *
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
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing(final Node pCell) {
            /* Note editing */
            isCellEditing = true;
            final TethysUIFXDateDialog myDialog = theManager.getDialog();

            /* Show the dialog */
            myDialog.showDialogUnderNode(pCell);
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
    }

    /**
     * ColourButtonField class.
     */
    public static class TethysUIFXColorButtonField
            extends TethysUIFXDataTextField<String> {
        /**
         * The colour picker.
         */
        private final TethysUIFXColorPicker thePicker;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXColorButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, (TethysUIFXColorPicker) pFactory.dialogFactory().newColorPicker());
        }

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         * @param pPicker  the picker
         */
        private TethysUIFXColorButtonField(final TethysUICoreFactory<?> pFactory,
                                           final TethysUIFXColorPicker pPicker) {
            /* Initialise underlying class */
            super(pFactory, TethysUIFXNode.getNode(pPicker));

            /* Store the picker */
            thePicker = pPicker;

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set listener on picker */
            pPicker.getEventRegistrar().addEventListener(this::handleEvent);

            /* Configure the label */
            getLabel().setContentDisplay(ContentDisplay.LEFT);
        }

        /**
         * handle Date Button event.
         *
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
        protected Button getEditControl() {
            return (Button) super.getEditControl();
        }

        @Override
        public void setValue(final String pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Declare value to the manager */
            thePicker.setValue(pValue);

            /* Configure the label */
            final Label myLabel = getLabel();
            myLabel.setText(pValue);
            myLabel.setGraphic(thePicker.getSwatch());
        }

        @Override
        public void startCellEditing(final Node pCell) {
            /* Note editing */
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
    }

    /**
     * ListButtonField class.
     *
     * @param <T> the data type
     */
    public static class TethysUIFXListButtonField<T extends Comparable<T>>
            extends TethysUIFXDataTextField<List<T>>
            implements TethysUIListButtonField<T> {
        /**
         * The list manager.
         */
        private final TethysUIFXListButtonManager<T> theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         */
        TethysUIFXListButtonField(final TethysUICoreFactory<?> pFactory) {
            this(pFactory, pFactory.buttonFactory().newListButton());
        }

        /**
         * Constructor.
         *
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        private TethysUIFXListButtonField(final TethysUICoreFactory<?> pFactory,
                                          final TethysUIListButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pFactory, TethysUIFXNode.getNode(pManager));

            /* Store the manager */
            theManager = (TethysUIFXListButtonManager<T>) pManager;

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(this::handleEvent);
        }

        /**
         * handle List Button event.
         *
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
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing(final Node pCell) {
            isCellEditing = true;
            theManager.buildMenu();
            final TethysUIFXScrollMenu<T> myMenu = theManager.getMenu();
            myMenu.showMenuAtPosition(pCell, Side.BOTTOM);
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
        public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
            theManager.setSelectables(pSelectables);
        }
    }
}
