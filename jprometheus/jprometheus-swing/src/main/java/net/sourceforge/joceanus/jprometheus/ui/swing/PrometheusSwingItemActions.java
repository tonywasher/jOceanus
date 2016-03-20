/*******************************************************************************
 * jMoneyWise: Finance Application
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusItemActions;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusItemEditActions.PrometheusItemEditParent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Utility panel to handle actions on selected item.
 */
public class PrometheusSwingItemActions
        extends PrometheusItemActions<JComponent> {
    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * Constructor.
     * @param pParent the parent panel
     */
    protected PrometheusSwingItemActions(final PrometheusItemEditParent pParent) {
        /* Initialise base class */
        super(pParent);

        /* Create the buttons */
        TethysSwingScrollButtonManager<PrometheusGoToEvent> myGoToButton = new TethysSwingScrollButtonManager<>();
        TethysSwingSimpleIconButtonManager<Boolean> myEditButton = new TethysSwingSimpleIconButtonManager<>();
        TethysSwingSimpleIconButtonManager<Boolean> myDeleteButton = new TethysSwingSimpleIconButtonManager<>();

        /* declare the buttons */
        declareButtons(myGoToButton, myEditButton, myDeleteButton);

        /* Create the standard strut */
        Dimension myStrutSize = new Dimension(0, STRUT_HEIGHT);

        /* Create the panel */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));

        /* Create the layout */
        thePanel.add(myGoToButton.getNode());
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(myEditButton.getNode());
        thePanel.add(Box.createRigidArea(myStrutSize));
        thePanel.add(myDeleteButton.getNode());
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
