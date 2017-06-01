/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.help;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.TethysHelpEntry;
import net.sourceforge.joceanus.jtethys.help.TethysHelpModule;

/**
 * Help Module for FinanceApp.
 * @author Tony Washer
 */
public class MoneyWiseHelp
        extends TethysHelpModule {
    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MoneyWiseHelp() throws OceanusException {
        /* Initialise the underlying module */
        super(MoneyWiseHelp.class, "MoneyWise Help");

        /* Create accounts tree */
        TethysHelpEntry myAccounts = addRootEntry(defineContentsEntry("Accounts"));
        myAccounts.addChildEntry(defineHelpEntry("Deposits", "Deposits.html"));
        myAccounts.addChildEntry(defineHelpEntry("Loans", "Loans.html"));

        /* Create static tree */
        TethysHelpEntry myStatic = addRootEntry(defineContentsEntry("StaticData"));
        myStatic.addChildEntry(defineHelpEntry("AccountTypes", "AccountTypes.html"));
        myStatic.addChildEntry(defineHelpEntry("TransactionTypes", "TransactionTypes.html"));

        /* Load help pages */
        loadHelpPages();

        /* Load the CSS */
        loadCSS("MoneyWiseHelp.css");
    }
}
