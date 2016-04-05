/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusItemEditActions.PrometheusItemEditParent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Item Action buttons.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class PrometheusItemActions<N, I>
        implements TethysEventProvider<PrometheusUIEvent>, TethysNode<N> {
    /**
     * Strut width.
     */
    protected static final int STRUT_HEIGHT = PrometheusActionButtons.STRUT_LENGTH;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The parent.
     */
    private final PrometheusItemEditParent theParent;

    /**
     * The GoTo button.
     */
    private TethysScrollButtonManager<PrometheusGoToEvent, N, I> theGoToButton;

    /**
     * The Edit button.
     */
    private TethysButton<N, I> theEditButton;

    /**
     * The Delete button.
     */
    private TethysButton<N, I> theDeleteButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pParent the parent
     */
    protected PrometheusItemActions(final TethysGuiFactory<N, I> pFactory,
                                    final PrometheusItemEditParent pParent) {
        /* Record the parent */
        theParent = pParent;

        /* Record the id */
        theId = pFactory.getNextId();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the buttons */
        theGoToButton = pFactory.newScrollButton();
        theEditButton = pFactory.newButton();
        theDeleteButton = pFactory.newButton();

        /* Configure the buttons */
        PrometheusIcon.configureGoToScrollButton(theGoToButton);
        PrometheusIcon.configureEditIconButton(theEditButton);
        PrometheusIcon.configureDeleteIconButton(theDeleteButton);

        /* Add the listener for item changes */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = theGoToButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> theEventManager.fireEvent(PrometheusUIEvent.BUILDGOTO, e.getDetails()));
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> theEventManager.fireEvent(PrometheusUIEvent.GOTO, theGoToButton.getValue()));
        theEditButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.EDIT));
        theDeleteButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.DELETE));

        /* Buttons are initially disabled */
        setEnabled(false);
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<PrometheusUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the goTo button.
     * @return the button
     */
    protected TethysScrollButtonManager<PrometheusGoToEvent, N, I> getGoToButton() {
        return theGoToButton;
    }

    /**
     * Obtain the undo button.
     * @return the button
     */
    protected TethysButton<N, I> getEditButton() {
        return theEditButton;
    }

    /**
     * Obtain the reset button.
     * @return the button
     */
    protected TethysButton<N, I> getDeleteButton() {
        return theDeleteButton;
    }

    /**
     * Set enabled.
     * @param bEnabled the enabled status
     */
    public void setEnabled(final boolean bEnabled) {
        /* If the table is locked clear the buttons */
        if (!bEnabled) {
            theGoToButton.setEnabled(false);
            theEditButton.setEnabled(false);
            theDeleteButton.setEnabled(false);

            /* Else look at the edit state */
        } else {
            /* Set the states */
            theGoToButton.setEnabled(true);
            theEditButton.setEnabled(theParent.isEditable());
            theDeleteButton.setEnabled(theParent.isDeletable());
        }
    }
}
