/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Generic class for displaying and editing a button data field.
 */
public final class TethysFXDataButtonField {
    /**
     * Private constructor.
     */
    private TethysFXDataButtonField() {
    }

    /**
     * IconButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXIconButtonField<T>
            extends TethysFXDataTextField<T>
            implements TethysIconButtonField<T, Node, Node> {
        /**
         * The icon manager.
         */
        private final TethysIconButtonManager<T, Node, Node> theManager;

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
         * @param pFactory the GUI factory
         */
        protected TethysFXIconButtonField(final TethysFXGuiFactory pFactory) {
            this(pFactory, pFactory.newIconButton());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        protected TethysFXIconButtonField(final TethysFXGuiFactory pFactory,
                                          final TethysIconButtonManager<T, Node, Node> pManager) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(e -> {
                setValue(theManager.getValue());
                fireEvent(TethysUIEvent.NEWVALUE, e.getDetails());
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
            boolean isEditable = isEditable();

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
        public void setIconMapSet(final Function<T, TethysIconMapSet<T>> pSupplier) {
            theManager.setIconMapSet(pSupplier);
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXScrollButtonField<T>
            extends TethysFXDataTextField<T>
            implements TethysScrollButtonField<T, Node, Node> {
        /**
         * The scroll manager.
         */
        private final TethysFXScrollButtonManager<T> theManager;

        /**
         * The configurator.
         */
        private Consumer<TethysScrollMenu<T, Node>> theConfigurator;

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
         * @param pFactory the GUI factory
         */
        protected TethysFXScrollButtonField(final TethysFXGuiFactory pFactory) {
            this(pFactory, pFactory.newScrollButton());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        private TethysFXScrollButtonField(final TethysFXGuiFactory pFactory,
                                          final TethysFXScrollButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
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
        public void setMenuConfigurator(final Consumer<TethysScrollMenu<T, Node>> pConfigurator) {
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
            TethysFXScrollContextMenu<T> myMenu = theManager.getMenu();
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
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }

    /**
     * DateButtonField class.
     */
    public static class TethysFXDateButtonField
            extends TethysFXDataTextField<TethysDate>
            implements TethysDateButtonField<Node, Node> {
        /**
         * The date manager.
         */
        private final TethysFXDateButtonManager theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXDateButtonField(final TethysFXGuiFactory pFactory) {
            this(pFactory, pFactory.newDateButton());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        private TethysFXDateButtonField(final TethysFXGuiFactory pFactory,
                                        final TethysFXDateButtonManager pManager) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode());

            /* Store the manager */
            theManager = pManager;

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

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
            TethysFXDateDialog myDialog = theManager.getDialog();

            /* Show the dialog */
            myDialog.showDialogUnderNode(pCell);
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
     * ColourButtonField class.
     */
    public static class TethysFXColorButtonField
            extends TethysFXDataTextField<String> {
        /**
         * The colour picker.
         */
        private final TethysFXColorPicker thePicker;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXColorButtonField(final TethysFXGuiFactory pFactory) {
            this(pFactory, pFactory.newColorPicker());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pPicker the picker
         */
        private TethysFXColorButtonField(final TethysFXGuiFactory pFactory,
                                         final TethysFXColorPicker pPicker) {
            /* Initialise underlying class */
            super(pFactory, pPicker.getNode());

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
            Label myLabel = getLabel();
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
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }

    /**
     * ListButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXListButtonField<T>
            extends TethysFXDataTextField<TethysItemList<T>> {
        /**
         * The list manager.
         */
        private final TethysFXListButtonManager<T> theManager;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXListButtonField(final TethysFXGuiFactory pFactory) {
            this(pFactory, pFactory.newListButton());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        private TethysFXListButtonField(final TethysFXGuiFactory pFactory,
                                        final TethysFXListButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode());

            /* Store the manager */
            theManager = pManager;

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

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
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing(final Node pCell) {
            isCellEditing = true;
            theManager.buildMenu();
            TethysFXScrollContextMenu<T> myMenu = theManager.getMenu();
            myMenu.showMenuAtPosition(pCell, Side.BOTTOM);
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
