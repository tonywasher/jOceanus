/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.core.button;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUINode;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIIconButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

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
 */
public abstract class TethysUICoreIconButtonManager<T>
        extends TethysUICoreComponent
        implements TethysUIIconButtonManager<T> {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The icon button.
     */
    private final TethysUIButton theButton;

    /**
     * The iconMap.
     */
    private final Map<TethysUIIconId, TethysUIIcon> theIconMap;

    /**
     * The value class.
     */
    private final Class<T> theClazz;

    /**
     * The icon Size.
     */
    private int theSize;

    /**
     * The value.
     */
    private T theValue;

    /**
     * The mapSet supplier to determine the icon for the value.
     */
    private Supplier<TethysUIIconMapSet<T>> theMapSet = () -> null;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pClazz the value class
     */
    protected TethysUICoreIconButtonManager(final TethysUICoreFactory<?> pFactory,
                                            final Class<T> pClazz) {
        /* Store parameters */
        theFactory = pFactory;
        theClazz = pClazz;

        /* Allocate resources */
        theEventManager = new OceanusEventManager<>();
        theButton = theFactory.buttonFactory().newButton();
        theIconMap = new HashMap<>();

        /* Note that the button should be Icon only */
        theButton.setIconOnly();

        /* Handle action */
        theButton.getEventRegistrar().addEventListener(e -> progressToNextState());
    }

    @Override
    public Integer getId() {
        return theButton.getId();
    }

    @Override
    public T getValue() {
        return theValue;
    }

    @Override
    public Class<T> getValueClass() {
        return theClazz;
    }

    @Override
    public TethysUINode getNode() {
        return theButton.getNode();
    }

    /**
     * Obtain button.
     * @return the button
     */
    protected TethysUIButton getButton() {
        return theButton;
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Check the icon size.
     * @param pSize the iconSize
     */
    private void checkSize(final int pSize) {
        if (theSize != pSize) {
            theSize = pSize;
            theIconMap.clear();
        }
    }

    @Override
    public void setValue(final T pValue) {
        /* Store the value */
        theValue = pValue;

        /* Apply the button state */
        applyButtonState();
    }

    @Override
    public void setIconMapSet(final Supplier<TethysUIIconMapSet<T>> pSelector) {
        theMapSet = pSelector;
    }

    @Override
    public Supplier<TethysUIIconMapSet<T>> getIconMapSet() {
        return theMapSet;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theButton.setVisible(pVisible);
    }

    @Override
    public void setNullMargins() {
        theButton.setNullMargins();
    }

    @Override
    public void applyButtonState() {
        /* Access MapSet and check iconWidth */
        final TethysUIIconMapSet<T> myMapSet = theMapSet.get();
        if (myMapSet != null) {
            checkSize(myMapSet.getSize());
        }

        /* Access Icon and ToolTip */
        final TethysUIIconId myIcon = myMapSet == null
                ? null
                : myMapSet.getIconForValue(theValue);
        final String myTip = myMapSet == null
                ? null
                : myMapSet.getTooltipForValue(theValue);

        /* Apply button state */
        theButton.setIcon(resolveIcon(myIcon));
        theButton.setToolTip(myTip);
    }

    /**
     * ResolveIcon.
     * @param pIconId the iconId
     * @return the icon
     */
    private TethysUIIcon resolveIcon(final TethysUIIconId pIconId) {
        /* Handle null icon */
        if (pIconId == null) {
            return null;
        }

        /* Look up icon */
        return theIconMap.computeIfAbsent(pIconId, i -> theFactory.resolveIcon(i, theSize));
    }

    @Override
    public void progressToNextState() {
        /* Access next value */
        final TethysUIIconMapSet<T> myMapSet = theMapSet.get();
        final T myValue = myMapSet == null
                ? theValue
                : myMapSet.getNextValueForValue(theValue);

        /* If there has been a change */
        if (valueChanged(myValue)) {
            /* Set the value */
            setValue(myValue);

            /* fire new Event */
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, myValue);
        } else {
            notifyCancelled();
        }
    }

    /**
     * notifyCancelled.
     */
    private void notifyCancelled() {
        /* fire menu cancelled event */
        theEventManager.fireEvent(TethysUIEvent.EDITFOCUSLOST);
    }

    /**
     * has value changed?
     * @param pNew the new value
     * @return true/false
     */
    private boolean valueChanged(final T pNew) {
        return !Objects.equals(theValue, pNew);
    }

    /**
     * MapSet.
     * @param <T> the object type
     */
    public static class TethysUICoreIconMapSet<T>
            implements TethysUIIconMapSet<T> {
        /**
         * The icon Size.
         */
        private final int theSize;

        /**
         * Value Map.
         */
        private final Map<T, T> theValueMap;

        /**
         * Icon Map.
         */
        private final Map<T, TethysUIIconId> theIconMap;

        /**
         * ToolTip Map.
         */
        private final Map<T, String> theTipMap;

        /**
         * Constructor.
         */
        public TethysUICoreIconMapSet() {
            this(TethysUIConstant.DEFAULT_ICONSIZE);
        }

        /**
         * Constructor.
         * @param pSize the icon Size
         */
        public TethysUICoreIconMapSet(final int pSize) {
            /* Store parameters */
            theSize = pSize;

            /* Allocate the maps */
            theValueMap = new HashMap<>();
            theIconMap = new HashMap<>();
            theTipMap = new HashMap<>();
        }

        @Override
        public int getSize() {
            return theSize;
        }

        @Override
        public void clearMaps() {
            theValueMap.clear();
            theIconMap.clear();
            theTipMap.clear();
        }

        @Override
        public void setMappingsForValue(final T pValue,
                                        final T pNext,
                                        final TethysUIIconId pId,
                                        final String pTooltip) {
            /* Store values */
            theValueMap.put(pValue, pNext);
            theIconMap.put(pValue, pId);
            theTipMap.put(pValue, pTooltip);
        }

        @Override
        public TethysUIIconId getIconForValue(final T pValue) {
            return theIconMap.get(pValue);
        }

        @Override
        public String getTooltipForValue(final T pValue) {
            return theTipMap.get(pValue);
        }

        @Override
        public T getNextValueForValue(final T pValue) {
            T myNext = theValueMap.get(pValue);
            if (myNext == null
                    && !theValueMap.containsKey(pValue)) {
                myNext = pValue;
            }
            return myNext;
        }
    }
}
