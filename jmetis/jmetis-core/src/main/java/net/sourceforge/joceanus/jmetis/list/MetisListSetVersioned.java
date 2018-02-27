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
package net.sourceforge.joceanus.jmetis.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Versioned ListSet.
 */
public class MetisListSetVersioned
        implements MetisFieldItem, TethysEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisListSetVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListSetVersioned.class);

    /**
     * Version Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListSetVersioned::getVersion);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisListSetVersioned> theFields;

    /**
     * The VersionedList Map.
     */
    private final Map<MetisListKey, MetisListVersioned<MetisFieldVersionedItem>> theListMap;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisListEvent> theEventManager;

    /**
     * The Underlying list (if any).
     */
    private final MetisListSetVersioned theBaseListSet;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     */
    protected MetisListSetVersioned() {
        this(null);
    }

    /**
     * Constructor.
     * @param pBaseListSet the baseListSet (if any)
     */
    protected MetisListSetVersioned(final MetisListSetVersioned pBaseListSet) {
        /* Store parameters */
        theBaseListSet = pBaseListSet;

        /* Create listMap and fieldSet */
        theListMap = new LinkedHashMap<>();
        theFields = MetisFieldSet.newFieldSet(this);

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public TethysEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the baseListSet.
     * @return the baseListSet
     */
    public MetisListSetVersioned getBaseListSet() {
        return theBaseListSet;
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
     * @return true/false
     */
    public Iterator<MetisListVersioned<MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Obtain the relevant list.
     * @param <T> the item type
     * @param pListKey the list key
     * @return the list (or null)
     */
    @SuppressWarnings("unchecked")
    public <T extends MetisFieldVersionedItem> MetisListVersioned<T> getList(final MetisListKey pListKey) {
        return (MetisListVersioned<T>) theListMap.get(pListKey);
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     */
    public void declareList(final MetisListKey pItemType) {
        /* Create the list and declare it */
        declareList(pItemType, new MetisListVersioned<>(this, pItemType));
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     * @param pList the list
     */
    protected void declareList(final MetisListKey pItemType,
                               final MetisListVersioned<MetisFieldVersionedItem> pList) {
        /* Add to the list map */
        theListMap.put(pItemType, pList);

        /* Create the DataField */
        theFields.declareLocalField(pItemType.getListName(), k -> pList);
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
