/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.threads;

import java.io.File;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.threads.ThreadStatus;
import net.sourceforge.jOceanus.jDataModels.threads.WorkerThread;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QDataSet;

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
     * Data Control.
     */
    private final DataControl<FinanceData> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<FinanceData> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public WriteQIF(final ThreadStatus<FinanceData> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JDataException {
        boolean doDelete = false;
        File myQIFFile = null;

        /* Catch Exceptions */
        try {
            /* Initialise the status window */
            theStatus.initTask("Analysing Data");

            /* Determine the archive name */
            myQIFFile = new File("c:\\Users\\Tony\\NewFinance.qif");

            /* Create QIF analysis */
            QDataSet myQData = new QDataSet(theStatus, theControl.getData());

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            theStatus.initTask("Writing QIF file");

            /* Create file */
            boolean bContinue = myQData.outputData(theStatus, myQIFFile);

            /* Check for cancellation */
            if (!bContinue) {
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
            }

            /* Catch any exceptions */
        } catch (JDataException e) {
            /* Delete the file */
            if ((doDelete)
                && (!myQIFFile.delete())) {
                doDelete = false;
            }

            /* Report the failure */
            throw e;
            /* Catch any exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.LOGIC, "Failed", e);
        }

        /* Return nothing */
        return null;
    }
}