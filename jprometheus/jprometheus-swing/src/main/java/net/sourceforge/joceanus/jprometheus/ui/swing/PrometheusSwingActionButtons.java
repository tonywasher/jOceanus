/*******************************************************************************
[p[p * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Action buttons panel.
 */
public class PrometheusSwingActionButtons
        extends PrometheusActionButtons<JComponent, Icon> {
    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     */
    public PrometheusSwingActionButtons(final TethysSwingGuiFactory pFactory,
                                        final UpdateSet<?> pUpdateSet) {
        this(pFactory, pUpdateSet, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pHorizontal is this horizontal panel?
     */
    public PrometheusSwingActionButtons(final TethysSwingGuiFactory pFactory,
                                        final UpdateSet<?> pUpdateSet,
                                        final boolean pHorizontal) {
        /* Initialise base class */
        super(pFactory, pUpdateSet, pHorizontal);
    }
}
