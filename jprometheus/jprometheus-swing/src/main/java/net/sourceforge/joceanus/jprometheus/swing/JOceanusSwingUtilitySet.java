/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldConfig;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * JOceanus Swing Utility Set.
 */
public class JOceanusSwingUtilitySet
        extends JOceanusUtilitySet<JComponent, Icon> {
    /**
     * Viewer Manager.
     */
    private final MetisSwingViewerManager theViewerManager;

    /**
     * Field Manager.
     */
    private final MetisFieldManager theFieldManager;

    /**
     * Constructor.
     * @param pParameters the security parameters
     * @param pPrefMgr the preference manager
     * @throws OceanusException on error
     */
    public JOceanusSwingUtilitySet() throws OceanusException {
        /* Create Toolkit */
        super(new MetisSwingToolkit());

        /* Allocate the FieldManager */
        theFieldManager = new MetisFieldManager(new MetisFieldConfig(getColorPreferences()));

        /* Create components */
        theViewerManager = new MetisSwingViewerManager(theFieldManager);

        /* Process the colour preferences */
        processColorPreferences();
    }

    @Override
    protected void processColorPreferences() {
        /* Call underlying class */
        super.processColorPreferences();

        /* Update the field manager */
        theFieldManager.setConfig(new MetisFieldConfig(getColorPreferences()));
    }

    @Override
    public MetisSwingViewerManager getViewerManager() {
        return theViewerManager;
    }

    @Override
    public TethysSwingGuiFactory getGuiFactory() {
        return (TethysSwingGuiFactory) super.getGuiFactory();
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public MetisFieldManager getFieldManager() {
        return theFieldManager;
    }
}
