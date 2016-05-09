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
package net.sourceforge.joceanus.jmetis.field.swing;

import java.awt.Color;

import net.sourceforge.joceanus.jmetis.field.MetisFieldColours.MetisColorPreferenceKey;
import net.sourceforge.joceanus.jmetis.field.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.jmetis.field.MetisFieldState;

/**
 * Render configuration.
 * @author Tony Washer
 */
public class MetisFieldConfig {
    /**
     * The error colour.
     */
    private final Color theErrorColor;

    /**
     * The changed colour.
     */
    private final Color theChangedColor;

    /**
     * The disabled colour.
     */
    private final Color theDisabledColor;

    /**
     * The zebra colour.
     */
    private final Color theZebraColor;

    /**
     * The standard colour.
     */
    private final Color theStandardColor;

    /**
     * The background colour.
     */
    private final Color theBackgroundColor;

    /**
     * The link colour.
     */
    private final Color theLinkColor;

    /**
     * The progress colour.
     */
    private final Color theProgressColor;

    /**
     * The value colour.
     */
    private final Color theValueColor;

    /**
     * The negative colour.
     */
    private final Color theNegativeColor;

    /**
     * Constructor.
     */
    public MetisFieldConfig() {
        theErrorColor = Color.red;
        theChangedColor = Color.magenta.darker();
        theDisabledColor = Color.lightGray;
        theZebraColor = Color.darkGray;
        theStandardColor = Color.black;
        theBackgroundColor = Color.white;
        theLinkColor = Color.blue;
        theValueColor = Color.blue;
        theNegativeColor = Color.red;
        theProgressColor = Color.green;
    }

    /**
     * Constructor.
     * @param pPreferences the color preferences
     */
    public MetisFieldConfig(final MetisColorPreferences pPreferences) {
        theErrorColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.ERROR));
        theChangedColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.CHANGED));
        theDisabledColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.DISABLED));
        theZebraColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.ZEBRA));
        theStandardColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.STANDARD));
        theBackgroundColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.BACKGROUND));
        theLinkColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.LINK));
        theValueColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.VALUE));
        theNegativeColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.NEGATIVE));
        theProgressColor = Color.decode(pPreferences.getStringValue(MetisColorPreferenceKey.PROGRESS));
    }

    /**
     * Get colour for render state.
     * @param pState the render state
     * @return the colour
     */
    public Color getColorForState(final MetisFieldState pState) {
        switch (pState) {
            case ERROR:
                return theErrorColor;
            case NEW:
            case CHANGED:
            case RESTORED:
                return theChangedColor;
            case NORMAL:
            case DELETED:
            default:
                return theStandardColor;
        }
    }

    /**
     * Get standard colour.
     * @return the colour
     */
    public Color getStandardColor() {
        return theStandardColor;
    }

    /**
     * Get disabled colour.
     * @return the colour
     */
    public Color getDisabledColor() {
        return theDisabledColor;
    }

    /**
     * Get changed colour.
     * @return the colour
     */
    public Color getChangedColor() {
        return theChangedColor;
    }

    /**
     * Get link colour.
     * @return the colour
     */
    public Color getLinkColor() {
        return theLinkColor;
    }

    /**
     * Get value colour.
     * @return the colour
     */
    public Color getValueColor() {
        return theValueColor;
    }

    /**
     * Get negative colour.
     * @return the colour
     */
    public Color getNegativeColor() {
        return theNegativeColor;
    }

    /**
     * Get background colour.
     * @return the colour
     */
    public Color getBackgroundColor() {
        return theBackgroundColor;
    }

    /**
     * Get error colour.
     * @return the colour
     */
    public Color getErrorColor() {
        return theErrorColor;
    }

    /**
     * Get zebra colour.
     * @return the colour
     */
    public Color getZebraColor() {
        return theZebraColor;
    }

    /**
     * Get progress colour.
     * @return the colour
     */
    public Color getProgressColor() {
        return theProgressColor;
    }
}
