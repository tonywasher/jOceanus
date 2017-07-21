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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
     * The Factory.
     */
    private final TethysGuiFactory<N, I> theFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The icon button.
     */
    private final TethysButton<N, I> theButton;

    /**
     * The iconMap.
     */
    private final Map<TethysIconId, I> theIconMap;

    /**
     * The icon Width.
     */
    private int theWidth;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The value.
     */
    private T theValue;

    /**
     * The function to determine the icon for the value.
     */
    private Function<T, TethysIconMapSet<T>> theMapSetForValue = p -> null;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysIconButtonManager(final TethysGuiFactory<N, I> pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Allocate resources */
        theEventManager = new TethysEventManager<>();
        theButton = theFactory.newButton();
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

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

    /**
     * Check the icon width.
     * @param pWidth the iconWidth
     */
    private void checkWidth(final int pWidth) {
        if (theWidth != pWidth) {
            theWidth = pWidth;
            theIconMap.clear();
        }
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
     * Set the mapSet selector.
     * @param pSelector the selector
     */
    public void setIconMapSet(final Function<T, TethysIconMapSet<T>> pSelector) {
        theMapSetForValue = pSelector;
    }

    /**
     * Get the mapSet selector.
     * @return the selector
     */
    public Function<T, TethysIconMapSet<T>> getIconMapSet() {
        return theMapSetForValue;
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
    public void applyButtonState() {
        /* Access MapSet and check iconWidth */
        TethysIconMapSet<T> myMapSet = theMapSetForValue.apply(theValue);
        if (myMapSet != null) {
            checkWidth(myMapSet.getWidth());
        }

        /* Access Icon and ToolTip */
        TethysIconId myIcon = myMapSet == null
                                               ? null
                                               : myMapSet.getIconForValue(theValue);
        String myTip = myMapSet == null
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
    private I resolveIcon(final TethysIconId pIconId) {
        /* Handle null icon */
        if (pIconId == null) {
            return null;
        }

        /* Look up icon */
        I myIcon = theIconMap.get(pIconId);
        if (myIcon == null) {
            myIcon = theFactory.resolveIcon(pIconId, theWidth);
            theIconMap.put(pIconId, myIcon);
        }
        return myIcon;
    }

    /**
     * Progress state.
     */
    public void progressToNextState() {
        /* Access next value */
        TethysIconMapSet<T> myMapSet = theMapSetForValue.apply(theValue);
        T myValue = myMapSet == null
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
        return theValue == null
                                ? pNew != null
                                : !theValue.equals(pNew);
    }

    /**
     * MapSet.
     * @param <T> the object type
     */
    public static class TethysIconMapSet<T> {
        /**
         * The icon Width.
         */
        private int theWidth;

        /**
         * Value Map.
         */
        private final Map<T, T> theValueMap;

        /**
         * Icon Map.
         */
        private final Map<T, TethysIconId> theIconMap;

        /**
         * ToolTip Map.
         */
        private final Map<T, String> theTipMap;

        /**
         * Constructor.
         */
        public TethysIconMapSet() {
            this(TethysIconBuilder.DEFAULT_ICONWIDTH);
        }

        /**
         * Constructor.
         * @param pWidth the icon width
         */
        public TethysIconMapSet(final int pWidth) {
            /* Store parameters */
            theWidth = pWidth;

            /* Allocate the maps */
            theValueMap = new HashMap<>();
            theIconMap = new HashMap<>();
            theTipMap = new HashMap<>();
        }

        /**
         * Obtain the iconWidth.
         * @return the iconWidth
         */
        public int getWidth() {
            return theWidth;
        }

        /**
         * Clear the mapSet.
         */
        public void clearMaps() {
            theValueMap.clear();
            theIconMap.clear();
            theTipMap.clear();
        }

        /**
         * Set mappings for value with no icon.
         * @param pValue the value
         * @param pNext the next value for value
         * @param pTooltip the toolTip
         */
        public void setMappingsForValue(final T pValue,
                                        final T pNext,
                                        final String pTooltip) {
            setMappingsForValue(pValue, pNext, null, pTooltip);
        }

        /**
         * Set mappings for value with no toolTip.
         * @param <K> the keyId type
         * @param pValue the value
         * @param pNext the next value for value
         * @param pId the mapped IconId
         */
        public <K extends Enum<K> & TethysIconId> void setMappingsForValue(final T pValue,
                                                                           final T pNext,
                                                                           final K pId) {
            setMappingsForValue(pValue, pNext, pId, null);
        }

        /**
         * Set mappings for value.
         * @param <K> the keyId type
         * @param pValue the value
         * @param pNext the next value for value
         * @param pId the mapped IconId
         * @param pTooltip the toolTip
         */
        public <K extends Enum<K> & TethysIconId> void setMappingsForValue(final T pValue,
                                                                           final T pNext,
                                                                           final K pId,
                                                                           final String pTooltip) {
            /* Store values */
            theValueMap.put(pValue, pNext);
            theIconMap.put(pValue, pId);
            theTipMap.put(pValue, pTooltip);
        }

        /**
         * Obtain Icon for value.
         * @param pValue the value
         * @return the value
         */
        protected TethysIconId getIconForValue(final T pValue) {
            return theIconMap.get(pValue);
        }

        /**
         * Obtain ToolTip for value.
         * @param pValue the value
         * @return the value
         */
        protected String getTooltipForValue(final T pValue) {
            return theTipMap.get(pValue);
        }

        /**
         * Obtain Next value for value.
         * @param pValue the value
         * @return the value
         */
        protected T getNextValueForValue(final T pValue) {
            T myNext = theValueMap.get(pValue);
            if (myNext == null
                && !theValueMap.containsKey(pValue)) {
                myNext = pValue;
            }
            return myNext;
        }
    }
}
