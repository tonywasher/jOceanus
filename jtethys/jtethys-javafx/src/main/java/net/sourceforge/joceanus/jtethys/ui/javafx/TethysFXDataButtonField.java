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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.text.Font;
import net.sourceforge.jdatebutton.javafx.JDateButton;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButton.TethysFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButton.TethysFXScrollButtonManager;

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
            extends TethysFXIconButtonField<T> {
        /**
         * Constructor.
         */
        public TethysFXStateIconButtonField() {
            super(new TethysFXStateIconButtonManager<>());
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
            extends TethysFXDataTextField<T> {
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
         */
        public TethysFXIconButtonField() {
            this(new TethysFXSimpleIconButtonManager<>());
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        public TethysFXIconButtonField(final TethysIconButtonManager<T, Node, Node> pManager) {
            /* Initialise underlying class */
            super((Control) pManager.getNode());

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

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysIconButtonManager<T, Node, Node> getIconManager() {
            return theManager;
        }

        @Override
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing() {
            Platform.runLater(() -> theButton.fire());
        }
    }

    /**
     * ScrollButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXScrollButtonField<T>
            extends TethysFXDataTextField<T> {
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
         */
        public TethysFXScrollButtonField() {
            this(new TethysFXScrollButtonManager<>());
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        public TethysFXScrollButtonField(final TethysFXScrollButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pManager.getNode());

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

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXScrollButtonManager<T> getScrollManager() {
            return theManager;
        }

        @Override
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing() {
            isCellEditing = true;
            setEditable(true);
            Platform.runLater(() -> theButton.fire());
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                setEditable(false);
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }

    /**
     * DateButtonField class.
     */
    public static class TethysFXDateButtonField
            extends TethysFXDataTextField<TethysDate> {
        /**
         * The date manager.
         */
        private final TethysFXDateButtonManager theManager;

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
        public TethysFXDateButtonField() {
            this(new TethysDataFormatter());
        }

        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysFXDateButtonField(final TethysDataFormatter pFormatter) {
            this(new TethysFXDateButtonManager(pFormatter));
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        public TethysFXDateButtonField(final TethysFXDateButtonManager pManager) {
            /* Initialise underlying class */
            super((JDateButton) pManager.getButton());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

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

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXDateButtonManager getDateManager() {
            return theManager;
        }

        @Override
        protected JDateButton getEditControl() {
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
            getLabel().setText(theManager.getButton().getText());
        }

        @Override
        public void startCellEditing() {
            isCellEditing = true;
            setEditable(true);
            Platform.runLater(() -> theButton.fire());
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                setEditable(false);
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
         * The button.
         */
        private final Button theButton;

        /**
         * Are we editing a cell?
         */
        private boolean isCellEditing;

        /**
         * Constructor.
         */
        public TethysFXListButtonField() {
            this(new TethysFXListButtonManager<>());
        }

        /**
         * Constructor.
         * @param pManager the manager
         */
        public TethysFXListButtonField(final TethysFXListButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pManager.getNode());

            /* Store the manager and button */
            theManager = pManager;
            theButton = getEditControl();

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
                    updateText();
                    fireEvent(TethysUIEvent.NEWVALUE, pEvent.getDetails());
                    break;
                case EDITFOCUSLOST:
                    haltCellEditing();
                    break;
                default:
                    break;
            }
        }

        /**
         * Obtain the manager.
         * @return the manager
         */
        public TethysFXListButtonManager<T> getListManager() {
            return theManager;
        }

        @Override
        protected Button getEditControl() {
            return (Button) super.getEditControl();
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
        public void startCellEditing() {
            isCellEditing = true;
            setEditable(true);
            Platform.runLater(() -> theButton.fire());
        }

        /**
         * haltCellEditing.
         */
        private void haltCellEditing() {
            if (isCellEditing) {
                setEditable(false);
                fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
            }
            isCellEditing = false;
        }
    }
}
