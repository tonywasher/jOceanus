/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.swing;

import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jmoneywise.lethe.views.View;
import net.sourceforge.joceanus.jprometheus.lethe.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Extension of view to cast utilities properly.
 */
public class SwingView
        extends View {
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
    public MetisSwingFieldManager getFieldManager() {
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
