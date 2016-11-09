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
package net.sourceforge.joceanus.jmetis.newlist;

import net.sourceforge.joceanus.jmetis.data.MetisFields;

/**
 * Set of DifferenceLists.
 * @param <E> the list type identifier
 */
public class MetisDifferenceListSet<E extends Enum<E>>
        extends MetisVersionedListSet<E, MetisDifferenceList<MetisVersionedItem>> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisDifferenceListSet.class.getSimpleName(), MetisVersionedListSet.getBaseFields());

    /**
     * Constructor.
     * @param pClass the enum class
     */
    protected MetisDifferenceListSet(final Class<E> pClass) {
        super(MetisListType.DIFFERENCE, pClass, FIELD_DEFS);
    }
}