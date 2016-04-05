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

import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Action buttons.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class PrometheusActionButtons<N, I>
        implements TethysEventProvider<PrometheusUIEvent>, TethysNode<N> {
    /**
     * Strut width.
     */
    protected static final int STRUT_LENGTH = 5;

    /**
     * Text for Box Title.
     */
    protected static final String NLS_TITLE = PrometheusUIResource.ACTION_TITLE_SAVE.getValue();

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The update set.
     */
    private final UpdateSet<?> theUpdateSet;

    /**
     * The Commit button.
     */
    private final TethysButton<N, I> theCommitButton;

    /**
     * The Undo button.
     */
    private final TethysButton<N, I> theUndoButton;

    /**
     * The Reset button.
     */
    private final TethysButton<N, I> theResetButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     */
    protected PrometheusActionButtons(final TethysGuiFactory<N, I> pFactory,
                                      final UpdateSet<?> pUpdateSet) {
        /* Record the update set */
        theUpdateSet = pUpdateSet;

        /* Record the id */
        theId = pFactory.getNextId();

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the buttons */
        theCommitButton = pFactory.newButton();
        theUndoButton = pFactory.newButton();
        theResetButton = pFactory.newButton();

        /* Configure the buttons */
        PrometheusIcon.configureCommitIconButton(theCommitButton);
        PrometheusIcon.configureUndoIconButton(theUndoButton);
        PrometheusIcon.configureResetIconButton(theResetButton);

        /* Add the listener for item changes */
        theCommitButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.OK));
        theUndoButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.UNDO));
        theResetButton.getEventRegistrar().addEventListener(e -> theEventManager.fireEvent(PrometheusUIEvent.RESET));

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
     * Obtain the commit button.
     * @return the button
     */
    protected TethysButton<N, I> getCommitButton() {
        return theCommitButton;
    }

    /**
     * Obtain the undo button.
     * @return the button
     */
    protected TethysButton<N, I> getUndoButton() {
        return theUndoButton;
    }

    /**
     * Obtain the reset button.
     * @return the button
     */
    protected TethysButton<N, I> getResetButton() {
        return theResetButton;
    }

    /**
     * Set enabled.
     * @param bEnabled the enabled status
     */
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
