/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.prometheus.exc;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

import java.io.Serial;

/**
 * Prometheus Data Exception.
 */
public class PrometheusDataException
        extends OceanusException {
    /**
     * SerialId.
     */
    @Serial
    private static final long serialVersionUID = 477418185621087538L;

    /**
     * Create a new Prometheus Exception object based on an object, a string and an underlying
     * exception.
     *
     * @param o the object
     * @param s the description of the exception
     * @param e the underlying exception
     */
    public PrometheusDataException(final Object o,
                                   final String s,
                                   final Throwable e) {
        super(o, s, e);
    }

    /**
     * Create a new Prometheus Exception object based on an object and a string.
     *
     * @param o the object
     * @param s the description of the exception
     */
    public PrometheusDataException(final Object o,
                                   final String s) {
        super(o, s);
    }

    /**
     * Create a new Prometheus Exception object based on a string and an underlying exception.
     *
     * @param s the description of the exception
     * @param e the underlying exception
     */
    public PrometheusDataException(final String s,
                                   final Throwable e) {
        super(s, e);
    }

    /**
     * Create a new Prometheus Exception object based on a string.
     *
     * @param s the description of the exception
     */
    public PrometheusDataException(final String s) {
        super(s);
    }
}
