/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.sheets;

import java.io.File;

import net.sourceforge.JDataManager.ModelException;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.threads.ThreadStatus;

public abstract class SpreadSheet<T extends DataSet<T>> {
    /**
     * The Data file name
     */
    public final static String fileData = "zipData";

    /**
     * Obtain a sheet reader
     * @param pThread Thread Control for task
     * @return the sheet reader
     */
    protected abstract SheetReader<T> getSheetReader(ThreadStatus<T> pThread);

    /**
     * Obtain a sheet writer
     * @param pThread Thread Control for task
     * @return the sheet writer
     */
    protected abstract SheetWriter<T> getSheetWriter(ThreadStatus<T> pThread);

    /**
     * Load a Backup Workbook
     * @param pThread Thread Control for task
     * @param pFile the backup file to load from
     * @return the newly loaded data
     * @throws ModelException
     */
    public T loadBackup(ThreadStatus<T> pThread,
                        File pFile) throws ModelException {
        /* Create a sheet reader object */
        SheetReader<T> myReader = getSheetReader(pThread);

        /* Load the backup */
        T myData = myReader.loadBackup(pFile);

        /* Return the data */
        return myData;
    }

    /**
     * Create a Backup Workbook
     * @param pThread Thread Control for task
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws ModelException
     */
    public void createBackup(ThreadStatus<T> pThread,
                             T pData,
                             File pFile) throws ModelException {
        /* Create a sheet writer object */
        SheetWriter<T> myWriter = getSheetWriter(pThread);

        /* Create the backup */
        myWriter.createBackup(pData, pFile);
    }

    /**
     * Load an Extract Workbook
     * @param pThread Thread Control for task
     * @param pFile the extract file to load from
     * @return the newly loaded data
     * @throws ModelException
     */
    public T loadExtract(ThreadStatus<T> pThread,
                         File pFile) throws ModelException {
        /* Create a Sheet Reader object */
        SheetReader<T> myReader = getSheetReader(pThread);

        /* Load the extract file */
        T myData = myReader.loadExtract(pFile);

        /* Return the data */
        return myData;
    }

    /**
     * Create an Extract Workbook
     * @param pThread Thread Control for task
     * @param pData Data to write out
     * @param pFile the extract file to write to
     * @throws ModelException
     */
    public void createExtract(ThreadStatus<T> pThread,
                              T pData,
                              File pFile) throws ModelException {
        /* Create a SheetWriter object */
        SheetWriter<T> myWriter = getSheetWriter(pThread);

        /* Create the Extract file */
        myWriter.createExtract(pData, pFile);
    }

    /**
     * Spreadsheet types
     */
    public enum SheetType {
        BACKUP, EXTRACT;
    }
}
