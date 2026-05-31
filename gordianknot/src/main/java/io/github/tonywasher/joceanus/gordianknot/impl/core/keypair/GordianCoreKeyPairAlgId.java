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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianCompositeEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianDHEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianDSAEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianDSTUEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianECEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianEdwardsEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianElGamalEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianGOSTEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianKeyPairIdEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianKeyPairParserRegistrar;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianLMSEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianNewHopeEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianRSAEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianXMSSEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.parser.GordianXMSSMTEncodedParser;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUPlusSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUPrimeSpec.GordianCoreNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCorePicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSnovaSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Mappings from EncodedId to KeyPairSpec.
 */
public class GordianCoreKeyPairAlgId
        implements GordianKeyPairParserRegistrar {
    /**
     * The algorithm error.
     */
    private static final String ERROR_ALGO = "Unrecognised algorithm";

    /**
     * The parser map.
     */
    private final Map<ASN1ObjectIdentifier, GordianEncodedParser> theParserMap;

    /**
     * Constructor.
     */
    public GordianCoreKeyPairAlgId() {
        /* Create the map */
        theParserMap = new HashMap<>();

        /* Register the parsers */
        GordianRSAEncodedParser.register(this);
        GordianElGamalEncodedParser.register(this);
        GordianDSAEncodedParser.register(this);
        GordianDHEncodedParser.register(this);
        GordianECEncodedParser.register(this);
        GordianDSTUEncodedParser.register(this);
        GordianGOSTEncodedParser.register(this);
        GordianEdwardsEncodedParser.register(this);
        GordianKeyPairIdEncodedParser.register(this, GordianCoreSLHDSASpec.values());
        GordianXMSSEncodedParser.register(this);
        GordianXMSSMTEncodedParser.register(this);
        GordianLMSEncodedParser.register(this);
        GordianNewHopeEncodedParser.register(this);
        GordianKeyPairIdEncodedParser.register(this, GordianCoreCMCESpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreFRODOSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreSABERSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreMLKEMSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreMLDSASpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreHQCSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreBIKESpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreNTRUSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreNTRUPlusSpec.values());
        GordianNTRUPrimeEncodedParser.register(this);
        GordianKeyPairIdEncodedParser.register(this, GordianCoreFalconSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreMayoSpec.values());
        GordianKeyPairIdEncodedParser.register(this, GordianCoreSnovaSpec.values());
        GordianPicnicEncodedParser.register(this);
        GordianCompositeEncodedParser.register(this);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final PKCS8EncodedKeySpec pEncoded) throws GordianException {
        /* Determine the algorithm Id. */
        final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncoded.getEncoded());
        final AlgorithmIdentifier myId = myInfo.getPrivateKeyAlgorithm();
        final ASN1ObjectIdentifier myAlgId = myId.getAlgorithm();

        /* Obtain the parser */
        final GordianEncodedParser myParser = theParserMap.get(myAlgId);
        if (myParser != null) {
            return myParser.determineKeyPairSpec(myInfo);
        }
        throw new GordianDataException(ERROR_ALGO);
    }

    @Override
    public GordianKeyPairSpec determineKeyPairSpec(final X509EncodedKeySpec pEncoded) throws GordianException {
        /* Determine the algorithm Id. */
        final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncoded.getEncoded());
        final AlgorithmIdentifier myId = myInfo.getAlgorithm();
        final ASN1ObjectIdentifier myAlgId = myId.getAlgorithm();

        /* Obtain the parser */
        final GordianEncodedParser myParser = theParserMap.get(myAlgId);
        if (myParser != null) {
            return myParser.determineKeyPairSpec(myInfo);
        }
        throw new GordianDataException(ERROR_ALGO);
    }

    @Override
    public void registerParser(final ASN1ObjectIdentifier pAlgId,
                               final GordianEncodedParser pParser) {
        theParserMap.put(pAlgId, pParser);
    }

    /**
     * NTRUPrime Encoded parser.
     */
    private static class GordianNTRUPrimeEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianNTRUPrimeEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianCoreKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreNTRUPrimeParams myParams : GordianCoreNTRUPrimeParams.values()) {
                pIdManager.registerParser(myParams.getNTRULIdentifier(),
                        new GordianNTRUPrimeEncodedParser(myBuilder.ntruprime(GordianNTRUPrimeType.NTRUL, myParams.getParams())));
                pIdManager.registerParser(myParams.getSNTRUIdentifier(),
                        new GordianNTRUPrimeEncodedParser(myBuilder.ntruprime(GordianNTRUPrimeType.SNTRU, myParams.getParams())));
            }
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * Picnic Encoded parser.
     */
    private static class GordianPicnicEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianPicnicEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianCoreKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCorePicnicSpec mySpec : GordianCorePicnicSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianPicnicEncodedParser(myBuilder.picnic(mySpec.getSpec())));
            }
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }
}
