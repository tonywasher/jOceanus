/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.preference.swing;

import java.awt.Color;

import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldConfig;
import net.sourceforge.joceanus.jmetis.preference.swing.MetisSwingPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Preferences for JFieldSet.
 */
public class JFieldPreferences
        extends MetisSwingPreferenceSet {
    /**
     * Registry name for Standard foreground.
     */
    public static final String NAME_STANDARD = "ColorStandard";

    /**
     * Registry name for Standard background.
     */
    public static final String NAME_BACKGROUND = "ColorBackground";

    /**
     * Registry name for Error foreground.
     */
    public static final String NAME_ERROR = "ColorError";

    /**
     * Registry name for New foreground.
     */
    public static final String NAME_NEW = "ColorNew";

    /**
     * Registry name for Changed foreground.
     */
    public static final String NAME_CHANGED = "ColorChanged";

    /**
     * Registry name for Disabled foreground.
     */
    public static final String NAME_DISABLED = "ColorDisabled";

    /**
     * Registry name for Recovered foreground.
     */
    public static final String NAME_ZEBRA = "ColorZebra";

    /**
     * Registry name for Link foreground.
     */
    public static final String NAME_LINK = "ColorLink";

    /**
     * Registry name for Changed Link foreground.
     */
    public static final String NAME_CHGLINK = "ColorChangedLink";

    /**
     * Display name for Standard foreground.
     */
    private static final String DISPLAY_STANDARD = "Standard Colour";

    /**
     * Display name for Standard background.
     */
    private static final String DISPLAY_BACKGROUND = "Background Colour";

    /**
     * Display name for Error foreground.
     */
    private static final String DISPLAY_ERROR = "Error Colour";

    /**
     * Display name for New foreground.
     */
    private static final String DISPLAY_NEW = "New Colour";

    /**
     * Display name for Changed foreground.
     */
    private static final String DISPLAY_CHANGED = "Changed Colour";

    /**
     * Display name for Disabled foreground.
     */
    private static final String DISPLAY_DISABLED = "Disabled Colour";

    /**
     * Display name for Zebra background.
     */
    private static final String DISPLAY_ZEBRA = "Zebra Colour";

    /**
     * Display name for Link foreground.
     */
    private static final String DISPLAY_LINK = "Link Colour";

    /**
     * Display name for Changed Link foreground.
     */
    private static final String DISPLAY_CHGLINK = "Changed Link Colour";

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public JFieldPreferences() throws OceanusException {
        super();
    }

    /**
     * Obtain configuration.
     * @return the render configuration
     */
    public MetisFieldConfig getConfiguration() {
        /* Allocate the configuration */
        MetisFieldConfig myConfig = new MetisFieldConfig();

        /* Set the values */
        myConfig.setBackgroundColor(getColorValue(NAME_BACKGROUND));
        myConfig.setChangedColor(getColorValue(NAME_CHANGED));
        myConfig.setDisabledColor(getColorValue(NAME_DISABLED));
        myConfig.setErrorColor(getColorValue(NAME_ERROR));
        myConfig.setNewColor(getColorValue(NAME_NEW));
        myConfig.setZebraColor(getColorValue(NAME_ZEBRA));
        myConfig.setStandardColor(getColorValue(NAME_STANDARD));
        myConfig.setLinkColor(getColorValue(NAME_LINK));
        myConfig.setChgLinkColor(getColorValue(NAME_CHGLINK));

        /* Return the configuration */
        return myConfig;
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        defineColorPreference(NAME_STANDARD, Color.black);
        defineColorPreference(NAME_BACKGROUND, Color.white);
        defineColorPreference(NAME_ERROR, Color.red);
        defineColorPreference(NAME_NEW, Color.blue);
        defineColorPreference(NAME_CHANGED, Color.magenta.darker());
        defineColorPreference(NAME_DISABLED, Color.gray);
        defineColorPreference(NAME_ZEBRA, Color.decode("#e3e4fa"));
        defineColorPreference(NAME_LINK, Color.blue);
        defineColorPreference(NAME_CHGLINK, Color.green);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_STANDARD)) {
            return DISPLAY_STANDARD;
        }
        if (pName.equals(NAME_BACKGROUND)) {
            return DISPLAY_BACKGROUND;
        }
        if (pName.equals(NAME_ERROR)) {
            return DISPLAY_ERROR;
        }
        if (pName.equals(NAME_NEW)) {
            return DISPLAY_NEW;
        }
        if (pName.equals(NAME_CHANGED)) {
            return DISPLAY_CHANGED;
        }
        if (pName.equals(NAME_DISABLED)) {
            return DISPLAY_DISABLED;
        }
        if (pName.equals(NAME_ZEBRA)) {
            return DISPLAY_ZEBRA;
        }
        if (pName.equals(NAME_LINK)) {
            return DISPLAY_LINK;
        }
        if (pName.equals(NAME_CHGLINK)) {
            return DISPLAY_CHGLINK;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
