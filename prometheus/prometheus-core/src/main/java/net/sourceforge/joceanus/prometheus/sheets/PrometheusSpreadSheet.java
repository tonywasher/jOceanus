/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.prometheus.sheets;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Spreadsheet control.
 * @author Tony Washer
 */
public abstract class PrometheusSpreadSheet {
    /**
     * The Data file name.
     */
    public static final String FILE_NAME = "zipData";

    /**
     * Constructor.
     */
    protected PrometheusSpreadSheet() {
    }

    /**
     * Obtain a sheet reader.
     * @param pReport the report
     * @param pPasswordMgr the password manager
     * @return the sheet reader
     */
    protected abstract PrometheusSheetReader getSheetReader(TethysUIThreadStatusReport pReport,
                                                            PrometheusSecurityPasswordManager pPasswordMgr);

    /**
     * Obtain a sheet writer.
     * @param pReport the report
     * @return the sheet writer
     */
    protected abstract PrometheusSheetWriter getSheetWriter(TethysUIThreadStatusReport pReport);

    /**
     * Load a Backup Workbook.
     * @param pReport the report
     * @param pPasswordMgr the password manager
     * @param pData the data to load into
     * @param pFile the backup file to load from
     * @throws OceanusException on error
     */
    public void loadBackup(final TethysUIThreadStatusReport pReport,
                           final PrometheusSecurityPasswordManager pPasswordMgr,
                           final PrometheusDataSet pData,
                           final File pFile) throws OceanusException {
        /* Create a sheet reader object */
        final PrometheusSheetReader myReader = getSheetReader(pReport, pPasswordMgr);

        /* Load the backup */
        myReader.loadBackup(pFile, pData);
    }

    /**
     * Load a Backup Workbook.
     * @param pReport the report
     * @param pPasswordMgr the password manager
     * @param pData the data to load into
     * @param pInStream the input stream to load from
     * @param pName the filename
     * @throws OceanusException on error
     */
    public void loadBackup(final TethysUIThreadStatusReport pReport,
                           final PrometheusSecurityPasswordManager pPasswordMgr,
                           final PrometheusDataSet pData,
                           final InputStream pInStream,
                           final String pName) throws OceanusException {
        /* Create a sheet reader object */
        final PrometheusSheetReader myReader = getSheetReader(pReport, pPasswordMgr);

        /* Load the backup */
        myReader.loadBackup(pInStream, pData, pName);
    }

    /**
     * Create a Backup Workbook.
     * @param pReport the report
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final TethysUIThreadStatusReport pReport,
                             final PrometheusDataSet pData,
                             final File pFile,
                             final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Create a sheet writer object */
        final PrometheusSheetWriter myWriter = getSheetWriter(pReport);

        /* Create the backup */
        myWriter.createBackup(pData, pFile, pType);
    }

    /**
     * Create a Backup Workbook.
     * @param pReport the report
     * @param pData Data to write out
     * @param pZipStream the stream to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final TethysUIThreadStatusReport pReport,
                             final PrometheusDataSet pData,
                             final OutputStream pZipStream,
                             final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Create a sheet writer object */
        final PrometheusSheetWriter myWriter = getSheetWriter(pReport);

        /* Create the backup */
        myWriter.createBackup(pData, pZipStream, pType);
    }
}
