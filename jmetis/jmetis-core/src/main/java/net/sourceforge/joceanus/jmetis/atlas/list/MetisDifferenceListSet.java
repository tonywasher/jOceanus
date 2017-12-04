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
package net.sourceforge.joceanus.jmetis.atlas.list;

import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisListKey;

/**
 * Set of DifferenceLists.
 */
public final class MetisDifferenceListSet
        extends MetisVersionedListSet {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisDifferenceListSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisDifferenceListSet.class);

    /**
     * Constructor.
     */
    protected MetisDifferenceListSet() {
        super();
    }

    @Override
    public MetisFieldSet<MetisDifferenceListSet> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MetisDifferenceList<MetisFieldVersionedItem> getList(final MetisListKey pListKey) {
        return (MetisDifferenceList<MetisFieldVersionedItem>) super.getList(pListKey);
    }
}
