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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Action buttons panel.
 * @author Tony Washer
 */
public class PrometheusSwingActionButtons
        extends PrometheusActionButtons<JComponent, Icon> {
    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     */
    public PrometheusSwingActionButtons(final TethysSwingGuiFactory pFactory,
                                        final UpdateSet<?> pUpdateSet) {
        this(pFactory, pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public PrometheusSwingActionButtons(final TethysSwingGuiFactory pFactory,
                                        final UpdateSet<?> pUpdateSet,
                                        final boolean pHorizontal) {
        /* Initialise base class */
        super(pFactory, pUpdateSet);

        /* Create the panel */
        thePanel = new TethysSwingEnablePanel();
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
        thePanel.add(getCommitButton().getNode());
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(getUndoButton().getNode());
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(getResetButton().getNode());
        thePanel.add(Box.createRigidArea(myStrutSize));
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }
}
