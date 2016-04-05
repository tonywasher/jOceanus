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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButtonManager.TethysFXStateIconButtonManager;

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
     * @param <S> the state class
     */
    public static class TethysFXStateIconButtonField<T, S>
            extends TethysFXIconButtonField<T>
            implements TethysStateIconField<T, S, Node, Node> {
        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysFXStateIconButtonField(final TethysFXGuiFactory pFactory) {
            super(pFactory, pFactory.newStateIconButton());
        }

        @SuppressWarnings("unchecked")
        @Override
        public TethysFXStateIconButtonManager<T, S> getIconManager() {
            return (TethysFXStateIconButtonManager<T, S>) super.getIconManager();
        }
    }

    /**
     * IconButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXIconButtonField<T>
            extends TethysFXDataTextField<T>
            implements TethysIconField<T, Node, Node> {
        /**
         * The icon manager.
         */
        private final TethysSimpleIconButtonManager<T, Node, Node> theManager;

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
            this(pFactory, pFactory.newSimpleIconButton());
        }

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the manager
         */
        protected TethysFXIconButtonField(final TethysFXGuiFactory pFactory,
                                          final TethysSimpleIconButtonManager<T, Node, Node> pManager) {
            /* Initialise underlying class */
            super(pFactory, pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

            /* Set padding */
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING - 1, PADDING));

            /* Set listener on manager */
            pManager.getEventRegistrar().addEventListener(e -> {
                setValue(theManager.getValue());
                fireEvent(TethysUIEvent.NEWVALUE, e.getDetails());
            });
        }

        @Override
        public TethysSimpleIconButtonManager<T, Node, Node> getIconManager() {
            return theManager;
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
            Platform.runLater(() -> theButton.fire());
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXScrollButtonField<T>
            extends TethysFXDataTextField<T>
            implements TethysScrollField<T, Node, Node> {
        /**
         * The scroll manager.
         */
        private final TethysFXScrollButtonManager<T> theManager;

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
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING - 1, PADDING));

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
        public TethysFXScrollButtonManager<T> getScrollManager() {
            return theManager;
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

    /**
     * DateButtonField class.
     */
    public static class TethysFXDateButtonField
            extends TethysFXDataTextField<TethysDate>
            implements TethysDateField<Node, Node> {
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
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING - 1, PADDING));

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
        public TethysFXDateButtonManager getDateManager() {
            return theManager;
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
     * ListButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXListButtonField<T>
            extends TethysFXDataTextField<TethysItemList<T>>
            implements TethysListField<T, Node, Node> {
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
            getLabel().setPadding(new Insets(PADDING, PADDING, PADDING - 1, PADDING));

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
        public TethysFXListButtonManager<T> getListManager() {
            return theManager;
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
