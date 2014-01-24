/*******************************************************************************
- * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseCancelException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jprometheus.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.SheetReader;
import net.sourceforge.joceanus.jprometheus.sheets.SheetWriter;
import net.sourceforge.joceanus.jprometheus.sheets.SpreadSheet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SpreadSheet extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseSheet
        extends SpreadSheet<MoneyWiseData> {
    /**
     * Number of base archive load areas. 10xStatic,EventClasses,2*Category,Pattern,Rate,Price,Account,TaxYear,Range+Event.
     */
    private static final int NUM_ARCHIVE_AREAS = 20;

    /**
     * Year boundary.
     */
    private static final int YEAR_BDY = 50;

    /**
     * Year constant.
     */
    private static final int YEAR_1900 = 1900;

    /**
     * Year constant.
     */
    private static final int YEAR_2000 = 2000;

    /**
     * Obtain a sheet reader.
     * @param pTask Thread Control for task
     * @return the sheet reader
     */
    @Override
    protected SheetReader<MoneyWiseData> getSheetReader(final TaskControl<MoneyWiseData> pTask) {
        /* Create a MoneyWise Reader object and return it */
        return new MoneyWiseReader(pTask);
    }

    /**
     * Obtain a sheet writer.
     * @param pTask Task Control for task
     * @return the sheet writer
     */
    @Override
    protected SheetWriter<MoneyWiseData> getSheetWriter(final TaskControl<MoneyWiseData> pTask) {
        /* Create a MoneyWise Writer object and return it */
        return new MoneyWiseWriter(pTask);
    }

    /**
     * NamedRange for Static.
     */
    private static final String AREA_YEARRANGE = "AssetsYears";

    /**
     * Simple class to define an archive year.
     */
    protected static final class ArchiveYear {
        /**
         * The date.
         */
        private final JDateDay theDate;

        /**
         * The range name.
         */
        private final String theRangeName;

        /**
         * Get the date.
         * @return the date
         */
        protected JDateDay getDate() {
            return theDate;
        }

        /**
         * Get the range name.
         * @return the name
         */
        protected String getRangeName() {
            return theRangeName;
        }

        /**
         * Constructor.
         * @param pName the range name
         */
        private ArchiveYear(final String pName) {
            /* Store parameters */
            theRangeName = pName;

            /* Isolate the year part */
            int myLen = pName.length();
            int myYear = Integer.parseInt(pName.substring(myLen - 2));

            /* Calculate the actual year */
            if (myYear < YEAR_BDY) {
                myYear += YEAR_2000;
            } else {
                myYear += YEAR_1900;
            }

            /* Create the date */
            theDate = new JDateDay(myYear, Calendar.APRIL, TaxYear.END_OF_MONTH_DAY);
        }
    }

    /**
     * Simple class to hold YearRange.
     */
    protected static final class YearRange {
        /**
         * The list of years.
         */
        private final List<ArchiveYear> theYears;

        /**
         * Constructor.
         */
        private YearRange() {
            theYears = new ArrayList<ArchiveYear>();
        }

        /**
         * Get the iterator.
         * @return the iterator
         */
        protected ListIterator<ArchiveYear> getIterator() {
            return theYears.listIterator();
        }

        /**
         * Get the reverse iterator.
         * @return the iterator
         */
        protected ListIterator<ArchiveYear> getReverseIterator() {
            return theYears.listIterator(theYears.size());
        }

        /**
         * Get the number of years.
         * @return the number of years
         */
        protected int getNumYears() {
            return theYears.size();
        }

        /**
         * Add a year to the front of the list.
         * @param pName the range name
         */
        private void addYear(final String pName) {
            ArchiveYear myYear = new ArchiveYear(pName);
            theYears.add(myYear);
        }
    }

    /**
     * Load the Static from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final YearRange pRange) throws JOceanusException {
        /* Find the range of cells */
        DataView myView = pWorkBook.getRangeView(AREA_YEARRANGE);

        /* Loop through the cells */
        for (int myIndex = 0; myIndex < myView.getColumnCount(); myIndex++) {
            /* Access the cell and add year to the list */
            DataCell myCell = myView.getCellByPosition(myIndex, 0);
            pRange.addYear(myCell.getStringValue());
        }

        /* Access the static */
        ControlDataList myStatic = pData.getControlData();

        /* Add the value into the finance tables (with no security as yet) */
        myStatic.addOpenItem(0, 0);

        /* Calculate the number of stages */
        int myStages = NUM_ARCHIVE_AREAS
                       + pRange.getNumYears();

        /* Declare the number of stages */
        return pTask.setNumStages(myStages);
    }

    /**
     * Load an Archive Workbook.
     * @param pTask Task Control for task
     * @param pPreferences the backup preferences
     * @return the newly loaded data
     * @throws JOceanusException on error
     */
    public static MoneyWiseData loadArchive(final TaskControl<MoneyWiseData> pTask,
                                            final BackupPreferences pPreferences) throws JOceanusException {
        InputStream myStream = null;

        /* Determine the archive name */
        String myName = pPreferences.getStringValue(BackupPreferences.NAME_ARCHIVE_FILE);
        JDateDay myLastEvent = pPreferences.getDateValue(BackupPreferences.NAME_LAST_EVENT);
        File myArchive = new File(myName);

        /* Protect the workbook retrieval */
        try {
            /* Create an input stream to the file */
            FileInputStream myInFile = new FileInputStream(myArchive);
            myStream = new BufferedInputStream(myInFile);

            /* Determine the WorkBookType */
            WorkBookType myType = WorkBookType.determineType(myName);

            /* Load the data from the stream */
            MoneyWiseData myData = loadArchiveStream(pTask, myStream, myType, myLastEvent);

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;
            return myData;
        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to load Workbook: "
                                            + myArchive.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null) {
                    myStream.close();
                }

                /* Ignore errors */
            } catch (IOException ex) {
                pTask.getLogger().log(Level.SEVERE, "Failed to close stream", ex);
            }
        }
    }

    /**
     * Load an Archive Workbook from a stream.
     * @param pTask Task Control for task
     * @param pStream Input stream to load from
     * @param pType the workBookType
     * @param pLastEvent the last event
     * @return the newly loaded data
     * @throws JOceanusException on error
     */
    private static MoneyWiseData loadArchiveStream(final TaskControl<MoneyWiseData> pTask,
                                                   final InputStream pStream,
                                                   final WorkBookType pType,
                                                   final JDateDay pLastEvent) throws JOceanusException {
        /* Protect the workbook retrieval */
        try {
            /* Create the Data */
            MoneyWiseData myData = pTask.getNewDataSet();

            /* Access the workbook from the stream */
            DataWorkBook myWorkbook = new DataWorkBook(pStream, pType);

            /* Create a YearRange */
            YearRange myRange = new YearRange();

            /* Determine Year Range */
            boolean bContinue = loadArchive(pTask, myWorkbook, myData, myRange);

            /* Load Tables */
            if (bContinue) {
                bContinue = SheetAccountCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventCategoryType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxBasis.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxCategory.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountCurrency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxRegime.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetFrequency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxYearInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventInfoType.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetEventClass.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetAccountCategory.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventCategory.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetTaxYear.loadArchive(pTask, myWorkbook, myData, myRange);
            }
            if (bContinue) {
                myData.calculateDateRange();
            }

            if (bContinue) {
                bContinue = SheetAccount.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountRate.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetSecurityPrice.loadArchive(pTask, myWorkbook, myData, pLastEvent);
            }
            if (bContinue) {
                myData.getAccounts().validateOnLoad();
            }

            if (bContinue) {
                bContinue = SheetEvent.loadArchive(pTask, myWorkbook, myData, myRange, pLastEvent);
            }
            // if (bContinue) {
            // bContinue = SheetPattern.loadArchive(pTask, myWorkbook, myData);
            // }

            /* Close the stream */
            pStream.close();

            /* Set the next stage */
            if (!pTask.setNewStage("Refreshing data")) {
                bContinue = false;
            }

            /* Check for cancellation */
            if (!bContinue) {
                throw new JMoneyWiseCancelException("Operation Cancelled");
            }

            /* Return the data */
            return myData;
        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to load Workbook", e);
        }
    }
}
