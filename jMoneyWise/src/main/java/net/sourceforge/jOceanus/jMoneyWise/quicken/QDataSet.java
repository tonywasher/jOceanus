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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataModels.threads.ThreadStatus;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * Quicken DataSet Representation.
 */
public class QDataSet {
    /**
     * Quicken Date Format.
     */
    private static final String QIF_DATEFORMAT = "dd/MM/yyyy";

    /**
     * Data Formatter.
     */
    private final JDataFormatter theFormatter = new JDataFormatter();

    /**
     * Data Set.
     */
    private final FinanceData theData;

    /**
     * Account List.
     */
    private final QAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pStatus the thread status
     * @param pData the dataSet
     */
    public QDataSet(final ThreadStatus<FinanceData> pStatus,
                    final FinanceData pData) {
        /* Store Data Set */
        theData = pData;

        /* Set Data Formatter to correct date format */
        theFormatter.setFormat(QIF_DATEFORMAT);

        /* Create the analysis */
        theAnalysis = new QAnalysis(theFormatter);

        /* Analyse the data */
        theAnalysis.analyseData(pStatus, theData);
    }

    /**
     * Output data to file.
     * @param pStatus the thread status
     * @param pFile the output file
     * @return success true/false
     * @throws JDataException on error
     */
    public boolean outputData(final ThreadStatus<FinanceData> pStatus,
                              final File pFile) throws JDataException {
        FileOutputStream myOutput = null;

        /* Protect against exceptions */
        try {
            /* Create the Stream writer */
            myOutput = new FileOutputStream(pFile);
            BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
            OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer);

            /* Output the data */
            boolean bSuccess = theAnalysis.outputData(pStatus, myWriter);
            myWriter.close();
            return bSuccess;

        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to write to file: "
                                                           + pFile.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myOutput != null) {
                    myOutput.close();
                }

                /* Ignore errors */
            } catch (IOException ex) {
                myOutput = null;
            }
        }
    }
}
