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
package net.sourceforge.joceanus.jprometheus.atlas.preference;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for jPrometheus Preference Fields.
 */
public enum PrometheusPreferenceResource implements TethysBundleId {
    /**
     * Preference type BYTEARRAY.
     */
    TYPE_BYTEARRAY("preference.type.BYTEARRAY"),

    /**
     * Preference type CHARARRAY.
     */
    TYPE_CHARARRAY("preference.type.CHARARRAY"),

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
    DBPREF_PASS("dbpref.password"),

    /**
     * BackUpPreference Display Name.
     */
    BUPREF_PREFNAME("bupref.prefname"),

    /**
     * BackUpPreference Directory.
     */
    BUPREF_DIR("bupref.directory"),

    /**
     * BackUpPreference Prefix.
     */
    BUPREF_PFIX("bupref.prefix"),

    /**
     * BackUpPreference Type.
     */
    BUPREF_TYPE("bupref.type"),

    /**
     * BackUpPreference Archive File.
     */
    BUPREF_ARCHIVE("bupref.archive"),

    /**
     * BackUpPreference LastEvent.
     */
    BUPREF_EVENT("bupref.lastevent"),

    /**
     * BackUpPreference TimeStamps.
     */
    BUPREF_TIMESTAMP("bupref.timestamps"),

    /**
     * DataListPreference Display Name.
     */
    DLPREF_PREFNAME("dlpref.prefname"),

    /**
     * DataListPreference Granularity.
     */
    DLPREF_GRANULARITY("dlpref.granularity");

    /**
     * The Driver Map.
     */
    private static final Map<PrometheusJDBCDriver, TethysBundleId> DRIVER_MAP = buildDriverMap();

    /**
     * The PreferenceType Map.
     */
    private static final Map<PrometheusPreferenceType, TethysBundleId> PREF_MAP = buildPreferenceMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(PrometheusDataException.class.getCanonicalName(),
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
    PrometheusPreferenceResource(final String pKeyName) {
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
    private static Map<PrometheusJDBCDriver, TethysBundleId> buildDriverMap() {
        /* Create the map and return it */
        final Map<PrometheusJDBCDriver, TethysBundleId> myMap = new EnumMap<>(PrometheusJDBCDriver.class);
        myMap.put(PrometheusJDBCDriver.SQLSERVER, DRIVER_SQLSERVER);
        myMap.put(PrometheusJDBCDriver.POSTGRESQL, DRIVER_POSTGRESQL);
        myMap.put(PrometheusJDBCDriver.MYSQL, DRIVER_MYSQL);
        myMap.put(PrometheusJDBCDriver.H2, DRIVER_H2);
        return myMap;
    }

    /**
     * Obtain key for DBDriver.
     * @param pValue the Value
     * @return the resource key
     */
    static TethysBundleId getKeyForDriver(final PrometheusJDBCDriver pValue) {
        return TethysBundleLoader.getKeyForEnum(DRIVER_MAP, pValue);
    }

    /**
     * Build preference map.
     * @return the map
     */
    private static Map<PrometheusPreferenceType, TethysBundleId> buildPreferenceMap() {
        /* Create the map and return it */
        final Map<PrometheusPreferenceType, TethysBundleId> myMap = new EnumMap<>(PrometheusPreferenceType.class);
        myMap.put(PrometheusPreferenceType.BYTEARRAY, TYPE_BYTEARRAY);
        myMap.put(PrometheusPreferenceType.CHARARRAY, TYPE_CHARARRAY);
        return myMap;
    }

    /**
     * Obtain key for prefType.
     * @param pType the type
     * @return the resource key
     */
    public static TethysBundleId getKeyForPrefType(final PrometheusPreferenceType pType) {
        return TethysBundleLoader.getKeyForEnum(PREF_MAP, pType);
    }
}
