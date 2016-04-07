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

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Check Box.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysCheckBox<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * is the CheckBox selected?
     */
    private boolean isSelected;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysCheckBox(final TethysGuiFactory<N, I> pFactory) {
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
     * handle selected.
     * @param pText the text.
     */
    public abstract void setText(final String pText);

    /**
     * Set the Border Title.
     * @param pTitle the title text
     */
    public abstract void setBorderTitle(final String pTitle);

    /**
     * set selected.
     * @param pSelected is the box selected?
     */
    public void setSelected(final boolean pSelected) {
        isSelected = pSelected;
    }

    /**
     * Is the box selected?
     * @return true/false
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Set handle selected.
     * @param pSelected is the box selected.
     */
    protected void handleSelected(final Boolean pSelected) {
        /* Only fire if the selection has changed */
        if (pSelected != isSelected) {
            /* record selection and fire event */
            isSelected = pSelected;
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pSelected);
        }
    }
}
