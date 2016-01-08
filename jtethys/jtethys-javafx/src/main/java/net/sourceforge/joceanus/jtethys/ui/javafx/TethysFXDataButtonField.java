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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.text.Font;
import net.sourceforge.jdatebutton.javafx.JDateButton;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
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
        private final TethysIconButtonManager<T, Node> theManager;

        /**
         * The button.
         */
        private final TethysFXIconButton theButton;

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
        public TethysFXIconButtonField(final TethysIconButtonManager<T, Node> pManager) {
            /* Initialise underlying class */
            super((TethysFXIconButton) pManager.getButton());

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
        public TethysIconButtonManager<T, Node> getIconManager() {
            return theManager;
        }

        @Override
        protected TethysFXIconButton getEditControl() {
            return (TethysFXIconButton) super.getEditControl();
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
        private final TethysFXScrollButton theButton;

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
            super((TethysFXScrollButton) pManager.getButton());

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
        protected TethysFXScrollButton getEditControl() {
            return (TethysFXScrollButton) super.getEditControl();
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
            getLabel().setText(theManager.getButton().getText());
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
         * Constructor.
         */
        public TethysFXDateButtonField() {
            this(new TethysFXDateButtonManager());
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
    }

    /**
     * ListButtonField class.
     * @param <T> the data type
     */
    public static class TethysFXListButtonField<T>
            extends TethysFXDataTextField<T> {
        /**
         * The list manager.
         */
        private final TethysFXListButtonManager<T> theManager;

        /**
         * The button.
         */
        private final TethysFXListButton theButton;

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
            super((TethysFXListButton) pManager.getButton());

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
                case TOGGLEITEM:
                    updateText();
                    fireEvent(TethysUIEvent.TOGGLEITEM, pEvent.getDetails());
                    break;
                case PREPAREDIALOG:
                    fireEvent(TethysUIEvent.PREPAREDIALOG, this);
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
        protected TethysFXListButton getEditControl() {
            return (TethysFXListButton) super.getEditControl();
        }

        @Override
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            getLabel().setFont(pFont);
            theButton.setFont(pFont);
        }

        @Override
        public void setValue(final T pValue) {
            /* Reject the call */
            throw new UnsupportedOperationException();
        }

        /**
         * Clear available items.
         */
        public void clearAvailableItems() {
            theManager.clearAvailableItems();
            updateText();
        }

        /**
         * Set available item.
         * @param pItem the available item
         */
        public void setAvailableItem(final T pItem) {
            theManager.setAvailableItem(pItem);
            updateText();
        }

        /**
         * clear all selected items.
         */
        public void clearAllSelected() {
            theManager.clearAllSelected();
            updateText();
        }

        /**
         * Set selected item.
         * @param pItem the item to select
         */
        public void setSelectedItem(final T pItem) {
            theManager.setSelectedItem(pItem);
            updateText();
        }

        /**
         * Clear selected item.
         * @param pItem the item to clear
         */
        public void clearSelectedItem(final T pItem) {
            theManager.clearSelectedItem(pItem);
            updateText();
        }

        /**
         * Update the button text.
         */
        private void updateText() {
            getLabel().setText(theManager.getText());
        }

        /**
         * Is item selected?
         * @param pItem the item to check
         * @return true/false
         */
        public boolean isItemSelected(final T pItem) {
            return theManager.isItemSelected(pItem);
        }
    }
}
