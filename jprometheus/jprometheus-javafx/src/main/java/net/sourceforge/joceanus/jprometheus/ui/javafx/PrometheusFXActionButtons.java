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
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiUtils;

/**
 * Action buttons panel.
 * @author Tony Washer
 */
public class PrometheusFXActionButtons
        extends PrometheusActionButtons<Node, Node> {
    /**
     * The panel.
     */
    private final Pane thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     */
    public PrometheusFXActionButtons(final TethysFXGuiFactory pFactory,
                                     final UpdateSet<?> pUpdateSet) {
        this(pFactory, pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public PrometheusFXActionButtons(final TethysFXGuiFactory pFactory,
                                     final UpdateSet<?> pUpdateSet,
                                     final boolean pHorizontal) {
        /* Initialise base class */
        super(pFactory, pUpdateSet);

        /* Create the panel */
        Pane myPane = pHorizontal
                                  ? new HBox(STRUT_LENGTH)
                                  : new VBox(STRUT_LENGTH);

        /* Define the layout */
        List<Node> myChildren = myPane.getChildren();
        if (!pHorizontal) {
            myChildren.add(new Label(NLS_TITLE));
        }
        myChildren.add(getCommitButton().getNode());
        myChildren.add(getUndoButton().getNode());
        myChildren.add(getResetButton().getNode());

        /* Set the panel */
        thePanel = pHorizontal
                               ? TethysFXGuiUtils.getTitledPane(NLS_TITLE, myPane)
                               : myPane;
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
