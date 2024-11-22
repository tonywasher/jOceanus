/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.ui;

import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.prometheus.views.PrometheusUIEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIPaneFactory;

/**
 * Action buttons.
 */
public class PrometheusActionButtons
        implements TethysEventProvider<PrometheusUIEvent>, TethysUIComponent {
    /**
     * Strut width.
     */
    protected static final int STRUT_LENGTH = 5;

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = PrometheusUIResource.ACTION_TITLE_SAVE.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The update set.
     */
    private final PrometheusEditSet theUpdateSet;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

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
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     */
    public PrometheusActionButtons(final TethysUIFactory<?> pFactory,
                                   final PrometheusEditSet pUpdateSet) {
        this(pFactory, pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public PrometheusActionButtons(final TethysUIFactory<?> pFactory,
                                   final PrometheusEditSet pUpdateSet,
                                   final boolean pHorizontal) {
        /* Record the update set */
        theUpdateSet = pUpdateSet;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the buttons */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theCommitButton = myButtons.newButton();
        theUndoButton = myButtons.newButton();
        theResetButton = myButtons.newButton();

        /* Configure the buttons */
        MetisIcon.configureCommitIconButton(theCommitButton);
        MetisIcon.configureUndoIconButton(theUndoButton);
        MetisIcon.configureResetIconButton(theResetButton);

        /* Add the listener for item changes */
        theCommitButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.OK));
        theUndoButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.UNDO));
        theResetButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.RESET));

        /* Create the panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        thePanel = pHorizontal
                ? myPanes.newHBoxPane()
                : myPanes.newVBoxPane();

        /* Define the layout */
        if (!pHorizontal) {
            thePanel.addNode(pFactory.controlFactory().newLabel(NLS_TITLE));
        }
        thePanel.addNode(theCommitButton);
        thePanel.addNode(theUndoButton);
        thePanel.addNode(theResetButton);

        /* Set border if required */
        if (pHorizontal) {
            thePanel.setBorderTitle(NLS_TITLE);
        }

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
            theCommitButton.setEnabled(false);
            theUndoButton.setEnabled(false);
            theResetButton.setEnabled(false);

            /* Else look at the edit state */
        } else {
            /* Switch on the edit state */
            switch (theUpdateSet.getEditState()) {
                case CLEAN:
                    theCommitButton.setEnabled(false);
                    theUndoButton.setEnabled(false);
                    theResetButton.setEnabled(theUpdateSet.hasUpdates());
                    break;
                case DIRTY:
                case ERROR:
                    theCommitButton.setEnabled(false);
                    theUndoButton.setEnabled(true);
                    theResetButton.setEnabled(true);
                    break;
                case VALID:
                    theCommitButton.setEnabled(true);
                    theUndoButton.setEnabled(true);
                    theResetButton.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    }
}
