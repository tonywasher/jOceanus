/*
 * Metis: Java Data Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.metis.viewer;

import java.util.Iterator;

/**
 * Data Viewer Entry.
 */
public interface MetisViewerEntry {
    /**
     * Set the object referred to by the entry.
     *
     * @param pObject the new object
     */
    void setObject(Object pObject);

    /**
     * Set the tree object referred to by the entry.
     *
     * @param pObject the new object
     */
    void setTreeObject(Object pObject);

    /**
     * Set Focus onto this entry.
     */
    void setFocus();

    /**
     * Set Focus onto child of this entry.
     *
     * @param pName the name of the child
     */
    void setFocus(String pName);

    /**
     * Is the entry visible?.
     *
     * @return true/false
     */
    boolean isVisible();

    /**
     * Set entry visibility.
     *
     * @param pVisible true/false
     */
    void setVisible(boolean pVisible);

    /**
     * Get object.
     *
     * @return the object
     */
    Object getObject();

    /**
     * Get parent.
     *
     * @return the parent
     */
    MetisViewerEntry getParent();

    /**
     * Get unique name.
     *
     * @return the name
     */
    String getUniqueName();

    /**
     * Get display name.
     *
     * @return the name
     */
    String getDisplayName();

    /**
     * Get child iterator.
     *
     * @return the iterator
     */
    Iterator<MetisViewerEntry> childIterator();
}
