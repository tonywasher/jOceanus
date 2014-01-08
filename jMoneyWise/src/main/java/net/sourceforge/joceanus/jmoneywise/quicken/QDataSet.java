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
package net.sourceforge.joceanus.jmoneywise.quicken;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamodels.threads.ThreadStatus;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.views.View;

/**
 * Quicken DataSet Representation.
 */
public class QDataSet {
    /**
     * Quicken Date Format.
     */
    private static final String QIF_DATEFORMAT = "dd/MM/yy";

    /**
     * Windows CharacterSet.
     */
    private static final String NAME_CHARSET = "Windows-1252";

    /**
     * Write failure error text.
     */
    private static final String ERROR_WRITE = "Failed to write file";

    /**
     * Data Formatter.
     */
    private final JDataFormatter theFormatter = new JDataFormatter();

    /**
     * QIF Preferences.
     */
    private final QIFPreference thePreferences;

    /**
     * QIF file type.
     */
    private final QIFType theQIFType;

    /**
     * Account List.
     */
    private final QAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pStatus the thread status
     * @param pView the dataView
     * @param pPreferences the preferences
     */
    public QDataSet(final ThreadStatus<FinanceData> pStatus,
                    final View pView,
                    final QIFPreference pPreferences) {
        /* Store parameters */
        thePreferences = pPreferences;

        /* Analyse the data */
        JDateDay myLastEvent = thePreferences.getDateValue(QIFPreference.NAME_LASTEVENT);
        theQIFType = thePreferences.getEnumValue(QIFPreference.NAME_QIFTYPE, QIFType.class);

        /* Set Data Formatter to correct date format */
        theFormatter.setFormat(QIF_DATEFORMAT);

        /* Create the analysis */
        theAnalysis = new QAnalysis(theFormatter, theQIFType);

        /* Analyse the data */
        theAnalysis.analyseData(pStatus, pView, myLastEvent);
    }

    /**
     * Output data to file.
     * @param pStatus the thread status
     * @return success true/false
     * @throws JDataException on error
     */
    public boolean outputData(final ThreadStatus<FinanceData> pStatus) throws JDataException {
        /* Determine whether to use consolidated file */
        if (theQIFType.useConsolidatedFile()) {
            return outputSingleFile(pStatus);
        } else {
            return outputAccounts(pStatus.getLogger());
        }
    }

    /**
     * Output all accounts.
     * @param pLogger the logger
     * @return success true/false
     * @throws JDataException on error
     */
    private boolean outputAccounts(final Logger pLogger) throws JDataException {
        boolean bContinue = true;
        /* Loop through the accounts */
        Iterator<QAccount> myIterator = theAnalysis.getAccountIterator();
        while ((bContinue)
               && (myIterator.hasNext())) {
            QAccount myAccount = myIterator.next();

            /* Output the file */
            bContinue = outputIndividualFile(pLogger, myAccount);
        }

        /* Return to the caller */
        return bContinue;
    }

    /**
     * Output data to single file.
     * @param pStatus the thread status
     * @return success true/false
     * @throws JDataException on error
     */
    public boolean outputSingleFile(final ThreadStatus<FinanceData> pStatus) throws JDataException {
        FileOutputStream myOutput = null;
        boolean doDelete = true;

        /* Determine name of output file */
        String myDirectory = thePreferences.getStringValue(QIFPreference.NAME_QIFDIR);

        /* Determine the archive name */
        File myQIFFile = new File(myDirectory
                                  + File.separator
                                  + theQIFType.getFileName());

        /* Protect against exceptions */
        try {
            /* Create the Stream writer */
            myOutput = new FileOutputStream(myQIFFile);
            BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
            OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, NAME_CHARSET);

            /* Output the data */
            boolean bSuccess = theAnalysis.outputData(pStatus, myWriter);
            myWriter.close();
            doDelete = false;
            return bSuccess;

        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to write to file: "
                                                           + myQIFFile.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myOutput != null) {
                    myOutput.close();
                }

                /* Delete the file */
                if ((doDelete)
                    && (!myQIFFile.delete())) {
                    doDelete = false;
                }

                /* Ignore errors */
            } catch (IOException ex) {
                pStatus.getLogger().log(Level.SEVERE, ERROR_WRITE, ex);
            }
        }
    }

    /**
     * Output data to individual file.
     * @param pLogger the logger
     * @param pAccount the account to dump
     * @return success true/false
     * @throws JDataException on error
     */
    private boolean outputIndividualFile(final Logger pLogger,
                                         final QAccount pAccount) throws JDataException {
        FileOutputStream myOutput = null;
        boolean doDelete = true;

        /* Determine name of output file */
        String myDirectory = thePreferences.getStringValue(QIFPreference.NAME_QIFDIR);

        /* Determine the archive name */
        File myQIFFile = new File(myDirectory
                                  + File.separator
                                  + pAccount.getName()
                                  + QIFType.QIF_SUFFIX);

        /* Protect against exceptions */
        try {
            /* Create the Stream writer */
            myOutput = new FileOutputStream(myQIFFile);
            BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
            OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, NAME_CHARSET);

            /* Output the data */
            pAccount.outputEvents(myWriter, theAnalysis.getStartDate());
            myWriter.close();
            doDelete = false;
            return true;

        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to write to file: "
                                                           + myQIFFile.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myOutput != null) {
                    myOutput.close();
                }

                /* Delete the file */
                if ((doDelete)
                    && (!myQIFFile.delete())) {
                    doDelete = false;
                }

                /* Ignore errors */
            } catch (IOException ex) {
                pLogger.log(Level.SEVERE, ERROR_WRITE, ex);
            }
        }
    }
}
