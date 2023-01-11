/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmetis.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter.TethysUIDataFormatterExtension;

/**
 * Generic Data object formatter.
 * @author Tony Washer
 */
public class MetisDataFormatter
        implements TethysUIDataFormatterExtension {
    /**
     * The formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public MetisDataFormatter(final TethysUIDataFormatter pFormatter) {
        theFormatter = pFormatter;
    }

    @Override
    public String formatObject(final Object pValue) {
        /* Handle maps and lists */
        if (pValue instanceof MetisDataMap) {
            return formatValue(pValue, ((MetisDataMap<?, ?>) pValue).size());
        }
        if (pValue instanceof MetisDataList) {
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
        if (pValue instanceof MetisDataObjectFormat) {
            return ((MetisDataObjectFormat) pValue).formatObject(theFormatter);
        }

        /* Handle delta class */
        if (pValue instanceof MetisDataDelta) {
            return formatObject(((MetisDataDelta) pValue).getObject());
        }

        /* Not supported */
        return null;
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
        final String myValue = formatValue(pValue);

        /* Return modified value */
        return myValue
                + '('
                + pSize
                + ')';
    }
}
