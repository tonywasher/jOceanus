/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
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
    private Region theNode;

    /**
     * The Pane.
     */
    private final GridPane theGridPane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXGridPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theGridPane = new GridPane();
        theGridPane.setHgap(getHGap());
        theGridPane.setVgap(getVGap());
        theNode = theGridPane;
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setHGap(final Integer pGap) {
        super.setHGap(pGap);
        theGridPane.setHgap(getHGap());
    }

    @Override
    public void setVGap(final Integer pGap) {
        super.setVGap(pGap);
        theGridPane.setVgap(getVGap());
    }

    @Override
    public void addCellAtPosition(final TethysNode<Node> pNode,
                                  final int pRow,
                                  final int pColumn) {
        /* Access the node */
        final Node myNode = pNode.getNode();

        /* add the node */
        theGridPane.add(myNode, pColumn, pRow);
        addNode(pNode);
    }

    @Override
    public void setCellColumnSpan(final TethysNode<Node> pNode,
                                  final int pNumCols) {
        GridPane.setColumnSpan(pNode.getNode(), pNumCols);
    }

    @Override
    public void setFinalCell(final TethysNode<Node> pNode) {
        GridPane.setColumnSpan(pNode.getNode(), GridPane.REMAINING);
    }

    @Override
    public void allowCellGrowth(final TethysNode<Node> pNode) {
        GridPane.setHgrow(pNode.getNode(), Priority.ALWAYS);
    }

    @Override
    public void setCellAlignment(final TethysNode<Node> pNode,
                                 final TethysAlignment pAlign) {
        final Node myNode = pNode.getNode();
        GridPane.setFillWidth(myNode, false);
        GridPane.setHalignment(myNode, determineHAlignment(pAlign));
        GridPane.setValignment(myNode, determineVAlignment(pAlign));
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theGridPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theGridPane.setPrefHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        createWrapperPane();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        createWrapperPane();
    }

    /**
     * create wrapper pane.
     */
    private void createWrapperPane() {
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theGridPane);
    }

    /**
     * Translate horizontal alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private HPos determineHAlignment(final TethysAlignment pAlign) {
        switch (pAlign) {
            case NORTHEAST:
            case EAST:
            case SOUTHEAST:
                return HPos.RIGHT;
            case NORTH:
            case CENTRE:
            case SOUTH:
                return HPos.CENTER;
            case NORTHWEST:
            case WEST:
            case SOUTHWEST:
            default:
                return HPos.LEFT;
        }
    }

    /**
     * Translate vertical alignment.
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private VPos determineVAlignment(final TethysAlignment pAlign) {
        switch (pAlign) {
            case NORTHEAST:
            case NORTH:
            case NORTHWEST:
                return VPos.TOP;
            case WEST:
            case CENTRE:
            case EAST:
                return VPos.CENTER;
            case SOUTHWEST:
            case SOUTH:
            case SOUTHEAST:
            default:
                return VPos.BOTTOM;
        }
    }
}
