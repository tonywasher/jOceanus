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

package io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMac;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianProtectedByteArray;
import org.bouncycastle.asn1.ASN1Object;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Access tokens.
 */
public class GordianCertGatewayTokens {
    /**
     * The token length.
     */
    private static final int TOKEN_LEN = GordianLength.LEN_128.getByteLength();

    /**
     * The secret length.
     */
    private static final int SECRET_LEN = GordianLength.LEN_256.getByteLength();

    /**
     * The expiry hours.
     */
    private static final int EXPIRY_HOURS = 3;

    /**
     * The Random.
     */
    private final SecureRandom theRandom;

    /**
     * The Mac.
     */
    private final GordianMac theMac;

    /**
     * The TokenMap.
     */
    private final Map<GordianProtectedByteArray, GordianCertGatewayToken> theTokenMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @throws GordianException on error
     */
    GordianCertGatewayTokens(final GordianBaseFactory pFactory) throws GordianException {
        /* Access the secureRandom and create hashMap */
        theRandom = pFactory.getRandomSource().getRandom();
        theTokenMap = new HashMap<>();

        /* Create the mac */
        final GordianDigestFactory myDigests = pFactory.getDigestFactory();
        final GordianDigestSpecBuilder myDigestBuilder = myDigests.newDigestSpecBuilder();
        final GordianDigestSpec myDigestSpec = myDigestBuilder.sha2(GordianLength.LEN_256);
        final GordianMacFactory myMacs = pFactory.getMacFactory();
        final GordianMacSpecBuilder myBuilder = myMacs.newMacSpecBuilder();
        final GordianMacSpec myMacSpec = myBuilder.hMac(myDigestSpec);
        theMac = myMacs.createMac(myMacSpec);
    }

    /**
     * Create a new token.
     *
     * @return the new token
     */
    GordianCertGatewayToken newToken() {
        /* Create the new token */
        final byte[] myTokenArray = new byte[TOKEN_LEN];
        theRandom.nextBytes(myTokenArray);
        final GordianProtectedByteArray myToken = new GordianProtectedByteArray(myTokenArray);

        /* Create the secret */
        final byte[] mySecretArray = new byte[SECRET_LEN];
        theRandom.nextBytes(mySecretArray);
        final GordianProtectedByteArray mySecret = new GordianProtectedByteArray(mySecretArray);

        /* Create the token */
        final ZonedDateTime myExpiry = ZonedDateTime.now(GordianBaseData.CLOCK).plusHours(EXPIRY_HOURS);
        final GordianCertGatewayToken myValue = new GordianCertGatewayToken(myToken, mySecret, myExpiry);

        /* Store the token and return it */
        theTokenMap.put(myToken, myValue);
        return myValue;
    }

    /**
     * Look up the token.
     *
     * @param pToken the token
     * @return the token details.
     */
    GordianCertGatewayToken lookUpToken(final byte[] pToken) {
        /* Create the key and access value */
        final GordianProtectedByteArray myKey = new GordianProtectedByteArray(pToken);
        final GordianCertGatewayToken myValue = theTokenMap.get(myKey);

        /* Check for expiry */
        if (myValue != null) {
            final ZonedDateTime myNow = ZonedDateTime.now(GordianBaseData.CLOCK);
            if (myNow.isAfter(myValue.expiry())) {
                theTokenMap.remove(myKey);
                return null;
            }
        }

        /* Return the value */
        return myValue;
    }

    /**
     * Create MACValue.
     *
     * @param pToken the token
     * @param pData  the data to calculate over
     * @return the MACValue
     * @throws GordianException on error
     */
    byte[] calculateMac(final GordianCertGatewayToken pToken,
                        final ASN1Object pData) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Initialise the mac */
            theMac.initKeyBytes(pToken.secret().getValue());

            /* Create the result */
            theMac.update(pData.toASN1Primitive().getEncoded());
            return theMac.finish();

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to calculate MACValue", e);
        }
    }

    /**
     * The token record.
     *
     * @param key    the key
     * @param secret the secret
     * @param expiry the expiry
     */
    public record GordianCertGatewayToken(GordianProtectedByteArray key,
                                          GordianProtectedByteArray secret,
                                          ZonedDateTime expiry) {
    }
}
