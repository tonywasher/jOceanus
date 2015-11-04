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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/src/main/java/net/sourceforge/joceanus/jtethys/swing/JIconButton.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;

/**
 * IconButton Manager.
 * @param <T> the object type
 * @param <I> the Icon type
 */
public abstract class IconButtonManager<T, I>
        implements JOceanusEventProvider {
    /**
     * Icon Button.
     * @param <I> the Icon type
     */
    public interface IconButton<I> {
        /**
         * Set the state.
         * @param pIcon the value to set.
         * @param pToolTip the toolTip to set.
         */
        void setButtonState(final I pIcon,
                            final String pToolTip);
    }

    /**
     * Icon updated.
     */
    public static final int ACTION_NEW_VALUE = 100;

    /**
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

    /**
     * The value.
     */
    private T theValue;

    /**
     * The icon button.
     */
    private IconButton<I> theButton;

    /**
     * Constructor.
     */
    protected IconButtonManager() {
        /* Create event manager */
        theEventManager = new JOceanusEventManager();
    }

    /**
     * Obtain value.
     * @return the value
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain button.
     * @return the button
     */
    public IconButton<I> getButton() {
        return theButton;
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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

    /**
     * Apply button state.
     */
    protected void applyButtonState() {
        /* Access Icon and ToolTip */
        I myIcon = getIconForValue(theValue);
        String myTip = getToolTipForValue(theValue);

        /* Apply button state */
        theButton.setButtonState(myIcon, myTip);
    }

    /**
     * Progress state.
     */
    public void progressToNextState() {
        /* Access next value */
        T myValue = getNewValueForValue(theValue);

        /* Set the value */
        setValue(myValue);

        /* fire new actionEvent */
        theEventManager.fireActionEvent(ACTION_NEW_VALUE, myValue);
    }

    /**
     * Declare button.
     * @param pButton the button
     */
    protected void declareButton(final IconButton<I> pButton) {
        /* Store the button */
        theButton = pButton;
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
     * @param <I> the Icon type
     */
    public abstract static class SimpleIconButtonManager<T, I>
            extends IconButtonManager<T, I> {
        /**
         * Active Map Set.
         */
        private IconMapSet<T, I> theMapSet;

        /**
         * Constructor.
         */
        protected SimpleIconButtonManager() {
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
     * @param <I> the Icon type
     */
    public abstract static class StateIconButtonManager<T, S, I>
            extends SimpleIconButtonManager<T, I> {
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
         */
        public StateIconButtonManager() {
            /* Initialise state to null */
            this(null);
        }

        /**
         * Constructor.
         * @param pState the initial state
         */
        public StateIconButtonManager(final S pState) {
            /* Allocate the maps */
            theStateMap = new HashMap<S, IconMapSet<T, I>>();

            /* Register the initial state */
            theMachineState = pState;
            theStateMap.put(theMachineState, getMapSet());
        }

        /**
         * Set state.
         * @param pState the new state
         */
        public void setMachineState(final S pState) {
            /* Ignore if we are already correct state */
            if (pState.equals(theMachineState)) {
                return;
            }

            /* Look for existing state */
            IconMapSet<T, I> mySet = theStateMap.get(pState);

            /* If this is a new state */
            if (mySet == null) {
                /* Create the new state and record it */
                mySet = new IconMapSet<T, I>();
                theStateMap.put(pState, mySet);
            }

            /* register the map Set */
            theMachineState = pState;
            setMapSet(mySet);
            applyButtonState();
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
            theValueMap = new HashMap<T, T>();
            theIconMap = new HashMap<T, I>();
            theTipMap = new HashMap<T, String>();
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
