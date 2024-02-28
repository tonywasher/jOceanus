/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.pane;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;

/**
 * Grid Pane Manager.
 */
public interface TethysUIGridPaneManager
        extends TethysUIComponent {
    /**
     * Obtain the Horizontal Grid Gap.
     * @return the GridGap.
     */
    Integer getHGap();

    /**
     * Obtain the Vertical Grid Gap.
     * @return the GridGap.
     */
    Integer getVGap();

    /**
     * Set the Horizontal Grid Gap.
     * @param pGap the GridGap.
     */
    void setHGap(Integer pGap);

    /**
     * Set the Vertical Grid Gap.
     * @param pGap the GridGap.
     */
    void setVGap(Integer pGap);

    /**
     * Adjust to next row.
     */
    void newRow();

    /**
     * Add cell at current column and increment column #.
     * @param pNode the node to add
     */
    void addCell(TethysUIComponent pNode);

    /**
     * Add cell that spans a number of columns at current column and adjust column #.
     * @param pNode the node to add
     * @param pNumCols the number of columns to span
     */
    void addCell(TethysUIComponent pNode,
                 int pNumCols);

    /**
     * Add cell at position.
     * @param pNode the node to add
     * @param pRow the row to add the cell at
     * @param pColumn the column to add the cell at
     */
    void addCellAtPosition(TethysUIComponent pNode,
                           int pRow,
                           int pColumn);

    /**
     * Set cell column span.
     * @param pNode the node to set column span on
     * @param pNumCols the number of columns to span
     */
    void setCellColumnSpan(TethysUIComponent pNode,
                           int pNumCols);

    /**
     * Set final cell.
     * @param pNode the node to set as final cell in row
     */
    void setFinalCell(TethysUIComponent pNode);

    /**
     * Allow Cell as growth.
     * @param pNode the node to allow growth on
     */
    void allowCellGrowth(TethysUIComponent pNode);

    /**
     * Set cell alignment.
     * @param pNode the node to align
     * @param pAlign the cell alignment
     */
    void setCellAlignment(TethysUIComponent pNode,
                          TethysUIAlignment pAlign);

    /**
     * Add simple Node.
     * @param pNode the node to add
     */
    void addNode(TethysUIComponent pNode);
}
