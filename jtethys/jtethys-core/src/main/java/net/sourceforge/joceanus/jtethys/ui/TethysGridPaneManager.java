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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid Pane Manager.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public abstract class TethysGridPaneManager<N, I>
        implements TethysNode<N> {
    /**
     * Grid Gap default.
     */
    private static final int GRID_GAP = 4;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Node List.
     */
    private final List<TethysNode<N>> theNodeList;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The Horizontal Gap.
     */
    private Integer theHGap;

    /**
     * The Vertical Gap.
     */
    private Integer theVGap;

    /**
     * The row id.
     */
    private Integer theRowIndex;

    /**
     * The next id.
     */
    private Integer theColIndex;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysGridPaneManager(final TethysGuiFactory<N, I> pFactory) {
        /* Initialise values */
        theId = pFactory.getNextId();
        theNodeList = new ArrayList<>();
        theRowIndex = 0;
        theColIndex = 0;
        theHGap = GRID_GAP;
        theVGap = GRID_GAP;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain the Horizontal Grid Gap.
     * @return the GridGap.
     */
    protected Integer getHGap() {
        return theHGap;
    }

    /**
     * Obtain the Vertical Grid Gap.
     * @return the GridGap.
     */
    protected Integer getVGap() {
        return theVGap;
    }

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Horizontal Grid Gap.
     * @param pGap the GridGap.
     */
    public void setHGap(final Integer pGap) {
        theHGap = pGap;
    }

    /**
     * Set the Vertical Grid Gap.
     * @param pGap the GridGap.
     */
    public void setVGap(final Integer pGap) {
        theVGap = pGap;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysNode<N> myNode : theNodeList) {
            myNode.setEnabled(pEnabled);
        }
    }

    /**
     * Adjust to next row.
     */
    public void newRow() {
        theRowIndex = theRowIndex + 1;
        theColIndex = 0;
    }

    /**
     * Use columns.
     * @param pCols the number of columns that have been filled.
     */
    private void useColumns(final int pCols) {
        theColIndex = theColIndex + pCols;
    }

    /**
     * Add cell at current column and increment column #.
     * @param pNode the node to add
     */
    public void addCell(final TethysNode<N> pNode) {
        /* add the node */
        addCellAtPosition(pNode, theRowIndex, theColIndex);

        /* Shift to next column */
        useColumns(1);
    }

    /**
     * Add cell that spans a number of columns at current column and adjust column #.
     * @param pNode the node to add
     * @param pNumCols the number of columns to span
     */
    public void addCell(final TethysNode<N> pNode,
                        final int pNumCols) {
        /* add the node */
        addCellAtPosition(pNode, theRowIndex, theColIndex);

        /* Set standard options */
        setCellColumnSpan(pNode, pNumCols);

        /* Shift to next column */
        useColumns(pNumCols);
    }

    /**
     * Add cell at position.
     * @param pNode the node to add
     * @param pRow the row to add the cell at
     * @param pColumn the column to add the cell at
     */
    public abstract void addCellAtPosition(TethysNode<N> pNode,
                                           int pRow,
                                           int pColumn);

    /**
     * Set cell column span.
     * @param pNode the node to set column span on
     * @param pNumCols the number of columns to span
     */
    public abstract void setCellColumnSpan(TethysNode<N> pNode,
                                           int pNumCols);

    /**
     * Set final cell.
     * @param pNode the node to set as final cell in row
     */
    public abstract void setFinalCell(TethysNode<N> pNode);

    /**
     * Allow Cell as growth.
     * @param pNode the node to allow growth on
     */
    public abstract void allowCellGrowth(TethysNode<N> pNode);

    /**
     * Set cell alignment.
     * @param pNode the node to align
     * @param pAlign the cell alignment
     */
    public abstract void setCellAlignment(TethysNode<N> pNode,
                                          TethysAlignment pAlign);

    /**
     * Add simple Node.
     * @param pNode the node to add
     */
    protected void addNode(final TethysNode<N> pNode) {
        theNodeList.add(pNode);
    }
}
