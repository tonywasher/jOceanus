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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Composite Encoded parser.
 */
public class GordianCompositeEncodedParser implements GordianEncodedParser {
    /**
     * The KeyPairFactory.
     */
    private final GordianKeyPairParserRegistrar theIdManager;

    /**
     * Constructor.
     *
     * @param pIdManager the idManager
     */
    GordianCompositeEncodedParser(final GordianKeyPairParserRegistrar pIdManager) {
        theIdManager = pIdManager;
    }

    /**
     * Registrar.
     *
     * @param pIdManager the idManager
     */
    public static void register(final GordianKeyPairParserRegistrar pIdManager) {
        pIdManager.registerParser(MiscObjectIdentifiers.id_alg_composite, new GordianCompositeEncodedParser(pIdManager));
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            final ASN1Sequence myKeys = ASN1Sequence.getInstance(pInfo.getPublicKeyData().getBytes());
            final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();

            /* Build the list from the keys sequence */
            final Enumeration<?> en = myKeys.getObjects();
            while (en.hasMoreElements()) {
                final SubjectPublicKeyInfo myPKInfo = SubjectPublicKeyInfo.getInstance(en.nextElement());
                mySpecs.add(theIdManager.determineKeyPairSpec(new X509EncodedKeySpec(myPKInfo.getEncoded())));
            }
            final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.composite(mySpecs);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
        /* Protect against exceptions */
        try {
            final ASN1Sequence myKeys = ASN1Sequence.getInstance(pInfo.getPrivateKey().getOctets());
            final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();

            /* Build the list from the keys sequence */
            final Enumeration<?> en = myKeys.getObjects();
            while (en.hasMoreElements()) {
                final PrivateKeyInfo myPKInfo = PrivateKeyInfo.getInstance(en.nextElement());
                mySpecs.add(theIdManager.determineKeyPairSpec(new PKCS8EncodedKeySpec(myPKInfo.getEncoded())));
            }
            final GordianKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.composite(mySpecs);

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException(ERROR_PARSE, e);
        }
    }
}
