/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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
 */

package net.sourceforge.joceanus.moneywise.test.data.storage;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * Test security.
 */
public class MoneyWiseDataTestSecurity {
    /**
     * The DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * Constructor.
     *
     * @param pDataSet the DataSet
     */
    public MoneyWiseDataTestSecurity(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Initialise security.
     *
     * @param pView the view
     */
    public void initSecurity(final MoneyWiseView pView) throws OceanusException {
        /* Access the Password manager and disable prompting */
        final PrometheusSecurityPasswordManager myManager = theDataSet.getPasswordMgr();
        myManager.setDialogController(new MoneyWiseNullPasswordDialog());

        /* Create the cloneSet and initialise security */
        final MoneyWiseDataSet myNullData = pView.getNewData();

        /* Create the control data */
        final TethysUIThreadStatusReport myReport = new MoneyWiseNullThreadStatusReport();
        theDataSet.getControlData().addNewControl(0);
        theDataSet.initialiseSecurity(myReport, myNullData);
        theDataSet.reBase(myReport, myNullData);
    }
}
