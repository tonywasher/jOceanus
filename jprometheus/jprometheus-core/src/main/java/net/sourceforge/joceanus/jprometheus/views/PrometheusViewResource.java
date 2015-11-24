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
package net.sourceforge.joceanus.jprometheus.views;

import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for jPrometheus View Fields.
 */
public enum PrometheusViewResource implements TethysResourceId {
    /**
     * DataEntry Underlying Data.
     */
    DATAENTRY_UNDERLYING("DataEntry.Underlying"),

    /**
     * DataEntry DataSet.
     */
    DATAENTRY_DATASET("DataEntry.DataSet"),

    /**
     * DataEntry Updates.
     */
    DATAENTRY_UPDATES("DataEntry.Updates"),

    /**
     * DataEntry Analysis.
     */
    DATAENTRY_ANALYSIS("DataEntry.Analysis"),

    /**
     * DataEntry DataViews.
     */
    DATAENTRY_VIEWS("DataEntry.DataViews"),

    /**
     * DataEntry Maintenance.
     */
    DATAENTRY_MAINT("DataEntry.Maint"),

    /**
     * DataEntry Error.
     */
    DATAENTRY_ERROR("DataEntry.Error"),

    /**
     * DataEntry Profile.
     */
    DATAENTRY_PROFILE("DataEntry.Profile"),

    /**
     * UpdateSet Name.
     */
    UPDATESET_NAME("UpdateSet.Name");

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(DataSet.class.getCanonicalName());

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
    PrometheusViewResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jPrometheus.views";
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
