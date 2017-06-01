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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;

/**
 * Table Calculator.
 * @param <R> the row type
 */
@FunctionalInterface
public interface MetisTableCalculator<R> {
    /**
     * Obtain calculated value.
     * @param pRow the row
     * @param pField the field
     * @return the value
     */
    Object calculateValue(R pRow,
                          MetisDataField pField);
}
