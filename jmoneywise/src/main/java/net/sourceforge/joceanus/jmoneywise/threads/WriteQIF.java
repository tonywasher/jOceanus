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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.threads;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseCancelException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.quicken.QDataSet;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jprometheus.threads.WorkerThread;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * WorkerThread extension to create a QIF archive.
 */
public class WriteQIF
        extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "QIF Creation";

    /**
     * Data View.
     */
    private final View theView;

    /**
     * Thread Status.
     */
    private final ThreadStatus<MoneyWiseData, MoneyWiseDataType> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public WriteQIF(final MoneyWiseStatus pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theView = pStatus.getView();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JOceanusException {
        /* Initialise the status window */
        theStatus.initTask("Analysing Data");

        /* Load configuration */
        PreferenceManager myMgr = theView.getPreferenceMgr();
        QIFPreference myPrefs = myMgr.getPreferenceSet(QIFPreference.class);

        /* Create QIF analysis */
        QDataSet myQData = new QDataSet(theStatus, theView, myPrefs);

        /* Initialise the status window */
        theStatus.initTask("Writing QIF file");

        /* Create file */
        boolean bContinue = myQData.outputData(theStatus);

        /* Check for cancellation */
        if (!bContinue) {
            throw new JMoneyWiseCancelException("Operation Cancelled");
        }

        /* Return nothing */
        return null;
    }
}
