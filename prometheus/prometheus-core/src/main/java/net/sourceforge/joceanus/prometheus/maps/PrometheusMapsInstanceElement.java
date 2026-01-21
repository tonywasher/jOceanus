/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.maps;

import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

import java.util.ArrayList;
import java.util.List;

/**
 * InstanceMap Elements.
 */
public interface PrometheusMapsInstanceElement
        extends MetisFieldItem {
    /**
     * Obtain the list of items.
     *
     * @return the list
     */
    List<PrometheusDataItem> getList();

    /**
     * Instance Element Item.
     */
    class PrometheusMapsInstanceElementItem
            implements PrometheusMapsInstanceElement {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PrometheusMapsInstanceElementItem> FIELD_DEFS
                = MetisFieldSet.newFieldSet(PrometheusMapsInstanceElementItem.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_ITEM, PrometheusMapsInstanceElementItem::getItem);
        }

        /**
         * The item.
         */
        private final PrometheusDataItem theItem;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        PrometheusMapsInstanceElementItem(final PrometheusDataItem pItem) {
            theItem = pItem;
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return pFormatter.formatObject(theItem);
        }

        /**
         * Obtain the item.
         *
         * @return the item
         */
        PrometheusDataItem getItem() {
            return theItem;
        }

        @Override
        public List<PrometheusDataItem> getList() {
            /* Obtain the item as a list */
            final List<PrometheusDataItem> myList = new ArrayList<>();
            myList.add(theItem);
            return myList;
        }
    }

    /**
     * Instance Element List.
     */
    class PrometheusMapsInstanceElementList
            implements PrometheusMapsInstanceElement {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PrometheusMapsInstanceElementList> FIELD_DEFS
                = MetisFieldSet.newFieldSet(PrometheusMapsInstanceElementList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_ITEMLIST, PrometheusMapsInstanceElementList::getList);
        }

        /**
         * The list.
         */
        private final List<PrometheusDataItem> theList;

        /**
         * Constructor.
         *
         * @param pPrevious the previous element
         * @param pItem     the item
         */
        PrometheusMapsInstanceElementList(final PrometheusMapsInstanceElement pPrevious,
                                          final PrometheusDataItem pItem) {
            /* Create the new list */
            theList = new ArrayList<>(pPrevious.getList());
            theList.add(pItem);
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return PrometheusMapsInstanceElementList.class.getSimpleName();
        }

        @Override
        public List<PrometheusDataItem> getList() {
            return theList;
        }
    }
}
