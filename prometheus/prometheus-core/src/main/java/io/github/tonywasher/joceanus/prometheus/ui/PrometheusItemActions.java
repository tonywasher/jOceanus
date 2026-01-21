/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.ui;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.metis.ui.MetisIcon;
import io.github.tonywasher.joceanus.prometheus.ui.PrometheusItemEditActions.PrometheusItemEditParent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * Item Action buttons.
 *
 * @param <G> the goto id type
 */
public class PrometheusItemActions<G extends Enum<G>>
        implements OceanusEventProvider<PrometheusUIEvent>, TethysUIComponent {
    /**
     * The Border padding.
     */
    protected static final int BORDER_PADDING = 5;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The parent.
     */
    private final PrometheusItemEditParent theParent;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The GoTo button.
     */
    private final TethysUIScrollButtonManager<TethysUIGenericWrapper> theGoToButton;

    /**
     * The Edit button.
     */
    private final TethysUIButton theEditButton;

    /**
     * The Delete button.
     */
    private final TethysUIButton theDeleteButton;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pParent  the parent
     */
    public PrometheusItemActions(final TethysUIFactory<?> pFactory,
                                 final PrometheusItemEditParent pParent) {
        /* Record the parent */
        theParent = pParent;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the buttons */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theGoToButton = myButtons.newScrollButton(TethysUIGenericWrapper.class);
        theEditButton = myButtons.newButton();
        theDeleteButton = myButtons.newButton();

        /* Configure the buttons */
        PrometheusIcon.configureGoToScrollButton(theGoToButton);
        MetisIcon.configureEditIconButton(theEditButton);
        MetisIcon.configureDeleteIconButton(theDeleteButton);

        /* Create the panel */
        thePanel = pFactory.paneFactory().newVBoxPane();
        thePanel.setBorderPadding(BORDER_PADDING);

        /* Create the layout */
        thePanel.addNode(theGoToButton);
        thePanel.addNode(theEditButton);
        thePanel.addNode(theDeleteButton);

        /* Add the listener for item changes */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theGoToButton.getEventRegistrar();
        theGoToButton.setMenuConfigurator(c -> theEventManager.fireEvent(PrometheusUIEvent.BUILDGOTO, c));
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> theEventManager.fireEvent(PrometheusUIEvent.GOTO, theGoToButton.getValue().getData()));
        theEditButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.EDIT));
        theDeleteButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.DELETE));

        /* Buttons are initially disabled */
        setEnabled(false);
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<PrometheusUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
