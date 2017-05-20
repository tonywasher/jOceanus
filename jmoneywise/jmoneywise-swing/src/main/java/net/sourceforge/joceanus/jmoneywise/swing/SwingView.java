/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.lethe.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Extension of view to cast utilities properly.
 */
public class SwingView
        extends View<JComponent, Icon> {
    /**
     * Constructor.
     * @param pInfo the program info
     * @throws OceanusException on error
     */
    public SwingView(final MetisProgram pInfo) throws OceanusException {
        super(new JOceanusSwingUtilitySet(pInfo), new MoneyWiseUKTaxYearCache());
    }

    @Override
    public JOceanusSwingUtilitySet getUtilitySet() {
        return (JOceanusSwingUtilitySet) super.getUtilitySet();
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public MetisFieldManager getFieldManager() {
        return getUtilitySet().getFieldManager();
    }

    @Override
    public TethysSwingGuiFactory getGuiFactory() {
        return (TethysSwingGuiFactory) super.getGuiFactory();
    }

    @Override
    public MetisSwingToolkit getToolkit() {
        return (MetisSwingToolkit) super.getToolkit();
    }
}
