/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.data;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter.OceanusDataFormatterExtension;

/**
 * Generic Data object formatter.
 * @author Tony Washer
 */
public class MetisDataFormatter
        implements OceanusDataFormatterExtension {
    /**
     * The formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public MetisDataFormatter(final OceanusDataFormatter pFormatter) {
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
