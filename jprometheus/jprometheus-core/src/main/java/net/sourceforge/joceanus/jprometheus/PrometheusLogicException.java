/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jprometheus;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Prometheus Logic Exception.
 */
public class PrometheusLogicException
        extends OceanusException {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = -7274938835745309630L;

    /**
     * Create a new Prometheus Exception object based on an object and a string.
     * @param o the object
     * @param s the description of the exception
     */
    public PrometheusLogicException(final Object o,
                                    final String s) {
        super(o, s);
    }

    /**
     * Create a new Prometheus Exception object based on a string.
     * @param s the description of the exception
     */
    public PrometheusLogicException(final String s) {
        super(s);
    }
}
