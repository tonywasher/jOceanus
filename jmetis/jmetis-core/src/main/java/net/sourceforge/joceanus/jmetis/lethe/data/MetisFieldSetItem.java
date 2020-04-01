/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.data;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;

/**
 * Basic Field interface.
 * @author Tony Washer
 */
public interface MetisFieldSetItem
        extends MetisDataContents {
    /**
     * Is the item editable?
     * @return true/false
     */
    boolean isEditable();

    /**
     * Is the item disabled?
     * @return true/false
     */
    boolean isDisabled();

    /**
     * Get the state for the item.
     * @return the render state
     */
    MetisFieldState getItemState();

    /**
     * Get the state for the field.
     * @param pField the field
     * @return the render state
     */
    MetisFieldState getFieldState(MetisField pField);

    /**
     * Get the Errors for the field.
     * @param pField the field
     * @return the error text
     */
    String getFieldErrors(MetisField pField);

    /**
     * Get the Errors for the fields.
     * @param pFields the fields
     * @return the error text
     */
    String getFieldErrors(MetisField[] pFields);
}
