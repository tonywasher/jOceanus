/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.database;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.prometheus.PrometheusDataException;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

/**
 * Resource IDs for jPrometheus Preference Fields.
 */
public enum PrometheusDBResource implements OceanusBundleId {
    /**
     * DBDriver SQLServer.
     */
    DRIVER_SQLSERVER("DBDriver.SQLSERVER"),

    /**
     * DBDriver PostgreSQL.
     */
    DRIVER_POSTGRESQL("DBDriver.POSTGRESQL"),

    /**
     * DBDriver SQLServer.
     */
    DRIVER_MYSQL("DBDriver.MYSQL"),

    /**
     * DBDriver MAriaDB.
     */
    DRIVER_MARIADB("DBDriver.MariaDB"),

    /**
     * DBDriver H2.
     */
    DRIVER_H2("DBDriver.H2"),

    /**
     * DatabasePreference Display Name.
     */
    DBPREF_PREFNAME("dbpref.prefname"),

    /**
     * DatabasePreference Driver.
     */
    DBPREF_DRIVER("dbpref.driver"),

    /**
     * DatabasePreference Server.
     */
    DBPREF_SERVER("dbpref.server"),

    /**
     * DatabasePreference Instance.
     */
    DBPREF_INSTANCE("dbpref.instance"),

    /**
     * DatabasePreference Port.
     */
    DBPREF_PORT("dbpref.port"),

    /**
     * DatabasePreference Name.
     */
    DBPREF_NAME("dbpref.name"),

    /**
     * DatabasePreference Batch.
     */
    DBPREF_BATCH("dbpref.batch"),

    /**
     * DatabasePreference User.
     */
    DBPREF_USER("dbpref.user"),

    /**
     * DatabasePreference Password.
     */
    DBPREF_PASS("dbpref.password");

    /**
     * The Driver Map.
     */
    private static final Map<PrometheusJDBCDriver, OceanusBundleId> DRIVER_MAP = buildDriverMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getPackageLoader(PrometheusDataException.class.getCanonicalName(),
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
    PrometheusDBResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jPrometheus";
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

    /**
     * Build driver map.
     * @return the map
     */
    private static Map<PrometheusJDBCDriver, OceanusBundleId> buildDriverMap() {
        /* Create the map and return it */
        final Map<PrometheusJDBCDriver, OceanusBundleId> myMap = new EnumMap<>(PrometheusJDBCDriver.class);
        myMap.put(PrometheusJDBCDriver.SQLSERVER, DRIVER_SQLSERVER);
        myMap.put(PrometheusJDBCDriver.POSTGRESQL, DRIVER_POSTGRESQL);
        myMap.put(PrometheusJDBCDriver.MYSQL, DRIVER_MYSQL);
        myMap.put(PrometheusJDBCDriver.MARIADB, DRIVER_MARIADB);
        myMap.put(PrometheusJDBCDriver.H2, DRIVER_H2);
        return myMap;
    }

    /**
     * Obtain key for DBDriver.
     * @param pValue the Value
     * @return the resource key
     */
    static OceanusBundleId getKeyForDriver(final PrometheusJDBCDriver pValue) {
        return OceanusBundleLoader.getKeyForEnum(DRIVER_MAP, pValue);
    }
}
