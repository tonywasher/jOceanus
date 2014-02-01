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

import java.io.File;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseCancelException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.threads.LoaderThread;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * WorkerThread extension to create a QIF archive.
 */
public class CreateXmlBackup
        extends LoaderThread<MoneyWiseData, MoneyWiseDataType> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Create XML Backup";

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
    public CreateXmlBackup(final FinanceStatus pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theView = pStatus.getView();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public MoneyWiseData performTask() throws JOceanusException {
        /* Catch Exceptions */
        try {
            /* Initialise the status window */
            theStatus.initTask(TASK_NAME);

            /* Create a new Data values formatter */
            DataValuesFormatter<MoneyWiseData, MoneyWiseDataType> myFormatter = new DataValuesFormatter<MoneyWiseData, MoneyWiseDataType>(theStatus);

            /* Create backup */
            boolean bContinue = myFormatter.createExtract(theView.getData(), new File("c:\\Users\\Tony\\TestXML.zip"));

            /* Check for cancellation */
            if (!bContinue) {
                throw new JMoneyWiseCancelException("Operation Cancelled");
            }

            /* Catch any exceptions */
        } catch (JOceanusException e) {
            throw e;
        } catch (Exception e) {
            throw new JMoneyWiseIOException("Failed", e);
        }

        /* Return nothing */
        return null;
    }
}
