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
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Action buttons.
 */
public class PrometheusActionButtons
        implements TethysEventProvider<PrometheusUIEvent>, TethysComponent {
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
    private final TethysEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The update set.
     */
    private final UpdateSet<?> theUpdateSet;

    /**
     * The panel.
     */
    private final TethysBoxPaneManager thePanel;

    /**
     * The Commit button.
     */
    private final TethysButton theCommitButton;

    /**
     * The Undo button.
     */
    private final TethysButton theUndoButton;

    /**
     * The Reset button.
     */
    private final TethysButton theResetButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     */
    public PrometheusActionButtons(final TethysGuiFactory pFactory,
                                   final UpdateSet<?> pUpdateSet) {
        this(pFactory, pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public PrometheusActionButtons(final TethysGuiFactory pFactory,
                                   final UpdateSet<?> pUpdateSet,
                                   final boolean pHorizontal) {
        /* Record the update set */
        theUpdateSet = pUpdateSet;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the buttons */
        theCommitButton = pFactory.newButton();
        theUndoButton = pFactory.newButton();
        theResetButton = pFactory.newButton();

        /* Configure the buttons */
        MetisIcon.configureCommitIconButton(theCommitButton);
        MetisIcon.configureUndoIconButton(theUndoButton);
        MetisIcon.configureResetIconButton(theResetButton);

        /* Add the listener for item changes */
        theCommitButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.OK));
        theUndoButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.UNDO));
        theResetButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.RESET));

        /* Create the panel */
        thePanel = pHorizontal
                               ? pFactory.newHBoxPane()
                               : pFactory.newVBoxPane();

        /* Define the layout */
        if (!pHorizontal) {
            thePanel.addNode(pFactory.newLabel(NLS_TITLE));
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
