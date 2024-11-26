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
package net.sourceforge.joceanus.tethys.ui.javafx.button;

import javafx.scene.layout.Region;

import net.sourceforge.joceanus.tethys.ui.core.button.TethysUICoreDateRangeSelector;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.tethys.ui.javafx.pane.TethysUIFXBoxPaneManager;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 */
public class TethysUIFXDateRangeSelector
        extends TethysUICoreDateRangeSelector {
    /**
     * Minimum width.
     */
    private static final int MIN_WIDTH = 440;

    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The Pane.
     */
    private final Region thePane;

    /**
     * Constructor.
     *
     * @param pFactory     the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    TethysUIFXDateRangeSelector(final TethysUICoreFactory<?> pFactory,
                                final boolean pBaseIsStart) {
        /* Initialise the underlying class */
        super(pFactory, pBaseIsStart);

        /* Record the Node and Create minimum width for panel */
        thePane = (Region) TethysUIFXNode.getNode(getControl());
        thePane.setMinWidth(MIN_WIDTH);

        /* Craeet the node */
        theNode = new TethysUIFXNode(thePane);

        /* Create the full sub-panel */
        applyState();
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    /**
     * Obtain the control.
     *
     * @return the control
     */
    @Override
    protected TethysUIFXBoxPaneManager getControl() {
        return (TethysUIFXBoxPaneManager) super.getControl();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public boolean isVisible() {
        return theNode.isVisible();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        thePane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        thePane.setPrefHeight(pHeight);
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
}