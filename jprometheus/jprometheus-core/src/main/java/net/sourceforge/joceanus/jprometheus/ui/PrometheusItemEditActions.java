/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.ui;

import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jprometheus.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

/**
 * Item Edit Action buttons.
 */
public class PrometheusItemEditActions
        implements TethysEventProvider<PrometheusUIEvent>, TethysUIComponent {
    /**
     * ItemEditParent interface.
     */
    public interface PrometheusItemEditParent {
        /**
         * Is the item editable?
         * @return true/false
         */
        boolean isEditable();

        /**
         * Is the item deletable?
         * @return true/false
         */
        boolean isDeletable();

        /**
         * Does the parent have updates.
         * @return true/false
         */
        boolean hasUpdates();

        /**
         * Does the parent have errors.
         * @return true/false
         */
        boolean hasErrors();

        /**
         * Is the parent new.
         * @return true/false
         */
        boolean isNew();
    }

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The parent.
     */
    private final PrometheusItemEditParent theParent;

    /**
     * The Commit button.
     */
    private final TethysUIButton theCommitButton;

    /**
     * The Undo button.
     */
    private final TethysUIButton theUndoButton;

    /**
     * The Reset button.
     */
    private final TethysUIButton theResetButton;

    /**
     * The Cancel button.
     */
    private final TethysUIButton theCancelButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pParent the parent
     */
    public PrometheusItemEditActions(final TethysUIFactory<?> pFactory,
                                     final PrometheusItemEditParent pParent) {
        /* Record the parent */
        theParent = pParent;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the buttons */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theCommitButton = myButtons.newButton();
        theUndoButton = myButtons.newButton();
        theResetButton = myButtons.newButton();
        theCancelButton = myButtons.newButton();

        /* Configure the buttons */
        MetisIcon.configureCommitIconButton(theCommitButton);
        MetisIcon.configureUndoIconButton(theUndoButton);
        MetisIcon.configureResetIconButton(theResetButton);
        MetisIcon.configureCancelIconButton(theCancelButton);

        /* Create the panel */
        thePanel = pFactory.paneFactory().newVBoxPane();
        thePanel.setBorderPadding(PrometheusItemActions.BORDER_PADDING);

        /* Create the layout */
        thePanel.addNode(theCommitButton);
        thePanel.addNode(theUndoButton);
        thePanel.addNode(theResetButton);
        thePanel.addNode(theCancelButton);

        /* Add the listener for item changes */
        theCommitButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.OK));
        theUndoButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.UNDO));
        theResetButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.RESET));
        theCancelButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.CANCEL));

        /* Buttons are initially disabled */
        setEnabled(false);
    }

    @Override
    public TethysEventRegistrar<PrometheusUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* If the table is locked clear the buttons */
        if (!bEnabled) {
            theCommitButton.setEnabled(false);
            theUndoButton.setEnabled(false);
            theResetButton.setEnabled(false);
            theCancelButton.setEnabled(false);

            /* Else look at the edit state */
        } else {
            /* Determine whether we have changes */
            boolean hasUpdates = theParent.hasUpdates();
            theUndoButton.setEnabled(hasUpdates);
            theResetButton.setEnabled(hasUpdates);

            /* Check for no errors */
            final boolean hasErrors = theParent.hasErrors();
            hasUpdates |= theParent.isNew();
            theCommitButton.setEnabled(hasUpdates && !hasErrors);

            /* Enable the cancel button */
            theCancelButton.setEnabled(true);
        }
    }
}
