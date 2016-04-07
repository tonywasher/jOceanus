/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * javaFX Grid Pane Manager.
 */
public class TethysFXGridPaneManager
        extends TethysGridPaneManager<Node, Node> {
    /**
     * The Node.
     */
    private Node theNode;

    /**
     * The Pane.
     */
    private final GridPane thePane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXGridPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        thePane = new GridPane();
        theNode = thePane;
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void addSingleCell(final TethysNode<Node> pNode) {
        /* Access the node */
        Node myNode = pNode.getNode();

        /* Set standard options */
        GridPane.setFillWidth(myNode, true);
        GridPane.setMargin(myNode, new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH));

        /* add the node */
        thePane.add(myNode, getColumnIndex(), getRowIndex());
        addNode(pNode);

        /* Shift to next column */
        useColumns(1);
    }

    @Override
    public void addFinalCell(final TethysNode<Node> pNode) {
        /* Access the node */
        Node myNode = pNode.getNode();

        /* Set to fill remaining row and expand horizontally */
        GridPane.setColumnSpan(myNode, GridPane.REMAINING);
        GridPane.setFillWidth(myNode, true);
        GridPane.setHgrow(myNode, Priority.ALWAYS);
        GridPane.setMargin(myNode, new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH));

        /* add the node */
        thePane.add(myNode, getColumnIndex(), getRowIndex());
        addNode(pNode);

        /* Shift to next row */
        newRow();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        theNode = TethysFXGuiUtils.getTitledPane(pTitle, thePane);
    }
}
