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

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Versioned ListSet.
 */
public class MetisListSetVersioned
            implements MetisFieldItem, OceanusEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisListSetVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListSetVersioned.class);

    /*
     * Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListSetVersioned::getVersion);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisListSetVersioned> theFields;

    /**
     * The versionedList Map.
     */
    private final Map<MetisListKey, MetisListVersioned<MetisFieldVersionedItem>> theListMap;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisListEvent> theEventManager;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
      */
    public MetisListSetVersioned() {
        /* Create the new fieldSet */
        theFields = MetisFieldSet.newFieldSet(this);

        /* Create the listMap */
        theListMap = new LinkedHashMap<>();

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Obtain the key iterator.
     * @return the iterator
     */
    public Iterator<MetisListKey> keyIterator() {
        return theListMap.keySet().iterator();
    }

    /**
     * Obtain the reverse key iterator.
     * @return the iterator
     */
    public Iterator<MetisListKey> reverseKeyIterator() {
        final List<MetisListKey> myList = new ArrayList<>(theListMap.keySet());
        return new MetisListReverseIterator<>(myList.listIterator(myList.size()));
    }

    /**
     * Obtain the List iterator.
     * @return the iterator
     */
    public Iterator<MetisListVersioned<MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     * @param pList the list
     */
    public void declareList(final MetisListKey pItemType,
                            final MetisListVersioned<MetisFieldVersionedItem> pList) {
        /* Add to the maps */
        theListMap.put(pItemType, pList);

        /* Create the DataField */
        theFields.declareLocalField(pItemType.getListName(), k -> pList);
    }

    /**
     * Obtain the relevant list.
     * @param <L> the list type
     * @param pListKey the list key
     * @param pClazz the list class
     * @return the list (or null)
     */
    public <L extends MetisListVersioned<?>> L getList(final MetisListKey pListKey,
                                                       final Class<L> pClazz) {
        return pClazz.cast(theListMap.get(pListKey));
    }

    /**
     * Is this an empty listSet?
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (MetisListVersioned<MetisFieldVersionedItem> myList : theListMap.values()) {
            if (!myList.isEmpty()) {
                return false;
            }
        }

        /* All lists are empty */
        return true;
    }

    /**
     * deriveChanges on version increment.
     */
    public void deriveChangesOnIncrement() {
        /* Create the listSetChange */
        final MetisListSetChange myEvent = new MetisListSetChange(theVersion);

        /* Loop through all the lists */
        for (MetisListVersioned<MetisFieldVersionedItem> myList : theListMap.values()) {
            final MetisListChange<?> myChange = myList.deriveChangesOnIncrement();
            if (myChange != null) {
                myEvent.registerChangedList(myChange);
            }
        }

        /* report the change */
        fireEvent(myEvent);
    }

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Loop through all the lists */
        for (MetisListVersioned<MetisFieldVersionedItem> myList : theListMap.values()) {
            myList.rewindToVersion(pVersion);
        }

        /* Adjust version */
        setVersion(pVersion);
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final MetisListSetChange pEvent) {
        /* If the change is non-empty */
        if (!pEvent.getEventType().hasContent()
                || !pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }
}
