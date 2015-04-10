/*******************************************************************************
 * jMoneyWise: Finance Application
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/jmoneywise-swing/src/main/java/net/sourceforge/joceanus/jmoneywise/swing/package-info.java $
 * $Revision: 590 $
 * $Author: Tony $
 * $Date: 2015-04-04 12:04:39 +0100 (Sat, 04 Apr 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.swing;

import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.SwingViewerManager;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Extension of view to cast utilities properly.
 */
public class SwingView
        extends View {
    /**
     * Constructor.
     * @param pProfile the profile
     * @throws JOceanusException on error
     */
    public SwingView(final JDataProfile pProfile) throws JOceanusException {
        super(JOceanusSwingUtilitySet.createDefault(), pProfile);
    }

    @Override
    public JOceanusSwingUtilitySet getUtilitySet() {
        return (JOceanusSwingUtilitySet) super.getUtilitySet();
    }

    @Override
    public SwingViewerManager getViewerManager() {
        return (SwingViewerManager) super.getViewerManager();
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public JFieldManager getFieldManager() {
        return getUtilitySet().getFieldManager();
    }
}
