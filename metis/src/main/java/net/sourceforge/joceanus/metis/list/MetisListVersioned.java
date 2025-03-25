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
package net.sourceforge.joceanus.metis.list;

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;

/**
 * Versioned List.
 * @param <T> the item type
 */
public class MetisListVersioned<T extends MetisFieldVersionedItem>
        extends MetisListIndexed<T>
        implements OceanusEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MetisListVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListVersioned.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_ITEMTYPE, MetisListVersioned::getItemType);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListVersioned::getVersion);
    }

    /**
     * The itemType.
     */
    private final MetisListKey theItemType;

    /**
     * The Event Manager.
     */
    private OceanusEventManager<MetisListEvent> theEventManager;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pItemType the itemType
     */
    public MetisListVersioned(final MetisListKey pItemType) {
        theItemType = pItemType;
    }

    /**
     * Access the event manager.
     * @return the event manager.
     */
    private OceanusEventManager<MetisListEvent> getEventManager() {
        /* Access the event manager and create it if it does not exist */
        synchronized (this) {
            if (theEventManager == null) {
                theEventManager = new OceanusEventManager<>();
            }
        }
        return theEventManager;
    }

    @Override
    public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
        return getEventManager().getEventRegistrar();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the itemType.
     * @return the itemType
     */
    public MetisListKey getItemType() {
        return theItemType;
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    public void fireEvent(final MetisListChange<T> pEvent) {
        /* If the change is non-empty */
        if (MetisListEvent.REFRESH.equals(pEvent.getEventType())
            || !pEvent.isEmpty()) {
            getEventManager().fireEvent(pEvent.getEventType(), pEvent);
        }
    }
}
