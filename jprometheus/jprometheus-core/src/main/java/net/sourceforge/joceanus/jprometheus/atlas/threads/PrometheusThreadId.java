/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.threads;

import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIMenuBarManager.TethysUIMenuId;

/**
 * Prometheus Thread Ids.
 */
public enum PrometheusThreadId
        implements TethysUIMenuId {
    /**
     * Create Database.
     */
    CREATEDB(PrometheusThreadResource.THREAD_CREATEDB),

    /**
     * Load Database.
     */
    LOADDB(PrometheusThreadResource.THREAD_LOADDB),

    /**
     * Store Database.
     */
    STOREDB(PrometheusThreadResource.THREAD_STOREDB),

    /**
     * Purge Database.
     */
    PURGEDB(PrometheusThreadResource.THREAD_PURGEDB),

    /**
     * Create Backup.
     */
    CREATEBACKUP(PrometheusThreadResource.THREAD_BACKUPCREATE),

    /**
     * Restore Backup.
     */
    RESTOREBACKUP(PrometheusThreadResource.THREAD_BACKUPRESTORE),

    /**
     * Create XML Backup.
     */
    CREATEXML(PrometheusThreadResource.THREAD_XMLCREATE),

    /**
     * Create XML Extract.
     */
    CREATEXTRACT(PrometheusThreadResource.THREAD_XTRACTCREATE),

    /**
     * Restore XML Backup.
     */
    RESTOREXML(PrometheusThreadResource.THREAD_XMLLOAD),

    /**
     * Renew Security.
     */
    RENEWSECURITY(PrometheusThreadResource.THREAD_SECURERENEW),

    /**
     * Change Password.
     */
    CHANGEPASS(PrometheusThreadResource.THREAD_CHANGEPASS);

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
