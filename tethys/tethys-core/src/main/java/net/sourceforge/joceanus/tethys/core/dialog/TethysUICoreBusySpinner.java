/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.core.dialog;

import net.sourceforge.joceanus.tethys.api.base.TethysUINode;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIBusySpinner;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreIcon;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * About Box.
 */
public abstract class TethysUICoreBusySpinner
        extends TethysUICoreComponent
        implements TethysUIBusySpinner {
    /**
     * Spinner size.
     */
    protected static final int SPINNER_SIZE = 70;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreBusySpinner(final TethysUICoreFactory<?> pFactory) {
        /* Create a new label for the spinner */
        final TethysUILabel mySpinner = pFactory.controlFactory().newLabel();
        mySpinner.setIconOnly();
        mySpinner.setIconSize(SPINNER_SIZE);
        mySpinner.setIcon(TethysUICoreIcon.DYNAMICSPINNER);

        /* Layout the panel */
        thePanel = pFactory.paneFactory().newVBoxPane();
        thePanel.addNode(mySpinner);
    }

    @Override
    public TethysUINode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }
}
