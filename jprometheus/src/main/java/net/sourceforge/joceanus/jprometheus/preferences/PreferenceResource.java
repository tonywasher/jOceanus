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
package net.sourceforge.joceanus.jprometheus.preferences;

import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jPrometheus Preference Fields.
 */
public enum PreferenceResource implements ResourceId {
    /**
     * DBDriver SQLServer.
     */
    DRIVER_SQLSERVER("SQLSERVER"),

    /**
     * DBDriver PostgreSQL.
     */
    DRIVER_POSTGRESQL("POSTGRESQL"),

    /**
     * DBDriver SQLServer.
     */
    DRIVER_MYSQL("MYSQL");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getPackageResourceBuilder(JPrometheusDataException.class.getCanonicalName());

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
    private PreferenceResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jPrometheus.DBDriver";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Obtain key for DBDriver.
     * @param pValue the Value
     * @return the resource key
     */
    protected static PreferenceResource getKeyForDriver(final JDBCDriver pValue) {
        switch (pValue) {
            case SQLSERVER:
                return DRIVER_SQLSERVER;
            case POSTGRESQL:
                return DRIVER_POSTGRESQL;
            case MYSQL:
                return DRIVER_MYSQL;
            default:
                return null;
        }
    }
}