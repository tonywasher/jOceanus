/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.core.base;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIValueKey;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;

/**
 * A set of named colours.
 */
public class TethysUICoreValueSet
        implements TethysUIValueSet {
    /**
     * The Variable start.
     */
    private static final String VAR_HDR = "${";

    /**
     * The Variable end.
     */
    private static final String VAR_TRL = "}";

    /**
     * The event manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The map of the default values.
     */
    private final Map<String, String> theDefaultMap;

    /**
     * The map of the values.
     */
    private final Map<String, String> theValueMap;

    /**
     * Constructor.
     */
    public TethysUICoreValueSet() {
        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the default map */
        theDefaultMap = new HashMap<>();

        /* Create the default colour values */
        theDefaultMap.put(TethysUIValueKey.COLOR_STANDARD, TethysUIValueKey.DEFAULT_COLOR_STANDARD);
        theDefaultMap.put(TethysUIValueKey.COLOR_ERROR, TethysUIValueKey.DEFAULT_COLOR_ERROR);
        theDefaultMap.put(TethysUIValueKey.COLOR_BACKGROUND, TethysUIValueKey.DEFAULT_COLOR_BACKGROUND);
        theDefaultMap.put(TethysUIValueKey.COLOR_DISABLED, TethysUIValueKey.DEFAULT_COLOR_DISABLED);
        theDefaultMap.put(TethysUIValueKey.COLOR_ZEBRA, TethysUIValueKey.DEFAULT_COLOR_ZEBRA);
        theDefaultMap.put(TethysUIValueKey.COLOR_CHANGED, TethysUIValueKey.DEFAULT_COLOR_CHANGED);
        theDefaultMap.put(TethysUIValueKey.COLOR_PROGRESS, TethysUIValueKey.DEFAULT_COLOR_PROGRESS);
        theDefaultMap.put(TethysUIValueKey.COLOR_LINK, TethysUIValueKey.DEFAULT_COLOR_LINK);
        theDefaultMap.put(TethysUIValueKey.COLOR_VALUE, TethysUIValueKey.DEFAULT_COLOR_VALUE);
        theDefaultMap.put(TethysUIValueKey.COLOR_NEGATIVE, TethysUIValueKey.DEFAULT_COLOR_NEGATIVE);
        theDefaultMap.put(TethysUIValueKey.COLOR_SECURITY, TethysUIValueKey.DEFAULT_COLOR_SECURITY);
        theDefaultMap.put(TethysUIValueKey.COLOR_HEADER, TethysUIValueKey.DEFAULT_COLOR_HEADER);

        /* Create the default font values */
        theDefaultMap.put(TethysUIValueKey.FONT_STANDARD, TethysUIValueKey.DEFAULT_FONT_STANDARD);
        theDefaultMap.put(TethysUIValueKey.FONT_NUMERIC, TethysUIValueKey.DEFAULT_FONT_NUMERIC);
        theDefaultMap.put(TethysUIValueKey.FONT_PITCH, TethysUIValueKey.DEFAULT_FONT_PITCH);

        /* Initialise values from defaults */
        theValueMap = new HashMap<>(theDefaultMap);
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void applyColorMapping(final Map<String, String> pMappings) {
        /* Put all the mappings */
        theValueMap.putAll(pMappings);

        /* Notify of mapping changed */
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, this);
    }

    @Override
    public String getValueForKey(final String pKey) {
        return theValueMap.get(pKey);
    }

    @Override
    public String getDefaultValueForKey(final String pKey) {
        return theDefaultMap.get(pKey);
    }

    @Override
    public String resolveValues(final String pSource) {
        /* Allocate a string builder */
        final StringBuilder myBuilder = new StringBuilder(pSource);

        /* Note wrapper lengths */
        final int iHdrLen = VAR_HDR.length();
        final int iTrlLen = VAR_TRL.length();

        /* Loop forever */
        for (;;) {
            /* Search for variable and break loop if none found */
            final int iStart = myBuilder.indexOf(VAR_HDR);
            final int iEnd = iStart == -1
                    ? -1
                    : myBuilder.indexOf(VAR_TRL, iStart + iHdrLen);
            if (iEnd == -1) {
                break;
            }

            /* Obtain the variable name and delete the reference */
            final String myName = myBuilder.substring(iStart + iHdrLen, iEnd);
            myBuilder.delete(iStart, iEnd + iTrlLen);

            /* Obtain the value and insert it if found */
            final String myValue = theValueMap.get(myName);
            if (myValue != null) {
                myBuilder.insert(iStart, myValue);
            }
        }

        /* Return the amended string */
        return myBuilder.toString();
    }
}
