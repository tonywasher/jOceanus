/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.joceanus.jdatamodels.sheets;

import java.io.File;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.TaskControl;
import net.sourceforge.joceanus.jspreadsheetmanager.WorkBookType;

/**
 * Spreadsheet control.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SpreadSheet<T extends DataSet<T, ?>> {
    /**
     * The Data file name.
     */
    public static final String FILE_NAME = "zipData";

    /**
     * The ZipFile extension.
     */
    public static final String ZIPFILE_EXT = ".zip";

    /**
     * Obtain a sheet reader.
     * @param pTask Task Control for task
     * @return the sheet reader
     */
    protected abstract SheetReader<T> getSheetReader(final TaskControl<T> pTask);

    /**
     * Obtain a sheet writer.
     * @param pTask Task Control for task
     * @return the sheet writer
     */
    protected abstract SheetWriter<T> getSheetWriter(final TaskControl<T> pTask);

    /**
     * Load a Backup Workbook.
     * @param pTask Task Control for task
     * @param pFile the backup file to load from
     * @return the newly loaded data
     * @throws JDataException on error
     */
    public T loadBackup(final TaskControl<T> pTask,
                        final File pFile) throws JDataException {
        /* Create a sheet reader object */
        SheetReader<T> myReader = getSheetReader(pTask);

        /* Load the backup */
        T myData = myReader.loadBackup(pFile);

        /* Return the data */
        return myData;
    }

    /**
     * Create a Backup Workbook.
     * @param pTask Task Control for task
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws JDataException on error
     */
    public void createBackup(final TaskControl<T> pTask,
                             final T pData,
                             final File pFile,
                             final WorkBookType pType) throws JDataException {
        /* Create a sheet writer object */
        SheetWriter<T> myWriter = getSheetWriter(pTask);

        /* Create the backup */
        myWriter.createBackup(pData, pFile, pType);
    }

    /**
     * Load an Extract Workbook.
     * @param pTask Task Control for task
     * @param pFile the extract file to load from
     * @return the newly loaded data
     * @throws JDataException on error
     */
    public T loadExtract(final TaskControl<T> pTask,
                         final File pFile) throws JDataException {
        /* Create a Sheet Reader object */
        SheetReader<T> myReader = getSheetReader(pTask);

        /* Load the extract file */
        T myData = myReader.loadExtract(pFile);

        /* Return the data */
        return myData;
    }

    /**
     * Create an Extract Workbook.
     * @param pTask Task Control for task
     * @param pData Data to write out
     * @param pFile the extract file to write to
     * @throws JDataException on error
     */
    public void createExtract(final TaskControl<T> pTask,
                              final T pData,
                              final File pFile) throws JDataException {
        /* Create a SheetWriter object */
        SheetWriter<T> myWriter = getSheetWriter(pTask);

        /* Create the Extract file */
        myWriter.createExtract(pData, pFile);
    }
}
