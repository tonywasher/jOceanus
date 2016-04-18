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
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
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
public class PrometheusItemActions<N, I>
        implements TethysEventProvider<PrometheusUIEvent>, TethysNode<N> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The parent.
     */
    private final PrometheusItemEditParent theParent;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The GoTo button.
     */
    private final TethysScrollButtonManager<PrometheusGoToEvent, N, I> theGoToButton;

    /**
     * The Edit button.
     */
    private final TethysButton<N, I> theEditButton;

    /**
     * The Delete button.
     */
    private final TethysButton<N, I> theDeleteButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pParent the parent
     */
    public PrometheusItemActions(final TethysGuiFactory<N, I> pFactory,
                                 final PrometheusItemEditParent pParent) {
        /* Record the parent */
        theParent = pParent;

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

        /* Create the panel */
        thePanel = pFactory.newVBoxPane();

        /* Create the layout */
        thePanel.addNode(theGoToButton);
        thePanel.addNode(theEditButton);
        thePanel.addNode(theDeleteButton);

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
    public N getNode() {
        return thePanel.getNode();
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public TethysEventRegistrar<PrometheusUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
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
