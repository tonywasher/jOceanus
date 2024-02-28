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
package net.sourceforge.joceanus.jmetis.list;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Reverse iterator.
 * @param <T> the item type
 */
public class MetisListReverseIterator<T> implements Iterator<T> {
    /**
     * The underlying iterator.
     */
    private final ListIterator<T> theIterator;

    /**
     * Constructor.
     * @param pIterator the iterator
     */
    MetisListReverseIterator(final ListIterator<T> pIterator) {
        theIterator = pIterator;
    }

    @Override
    public boolean hasNext() {
        return theIterator.hasPrevious();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return theIterator.previous();
    }

    @Override
    public void remove() {
        theIterator.remove();
    }
}
