/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Program Definitions.
 */
public abstract class TethysProgram {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysProgram.class);

    /**
     * APP Key.
     */
    private static final String PFX_APP = "App.";

    /**
     * Version Key.
     */
    private static final String PFX_VERS = "Version.";

    /**
     * Name Key.
     */
    private static final String KEY_NAME = PFX_APP + "name";

    /**
     * Version Key.
     */
    private static final String KEY_VERSION = PFX_APP + "version";

    /**
     * Revision Key.
     */
    private static final String KEY_REVISION = PFX_APP + "revision";

    /**
     * Copyright Key.
     */
    private static final String KEY_COPYRIGHT = PFX_APP + "copyright";

    /**
     * TimeStamp Key.
     */
    private static final String KEY_BUILTON = PFX_APP + "timeStamp";

    /**
     * Program Details.
     */
    private final Map<String, String> theDetails;

    /**
     * Program Dependencies.
     */
    private final Map<String, String> theDependencies;

    /**
     * Constructor.
     * @param pProperties the inputStream of the properties
     */
    protected TethysProgram(final InputStream pProperties) {
        /* Create the maps */
        theDetails = new LinkedHashMap<>();
        theDependencies = new LinkedHashMap<>();

        /* Protect against exceptions */
        try {
            /* Load the properties */
            final Properties myProperties = new Properties();
            myProperties.load(pProperties);

            /* Load values */
            loadValues(myProperties);

            /* Catch Exceptions */
        } catch (IOException e) {
            LOGGER.error("Failed to load properties", e);
        }
    }

    /**
     * Load the values.
     * @param pProperties the properties
     */
    private void loadValues(final Properties pProperties) {
        /* Loop through the property names */
        for (String myName : pProperties.stringPropertyNames()) {
            /* If this is an Application property */
            if (myName.startsWith(PFX_APP)) {
                theDetails.put(myName, pProperties.getProperty(myName));

                /* If this is a Version property */
            } else if (myName.startsWith(PFX_VERS)) {
                theDependencies.put(myName.substring(PFX_VERS.length()), pProperties.getProperty(myName));
            }
        }
    }

    /**
     * Obtain the program iconIds.
     * @return the icon Ids.
     */
    public abstract TethysIconId[] getIcons();

    /**
     * Obtain the splash iconId.
     * @return the iconId.
     */
    public abstract TethysIconId getSplash();

    /**
     * Obtain program name.
     * @return the name.
     */
    public String getName() {
        return theDetails.get(KEY_NAME);
    }

    /**
     * Obtain program version.
     * @return the version.
     */
    public String getVersion() {
        return theDetails.get(KEY_VERSION);
    }

    /**
     * Obtain program revision.
     * @return the revision.
     */
    public String getRevision() {
        return theDetails.get(KEY_REVISION);
    }

    /**
     * Obtain program copyright.
     * @return the copyright.
     */
    public String getCopyright() {
        return theDetails.get(KEY_COPYRIGHT);
    }

    /**
     * Obtain program build date.
     * @return the build date.
     */
    public String getBuiltOn() {
        return theDetails.get(KEY_BUILTON);
    }

    /**
     * Obtain iterator for the dependencies.
     * @return the iterator.
     */
    public Iterator<Entry<String, String>> dependencyIterator() {
        return theDependencies.entrySet().iterator();
    }
}
