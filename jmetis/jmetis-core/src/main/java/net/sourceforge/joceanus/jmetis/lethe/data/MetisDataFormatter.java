/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.data;

import java.util.Locale;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataFormat;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Generic Data object formatter.
 * @author Tony Washer
 */
public class MetisDataFormatter
        extends TethysDataFormatter {
    /**
     * Format object interface.
     */
    @FunctionalInterface
    public interface MetisDataObjectFormat {
        /**
         * Obtain Object summary.
         * @param pFormatter the data formatter
         * @return the display summary of the object
         */
        String formatObject(MetisDataFormatter pFormatter);
    }

    /**
     * Constructor.
     */
    public MetisDataFormatter() {
        this(Locale.getDefault());
    }

    /**
     * Constructor for a locale.
     * @param pLocale the locale
     */
    public MetisDataFormatter(final Locale pLocale) {
        super(pLocale);
    }

    @Override
    public String formatObject(final Object pValue) {
        /* Handle null value */
        if (pValue == null) {
            return null;
        }

        /* Handle ones that we can directly format */
        if (MetisDataFormat.class.isInstance(pValue)) {
            return ((MetisDataFormat) pValue).formatObject();
        }
        if (MetisDataObjectFormat.class.isInstance(pValue)) {
            return ((MetisDataObjectFormat) pValue).formatObject(this);
        }

        /* Handle difference class */
        if (MetisDataDifference.class.isInstance(pValue)) {
            return formatObject(((MetisDataDifference) pValue).getObject());
        }

        /* Pass call on */
        return super.formatObject(pValue);
    }
}
