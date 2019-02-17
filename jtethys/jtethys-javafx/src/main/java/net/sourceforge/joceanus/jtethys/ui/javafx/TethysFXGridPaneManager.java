/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;

/**
 * javaFX Grid Pane Manager.
 */
public class TethysFXGridPaneManager
        extends TethysGridPaneManager {
    /**
     * The Node.
     */
    private final TethysFXNode theNode;

    /**
     * The Pane.
     */
    private final GridPane theGridPane;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysFXGridPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theGridPane = new GridPane();
        theGridPane.setHgap(getHGap());
        theGridPane.setVgap(getVGap());
        theNode = new TethysFXNode(theGridPane);
    }

    @Override
    public TethysFXNode getNode() {
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
    public void addCellAtPosition(final TethysComponent pNode,
                                  final int pRow,
                                  final int pColumn) {
        /* Access the node */
        final Node myNode = TethysFXNode.getNode(pNode);

        /* add the node */
        theGridPane.add(myNode, pColumn, pRow);
        addNode(pNode);
    }

    @Override
    public void setCellColumnSpan(final TethysComponent pNode,
                                  final int pNumCols) {
        GridPane.setColumnSpan(TethysFXNode.getNode(pNode), pNumCols);
    }

    @Override
    public void setFinalCell(final TethysComponent pNode) {
        GridPane.setColumnSpan(TethysFXNode.getNode(pNode), GridPane.REMAINING);
    }

    @Override
    public void allowCellGrowth(final TethysComponent pNode) {
        GridPane.setHgrow(TethysFXNode.getNode(pNode), Priority.ALWAYS);
    }

    @Override
    public void setCellAlignment(final TethysComponent pNode,
                                 final TethysAlignment pAlign) {
        final Node myNode = TethysFXNode.getNode(pNode);
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
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    /**
     * Translate horizontal alignment.
     *
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private static HPos determineHAlignment(final TethysAlignment pAlign) {
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
     *
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private static VPos determineVAlignment(final TethysAlignment pAlign) {
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
