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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * IconButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>NEWVALUE
 * <dd>fired when a new value is selected. <br>
 * Detail is new value
 * </dl>
 * @param <T> the object type
 * @param <N> the button type
 * @param <I> the Icon type
 */
public abstract class TethysIconButtonManager<T, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * Default icon width.
     */
    protected static final int DEFAULT_ICONWIDTH = TethysIconBuilder.DEFAULT_ICONWIDTH;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The icon button.
     */
    private final TethysButton<N, I> theButton;

    /**
     * The icon Width.
     */
    private int theWidth;

    /**
     * The value.
     */
    private T theValue;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysIconButtonManager(final TethysGuiFactory<N, I> pFactory) {
        /* Allocate resources */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        theButton = pFactory.newButton();
        theWidth = DEFAULT_ICONWIDTH;

        /* Note that the button should be Icon only */
        theButton.setIconOnly();

        /* Handle action */
        theButton.getEventRegistrar().addEventListener(e -> progressToNextState());
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain value.
     * @return the value
     */
    public T getValue() {
        return theValue;
    }

    @Override
    public N getNode() {
        return theButton.getNode();
    }

    /**
     * Obtain button.
     * @return the button
     */
    protected TethysButton<N, I> getButton() {
        return theButton;
    }

    /**
     * Obtain width.
     * @return the width
     */
    public int getWidth() {
        return theWidth;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set the width.
     * @param pWidth the width to set
     */
    public void setWidth(final int pWidth) {
        /* Store the width */
        theWidth = pWidth;
    }

    /**
     * Set the value.
     * @param pValue the value to set
     */
    public void setValue(final T pValue) {
        /* Store the value */
        theValue = pValue;

        /* Apply the button state */
        applyButtonState();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
    }

    /**
     * Set Null Margins.
     */
    public void setNullMargins() {
        theButton.setNullMargins();
    }

    /**
     * Apply button state.
     */
    protected void applyButtonState() {
        /* Access Icon and ToolTip */
        I myIcon = getIconForValue(theValue);
        String myTip = getToolTipForValue(theValue);

        /* Apply button state */
        theButton.setIcon(myIcon);
        theButton.setToolTip(myTip);
    }

    /**
     * Progress state.
     */
    public void progressToNextState() {
        /* Access next value */
        T myValue = getNewValueForValue(theValue);

        /* Set the value */
        setValue(myValue);

        /* fire new Event */
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myValue);
    }

    /**
     * Obtain icon for value.
     * @param pValue the value
     * @return the icon
     */
    protected abstract I getIconForValue(final Object pValue);

    /**
     * Obtain toolTip for value.
     * @param pValue the value
     * @return the toolTip
     */
    protected abstract String getToolTipForValue(final Object pValue);

    /**
     * Obtain new value on click.
     * @param pValue the current value
     * @return the new value
     */
    protected abstract T getNewValueForValue(final Object pValue);

    /**
     * Simple IconButton Manager.
     * @param <T> the object type
     * @param <N> the button type
     * @param <I> the Icon type
     */
    public abstract static class TethysSimpleIconButtonManager<T, N, I>
            extends TethysIconButtonManager<T, N, I> {
        /**
         * Active Map Set.
         */
        private IconMapSet<T, I> theMapSet;

        /**
         * Constructor.
         * @param pManager the GUI manager
         */
        protected TethysSimpleIconButtonManager(final TethysGuiFactory<N, I> pManager) {
            /* Initialise the underlying class */
            super(pManager);

            /* Set a default mapSet */
            setMapSet(new IconMapSet<T, I>());
        }

        /**
         * Obtain the mapSet.
         * @return the mapSet
         */
        protected IconMapSet<T, I> getMapSet() {
            return theMapSet;
        }

        /**
         * Set the mapSet.
         * @param pMapSet the mapSet
         */
        protected void setMapSet(final IconMapSet<T, I> pMapSet) {
            theMapSet = pMapSet;
        }

        /**
         * Reset the configuration.
         */
        public void clearMaps() {
            theMapSet.clearMaps();
        }

        @Override
        protected I getIconForValue(final Object pValue) {
            Map<T, I> myMap = theMapSet.getIconMap();
            return myMap.get(pValue);
        }

        @Override
        protected String getToolTipForValue(final Object pValue) {
            Map<T, String> myMap = theMapSet.getToolTipMap();
            return myMap.get(pValue);
        }

        @Override
        protected T getNewValueForValue(final Object pValue) {
            Map<T, T> myMap = theMapSet.getValueMap();
            return myMap.get(pValue);
        }

        /**
         * Map value.
         * @param pValue the value
         * @param pIcon the mapped Icon
         */
        public void setIconForValue(final T pValue,
                                    final I pIcon) {
            /* Put value into map */
            Map<T, I> myMap = theMapSet.getIconMap();
            myMap.put(pValue, pIcon);
        }

        /**
         * Map simple details.
         * @param <K> the keyId type
         * @param pValue the value
         * @param pId the mapped IconId
         * @param pToolTip the toolTip for value
         */
        public <K extends Enum<K> & TethysIconId> void setSimpleDetailsForValue(final T pValue,
                                                                                final K pId,
                                                                                final String pToolTip) {
            setDetailsForValue(pValue, pValue, pId, pToolTip);
        }

        /**
         * Map details.
         * @param pValue the value
         * @param pNext the next value for value
         * @param pToolTip the toolTip for value
         */
        public void setDetailsForValue(final T pValue,
                                       final T pNext,
                                       final String pToolTip) {
            setDetailsForValue(pValue, pNext, null, pToolTip);
        }

        /**
         * Map details.
         * @param <K> the keyId type
         * @param pValue the value
         * @param pNext the next value for value
         * @param pId the mapped IconId
         */
        public <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                          final T pNext,
                                                                          final K pId) {
            setDetailsForValue(pValue, pNext, pId, null);
        }

        /**
         * Map details.
         * @param <K> the keyId type
         * @param pValue the value
         * @param pNext the next value for value
         * @param pId the mapped IconId
         * @param pToolTip the toolTip for value
         */
        public abstract <K extends Enum<K> & TethysIconId> void setDetailsForValue(final T pValue,
                                                                                   final T pNext,
                                                                                   final K pId,
                                                                                   final String pToolTip);

        /**
         * Map ToolTip.
         * @param pValue the value
         * @param pTip the mapped toolTip
         */
        public void setTooltipForValue(final T pValue,
                                       final String pTip) {
            /* Put value into map */
            Map<T, String> myMap = theMapSet.getToolTipMap();
            myMap.put(pValue, pTip);
        }

        /**
         * Map New Value.
         * @param pValue the value
         * @param pNewValue the new value
         */
        public void setNewValueForValue(final T pValue,
                                        final T pNewValue) {
            /* Put value into map */
            Map<T, T> myMap = theMapSet.getValueMap();
            myMap.put(pValue, pNewValue);
        }
    }

    /**
     * State-based IconButton Manager.
     * @param <T> the object type
     * @param <S> the state
     * @param <N> the button type
     * @param <I> the Icon type
     */
    public abstract static class TethysStateIconButtonManager<T, S, N, I>
            extends TethysSimpleIconButtonManager<T, N, I> {
        /**
         * State set?
         */
        private boolean stateSet;

        /**
         * Current state.
         */
        private S theMachineState;

        /**
         * MapSet Map.
         */
        private Map<S, IconMapSet<T, I>> theStateMap;

        /**
         * Constructor.
         * @param pManager the GUI manager
         */
        public TethysStateIconButtonManager(final TethysGuiFactory<N, I> pManager) {
            /* Initialise the underlying class */
            super(pManager);

            /* Allocate the maps */
            theStateMap = new HashMap<>();
        }

        /**
         * Obtain state.
         * @return the state
         */
        public S getMachineState() {
            return theMachineState;
        }

        /**
         * Set state.
         * @param pState the new state
         */
        public void setMachineState(final S pState) {
            /* Ignore if we are already correct state */
            if (stateSet
                && pState.equals(theMachineState)) {
                return;
            }

            /* If this is the initial state */
            if (!stateSet) {
                /* Register against default set */
                theStateMap.put(pState, getMapSet());
                stateSet = true;

                /* Else this is a subsequent state */
            } else {
                /* Look for existing state */
                IconMapSet<T, I> mySet = theStateMap.get(pState);

                /* If this is a new state */
                if (mySet == null) {
                    /* Create the new state and record it */
                    mySet = new IconMapSet<>();
                    theStateMap.put(pState, mySet);
                }

                /* Switch to this set */
                setMapSet(mySet);
            }

            /* register the map Set */
            theMachineState = pState;
            applyButtonState();
        }

        @Override
        public void clearMaps() {
            /* Reset state Maps */
            theStateMap.clear();
            theStateMap.put(theMachineState, getMapSet());

            /* Clear the current map */
            super.clearMaps();
        }
    }

    /**
     * MapSet.
     * @param <T> the object type
     * @param <I> the Icon type
     */
    private static final class IconMapSet<T, I> {
        /**
         * Value Map.
         */
        private final Map<T, T> theValueMap;

        /**
         * Icon Map.
         */
        private final Map<T, I> theIconMap;

        /**
         * ToolTip Map.
         */
        private final Map<T, String> theTipMap;

        /**
         * Constructor.
         */
        private IconMapSet() {
            /* Allocate the maps */
            theValueMap = new HashMap<>();
            theIconMap = new HashMap<>();
            theTipMap = new HashMap<>();
        }

        /**
         * Clear the mapSet.
         */
        private void clearMaps() {
            theValueMap.clear();
            theIconMap.clear();
            theTipMap.clear();
        }

        /**
         * Obtain the iconMap.
         * @return the iconMap
         */
        private Map<T, I> getIconMap() {
            return theIconMap;
        }

        /**
         * Obtain the toolTipMap.
         * @return the toolTipMap
         */
        private Map<T, String> getToolTipMap() {
            return theTipMap;
        }

        /**
         * Obtain the valueTipMap.
         * @return the valueTipMap
         */
        private Map<T, T> getValueMap() {
            return theValueMap;
        }
    }
}
