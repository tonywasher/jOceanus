/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.base;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueKey;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;

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
     * Standard colour default.
     */
    private static final String DEFAULT_COLOR_STANDARD = "#000000";

    /**
     * Error colour default.
     */
    private static final String DEFAULT_COLOR_ERROR = "#ff0000";

    /**
     * Background colour default.
     */
    private static final String DEFAULT_COLOR_BACKGROUND = "#f5f5f5";

    /**
     * Disabled colour default.
     */
    private static final String DEFAULT_COLOR_DISABLED = "#778899";

    /**
     * Zebra colour default.
     */
    private static final String DEFAULT_COLOR_ZEBRA = "#e3e4fa";

    /**
     * Changed colour default.
     */
    private static final String DEFAULT_COLOR_CHANGED = "#8b008b";

    /**
     * Progress colour default.
     */
    private static final String DEFAULT_COLOR_PROGRESS = "#32cd32";

    /**
     * Link colour default.
     */
    private static final String DEFAULT_COLOR_LINK = "#c71585";

    /**
     * Value colour default.
     */
    private static final String DEFAULT_COLOR_VALUE = "#0000ff";

    /**
     * Negative colour default.
     */
    private static final String DEFAULT_COLOR_NEGATIVE = "#b22222";

    /**
     * Security colour default.
     */
    private static final String DEFAULT_COLOR_SECURITY = "#daa520";

    /**
     * Header colour default.
     */
    private static final String DEFAULT_COLOR_HEADER = "#0000cd";

    /**
     * Standard Font default.
     */
    private static final String DEFAULT_FONT_STANDARD = "Arial";

    /**
     * Numeric Font default.
     */
    private static final String DEFAULT_FONT_NUMERIC = "Courier";

    /**
     * Font Pitch default.
     */
    private static final String DEFAULT_FONT_PITCH = "12";

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

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
        theEventManager = new TethysEventManager<>();

        /* Create the default map */
        theDefaultMap = new HashMap<>();

        /* Create the default colour values */
        theDefaultMap.put(TethysUIValueKey.COLOR_STANDARD, DEFAULT_COLOR_STANDARD);
        theDefaultMap.put(TethysUIValueKey.COLOR_ERROR, DEFAULT_COLOR_ERROR);
        theDefaultMap.put(TethysUIValueKey.COLOR_BACKGROUND, DEFAULT_COLOR_BACKGROUND);
        theDefaultMap.put(TethysUIValueKey.COLOR_DISABLED, DEFAULT_COLOR_DISABLED);
        theDefaultMap.put(TethysUIValueKey.COLOR_ZEBRA, DEFAULT_COLOR_ZEBRA);
        theDefaultMap.put(TethysUIValueKey.COLOR_CHANGED, DEFAULT_COLOR_CHANGED);
        theDefaultMap.put(TethysUIValueKey.COLOR_PROGRESS, DEFAULT_COLOR_PROGRESS);
        theDefaultMap.put(TethysUIValueKey.COLOR_LINK, DEFAULT_COLOR_LINK);
        theDefaultMap.put(TethysUIValueKey.COLOR_VALUE, DEFAULT_COLOR_VALUE);
        theDefaultMap.put(TethysUIValueKey.COLOR_NEGATIVE, DEFAULT_COLOR_NEGATIVE);
        theDefaultMap.put(TethysUIValueKey.COLOR_SECURITY, DEFAULT_COLOR_SECURITY);
        theDefaultMap.put(TethysUIValueKey.COLOR_HEADER, DEFAULT_COLOR_HEADER);

        /* Create the default font values */
        theDefaultMap.put(TethysUIValueKey.FONT_STANDARD, DEFAULT_FONT_STANDARD);
        theDefaultMap.put(TethysUIValueKey.FONT_NUMERIC, DEFAULT_FONT_NUMERIC);
        theDefaultMap.put(TethysUIValueKey.FONT_PITCH, DEFAULT_FONT_PITCH);

        /* Initialise values from defaults */
        theValueMap = new HashMap<>(theDefaultMap);
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void applyColorMapping(final Map<String, String> pMappings) {
        /* Put all the mappings */
        theValueMap.putAll(pMappings);

        /* Notify of mapping changed */
        theEventManager.fireEvent(TethysUIXEvent.NEWVALUE, this);
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