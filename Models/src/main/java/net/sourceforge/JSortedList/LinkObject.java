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

public interface LinkObject<T extends LinkObject<T>> extends Comparable<Object> {
    /**
     * Determine whether the item is Hidden
     * @return <code>true/false</code>
     */
    public boolean isHidden();

    /**
     * Set the linkNode for a list
     * @param pList the list
     * @param pNode the node
     */
    public void setLinkNode(SortedList<T> pList,
                            LinkNode<T> pNode);

    /**
     * Get the linkNode for a List
     * @param pList the list
     * @return the Link node
     */
    public LinkNode<T> getLinkNode(SortedList<T> pList);
}
