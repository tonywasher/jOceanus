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
package net.sourceforge.joceanus.jtethys.ui.core.pane;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Core Grid Pane Manager.
 */
public abstract class TethysUICoreGridPaneManager
        extends TethysUICoreComponent
        implements TethysUIGridPaneManager {
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
    private final List<TethysUIComponent> theNodeList;

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
     * @param pFactory the factory
     */
    protected TethysUICoreGridPaneManager(final TethysUICoreFactory<?> pFactory) {
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

    @Override
    public Integer getHGap() {
        return theHGap;
    }

    @Override
    public Integer getVGap() {
        return theVGap;
    }

    @Override
    public void setHGap(final Integer pGap) {
        theHGap = pGap;
    }

    @Override
    public void setVGap(final Integer pGap) {
        theVGap = pGap;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysUIComponent myNode : theNodeList) {
            myNode.setEnabled(pEnabled);
        }
    }

    @Override
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

    @Override
    public void addCell(final TethysUIComponent pNode) {
        /* add the node */
        addCellAtPosition(pNode, theRowIndex, theColIndex);

        /* Shift to next column */
        useColumns(1);
    }

    @Override
    public void addCell(final TethysUIComponent pNode,
                        final int pNumCols) {
        /* add the node */
        addCellAtPosition(pNode, theRowIndex, theColIndex);

        /* Set standard options */
        setCellColumnSpan(pNode, pNumCols);

        /* Shift to next column */
        useColumns(pNumCols);
    }

    @Override
    public void addNode(final TethysUIComponent pNode) {
        theNodeList.add(pNode);
    }
}
