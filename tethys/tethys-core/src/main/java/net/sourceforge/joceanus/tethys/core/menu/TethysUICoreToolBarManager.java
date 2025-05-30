/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.core.menu;

import net.sourceforge.joceanus.oceanus.event.OceanusEvent.OceanusEventListener;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIToolBarManager;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ToolBar Manager.
 */
public abstract class TethysUICoreToolBarManager
        extends TethysUICoreComponent
        implements TethysUIToolBarManager {
    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Element Map.
     */
    private final Map<TethysUIToolBarId, TethysUICoreToolElement> theElementMap;

    /**
     * The icon Size.
     */
    private int theSize;

    /**
     * Constructor.
     * @param pFactory the Gui factory
     */
    protected TethysUICoreToolBarManager(final TethysUICoreFactory<?> pFactory) {
        /* Create the map */
        theElementMap = new HashMap<>();
        theId = pFactory.getNextId();
        theSize = TethysUIConstant.DEFAULT_ICONSIZE;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public int getIconSize() {
        return theSize;
    }

    @Override
    public void setIconSize(final int pSize) {
        theSize = pSize;
    }

    @Override
    public void setVisible(final TethysUIToolBarId pId,
                           final boolean pVisible) {
        final TethysUICoreToolElement myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setVisible(pVisible);
        }
    }

    @Override
    public void setEnabled(final TethysUIToolBarId pId,
                           final boolean pEnabled) {
        final TethysUIToolElement myElement = theElementMap.get(pId);
        if (myElement != null) {
            myElement.setEnabled(pEnabled);
        }
    }

    @Override
    public void newIcon(final TethysUIToolBarId pId,
                        final String pText,
                        final OceanusEventListener<TethysUIEvent> pListener) {
        /* Create the new element with Icon and text */
        final TethysUIToolElement myElement = newIcon(pId);
        myElement.setText(pText);
        myElement.setToolTip(pText);
        myElement.setTextAndIcon();
        myElement.getEventRegistrar().addEventListener(pListener);
    }

    /**
     * Look up icon.
     * @param pId the id of the element
     * @return the subMenu
     */
    @Override
    public TethysUIToolElement lookUpIcon(final TethysUIToolBarId pId) {
        return theElementMap.get(pId);
    }

    /**
     * ToolElement.
     */
    public abstract static class TethysUICoreToolElement
            implements TethysUIToolElement, OceanusEventProvider<TethysUIEvent> {
        /**
         * The Manager.
         */
        private final TethysUICoreToolBarManager theManager;

        /**
         * The Id.
         */
        private final TethysUIToolBarId theId;

        /**
         * Event Manager.
         */
        private final OceanusEventManager<TethysUIEvent> theEventManager;

        /**
         * Is the element enabled?
         */
        private boolean isEnabled;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the id
         */
        protected TethysUICoreToolElement(final TethysUICoreToolBarManager pManager,
                                          final TethysUIToolBarId pId) {
            /* record details */
            theManager = pManager;
            theId = pId;
            isEnabled = true;

            /* Access the element map */
            theEventManager = new OceanusEventManager<>();
            final Map<TethysUIToolBarId, TethysUICoreToolElement> myElementMap = pManager.theElementMap;

            /* Check uniqueness of item */
            if (myElementMap.containsKey(pId)) {
                throw new IllegalArgumentException("Duplicate MenuId: " + pId);
            }

            /* Store into map */
            myElementMap.put(pId, this);
        }

        @Override
        public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

         /**
         * Obtain the manager.
         * @return the manager
         */
        protected TethysUICoreToolBarManager getManager() {
            return theManager;
        }

        @Override
        public TethysUIToolBarId getId() {
            return theId;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }

        @Override
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
         * handle pressed.
         */
        protected void handlePressed() {
            theEventManager.fireEvent(TethysUIEvent.PRESSED);
        }
    }
}
