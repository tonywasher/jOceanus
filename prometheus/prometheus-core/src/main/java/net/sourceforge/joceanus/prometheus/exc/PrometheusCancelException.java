/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.exc;

import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.io.Serial;

/**
 * Prometheus Cancel Exception.
 */
public class PrometheusCancelException
        extends OceanusException {
    /**
     * SerialId.
     */
    @Serial
    private static final long serialVersionUID = 6444216782361549187L;

    /**
     * Create a new Prometheus Exception object based on a string.
     * @param s the description of the exception
     */
    public PrometheusCancelException(final String s) {
        super(s);
    }
}
