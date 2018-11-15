/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * ToolBar Manager.
 */
public abstract class TethysToolBarManager
        implements TethysComponent {
    /**
     * Default icon width.
     */
    protected static final int DEFAULT_ICONWIDTH = TethysIconButtonManager.DEFAULT_ICONWIDTH;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Element Map.
     */
    private final Map<Object, TethysToolElement<?>> theElementMap;

    /**
     * The icon Width.
     */
    private int theWidth;

    /**
     * Constructor.
     * @param pFactory the Gui factory
     */
    protected TethysToolBarManager(final TethysGuiFactory pFactory) {
        /* Create the map */
        theElementMap = new HashMap<>();
        theId = pFactory.getNextId();
        theWidth = DEFAULT_ICONWIDTH;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain width.
     * @return the width
     */
    public int getIconWidth() {
        return theWidth;
    }

    /**
     * Set the width.
     * @param pWidth the width to set
     */
    public void setIconWidth(final int pWidth) {
        /* Store the width */
        theWidth = pWidth;
    }

    /**
     * Set visible state for element.
     * @param <E> the type of the id
     * @param pId the id of the element
     * @param pVisible true/false
     */
    public <E> void setVisible(final E pId,
                               final boolean pVisible) {
        final TethysToolElement<?> myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setVisible(pVisible);
        }
    }

    /**
     * Set enabled state for element.
     * @param <E> the type of the id
     * @param pId the id of the element
     * @param pEnabled true/false
     */
    public <E> void setEnabled(final E pId,
                               final boolean pEnabled) {
        final TethysToolElement<?> myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setEnabled(pEnabled);
        }
    }

    /**
     * Configure Icon.
     * @param <K> the type if Icon Id
     * @param pId the IconId
     * @param pText the item Text
     * @param pListener the item listener
     */
    public <K extends Enum<K> & TethysIconId> void newIcon(final K pId,
                                                           final String pText,
                                                           final TethysEventListener<TethysUIEvent> pListener) {
        /* Create the new element with Icon and text */
        final TethysToolElement<K> myElement = newIcon(pId);
        myElement.setIcon(pId);
        myElement.setText(pText);
        myElement.setToolTip(pText);
        myElement.setTextAndIcon();
        myElement.getEventRegistrar().addEventListener(pListener);
    }

    /**
     * Add a new Icon element.
     * @param <E> the id type
     * @param pId the id of the element
     * @return the new element
     */

    public abstract <E> TethysToolElement<E> newIcon(E pId);

    /**
     * Add Separator.
     */
    public abstract void newSeparator();

    /**
     * Look up icon.
     * @param <E> the type of the id
     * @param pId the id of the element
     * @return the subMenu
     */
    @SuppressWarnings("unchecked")
    public <E> TethysToolElement<E> lookUpIcon(final E pId) {
        final TethysToolElement<?> myElement = theElementMap.get(pId);
        return myElement instanceof TethysToolElement
                                                      ? (TethysToolElement<E>) myElement
                                                      : null;
    }

    /**
     * ToolElement.
     * @param <E> the id type
     */
    public abstract static class TethysToolElement<E>
            implements TethysEventProvider<TethysUIEvent> {
        /**
         * The Manager.
         */
        private final TethysToolBarManager theManager;

        /**
         * The Id.
         */
        private final E theId;

        /**
         * Event Manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * Is the element enabled?
         */
        private boolean isEnabled;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         */
        protected TethysToolElement(final TethysToolBarManager pManager,
                                    final E pId) {
            /* record details */
            theManager = pManager;
            theId = pId;
            isEnabled = true;

            /* Access the element map */
            theEventManager = new TethysEventManager<>();
            final Map<Object, TethysToolElement<?>> myElementMap = pManager.theElementMap;

            /* Check uniqueness of item */
            if (myElementMap.containsKey(pId)) {
                throw new IllegalArgumentException("Duplicate MenuId: " + pId);
            }

            /* Store into map */
            myElementMap.put(pId, this);
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Set text for icon.
         * @param pText the text
         */
        public abstract void setText(String pText);

        /**
         * Set toolTip for icon.
         * @param pTip the toolTip
         */
        public abstract void setToolTip(String pTip);

        /**
         * Set icon for button.
         * @param <K> the keyId type
         * @param pId the icon Id
         */
        public abstract <K extends Enum<K> & TethysIconId> void setIcon(K pId);

        /**
         * Set icon for button.
         * @param pIcon the icon
         */
        public abstract void setIcon(TethysIcon pIcon);

        /**
         * Obtain the manager.
         * @return the manager
         */
        protected TethysToolBarManager getManager() {
            return theManager;
        }

        /**
         * Obtain the id.
         * @return the id
         */
        public E getId() {
            return theId;
        }

        /**
         * Is the icon enabled?
         * @return true/false
         */
        public boolean isEnabled() {
            return isEnabled;
        }

        /**
         * Set the enabled state of the menu.
         * @param pEnabled true/false
         */
        public void setEnabled(final boolean pEnabled) {
            /* If we are changing enabled state */
            if (pEnabled != isEnabled) {
                /* Set new enabled state */
                isEnabled = pEnabled;

                /* enable the item */
                enableItem(isEnabled);
            }
        }

        /**
         * Enable/disable the item.
         * @param pEnabled true/false
         */
        protected abstract void enableItem(boolean pEnabled);

        /**
         * Set item visibility.
         * @param pVisible true/false
         */
        protected abstract void setVisible(boolean pVisible);

        /**
         * Set Icon only.
         */
        public abstract void setIconOnly();

        /**
         * Set Text And Icon.
         */
        public abstract void setTextAndIcon();

        /**
         * Set Text Only.
         */
        public abstract void setTextOnly();

        /**
         * handle pressed.
         */
        protected void handlePressed() {
            theEventManager.fireEvent(TethysUIEvent.PRESSED);
        }
    }
}
