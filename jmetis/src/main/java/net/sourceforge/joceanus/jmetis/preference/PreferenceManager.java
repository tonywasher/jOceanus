/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jtethys.event.JEventObject;

/**
 * Manager class for preference sets.
 * @author Tony Washer
 */
public class PreferenceManager
        extends JEventObject
        implements JDataContents {
    /**
     * Report fields.
     */
    private final JDataFields theFields = new JDataFields(PreferenceManager.class.getSimpleName());

    /**
     * Load error text.
     */
    private static final String ERROR_LOAD = "Failed to load preference Set";

    @Override
    public JDataFields getDataFields() {
        return theFields;
    }

    @Override
    public String formatObject() {
        return theFields.getName();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Access preference set */
        PreferenceSet mySet = theMap.get(pField.getName());

        /* Return the value */
        return (mySet == null)
                ? JDataFieldValue.UNKNOWN
                : mySet;
    }

    /**
     * Map of preferenceSets.
     */
    private Map<String, PreferenceSet> theMap = new HashMap<String, PreferenceSet>();

    /**
     * Logger.
     */
    private final Logger theLogger;

    /**
     * Obtain the collection of preference sets.
     * @return the preference sets
     */
    public Collection<PreferenceSet> getPreferenceSets() {
        return theMap.values();
    }

    /**
     * Obtain logger.
     * @return the logger
     */
    public Logger getLogger() {
        return theLogger;
    }

    /**
     * Constructor.
     * @param pLogger the logger.
     */
    public PreferenceManager(final Logger pLogger) {
        /* Store the logger */
        theLogger = pLogger;
    }

    /**
     * Obtain the preference set for the calling class.
     * @param <X> the preference set type
     * @param pClass the class of the preference set
     * @return the relevant preferenceSet
     */
    public synchronized <X extends PreferenceSet> X getPreferenceSet(final Class<X> pClass) {
        /* Locate a cached PreferenceSet */
        String myName = pClass.getSimpleName();
        X mySet = pClass.cast(theMap.get(myName));

        /* If we have not seen this set before */
        if (mySet == null) {
            /* Protect against exceptions */
            try {
                /* Access the new set */
                mySet = pClass.newInstance();

                /* Cache the set */
                theMap.put(myName, mySet);

                /* Create the DataField */
                theFields.declareLocalField(myName);

                /* Fire the action performed */
                fireActionEvent(ActionEvent.ACTION_PERFORMED, mySet);
            } catch (IllegalAccessException e) {
                theLogger.log(Level.SEVERE, ERROR_LOAD, e);
                mySet = null;
            } catch (InstantiationException e) {
                theLogger.log(Level.SEVERE, ERROR_LOAD, e);
                mySet = null;
            }
        }

        /* Return the PreferenceSet */
        return mySet;
    }
}
