/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.ui;

import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusItemEditActions.PrometheusItemEditParent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Item Action buttons.
 * @param <G> the goto id type
 */
public class PrometheusItemActions<G extends Enum<G>>
        implements TethysEventProvider<PrometheusUIEvent>, TethysComponent {
    /**
     * The Border padding.
     */
    protected static final int BORDER_PADDING = 5;

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
    private final TethysBoxPaneManager thePanel;

    /**
     * The GoTo button.
     */
    private final TethysScrollButtonManager<PrometheusGoToEvent<G>> theGoToButton;

    /**
     * The Edit button.
     */
    private final TethysButton theEditButton;

    /**
     * The Delete button.
     */
    private final TethysButton theDeleteButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pParent the parent
     */
    public PrometheusItemActions(final TethysGuiFactory pFactory,
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
        MetisIcon.configureEditIconButton(theEditButton);
        MetisIcon.configureDeleteIconButton(theDeleteButton);

        /* Create the panel */
        thePanel = pFactory.newVBoxPane();
        thePanel.setBorderPadding(BORDER_PADDING);

        /* Create the layout */
        thePanel.addNode(theGoToButton);
        thePanel.addNode(theEditButton);
        thePanel.addNode(theDeleteButton);

        /* Add the listener for item changes */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theGoToButton.getEventRegistrar();
        theGoToButton.setMenuConfigurator(c -> theEventManager.fireEvent(PrometheusUIEvent.BUILDGOTO, c));
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> theEventManager.fireEvent(PrometheusUIEvent.GOTO, theGoToButton.getValue()));
        theEditButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.EDIT));
        theDeleteButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.DELETE));

        /* Buttons are initially disabled */
        setEnabled(false);
    }

    @Override
    public TethysNode getNode() {
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
