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
package net.sourceforge.joceanus.prometheus.threads;

import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

import java.util.ResourceBundle;

/**
 * Resource IDs for Prometheus Threads.
 */
public enum PrometheusThreadResource
        implements OceanusBundleId {
    /**
     * LoadDB.
     */
    THREAD_LOADDB("LoadDB"),

    /**
     * StoreDB.
     */
    THREAD_STOREDB("StoreDB"),

    /**
     * CreateDB.
     */
    THREAD_CREATEDB("CreateDB"),

    /**
     * CreateDBTables.
     */
    THREAD_CREATEDBTABLES("CreateDBTables"),

    /**
     * PurgeDB.
     */
    THREAD_PURGEDB("PurgeDB"),

    /**
     * Create Backup.
     */
    THREAD_BACKUPCREATE("CreateBackup"),

    /**
     * Restore Backup.
     */
    THREAD_BACKUPRESTORE("RestoreBackup"),

    /**
     * Create XML Backup.
     */
    THREAD_XMLCREATE("CreateXml"),

    /**
     * Restore XMLBackup MenuItem.
     */
    THREAD_XMLLOAD("RestoreXml"),

    /**
     * Create XmlExtract.
     */
    THREAD_XTRACTCREATE("CreateXtract"),

    /**
     * RenewSecurity.
     */
    THREAD_SECURERENEW("RenewSecurity"),

    /**
     * Change Password.
     */
    THREAD_CHANGEPASS("ChangePass"),

    /**
     * Seloect Backup File.
     */
    TASK_SELECT_BACKUP("task.SelectBackup");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(PrometheusThreadResource.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    PrometheusThreadResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "Prometheus.thread";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
