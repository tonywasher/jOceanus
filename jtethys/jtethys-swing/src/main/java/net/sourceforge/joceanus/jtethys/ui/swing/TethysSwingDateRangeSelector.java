/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 */
public class TethysSwingDateRangeSelector
        extends TethysDateRangeSelector<JComponent, Icon> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysSwingDateRangeSelector(final TethysSwingGuiFactory pFactory,
                                        final boolean pBaseIsStart) {
        /* Initialise the underlying class */
        super(pFactory, pBaseIsStart);

        /* Create the full sub-panel */
        applyState();
    }

    @Override
    public JComponent getNode() {
        return getControl().getNode();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        JComponent myNode = getNode();
        Dimension myDim = myNode.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        myNode.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        JComponent myNode = getNode();
        Dimension myDim = myNode.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        myNode.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), getNode());
    }

    @Override
    public void setVisible(final boolean pVisible) {
        getNode().setVisible(pVisible);
    }

    @Override
    public boolean isVisible() {
        return getNode().isVisible();
    }
}
