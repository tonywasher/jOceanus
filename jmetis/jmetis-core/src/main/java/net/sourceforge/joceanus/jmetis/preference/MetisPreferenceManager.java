/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager class for preference sets.
 * @author Tony Washer
 */
public class MetisPreferenceManager
        implements MetisFieldItem, TethysEventProvider<MetisPreferenceEvent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetisPreferenceManager.class);

    /**
     * Report fields.
     */
    private final MetisFieldSet<MetisPreferenceManager> theFields;

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
        theFields = MetisFieldSet.newFieldSet(MetisPreferenceManager.this);
        theSecurityManager = new MetisPreferenceSecurity(this);
    }

    @Override
    public MetisFieldSet<MetisPreferenceManager> getDataFieldSet() {
        return theFields;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return theFields.getName();
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
     * @param pClazz the class of the preference set
     * @return the relevant preferenceSet
     */
    public <X extends MetisPreferenceSet<?>> X getPreferenceSet(final Class<X> pClazz) {
        /* Synchronise */
        synchronized (this) {
            /* Locate a cached PreferenceSet */
            final String myName = pClazz.getSimpleName();
            X mySet = pClazz.cast(theMap.get(myName));

            /* If we have not seen this set before */
            if (mySet == null) {
                /* Create the new preferenceSet */
                mySet = newPreferenceSet(myName, pClazz);
            }

            /* Return the PreferenceSet */
            return mySet;
        }
    }

    /**
     * Create a new preferenceSet.
     * @param <X> the preference set type
     * @param pName the name of the preference set
     * @param pClazz the class of the preference set
     * @return the new preferenceSet
     */
    private <X extends MetisPreferenceSet<?>> X newPreferenceSet(final String pName,
                                                                 final Class<X> pClazz) {
        /* Protect against exceptions */
        try {
            /* Obtain the relevant constructor */
            final Constructor<X> myConstructor = pClazz.getConstructor(getClass());

            /* Access the new set */
            final X mySet = myConstructor.newInstance(this);

            /* Cache the set */
            theMap.put(pName, mySet);

            /* Create the DataField */
            theFields.declareLocalField(pName, m -> mySet);

            /* Fire the action performed */
            theEventManager.fireEvent(MetisPreferenceEvent.NEWSET, mySet);

            /* Return the PreferenceSet */
            return mySet;

        } catch (IllegalAccessException
                | InstantiationException
                | NoSuchMethodException
                | SecurityException
                | IllegalArgumentException
                | InvocationTargetException e) {
            LOGGER.error(ERROR_LOAD, e);
            return null;
        }
    }
}
