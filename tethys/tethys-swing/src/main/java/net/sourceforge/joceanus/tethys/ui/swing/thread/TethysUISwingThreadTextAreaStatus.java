/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.ui.swing.thread;

import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadManager;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadTextAreaStatus;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;

/**
 * JavaFX Thread TextArea Status Manager.
 */
public class TethysUISwingThreadTextAreaStatus
        extends TethysUICoreThreadTextAreaStatus {
    /**
     * Constructor.
     *
     * @param pManager the thread manager
     * @param pFactory the GUI factory
     */
    TethysUISwingThreadTextAreaStatus(final TethysUICoreThreadManager pManager,
                                      final TethysUICoreFactory<?> pFactory) {
        /* Initialise underlying class */
        super(pManager, pFactory);
    }

    @Override
    public TethysUISwingNode getNode() {
        return (TethysUISwingNode) super.getNode();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        getNode().setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        getNode().setPreferredHeight(pHeight);
    }
}
