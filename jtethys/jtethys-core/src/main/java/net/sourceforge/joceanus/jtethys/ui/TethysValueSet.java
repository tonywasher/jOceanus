/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * A set of named colours.
 */
public class TethysValueSet
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The Variable start.
     */
    private static final String VAR_HDR = "${";

    /**
     * The Variable end.
     */
    private static final String VAR_TRL = "}";

    /**
     * The base style.
     */
    private static final String STYLE_BASE = "-jtethys";

    /**
     * The color style.
     */
    private static final String STYLE_COLOR = STYLE_BASE + "-color";

    /**
     * The font style.
     */
    private static final String STYLE_FONT = STYLE_BASE + "-font";

    /**
     * The standard colour name.
     */
    public static final String TETHYS_COLOR_STANDARD = STYLE_COLOR + "-standard";

    /**
     * The error colour name.
     */
    public static final String TETHYS_COLOR_ERROR = STYLE_COLOR + "-error";

    /**
     * The background colour name.
     */
    public static final String TETHYS_COLOR_BACKGROUND = STYLE_COLOR + "-background";

    /**
     * The disabled colour name.
     */
    public static final String TETHYS_COLOR_DISABLED = STYLE_COLOR + "-disabled";

    /**
     * The progress colour name.
     */
    public static final String TETHYS_COLOR_PROGRESS = STYLE_COLOR + "-progress";

    /**
     * The zebra colour name.
     */
    public static final String TETHYS_COLOR_ZEBRA = STYLE_COLOR + "-zebra";

    /**
     * The changed colour name.
     */
    public static final String TETHYS_COLOR_CHANGED = STYLE_COLOR + "-changed";

    /**
     * The link colour name.
     */
    public static final String TETHYS_COLOR_LINK = STYLE_COLOR + "-link";

    /**
     * The value colour name.
     */
    public static final String TETHYS_COLOR_VALUE = STYLE_COLOR + "-data";

    /**
     * The negative colour name.
     */
    public static final String TETHYS_COLOR_NEGATIVE = STYLE_COLOR + "-negative";

    /**
     * The standard font family name.
     */
    public static final String TETHYS_FONT_STANDARD = STYLE_FONT + "-standard";

    /**
     * The standard font pitch.
     */
    public static final String TETHYS_FONT_PITCH = STYLE_FONT + "-pitch";

    /**
     * The numeric font family name.
     */
    public static final String TETHYS_FONT_NUMERIC = STYLE_FONT + "-numeric";

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
    private final TethysEventManager<TethysUIEvent> theEventManager;

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
    public TethysValueSet() {
        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the default map */
        theDefaultMap = new HashMap<>();

        /* Create the default colour values */
        theDefaultMap.put(TETHYS_COLOR_STANDARD, DEFAULT_COLOR_STANDARD);
        theDefaultMap.put(TETHYS_COLOR_ERROR, DEFAULT_COLOR_ERROR);
        theDefaultMap.put(TETHYS_COLOR_BACKGROUND, DEFAULT_COLOR_BACKGROUND);
        theDefaultMap.put(TETHYS_COLOR_DISABLED, DEFAULT_COLOR_DISABLED);
        theDefaultMap.put(TETHYS_COLOR_ZEBRA, DEFAULT_COLOR_ZEBRA);
        theDefaultMap.put(TETHYS_COLOR_CHANGED, DEFAULT_COLOR_CHANGED);
        theDefaultMap.put(TETHYS_COLOR_PROGRESS, DEFAULT_COLOR_PROGRESS);
        theDefaultMap.put(TETHYS_COLOR_LINK, DEFAULT_COLOR_LINK);
        theDefaultMap.put(TETHYS_COLOR_VALUE, DEFAULT_COLOR_VALUE);
        theDefaultMap.put(TETHYS_COLOR_NEGATIVE, DEFAULT_COLOR_NEGATIVE);

        /* Create the default font values */
        theDefaultMap.put(TETHYS_FONT_STANDARD, DEFAULT_FONT_STANDARD);
        theDefaultMap.put(TETHYS_FONT_NUMERIC, DEFAULT_FONT_NUMERIC);
        theDefaultMap.put(TETHYS_FONT_PITCH, DEFAULT_FONT_PITCH);

        /* Initialise values from defaults */
        theValueMap = new HashMap<>(theDefaultMap);
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Apply new Mappings.
     * @param pMappings the colour mappings
     */
    public void applyColorMapping(final Map<String, String> pMappings) {
        /* Loop through the mappings */
        Iterator<Map.Entry<String, String>> myIterator = pMappings.entrySet().iterator();
        while (myIterator.hasNext()) {
            Map.Entry<String, String> myMapping = myIterator.next();
            theValueMap.put(myMapping.getKey(), myMapping.getValue());
        }

        /* Notify of mapping changed */
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE, this);
    }

    /**
     * Get value for key.
     * @param pKey the value key
     * @return the relevant value
     */
    public String getValueForKey(final String pKey) {
        return theValueMap.get(pKey);
    }

    /**
     * Get default value for key.
     * @param pKey the value key
     * @return the relevant value
     */
    public String getDefaultValueForKey(final String pKey) {
        return theDefaultMap.get(pKey);
    }

    /**
     * Resolve values.
     * @param pSource the source string
     * @return the resolved string
     */
    public String resolveValues(final String pSource) {
        /* Allocate a string builder */
        StringBuilder myBuilder = new StringBuilder(pSource);

        /* Note wrapper lengths */
        int iHdrLen = VAR_HDR.length();
        int iTrlLen = VAR_TRL.length();

        /* Loop forever */
        for (;;) {
            /* Search for variable and break loop if none found */
            int iStart = myBuilder.indexOf(VAR_HDR);
            int iEnd = iStart == -1
                                    ? -1
                                    : myBuilder.indexOf(VAR_TRL, iStart + iHdrLen);
            if (iEnd == -1) {
                break;
            }

            /* Obtain the variable name and delete the reference */
            String myName = myBuilder.substring(iStart + iHdrLen, iEnd);
            myBuilder.delete(iStart, iEnd + iTrlLen);

            /* Obtain the value and insert it if found */
            String myValue = theValueMap.get(myName);
            if (myValue != null) {
                myBuilder.insert(iStart, myValue);
            }
        }

        /* Return the amended string */
        return myBuilder.toString();
    }
}
