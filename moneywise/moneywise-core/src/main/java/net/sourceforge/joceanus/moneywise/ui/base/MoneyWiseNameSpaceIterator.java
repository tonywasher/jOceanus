/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.ui.base;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.jprometheus.data.PrometheusListKey;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;

/**
 * NameSpace iterator.
 */
public class MoneyWiseNameSpaceIterator
        implements Iterator<PrometheusDataItem> {
    /**
     * The editSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The keySet.
     */
    private final Deque<PrometheusListKey> theKeys;

    /**
     * The current iterator.
     */
    private Iterator<PrometheusDataItem> theIterator;

    /**
     * Constructor.
     * @param pEditSet the editSet
     * @param pKeys the listKeys.
     */
    public MoneyWiseNameSpaceIterator(final PrometheusEditSet pEditSet,
                                      final PrometheusListKey... pKeys) {
        /* Store the editSet */
        theEditSet = pEditSet;

        /* Create the list */
        theKeys = new ArrayDeque<>(Arrays.asList(pKeys));
    }


    @Override
    public boolean hasNext() {
        /* If we have an iterator and a next item */
        if (theIterator != null) {
            if (theIterator.hasNext()) {
                return true;
            }
            theIterator = null;
        }

        /* Loop to get an iterator that has an item */
        for (;;) {
            /* No more items if the keys are empty */
            if (theKeys.isEmpty()) {
                return false;
            }

            /* Pop the next item */
            final PrometheusListKey myKey = theKeys.pop();

            /* Create iterator for next key */
            @SuppressWarnings("unchecked")
            final PrometheusDataList<PrometheusDataItem> myList = theEditSet.getDataList(myKey, PrometheusDataList.class);
            theIterator = myList.iterator();
            if (theIterator.hasNext()) {
                return true;
            }
            theIterator = null;
        }
    }

    @Override
    public PrometheusDataItem next() {
        return theIterator.next();
    }
}
