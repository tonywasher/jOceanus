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
package net.sourceforge.joceanus.jprometheus.atlas.database;

import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferences;

/**
 * Databse config.
 */
public class PrometheusDBConfig {
    /**
     * Default user name.
     */
    private static final String DEFAULT_USER = "testUser";

    /**
     * Default password.
     */
    private static final char[] DEFAULT_PASS = "testPass".toCharArray();

    /**
     * Default server.
     */
    private static final String DEFAULT_SERVER = "localhost";

    /**
     * Default batchSize.
     */
    private static final int DEFAULT_BATCH = 50;

    /**
     * JDBC Driver.
     */
    private PrometheusJDBCDriver theDriver;

    /**
     * Username.
     */
    private String theUser;

    /**
     * Password.
     */
    private char[] thePassword;

    /**
     * Server.
     */
    private String theServer;

    /**
     * Instance.
     */
    private String theInstance;

    /**
     * BatchSize.
     */
    private int theBatch;

    /**
     * Set the driver.
     * @param pDriver the driver
     */
    public void setDriver(final PrometheusJDBCDriver pDriver) {
        theDriver = pDriver;
    }

    /**
     * Get driver.
     * @return the driver
     */
    public PrometheusJDBCDriver getDriver() {
        return theDriver;
    }

    /**
     * Set the user.
     * @param pUser the user
     */
    public void setUser(final String pUser) {
        theUser = pUser;
    }

    /**
     * Get user.
     * @return the user
     */
    public String getUser() {
        return theUser;
    }

    /**
     * Set the password.
     * @param pPassword the password
     */
    public void setPassword(final char[] pPassword) {
        thePassword = pPassword;
    }

    /**
     * Get password.
     * @return the password
     */
    public char[] getPassword() {
        return thePassword;
    }

    /**
     * Set the server.
     * @param pServer the server
     */
    public void setServer(final String pServer) {
        theServer = pServer;
    }

    /**
     * Get server.
     * @return the server
     */
    public String getServer() {
        return theServer;
    }

    /**
     * Set the instance.
     * @param pInstance the instance
     */
    public void setInstance(final String pInstance) {
        theInstance = pInstance;
    }

    /**
     * Get instance.
     * @return the instance
     */
    public String getInstance() {
        return theInstance;
    }

    /**
     * Set the batchSize.
     * @param pSize the batchSize
     */
    public void setBatchSize(final int pSize) {
        theBatch = pSize;
    }

    /**
     * Get batchSize.
     * @return the batchSize
     */
    public int getBatchSize() {
        return theBatch;
    }

    /**
     * Construct config from prefs.
     * @param pPreferences the preferences
     */
    public static PrometheusDBConfig fromPrefs(final PrometheusDatabasePreferences pPreferences) {
        final PrometheusDBConfig myConfig = new PrometheusDBConfig();
        myConfig.setDriver(pPreferences.getEnumValue(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class));
        myConfig.setUser(pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBUSER));
        myConfig.setPassword(pPreferences.getCharArrayValue(PrometheusDatabasePreferenceKey.DBPASS));
        myConfig.setServer(pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBSERVER));
        myConfig.setBatchSize(pPreferences.getIntegerValue(PrometheusDatabasePreferenceKey.DBBATCH));
        if (myConfig.getDriver().useInstance()) {
            myConfig.setInstance(pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBINSTANCE));
        }
        return myConfig;
    }

    /**
     * Construct a simple postgres config.
     * @return the config
     */
    public static PrometheusDBConfig postgres() {
        final PrometheusDBConfig myConfig = new PrometheusDBConfig();
        myConfig.setDriver(PrometheusJDBCDriver.POSTGRESQL);
        myConfig.setUser(DEFAULT_USER);
        myConfig.setPassword(DEFAULT_PASS);
        myConfig.setServer(DEFAULT_SERVER);
        myConfig.setBatchSize(DEFAULT_BATCH);
        return myConfig;
    }

    /**
     * Construct a simple mysql config.
     * @return the config
     */
    public static PrometheusDBConfig mysql() {
        final PrometheusDBConfig myConfig = new PrometheusDBConfig();
        myConfig.setDriver(PrometheusJDBCDriver.MYSQL);
        myConfig.setUser(DEFAULT_USER);
        myConfig.setPassword(DEFAULT_PASS);
        myConfig.setServer(DEFAULT_SERVER);
        myConfig.setBatchSize(DEFAULT_BATCH);
        return myConfig;
    }

    /**
     * Construct a simple selserver config.
     * @return the config
     */
    public static PrometheusDBConfig sqlserver() {
        final PrometheusDBConfig myConfig = new PrometheusDBConfig();
        myConfig.setDriver(PrometheusJDBCDriver.SQLSERVER);
        myConfig.setUser(DEFAULT_USER);
        myConfig.setPassword(DEFAULT_PASS);
        myConfig.setServer(DEFAULT_SERVER);
        myConfig.setInstance("SQLEXPRESS");
        myConfig.setBatchSize(DEFAULT_BATCH);
        return myConfig;
    }

    /**
     * Construct a simple h2 config.
     * @return the config
     */
    public static PrometheusDBConfig h2() {
        final PrometheusDBConfig myConfig = new PrometheusDBConfig();
        myConfig.setDriver(PrometheusJDBCDriver.H2);
        myConfig.setUser(DEFAULT_USER);
        myConfig.setPassword(DEFAULT_PASS);
        myConfig.setServer(DEFAULT_SERVER);
        myConfig.setBatchSize(DEFAULT_BATCH);
        return myConfig;
    }
}
