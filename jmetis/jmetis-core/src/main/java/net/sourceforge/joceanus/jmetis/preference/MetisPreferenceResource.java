/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for JMetis preferences.
 */
public enum MetisPreferenceResource implements TethysBundleId {
    /**
     * Preference type STRING.
     */
    TYPE_STRING("type.STRING"),

    /**
     * Preference type INTEGER.
     */
    TYPE_INTEGER("type.INTEGER"),

    /**
     * Preference type BOOLEAN.
     */
    TYPE_BOOLEAN("type.BOOLEAN"),

    /**
     * Preference type DATE.
     */
    TYPE_DATE("type.DATE"),

    /**
     * Preference type FILE.
     */
    TYPE_FILE("type.FILE"),

    /**
     * Preference type DIRECTORY.
     */
    TYPE_DIRECTORY("type.DIRECTORY"),

    /**
     * Preference type ENUM.
     */
    TYPE_ENUM("type.ENUM"),

    /**
     * Preference type COLOR.
     */
    TYPE_COLOR("type.COLOR"),

    /**
     * OK button text.
     */
    UI_BUTTON_OK("ui.button.Ok"),

    /**
     * Reset button text.
     */
    UI_BUTTON_RESET("ui.button.Reset"),

    /**
     * Save Title text.
     */
    UI_TITLE_SAVE("ui.title.Save"),

    /**
     * Select Title text.
     */
    UI_TITLE_SELECT("ui.title.Select"),

    /**
     * Preferences Title text.
     */
    UI_TITLE_PREFERENCES("ui.title.Preferences"),

    /**
     * Options Title text.
     */
    UI_TITLE_OPTIONS("ui.title.Options"),

    /**
     * Colour Title text.
     */
    UI_TITLE_COLOR("ui.title.Color"),

    /**
     * Colour Prompt text.
     */
    UI_PROMPT_COLOR("ui.prompt.Color"),

    /**
     * PreferenceSet label text.
     */
    UI_LABEL_SET("ui.label.Set"),

    /**
     * Range minimum.
     */
    UI_RANGE_MIN("ui.range.Minimum"),

    /**
     * Range maximum.
     */
    UI_RANGE_MAX("ui.range.Maximum"),

    /**
     * Range error.
     */
    UI_RANGE_ERROR("ui.range.Error"),

    /**
     * Store Error text.
     */
    UI_ERROR_STORE("ui.error.Store"),

    /**
     * Select header text.
     */
    UI_HEADER_SELECT("ui.header.Select"),

    /**
     * SecurityPreference Display Name.
     */
    SECPREF_BASEPREFNAME("secpref.baseprefname"),

    /**
     * SecurityPreference Display Name.
     */
    SECPREF_PREFNAME("secpref.prefname"),

    /**
     * SecurityPreference Factory.
     */
    SECPREF_FACTORY("secpref.factory"),

    /**
     * SecurityPreference keyLength.
     */
    SECPREF_KEYLEN("secpref.keylen"),

    /**
     * SecurityPreference Cipher Steps.
     */
    SECPREF_CIPHERSTEPS("secpref.ciphersteps"),

    /**
     * SecurityPreference Hash Iterations.
     */
    SECPREF_ITERATIONS("secpref.hashiterations"),

    /**
     * SecurityPreference Phrase.
     */
    SECPREF_PHRASE("secpref.phrase"),

    /**
     * SecurityPreference Active KeySets.
     */
    SECPREF_KEYSETS("secpref.keysets");

    /**
     * The PreferenceType Map.
     */
    private static final Map<MetisPreferenceType, TethysBundleId> PREF_MAP = buildPreferenceMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MetisDataException.class.getCanonicalName(),
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
    MetisPreferenceResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.preference";
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
     * Build preference map.
     * @return the map
     */
    private static Map<MetisPreferenceType, TethysBundleId> buildPreferenceMap() {
        /* Create the map and return it */
        final Map<MetisPreferenceType, TethysBundleId> myMap = new EnumMap<>(MetisPreferenceType.class);
        myMap.put(MetisPreferenceType.STRING, TYPE_STRING);
        myMap.put(MetisPreferenceType.INTEGER, TYPE_INTEGER);
        myMap.put(MetisPreferenceType.BOOLEAN, TYPE_BOOLEAN);
        myMap.put(MetisPreferenceType.DATE, TYPE_DATE);
        myMap.put(MetisPreferenceType.FILE, TYPE_FILE);
        myMap.put(MetisPreferenceType.DIRECTORY, TYPE_DIRECTORY);
        myMap.put(MetisPreferenceType.ENUM, TYPE_ENUM);
        myMap.put(MetisPreferenceType.COLOR, TYPE_COLOR);
        return myMap;
    }

    /**
     * Obtain key for prefType.
     * @param pType the type
     * @return the resource key
     */
    public static TethysBundleId getKeyForPrefType(final MetisPreferenceType pType) {
        return TethysBundleLoader.getKeyForEnum(PREF_MAP, pType);
    }
}
