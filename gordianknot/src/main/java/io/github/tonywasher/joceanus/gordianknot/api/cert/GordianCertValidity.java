/*
 * GordianKnot: Security Suite
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

package io.github.tonywasher.joceanus.gordianknot.api.cert;

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;

import java.time.ZonedDateTime;

/**
 * Certificate Usage.
 */
public interface GordianCertValidity {
    /**
     * Obtain the notBefore dateTime.
     *
     * @return the notBefore time
     */
    ZonedDateTime notBefore();

    /**
     * Obtain the notAfter dateTime.
     *
     * @return the notAfter time
     */
    ZonedDateTime notAfter();

    /**
     * Is the certificate valid at this moment?
     *
     * @return true/false
     */
    boolean isValidNow();

    /**
     * Is the certificate valid at the specified dateTime?
     *
     * @param pDateTime the dateTime to test
     * @return true/false
     */
    boolean isValidAtDateTime(ZonedDateTime pDateTime);

    /**
     * Set the notBefore dateTime.
     *
     * @param pNotBefore the notBefore time
     * @return the validity
     * @throws GordianException on error
     */
    GordianCertValidity notBefore(ZonedDateTime pNotBefore) throws GordianException;

    /**
     * Set the notAfter dateTime.
     *
     * @param pNotAfter the notAfter time
     * @return the validity
     * @throws GordianException on error
     */
    GordianCertValidity notAfter(ZonedDateTime pNotAfter) throws GordianException;
}
