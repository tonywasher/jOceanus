/*******************************************************************************
 * JFieldSet: Java Swing Field Set
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
package net.sourceforge.jArgo.jFieldSet;

import net.sourceforge.jArgo.jDataManager.JDataFields.JDataField;
import net.sourceforge.jArgo.jDataManager.JDataObject.JDataContents;

/**
 * Basic Field interface.
 * @author Tony Washer
 */
public interface JFieldSetItem extends JDataContents {
    /**
     * Get the render state for the item.
     * @return the render state
     */
    RenderState getRenderState();

    /**
     * Get the render state for the field.
     * @param pField the field
     * @return the render state
     */
    RenderState getRenderState(final JDataField pField);

    /**
     * Get the Errors for the field.
     * @param pField the field
     * @return the error text
     */
    String getFieldErrors(final JDataField pField);

    /**
     * Get the Errors for the fields.
     * @param pFields the fields
     * @return the error text
     */
    String getFieldErrors(final JDataField[] pFields);
}
