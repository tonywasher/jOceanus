/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.core.thread;

import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;
import net.sourceforge.joceanus.tethys.core.base.TethysUIResource;

import java.util.ResourceBundle;

/**
 * Thread Resources.
 */
public enum TethysUIThreadResource implements OceanusBundleId {
    /**
     * StatusBar Cancel Button.
     */
    STATUSBAR_BUTTON_CANCEL("StatusBar.Button.Cancel"),

    /**
     * StatusBar Clear Button.
     */
    STATUSBAR_BUTTON_CLEAR("StatusBar.Button.Clear"),

    /**
     * StatusBar Progress Title.
     */
    STATUSBAR_TITLE_PROGRESS("StatusBar.Title.Progress"),

    /**
     * StatusBar Status Title.
     */
    STATUSBAR_TITLE_STATUS("StatusBar.Title.Status"),

    /**
     * StatusBar Success Status.
     */
    STATUSBAR_STATUS_SUCCESS("StatusBar.Status.Succeeded"),

    /**
     * StatusBar Success Status.
     */
    STATUSBAR_STATUS_FAIL("StatusBar.Status.Failed"),

    /**
     * StatusBar Cancel Status.
     */
    STATUSBAR_STATUS_CANCEL("StatusBar.Status.Cancelled");

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(TethysUIResource.class.getCanonicalName(),
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
    TethysUIThreadResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "ui.thread";
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
}
