/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.sheets;

import java.io.File;

import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * @throws JOceanusException on error
     */
    public T loadBackup(final TaskControl<T> pTask,
                        final File pFile) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public void createBackup(final TaskControl<T> pTask,
                             final T pData,
                             final File pFile,
                             final WorkBookType pType) throws JOceanusException {
        /* Create a sheet writer object */
        SheetWriter<T> myWriter = getSheetWriter(pTask);

        /* Create the backup */
        myWriter.createBackup(pData, pFile, pType);
    }
}
