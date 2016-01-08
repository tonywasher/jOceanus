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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.DefaultIconButtonState;

/**
 * Action buttons panel.
 * @author Tony Washer
 */
public class ActionButtons
        implements TethysEventProvider<PrometheusUIEvent> {
    /**
     * Strut width.
     */
    private static final int STRUT_LENGTH = 5;

    /**
     * Text for Box Title.
     */
    private static final String NLS_TITLE = PrometheusUIResource.ACTION_TITLE_SAVE.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusUIEvent> theEventManager;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The update set.
     */
    private final UpdateSet<?> theUpdateSet;

    /**
     * The Commit button.
     */
    private final JIconButton<Boolean> theCommitButton;

    /**
     * The Undo button.
     */
    private final JIconButton<Boolean> theUndoButton;

    /**
     * The Reset button.
     */
    private final JIconButton<Boolean> theResetButton;

    /**
     * Constructor.
     * @param pUpdateSet the update set
     */
    public ActionButtons(final UpdateSet<?> pUpdateSet) {
        this(pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public ActionButtons(final UpdateSet<?> pUpdateSet,
                         final boolean pHorizontal) {
        /* Record the update set */
        theUpdateSet = pUpdateSet;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the button states */
        DefaultIconButtonState<Boolean> myCommitState = new DefaultIconButtonState<>();
        DefaultIconButtonState<Boolean> myUndoState = new DefaultIconButtonState<>();
        DefaultIconButtonState<Boolean> myResetState = new DefaultIconButtonState<>();

        /* Create the buttons */
        theCommitButton = new JIconButton<>(myCommitState);
        theUndoButton = new JIconButton<>(myUndoState);
        theResetButton = new JIconButton<>(myResetState);

        /* Make buttons the size of the icons */
        Insets myInsets = new Insets(0, 0, 0, 0);
        theCommitButton.setMargin(myInsets);
        theUndoButton.setMargin(myInsets);
        theResetButton.setMargin(myInsets);

        /* Set the states */
        PrometheusIcons.buildCommitButton(myCommitState);
        PrometheusIcons.buildUndoButton(myUndoState);
        PrometheusIcons.buildResetButton(myResetState);

        /* Create the panel */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, pHorizontal
                                                               ? BoxLayout.X_AXIS
                                                               : BoxLayout.Y_AXIS));

        /* Create the title */
        if (pHorizontal) {
            thePanel.setBorder(BorderFactory.createTitledBorder(NLS_TITLE));
        } else {
            thePanel.add(new JLabel(NLS_TITLE));
        }

        /* Create the standard strut */
        Dimension myStrutSize = pHorizontal
                                            ? new Dimension(STRUT_LENGTH, 0)
                                            : new Dimension(0, STRUT_LENGTH);

        /* Define the layout */
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(theCommitButton);
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(theUndoButton);
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(theResetButton);
        thePanel.add(Box.createRigidArea(myStrutSize));

        /* Add the listener for item changes */
        theCommitButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, e -> theEventManager.fireEvent(PrometheusUIEvent.OK));
        theUndoButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, e -> theEventManager.fireEvent(PrometheusUIEvent.UNDO));
        theResetButton.addPropertyChangeListener(JIconButton.PROPERTY_VALUE, e -> theEventManager.fireEvent(PrometheusUIEvent.RESET));

        /* Buttons are initially disabled */
        theCommitButton.setEnabled(false);
        theUndoButton.setEnabled(false);
        theResetButton.setEnabled(false);
    }

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public TethysEventRegistrar<PrometheusUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set visibility.
     * @param pVisible true/false
     */
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
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
            /* Set the values */
            theCommitButton.storeValue(Boolean.TRUE);
            theUndoButton.storeValue(Boolean.TRUE);
            theResetButton.storeValue(Boolean.TRUE);
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
