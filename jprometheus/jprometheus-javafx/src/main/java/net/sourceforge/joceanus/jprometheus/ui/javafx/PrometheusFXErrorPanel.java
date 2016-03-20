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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusErrorPanel;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;

/**
 * Error panel.
 * @author Tony Washer
 */
public class PrometheusFXErrorPanel
        extends PrometheusErrorPanel<Node> {
    /**
     * The Panel.
     */
    private final Pane thePanel;

    /**
     * The error field.
     */
    private final Label theErrorField;

    /**
     * The clear button.
     */
    private final Button theClearButton;

    /**
     * Constructor.
     * @param pManager the data manager
     * @param pParent the parent data entry
     */
    public PrometheusFXErrorPanel(final MetisViewerManager pManager,
                                  final MetisViewerEntry pParent) {
        /* Initialise underlying class */
        super(pManager, pParent);

        /* Create the error field */
        theErrorField = new Label();

        /* Create the clear button */
        theClearButton = new Button(NLS_CLEAR);

        /* Add the listener for item changes */
        theClearButton.setOnAction(e -> clearErrors());

        /* Create the error panel */
        Pane myPane = new HBox(STRUT_WIDTH);
        List<Node> myChildren = myPane.getChildren();
        thePanel = TethysFXGuiUtils.getTitledPane(NLS_TITLE, myPane);

        /* Define the layout */
        myChildren.add(theClearButton);
        myChildren.add(theErrorField);

        /* Set the Error panel to be red and invisible */
        theErrorField.setTextFill(Color.RED);
        thePanel.setVisible(false);
    }

    @Override
    public Node getNode() {
        return thePanel;
    }

    @Override
    protected void setErrorText(final String pText) {
        /* Set the string for the error field */
        theErrorField.setText(pText);

        /* Make the panel visible */
        thePanel.setVisible(true);
    }

    @Override
    public void setVisible(final boolean bVisible) {
        thePanel.setVisible(bVisible);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        theClearButton.setDisable(!bEnabled);
    }
}
