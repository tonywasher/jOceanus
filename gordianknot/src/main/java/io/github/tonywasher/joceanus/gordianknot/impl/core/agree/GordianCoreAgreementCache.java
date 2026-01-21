/*
 * GordianKnot: Security Suite
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
package io.github.tonywasher.joceanus.gordianknot.impl.core.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreement;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Agreement Cache.
 */
public class GordianCoreAgreementCache {
    /**
     * The id control.
     */
    private final AtomicLong theNextId;

    /**
     * The map of id to agreement.
     */
    private final Map<Long, GordianCoreAgreement> theMap;

    /**
     * Constructor.
     *
     * @param pRandom the random generator.
     */
    GordianCoreAgreementCache(final GordianRandomSource pRandom) {
        /* Initialize the id bank */
        final long myInit = ((long) pRandom.getRandom().nextInt()) << Integer.SIZE;
        theNextId = new AtomicLong(myInit);

        /* Create the map */
        theMap = new HashMap<>();
    }

    /**
     * Obtain the nextId.
     *
     * @return the nextId
     */
    Long getNextId() {
        return theNextId.getAndIncrement();
    }

    /**
     * Lookup id.
     *
     * @param pId   the id to look up
     * @param pSpec the expected agreementSpec
     * @return the matching agreement
     * @throws GordianException on error
     */
    GordianCoreAgreement lookUpAgreement(final Long pId,
                                         final GordianAgreementSpec pSpec) throws GordianException {
        /* Look up the Agreement */
        final GordianCoreAgreement myAgreement = theMap.get(pId);

        /* Validate the agreement */
        if (myAgreement == null) {
            throw new GordianDataException("No matching cached Agreement found");
        }
        if (!Objects.equals(myAgreement.getAgreementSpec(), pSpec)) {
            throw new GordianDataException("Agreement specification mismatch");
        }

        /* Return the agreement */
        return myAgreement;
    }

    /**
     * Store agreement under id.
     *
     * @param pId        the id
     * @param pAgreement the agreement
     */
    void storeAgreement(final Long pId,
                        final GordianAgreement pAgreement) {
        theMap.put(pId, (GordianCoreAgreement) pAgreement);
    }

    /**
     * Release agreement.
     *
     * @param pId the id
     */
    void removeAgreement(final Long pId) {
        theMap.remove(pId);
    }
}
