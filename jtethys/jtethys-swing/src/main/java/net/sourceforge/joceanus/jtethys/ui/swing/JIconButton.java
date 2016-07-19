/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Simple button that displays icon.
 * @param <T> the object type
 * @deprecated as of 1.5.0 use {@link TethysSwingIconButtonManager}
 */
@Deprecated
public class JIconButton<T>
        extends JButton {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4943316534086830953L;

    /**
     * Name of the Value property.
     */
    public static final String PROPERTY_VALUE = "Value";

    /**
     * Button value.
     */
    private transient T theValue;

    /**
     * State Machine.
     */
    private final transient IconButtonState<T> theState;

    /**
     * Constructor.
     * @param pState the state machine
     */
    public JIconButton(final IconButtonState<T> pState) {
        /* Store the state */
        theState = pState;

        /* Declare this button to the state */
        pState.declareButton(this);
    }

    /**
     * Obtain value.
     * @return the value
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain state.
     * @return the state
     */
    public IconButtonState<T> getState() {
        return theState;
    }

    /**
     * Set the value.
     * @param pValue the value to set.
     */
    public void setValue(final T pValue) {
        /* Access current value */
        T myOld = theValue;

        /* Store new values */
        storeTheValue(pValue);

        /* If the value has changed */
        if (isValueChanged(myOld, theValue)) {
            /* Fire the property change */
            firePropertyChange(PROPERTY_VALUE, myOld, theValue);
        }
    }

    /**
     * Has the value changed?
     * @param pFirst the first value
     * @param pSecond the second value
     * @param <T> the object type
     * @return <code>true/false</code>
     */
    protected static <T> boolean isValueChanged(final T pFirst,
                                                final T pSecond) {
        if (pFirst == null) {
            return pSecond != null;
        } else {
            return !pFirst.equals(pSecond);
        }
    }

    /**
     * Store the value without firing events.
     * @param pValue the value to set.
     */
    public void storeValue(final T pValue) {
        /* Store new values */
        storeTheValue(pValue);
    }

    /**
     * Set value of button.
     * @param pValue the value to set
     */
    private void storeTheValue(final T pValue) {
        /* Store value */
        theValue = pValue;

        /* Access Icon */
        Icon myIcon = theState.getIconForValue(pValue);
        setIcon(myIcon);

        /* Access ToolTip */
        String myTip = theState.getToolTipForValue(pValue);
        setToolTipText(myTip);
    }

    /**
     * State Machine class.
     * @param <T> the object type
     */
    @Deprecated
    public abstract static class IconButtonState<T> {
        /**
         * The icon button.
         */
        private JIconButton<T> theButton;

        /**
         * Declare button.
         * @param pButton the button
         */
        private void declareButton(final JIconButton<T> pButton) {
            /* Store the button */
            theButton = pButton;

            /* Add the button listener */
            theButton.addActionListener(new ButtonListener());
        }

        /**
         * Obtain icon for value.
         * @param pValue the value
         * @return the icon
         */
        public abstract Icon getIconForValue(final Object pValue);

        /**
         * Obtain toolTip for value.
         * @param pValue the value
         * @return the toolTip
         */
        public String getToolTipForValue(final Object pValue) {
            return null;
        }

        /**
         * Obtain new value on click.
         * @param pValue the current value
         * @return the new value
         */
        protected abstract T getNewValueForValue(final Object pValue);

        /**
         * Button Listener class.
         */
        private class ButtonListener
                implements ActionListener {
            @Override
            public void actionPerformed(final ActionEvent pEvent) {
                /* Obtain the new value for the button */
                T myNewValue = getNewValueForValue(theButton.getValue());

                /* Set the value for the button */
                theButton.setValue(myNewValue);
            }
        }
    }

    /**
     * Default State Machine class.
     * @param <T> the object type
     */
    @Deprecated
    public static class DefaultIconButtonState<T>
            extends IconButtonState<T> {
        /**
         * Active Map Set.
         */
        private IconMapSet<T> theMapSet;

        /**
         * Constructor.
         */
        public DefaultIconButtonState() {
            /* Set a default mapSet */
            setMapSet(new IconMapSet<T>());
        }

        /**
         * Obtain the mapSet.
         * @return the mapSet
         */
        protected IconMapSet<T> getMapSet() {
            return theMapSet;
        }

        /**
         * Set the mapSet.
         * @param pMapSet the mapSet
         */
        protected void setMapSet(final IconMapSet<T> pMapSet) {
            theMapSet = pMapSet;
        }

        @Override
        public Icon getIconForValue(final Object pValue) {
            Map<T, Icon> myMap = theMapSet.getIconMap();
            return myMap.get(pValue);
        }

        @Override
        public String getToolTipForValue(final Object pValue) {
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
                                    final Icon pIcon) {
            /* Put value into map */
            Map<T, Icon> myMap = theMapSet.getIconMap();
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
     * Default State Machine class.
     * @param <T> the object type
     * @param <S> the state
     */
    @Deprecated
    public static class ComplexIconButtonState<T, S>
            extends DefaultIconButtonState<T> {
        /**
         * Current state.
         */
        private S theState;

        /**
         * MapSet Map.
         */
        private Map<S, IconMapSet<T>> theStateMap;

        /**
         * Constructor.
         */
        public ComplexIconButtonState() {
            /* Initialise state to null */
            this(null);
        }

        /**
         * Constructor.
         * @param pState the initial state
         */
        public ComplexIconButtonState(final S pState) {
            /* Allocate the maps */
            theStateMap = new HashMap<>();

            /* Register the initial state */
            theState = pState;
            theStateMap.put(theState, getMapSet());
        }

        /**
         * Set state.
         * @param pState the new state
         */
        public void setState(final S pState) {
            /* Ignore if we are already correct state */
            if (pState.equals(theState)) {
                return;
            }

            /* Look for existing state */
            IconMapSet<T> mySet = theStateMap.get(pState);

            /* If this is a new state */
            if (mySet == null) {
                /* Create the new state and record it */
                mySet = new IconMapSet<>();
                theStateMap.put(pState, mySet);
            }

            /* register the map Set */
            theState = pState;
            setMapSet(mySet);
        }

        /**
         * Obtain state.
         * @return the state
         */
        public S getState() {
            return theState;
        }

        /**
         * Obtain icon for explicit value and state.
         * @param pValue the value
         * @param pState the state
         * @return the icon
         */
        public Icon getIconForValueAndState(final Object pValue,
                                            final S pState) {
            /* Look for state */
            IconMapSet<T> mySet = theStateMap.get(pState);
            Map<T, Icon> myMap = mySet.getIconMap();
            return myMap.get(pValue);
        }

        /**
         * Obtain toolTip for explicit value and state.
         * @param pValue the value
         * @param pState the state
         * @return the toolTip
         */
        public String getToolTipForValueAndState(final Object pValue,
                                                 final S pState) {
            /* Look for state */
            IconMapSet<T> mySet = theStateMap.get(pState);
            Map<T, String> myMap = mySet.getToolTipMap();
            return myMap.get(pValue);
        }
    }

    /**
     * MapSet.
     * @param <T> the object type
     */
    @Deprecated
    private static final class IconMapSet<T> {
        /**
         * Value Map.
         */
        private final Map<T, T> theValueMap;

        /**
         * Icon Map.
         */
        private final Map<T, Icon> theIconMap;

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
         * Obtain the iconMap.
         * @return the iconMap
         */
        private Map<T, Icon> getIconMap() {
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
