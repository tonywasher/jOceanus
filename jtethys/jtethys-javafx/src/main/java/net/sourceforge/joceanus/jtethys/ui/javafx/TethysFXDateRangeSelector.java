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

import javafx.scene.Node;
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 */
public class TethysFXDateRangeSelector
        extends TethysDateRangeSelector<Node, Node> {
    /**
     * Minimum width.
     */
    private static final int MIN_WIDTH = 440;

    /**
     * The Node.
     */
    private Region theNode;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    protected TethysFXDateRangeSelector(final TethysFXGuiFactory pFactory,
                                        final boolean pBaseIsStart) {
        /* Initialise the underlying class */
        super(pFactory, pBaseIsStart);

        /* Set the arrow icons */
        getNextButton().setIcon(TethysFXArrowIcon.RIGHT.getArrow());
        getPrevButton().setIcon(TethysFXArrowIcon.LEFT.getArrow());

        /* Record the Node and Create minimum width for panel */
        theNode = getControl().getNode();
        theNode.setMinWidth(MIN_WIDTH);

        /* Create the full sub-panel */
        applyState();
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    /**
     * Obtain the control.
     * @return the control
     */
    @Override
    protected TethysFXBoxPaneManager getControl() {
        return (TethysFXBoxPaneManager) super.getControl();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public boolean isVisible() {
        return theNode.isVisible();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPrefHeight(pHeight);
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
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), getControl().getNode());
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Pass call on to node */
        theNode.setDisable(!pEnable);

        /* If we are enabling */
        if (pEnable) {
            /* Ensure correct values */
            applyState();
        }
    }
}
