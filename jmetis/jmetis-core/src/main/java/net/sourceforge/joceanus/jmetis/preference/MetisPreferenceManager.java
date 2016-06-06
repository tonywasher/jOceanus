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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Manager class for preference sets.
 * @author Tony Washer
 */
public class MetisPreferenceManager
        implements MetisDataContents, TethysEventProvider<MetisPreferenceEvent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisPreferenceManager.class);

    /**
     * Report fields.
     */
    private final MetisFields theFields = new MetisFields(MetisPreferenceManager.class.getSimpleName());

    /**
     * Load error text.
     */
    private static final String ERROR_LOAD = "Failed to load preference Set";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * Viewer Manager.
     */
    private final MetisViewerManager theViewerManager;

    /**
     * The Security Manager.
     */
    private final MetisPreferenceSecurity theSecurityManager;

    /**
     * Map of preferenceSets.
     */
    private Map<String, MetisPreferenceSet<?>> theMap = new HashMap<>();

    /**
     * Constructor.
     * @param pViewer the viewer manager
     * @throws OceanusException on error
     */
    public MetisPreferenceManager(final MetisViewerManager pViewer) throws OceanusException {
        theViewerManager = pViewer;
        theEventManager = new TethysEventManager<>();
        theSecurityManager = new MetisPreferenceSecurity(this);
    }

    @Override
    public MetisFields getDataFields() {
        return theFields;
    }

    @Override
    public String formatObject() {
        return theFields.getName();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Access preference set */
        MetisPreferenceSet<?> mySet = theMap.get(pField.getName());

        /* Return the value */
        return (mySet == null)
                               ? MetisFieldValue.UNKNOWN
                               : mySet;
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the security manager.
     * @return the security manager
     */
    protected MetisPreferenceSecurity getSecurity() {
        return theSecurityManager;
    }

    /**
     * Obtain the viewer manager.
     * @return the viewer manager
     */
    protected MetisViewerManager getViewer() {
        return theViewerManager;
    }

    /**
     * Obtain the collection of preference sets.
     * @return the preference sets
     */
    public Collection<MetisPreferenceSet<?>> getPreferenceSets() {
        return theMap.values();
    }

    /**
     * Obtain the preference set for the calling class.
     * @param <X> the preference set type
     * @param pClass the class of the preference set
     * @return the relevant preferenceSet
     */
    public synchronized <X extends MetisPreferenceSet<?>> X getPreferenceSet(final Class<X> pClass) {
        /* Locate a cached PreferenceSet */
        String myName = pClass.getSimpleName();
        X mySet = pClass.cast(theMap.get(myName));

        /* If we have not seen this set before */
        if (mySet == null) {
            /* Protect against exceptions */
            try {
                /* Obtain the relevant constructor */
                Constructor<X> myConstructor = pClass.getConstructor(getClass());

                /* Access the new set */
                mySet = myConstructor.newInstance(this);

                /* Cache the set */
                theMap.put(myName, mySet);

                /* Create the DataField */
                theFields.declareLocalField(myName);

                /* Fire the action performed */
                theEventManager.fireEvent(MetisPreferenceEvent.NEWSET, mySet);

            } catch (IllegalAccessException
                    | InstantiationException
                    | NoSuchMethodException
                    | SecurityException
                    | IllegalArgumentException
                    | InvocationTargetException e) {
                LOGGER.error(ERROR_LOAD, e);
            }
        }

        /* Return the PreferenceSet */
        return mySet;
    }
}
