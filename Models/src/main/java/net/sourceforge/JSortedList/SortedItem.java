/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JSortedList;

public abstract class SortedItem<T extends SortedItem<T>> implements LinkObject<T> {
    /**
     * The list to which this item belongs
     */
    private SortedList<T> theList = null;

    /**
     * Storage for the List Node
     */
    private LinkNode<T> theLink = null;

    /**
     * Get the list control for this item
     * @return the list control
     */
    public SortedList<T> getList() {
        return theList;
    }

    @Override
    public LinkNode<T> getLinkNode(SortedList<T> pList) {
        return theLink;
    }

    @Override
    public void setLinkNode(SortedList<T> l,
                            LinkNode<T> o) {
        theLink = o;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    /**
     * Construct a new item
     * @param pList the list to which this item belongs
     */
    protected SortedItem(SortedList<T> pList) {
        theList = pList;
    }
}
