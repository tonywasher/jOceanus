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

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Slider.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysSlider<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * Tick factor.
     */
    private static final int TICK_FACTOR = 10;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSlider(final TethysGuiFactory<N, I> pFactory) {
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public Integer getId() {
        return theId;
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
     * determine Tick intervals.
     * @param pMax the maximum size
     */
    protected void determineTickIntervals(final int pMax) {
        /* Initialise values */
        int iMax = pMax;
        int iMajor = 1;

        /* Calculate nearest power of Ten */
        while (iMax > TICK_FACTOR) {
            /* Divide max by ten and multiply major by ten */
            iMax /= TICK_FACTOR;
            iMajor *= TICK_FACTOR;
        }

        /* If major tick spacing is one */
        if (iMajor == 1) {
            /* Set major and minor ticks to 1 */
            setTickIntervals(iMajor, iMajor);

            /* else check on spacing */
        } else {
            /* Determine how many major ticks that gives us */
            final int iNumTicks = pMax / iMajor;

            /* If we have 5 or more ticks */
            if (iNumTicks >= (TICK_FACTOR >> 1)) {
                /* Use the major ticks and minor at half major ticks */
                setTickIntervals(iMajor, iMajor >> 1);
            } else {
                /* Use half the major ticks with minor ticks as one-tenth the major ticks */
                setTickIntervals(iMajor >> 1, iMajor / TICK_FACTOR);
            }
        }
    }

    /**
     * Set Tick Intervals.
     * @param pMajor the major interval
     * @param pMinor the minor interval
     */
    protected abstract void setTickIntervals(int pMajor,
                                             int pMinor);

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
     * Set the Maximum Value.
     * @param pMax the maximum
     */
    public abstract void setMaximum(Integer pMax);

    /**
     * Set the Value.
     * @param pValue the value
     */
    public abstract void setValue(Integer pValue);

    /**
     * Get Value.
     * @return the value
     */
    public abstract Integer getValue();

    /**
     * handleNewValue.
     */
    protected void handleNewValue() {
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, getValue());
    }
}
