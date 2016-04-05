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
package net.sourceforge.joceanus.jprometheus.ui.javafx;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusItemEditActions;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Utility panel to handle actions on selected item.
 */
public class PrometheusFXItemEditActions
        extends PrometheusItemEditActions<Node, Node> {
    /**
     * The panel.
     */
    private final Pane thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pParent the parent panel
     */
    protected PrometheusFXItemEditActions(final TethysFXGuiFactory pFactory,
                                          final PrometheusItemEditParent pParent) {
        /* Initialise base class */
        super(pFactory, pParent);

        /* Create the panel */
        thePanel = new VBox(STRUT_HEIGHT);

        /* Create the layout */
        List<Node> myChildren = thePanel.getChildren();
        myChildren.add(getCommitButton().getNode());
        myChildren.add(getUndoButton().getNode());
        myChildren.add(getResetButton().getNode());
        myChildren.add(getCancelButton().getNode());
    }

    @Override
    public Node getNode() {
        return thePanel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }
}
