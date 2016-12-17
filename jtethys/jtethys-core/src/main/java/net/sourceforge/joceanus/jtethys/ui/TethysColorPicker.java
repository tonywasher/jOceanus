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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Colour Picker.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysColorPicker<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysColorPicker(final TethysGuiFactory<N, I> pFactory) {
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
     * Set the value.
     * @param pValue the value
     */
    public void setValue(final String pValue) {
        theValue = pValue;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public String getValue() {
        return theValue;
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
     * handle new value.
     * @param pValue the new value
     */
    protected void handleNewValue(final String pValue) {
        /* Only fire if the selection has changed */
        if (valueChanged(pValue)) {
            /* record selection and fire event */
            theValue = pValue;
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pValue);
        }
    }

    /**
     * has value changed?
     * @param pNew the new value
     * @return true/false
     */
    private boolean valueChanged(final String pNew) {
        return theValue == null
                                ? pNew != null
                                : !theValue.equals(pNew);
    }

    /**
     * handle focus loss.
     */
    protected void handleFocusLoss() {
        theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);
    }
}
