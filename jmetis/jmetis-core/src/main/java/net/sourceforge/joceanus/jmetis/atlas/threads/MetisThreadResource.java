/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.threads;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Thread Resources.
 */
public enum MetisThreadResource implements TethysResourceId {
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
    STATUSBAR_STATUS_CANCEL("StatusBar.Status.Cancelled"),

    /**
     * ThreadPreference Display Name.
     */
    THDPREF_PREFNAME("thdpref.prefname"),

    /**
     * ThreadPreference Reporting.
     */
    THDPREF_REPORTING("thdpref.reporting");

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
    MetisThreadResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.thread";
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
}
