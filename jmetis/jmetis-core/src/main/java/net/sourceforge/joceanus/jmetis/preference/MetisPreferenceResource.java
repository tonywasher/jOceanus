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

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for JMetis preferences.
 */
public enum MetisPreferenceResource implements TethysResourceId {
    /**
     * valueClass STRING.
     */
    VALUE_STRING("valueClass.STRING"),

    /**
     * valueClass INTEGER.
     */
    VALUE_INTEGER("valueClass.INTEGER"),

    /**
     * valueClass MONEY.
     */
    VALUE_MONEY("valueClass.MONEY"),

    /**
     * valueClass MONEY.
     */
    VALUE_RATE("valueClass.RATE"),

    /**
     * valueClass UNITS.
     */
    VALUE_UNITS("valueClass.UNITS"),

    /**
     * valueClass PRICE.
     */
    VALUE_PRICE("valueClass.PRICE"),

    /**
     * valueClass DILUTION.
     */
    VALUE_DILUTION("valueClass.DILUTION"),

    /**
     * valueClass CHARARRAY.
     */
    VALUE_CHARARRAY("valueClass.CHARARRAY"),

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
     * Preference type FONT.
     */
    TYPE_FONT("type.FONT"),

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
     * Store Error text.
     */
    UI_ERROR_STORE("ui.error.Store"),

    /**
     * Select header text.
     */
    UI_HEADER_SELECT("ui.header.Select");

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(MetisDataException.class.getCanonicalName());

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
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Obtain key for valueClass.
     * @param pValue the Value
     * @return the resource key
     */
    protected static TethysResourceId getKeyForValueClass(final MetisPreferenceValueClass pValue) {
        switch (pValue) {
            case STRING:
                return VALUE_STRING;
            case INTEGER:
                return VALUE_INTEGER;
            case MONEY:
                return VALUE_MONEY;
            case RATE:
                return VALUE_RATE;
            case UNITS:
                return VALUE_UNITS;
            case PRICE:
                return VALUE_PRICE;
            case DILUTION:
                return VALUE_DILUTION;
            case CHARARRAY:
                return VALUE_CHARARRAY;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pValue));
        }
    }

    /**
     * Obtain key for prefType.
     * @param pType the type
     * @return the resource key
     */
    protected static TethysResourceId getKeyForPrefType(final MetisPreferenceType pType) {
        switch (pType) {
            case STRING:
                return TYPE_STRING;
            case INTEGER:
                return TYPE_INTEGER;
            case BOOLEAN:
                return TYPE_BOOLEAN;
            case DATE:
                return TYPE_DATE;
            case FILE:
                return TYPE_FILE;
            case DIRECTORY:
                return TYPE_DIRECTORY;
            case ENUM:
                return TYPE_ENUM;
            case COLOR:
                return TYPE_COLOR;
            case FONT:
                return TYPE_FONT;
            default:
                throw new IllegalArgumentException(TethysResourceBuilder.getErrorNoResource(pType));
        }
    }
}
