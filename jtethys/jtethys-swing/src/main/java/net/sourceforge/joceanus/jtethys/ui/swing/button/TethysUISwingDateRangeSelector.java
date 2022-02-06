/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.button;

import net.sourceforge.joceanus.jtethys.ui.core.button.TethysUICoreDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 */
public class TethysUISwingDateRangeSelector
        extends TethysUICoreDateRangeSelector {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    TethysUISwingDateRangeSelector(final TethysUICoreFactory<?> pFactory,
                                   final boolean pBaseIsStart) {
        /* Initialise the underlying class */
        super(pFactory, pBaseIsStart);

        /* Create the full sub-panel */
        applyState();
    }

    @Override
    public TethysUISwingNode getNode() {
        return (TethysUISwingNode) getControl().getNode();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        getNode().createWrapperPane(getBorderTitle(), getBorderPadding());
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
