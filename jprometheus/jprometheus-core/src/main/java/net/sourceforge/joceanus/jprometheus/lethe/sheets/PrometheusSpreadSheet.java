/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.sheets;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Spreadsheet control.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class PrometheusSpreadSheet<T extends DataSet<T, ?>> {
    /**
     * The Data file name.
     */
    public static final String FILE_NAME = "zipData";

    /**
     * Obtain a sheet reader.
     * @param pReport the report
     * @param pSecureMgr the security manager
     * @return the sheet reader
     */
    protected abstract PrometheusSheetReader<T> getSheetReader(MetisThreadStatusReport pReport,
                                                               GordianHashManager pSecureMgr);

    /**
     * Obtain a sheet writer.
     * @param pReport the report
     * @return the sheet writer
     */
    protected abstract PrometheusSheetWriter<T> getSheetWriter(MetisThreadStatusReport pReport);

    /**
     * Load a Backup Workbook.
     * @param pReport the report
     * @param pSecureMgr the security manager
     * @param pData the data to load into
     * @param pFile the backup file to load from
     * @throws OceanusException on error
     */
    public void loadBackup(final MetisThreadStatusReport pReport,
                           final GordianHashManager pSecureMgr,
                           final T pData,
                           final File pFile) throws OceanusException {
        /* Create a sheet reader object */
        final PrometheusSheetReader<T> myReader = getSheetReader(pReport, pSecureMgr);

        /* Load the backup */
        myReader.loadBackup(pFile, pData);
    }

    /**
     * Create a Backup Workbook.
     * @param pReport the report
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final MetisThreadStatusReport pReport,
                             final T pData,
                             final File pFile,
                             final MetisWorkBookType pType) throws OceanusException {
        /* Create a sheet writer object */
        final PrometheusSheetWriter<T> myWriter = getSheetWriter(pReport);

        /* Create the backup */
        myWriter.createBackup(pData, pFile, pType);
    }
}
