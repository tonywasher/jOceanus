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
package net.sourceforge.joceanus.jprometheus.threads;

/**
 * Prometheus Thread Ids.
 */
public enum PrometheusThreadId {
    /**
     * Create Database.
     */
    CREATEDB(PrometheusThreadResource.CREATEDB),

    /**
     * Load Database.
     */
    LOADDB(PrometheusThreadResource.LOADDB),

    /**
     * Store Database.
     */
    STOREDB(PrometheusThreadResource.STOREDB),

    /**
     * Purge Database.
     */
    PURGEDB(PrometheusThreadResource.PURGEDB),

    /**
     * Create Backup.
     */
    CREATEBACKUP(PrometheusThreadResource.BACKUPCREATE),

    /**
     * Restore Backup.
     */
    RESTOREBACKUP(PrometheusThreadResource.BACKUPRESTORE),

    /**
     * Create XML Backup.
     */
    CREATEXML(PrometheusThreadResource.XMLCREATE),

    /**
     * Create XML Extract.
     */
    CREATEXTRACT(PrometheusThreadResource.XTRACTCREATE),

    /**
     * Restore XML Backup.
     */
    RESTOREXML(PrometheusThreadResource.XMLLOAD),

    /**
     * Renew Security.
     */
    RENEWSECURITY(PrometheusThreadResource.SECURERENEW),

    /**
     * Change Password.
     */
    CHANGEPASS(PrometheusThreadResource.CHANGEPASS);

    /**
     * The name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pId the id
     */
    PrometheusThreadId(final PrometheusThreadResource pId) {
        theName = pId.getValue();
    }

    @Override
    public String toString() {
        return theName;
    }
}
