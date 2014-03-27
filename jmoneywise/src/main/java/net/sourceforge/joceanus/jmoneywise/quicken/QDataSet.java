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
package net.sourceforge.joceanus.jmoneywise.quicken;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * Quicken DataSet Representation.
 */
public class QDataSet {
    /**
     * Windows CharacterSet.
     */
    private static final String NAME_CHARSET = "Windows-1252";

    /**
     * Delete error text.
     */
    private static final String ERROR_DELETE = "Failed to delete file";

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
    public QDataSet(final ThreadStatus<MoneyWiseData, MoneyWiseDataType> pStatus,
                    final View pView,
                    final QIFPreference pPreferences) {
        /* Store parameters */
        thePreferences = pPreferences;

        /* Analyse the data */
        JDateDay myLastEvent = thePreferences.getDateValue(QIFPreference.NAME_LASTEVENT);
        theQIFType = thePreferences.getEnumValue(QIFPreference.NAME_QIFTYPE, QIFType.class);

        /* Set Data Formatter to correct date format */
        theFormatter.setFormat(theQIFType.getDateFormat());

        /* Create the analysis */
        theAnalysis = new QAnalysis(theFormatter, theQIFType);

        /* Analyse the data */
        theAnalysis.analyseData(pStatus, pView, myLastEvent);
    }

    /**
     * Output data to file.
     * @param pStatus the thread status
     * @return success true/false
     * @throws JOceanusException on error
     */
    public boolean outputData(final ThreadStatus<MoneyWiseData, MoneyWiseDataType> pStatus) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    private boolean outputAccounts(final Logger pLogger) throws JOceanusException {
        boolean bContinue = true;
        /* Loop through the accounts */
        Iterator<QAccount> myIterator = theAnalysis.getAccountIterator();
        while ((bContinue) && (myIterator.hasNext())) {
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
     * @throws JOceanusException on error
     */
    public boolean outputSingleFile(final ThreadStatus<MoneyWiseData, MoneyWiseDataType> pStatus) throws JOceanusException {
        /* Assume failure */
        boolean bSuccess = false;

        /* Determine name of output file */
        String myDirectory = thePreferences.getStringValue(QIFPreference.NAME_QIFDIR);

        /* Determine the archive name */
        File myQIFFile = new File(myDirectory + File.separator + theQIFType.getFileName());

        /* Protect against exceptions */
        try (FileOutputStream myOutput = new FileOutputStream(myQIFFile);
             BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
             OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, NAME_CHARSET)) {

            /* Output the data */
            boolean isSuccess = theAnalysis.outputData(pStatus, myWriter);
            myWriter.close();
            bSuccess = isSuccess;

        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to write to file: " + myQIFFile.getName(), e);
        } finally {
            /* Delete the file */
            if ((!bSuccess) && (!myQIFFile.delete())) {
                /* Nothing that we can do. At least we tried */
                pStatus.getLogger().log(Level.SEVERE, ERROR_DELETE);
            }
        }

        /* Return success indication */
        return bSuccess;
    }

    /**
     * Output data to individual file.
     * @param pLogger the logger
     * @param pAccount the account to dump
     * @return success true/false
     * @throws JOceanusException on error
     */
    private boolean outputIndividualFile(final Logger pLogger,
                                         final QAccount pAccount) throws JOceanusException {
        /* Assume failure */
        boolean bSuccess = false;

        /* Determine name of output file */
        String myDirectory = thePreferences.getStringValue(QIFPreference.NAME_QIFDIR);

        /* Determine the archive name */
        File myQIFFile = new File(myDirectory + File.separator + pAccount.getName() + QIFType.QIF_SUFFIX);

        /* Protect against exceptions */
        try (FileOutputStream myOutput = new FileOutputStream(myQIFFile);
             BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
             OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, NAME_CHARSET)) {
            /* Output the data */
            pAccount.outputEvents(myWriter, theAnalysis.getStartDate());

            /* Close file and set success */
            myWriter.close();
            bSuccess = true;

        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to write to file: " + myQIFFile.getName(), e);
        } finally {
            /* Delete the file on error */
            if ((!bSuccess) && (!myQIFFile.delete())) {
                /* Nothing that we can do. At least we tried */
                pLogger.log(Level.SEVERE, ERROR_DELETE);
            }
        }

        /* Return success indication */
        return bSuccess;
    }
}
