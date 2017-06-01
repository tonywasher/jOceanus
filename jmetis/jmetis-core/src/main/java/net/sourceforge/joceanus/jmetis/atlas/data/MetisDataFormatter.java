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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.Locale;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionDelta.MetisDataDelta;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Generic Data object formatter.
 * @author Tony Washer
 */
public class MetisDataFormatter
        extends TethysDataFormatter {
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

        /* Handle maps and lists */
        if (MetisDataMap.class.isInstance(pValue)) {
            return formatValue(pValue, ((MetisDataMap<?, ?>) pValue).size());
        }
        if (MetisDataList.class.isInstance(pValue)) {
            return formatValue(pValue, ((MetisDataList<?>) pValue).size());
        }

        /* Format other values */
        return formatValue(pValue);
    }

    /**
     * Format the value.
     * @param pValue the value
     * @return the formatted value
     */
    private String formatValue(final Object pValue) {
        /* Handle ones that we can directly format */
        if (MetisDataObjectFormat.class.isInstance(pValue)) {
            return ((MetisDataObjectFormat) pValue).formatObject(this);
        }

        /* Handle delta class */
        if (MetisDataDelta.class.isInstance(pValue)) {
            return formatObject(((MetisDataDelta) pValue).getObject());
        }

        /* Pass call on */
        return super.formatObject(pValue);
    }

    /**
     * Format the list/map.
     * @param pValue the value
     * @param pSize the size
     * @return the formatted value
     */
    private String formatValue(final Object pValue,
                               final int pSize) {
        /* Format value normally */
        String myValue = formatValue(pValue);

        /* Build modified format */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myValue);
        myBuilder.append('(');
        myBuilder.append(pSize);
        myBuilder.append(')');

        /* Pass call on */
        return myBuilder.toString();
    }
}
