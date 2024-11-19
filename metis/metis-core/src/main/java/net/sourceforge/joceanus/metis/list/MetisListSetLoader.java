/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.list;

import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Loader.
 */
public class MetisListSetLoader {
    /**
     * The underlying listSet.
     */
    private final MetisListSetVersioned theListSet;

    /**
     * The formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * The NameMap.
     */
    private final MetisListSetNameMap theNameMap;

    /**
     * The UniqueMap.
     */
    private final MetisListSetUniqueMap theUniqueMap;

    /**
     * The SingularMap.
     */
    private final MetisListSetSingularMap theSingularMap;

    /**
     * Constructor.
     * @param pListSet the target listSet
     * @param pFormatter the formatter
     */
    MetisListSetLoader(final MetisListSetVersioned pListSet,
                       final TethysUIDataFormatter pFormatter) {
        /* Store the listSet */
        theListSet = pListSet;
        theFormatter = pFormatter;

        /* Create the maps */
        theNameMap = new MetisListSetNameMap(theListSet, false);
        theUniqueMap = new MetisListSetUniqueMap(theListSet, false);
        theSingularMap = new MetisListSetSingularMap(theListSet, false);
    }
}
