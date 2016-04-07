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
     * Inset depth.
     */
    protected static final int INSET_DEPTH = 5;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Node List.
     */
    private final List<TethysNode<N>> theNodeList;

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
        theId = pFactory.getNextId();
        theNodeList = new ArrayList<>();
        theRowIndex = 0;
        theColIndex = 0;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Set the Border Title.
     * @param pTitle the title text
     */
    public abstract void setBorderTitle(final String pTitle);

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysNode<N> myNode : theNodeList) {
            myNode.setEnabled(pEnabled);
        }
    }

    /**
     * Shift to next row.
     */
    public void newRow() {
        theRowIndex = theRowIndex + 1;
        theColIndex = 0;
    }

    /**
     * Use columns.
     * @param pCols the number of columns that have been filled.
     */
    protected void useColumns(final int pCols) {
        theColIndex = theColIndex + pCols;
    }

    /**
     * Add cell.
     * @param pNode the node to add
     */
    public abstract void addSingleCell(final TethysNode<N> pNode);

    /**
     * Add final cell for row
     * @param pNode the node to add
     */
    public abstract void addFinalCell(final TethysNode<N> pNode);

    /**
     * Add simple Node
     * @param pNode the node to add
     */
    protected void addNode(final TethysNode<N> pNode) {
        theNodeList.add(pNode);
    }

    /**
     * Obtain the row Id.
     * @return the row Id.
     */
    protected Integer getRowIndex() {
        return theRowIndex;
    }

    /**
     * Obtain the column Id.
     * @return the column Id.
     */
    protected Integer getColumnIndex() {
        return theColIndex;
    }
}
