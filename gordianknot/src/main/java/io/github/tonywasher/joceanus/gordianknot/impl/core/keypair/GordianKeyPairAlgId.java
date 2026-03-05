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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSMTLayers;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDHSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreDSTUSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreECSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreGOSTSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreLMSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUPrimeSpec.GordianCoreNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCorePicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreRSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSM2Spec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreSnovaSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.util.Pack;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mappings from EncodedId to KeyPairSpec.
 */
public class GordianKeyPairAlgId {
    /**
     * TWO as big integer.
     */
    private static final BigInteger TWO = BigInteger.valueOf(2);

    /**
     * The algorithm error.
     */
    private static final String ERROR_ALGO = "Unrecognised algorithm";

    /**
     * The parse error.
     */
    private static final String ERROR_PARSE = "Failed to parse Key";

    /**
     * The namedCurve error.
     */
    private static final String ERROR_NAMEDCURVE = "Not a Named Curve";

    /**
     * The unsupportedCurve error.
     */
    private static final String ERROR_UNSUPCURVE = "Unsupported Curve: ";

    /**
     * The treeDigest error.
     */
    private static final String ERROR_TREEDIGEST = "Unsupported treeDigest: ";

    /**
     * The parser map.
     */
    private final Map<ASN1ObjectIdentifier, GordianEncodedParser> theParserMap;

    /**
     * Constructor.
     */
    public GordianKeyPairAlgId() {
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
        GordianSLHDSAEncodedParser.register(this);
        GordianXMSSEncodedParser.register(this);
        GordianXMSSMTEncodedParser.register(this);
        GordianLMSEncodedParser.register(this);
        GordianNewHopeEncodedParser.register(this);
        GordianCMCEEncodedParser.register(this);
        GordianFrodoEncodedParser.register(this);
        GordianSABEREncodedParser.register(this);
        GordianMLKEMEncodedParser.register(this);
        GordianMLDSAEncodedParser.register(this);
        GordianHQCEncodedParser.register(this);
        GordianBIKEEncodedParser.register(this);
        GordianNTRUEncodedParser.register(this);
        GordianNTRUPrimeEncodedParser.register(this);
        GordianFalconEncodedParser.register(this);
        GordianMayoEncodedParser.register(this);
        GordianSnovaEncodedParser.register(this);
        GordianPicnicEncodedParser.register(this);
        GordianCompositeEncodedParser.register(this);
    }

    /**
     * Obtain KeySpec from X509KeySpec.
     *
     * @param pEncoded X509 keySpec
     * @return the keySpec
     * @throws GordianException on error
     */
    public GordianNewKeyPairSpec determineKeyPairSpec(final PKCS8EncodedKeySpec pEncoded) throws GordianException {
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

    /**
     * Obtain KeySpec from X509KeySpec.
     *
     * @param pEncoded X509 keySpec
     * @return the keySpec
     * @throws GordianException on error
     */
    public GordianNewKeyPairSpec determineKeyPairSpec(final X509EncodedKeySpec pEncoded) throws GordianException {
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

    /**
     * register the parser.
     *
     * @param pAlgId  the algorithm Id.
     * @param pParser the parser
     */
    void registerParser(final ASN1ObjectIdentifier pAlgId,
                        final GordianEncodedParser pParser) {
        theParserMap.put(pAlgId, pParser);
    }

    /**
     * EncodedParser interface.
     */
    private interface GordianEncodedParser {
        /**
         * Obtain KeySpec from PrivateKeyInfo.
         *
         * @param pInfo keySpec
         * @return the keySpec
         * @throws GordianException on error
         */
        GordianNewKeyPairSpec determineKeyPairSpec(SubjectPublicKeyInfo pInfo) throws GordianException;

        /**
         * Obtain KeySpec from SubjectPublicKeyInfo.
         *
         * @param pInfo keySpec
         * @return the keySpec
         * @throws GordianException on error
         */
        GordianNewKeyPairSpec determineKeyPairSpec(PrivateKeyInfo pInfo) throws GordianException;
    }

    /**
     * RSA Encoded parser.
     */
    private static final class GordianRSAEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.rsaEncryption, new GordianRSAEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse the publicKey */
                final RSAPublicKey myPublic = RSAPublicKey.getInstance(pInfo.parsePublicKey());
                return determineKeyPairSpec(myPublic.getModulus());

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse the publicKey */
                final RSAPrivateKey myPrivate = RSAPrivateKey.getInstance(pInfo.parsePrivateKey());
                return determineKeyPairSpec(myPrivate.getModulus());

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        /**
         * Obtain keySpec from Modulus.
         *
         * @param pModulus the modulus
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final BigInteger pModulus) throws GordianException {
            final GordianCoreRSASpec mySpec = GordianCoreRSASpec.getRSASpecForInteger(pModulus);
            if (mySpec == null) {
                throw new GordianDataException("RSA strength not supported: " + pModulus.bitLength());
            }
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.rsa(mySpec.getSpec());
        }
    }

    /**
     * DSA Encoded parser.
     */
    private static final class GordianDSAEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(X9ObjectIdentifiers.id_dsa, new GordianDSAEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final DSAParameter pParms) throws GordianException {
            final GordianCoreDSASpec mySpec = GordianCoreDSASpec.getDSASpecForParms(pParms);
            if (mySpec == null) {
                throw new GordianDataException("Unsupported DSA parameters: "
                        + pParms.getP().bitLength() + ":" + pParms.getQ().bitLength());
            }
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.dsa(mySpec.getSpec());
        }
    }

    /**
     * DH Encoded parser.
     */
    public static class GordianDHEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.dhKeyAgreement, new GordianDHEncodedParser());
            pIdManager.registerParser(X9ObjectIdentifiers.dhpublicnumber, new GordianDHEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final DHParameters myParms = determineParameters(pInfo.getAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final DHParameters myParms = determineParameters(pInfo.getPrivateKeyAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain parameters from encoded sequence.
         *
         * @param pId the algorithm Identifier
         * @return the parameters
         */
        public static DHParameters determineParameters(final AlgorithmIdentifier pId) {
            /* Access algorithmId */
            final ASN1ObjectIdentifier myId = pId.getAlgorithm();

            /* If this is key agreement */
            if (PKCSObjectIdentifiers.dhKeyAgreement.equals(myId)) {
                /* Access the DHParameter */
                final DHParameter myParams = DHParameter.getInstance(pId.getParameters());

                /* If we have an L value */
                return myParams.getL() != null
                        ? new DHParameters(myParams.getP(), myParams.getG(), null, myParams.getL().intValue())
                        : new DHParameters(myParams.getP(), myParams.getG());

            } else if (X9ObjectIdentifiers.dhpublicnumber.equals(myId)) {
                /* Access Domain Parameters */
                final DomainParameters myParams = DomainParameters.getInstance(pId.getParameters());

                return new DHParameters(myParams.getP(), myParams.getG(), myParams.getQ(), myParams.getJ(), null);
            } else {
                throw new IllegalArgumentException("unknown algorithm type: " + myId);
            }
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final DHParameters pParms) throws GordianException {
            final GordianCoreDHSpec myGroup = GordianCoreDHSpec.getSpecForParams(pParms);
            if (myGroup == null) {
                throw new GordianDataException("Unsupported DH parameters: "
                        + pParms.getP().bitLength());
            }
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.dh(myGroup.getSpec());
        }
    }

    /**
     * ElGamal Encoded parser.
     */
    public static class GordianElGamalEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(OIWObjectIdentifiers.elGamalAlgorithm, new GordianElGamalEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final DHParameters myParms = determineParameters(pInfo.getAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final DHParameters myParms = determineParameters(pInfo.getPrivateKeyAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain parameters from encoded sequence.
         *
         * @param pId the algorithm Identifier
         * @return the parameters
         */
        public static DHParameters determineParameters(final AlgorithmIdentifier pId) {
            /* Access the ElGamalParameter */
            final ElGamalParameter myParams = ElGamalParameter.getInstance(pId.getParameters());

            /* Convert to DH parameters */
            return new DHParameters(myParams.getP(), TWO, myParams.getG());
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final DHParameters pParms) throws GordianException {
            final GordianCoreDHSpec myGroup = GordianCoreDHSpec.getSpecForParams(pParms);
            if (myGroup == null) {
                throw new GordianDataException("Unsupported DH parameters: "
                        + pParms.getP().bitLength());
            }
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.elGamal(myGroup.getSpec());
        }
    }

    /**
     * EC Encoded parser.
     */
    private static final class GordianECEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(X9ObjectIdentifiers.id_ecPublicKey, new GordianECEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final X962Parameters pParms) throws GordianException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }

            /* Check for EC named curve */
            final ASN1ObjectIdentifier myId = (ASN1ObjectIdentifier) pParms.getParameters();
            String myName = CustomNamedCurves.getName(myId);
            if (myName == null) {
                myName = ECNamedCurveTable.getName(myId);
            }
            if (myName != null) {
                final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
                final GordianCoreECSpec myECCurve = GordianCoreECSpec.getCurveForName(myName);
                if (myECCurve != null) {
                    return myBuilder.ec(myECCurve.getSpec());
                }
                final GordianCoreSM2Spec mySM2Curve = GordianCoreSM2Spec.getCurveForName(myName);
                if (mySM2Curve != null) {
                    return myBuilder.sm2(mySM2Curve.getSpec());
                }
                throw new GordianDataException(ERROR_UNSUPCURVE + myName);
            }

            /* Curve is not supported */
            throw new GordianDataException(ERROR_UNSUPCURVE + pParms);
        }
    }

    /**
     * DSTU Encoded parser.
     */
    private static final class GordianDSTUEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(UAObjectIdentifiers.dstu4145be, new GordianDSTUEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final DSTU4145Params myParms = DSTU4145Params.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final DSTU4145Params pParms) throws GordianException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }
            return determineKeyPairSpec(pParms.getNamedCurve());
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final X962Parameters pParms) throws GordianException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }
            return determineKeyPairSpec((ASN1ObjectIdentifier) pParms.getParameters());
        }

        /**
         * Obtain keySpec from curveId.
         *
         * @param pId the curveId
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final ASN1ObjectIdentifier pId) throws GordianException {
            /* Check for EC named surve */
            final String myName = pId.toString();
            final ECDomainParameters myParms = DSTU4145NamedCurves.getByOID(pId);
            final GordianCoreDSTUSpec myCurve = GordianCoreDSTUSpec.getCurveForName(myName);
            if (myParms == null || myCurve == null) {
                throw new GordianDataException(ERROR_UNSUPCURVE + myName);
            }
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.dstu4145(myCurve.getSpec());
        }
    }

    /**
     * GOST Encoded parser.
     */
    private static final class GordianGOSTEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianGOSTEncodedParser myParser = new GordianGOSTEncodedParser();
            pIdManager.registerParser(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, myParser);
            pIdManager.registerParser(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, myParser);
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return determineKeyPairSpec(pInfo.getAlgorithm());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return determineKeyPairSpec(pInfo.getPrivateKeyAlgorithm());
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pId the algorithmId
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final AlgorithmIdentifier pId) throws GordianException {
            /* Determine the curve name */
            final GOST3410PublicKeyAlgParameters myParms = GOST3410PublicKeyAlgParameters.getInstance(pId.getParameters());
            final ASN1ObjectIdentifier myId = myParms.getPublicKeyParamSet();
            final String myName = ECGOST3410NamedCurves.getName(myId);

            /* Determine curve */
            if (myName != null) {
                final GordianCoreGOSTSpec myCurve = GordianCoreGOSTSpec.getCurveForName(myName);
                if (myCurve == null) {
                    throw new GordianDataException(ERROR_UNSUPCURVE + myName);
                }
                final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
                return myBuilder.gost2012(myCurve.getSpec());
            }

            /* Curve is not supported */
            throw new GordianDataException(ERROR_UNSUPCURVE + myParms);
        }
    }

    /**
     * SLHDSA Encoded parser.
     */
    private static class GordianSLHDSAEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianSLHDSAEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreSLHDSASpec mySpec : GordianCoreSLHDSASpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianSLHDSAEncodedParser(myBuilder.slhdsa(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * CMCE Encoded parser.
     */
    private static class GordianCMCEEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianCMCEEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreCMCESpec mySpec : GordianCoreCMCESpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianCMCEEncodedParser(myBuilder.cmce(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * Frodo Encoded parser.
     */
    private static class GordianFrodoEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianFrodoEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreFRODOSpec mySpec : GordianCoreFRODOSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianFrodoEncodedParser(myBuilder.frodo(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * SABER Encoded parser.
     */
    private static class GordianSABEREncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianSABEREncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreSABERSpec mySpec : GordianCoreSABERSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianSABEREncodedParser(myBuilder.saber(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * MLKEM Encoded parser.
     */
    private static class GordianMLKEMEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianMLKEMEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreMLKEMSpec mySpec : GordianCoreMLKEMSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianMLKEMEncodedParser(myBuilder.mlkem(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * MLDSA Encoded parser.
     */
    private static class GordianMLDSAEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianMLDSAEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreMLDSASpec mySpec : GordianCoreMLDSASpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianMLDSAEncodedParser(myBuilder.mldsa(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * HQC Encoded parser.
     */
    private static class GordianHQCEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianHQCEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreHQCSpec mySpec : GordianCoreHQCSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianHQCEncodedParser(myBuilder.hqc(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * BIKE Encoded parser.
     */
    private static class GordianBIKEEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianBIKEEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreBIKESpec mySpec : GordianCoreBIKESpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianBIKEEncodedParser(myBuilder.bike(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * NTRU Encoded parser.
     */
    private static class GordianNTRUEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianNTRUEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreNTRUSpec mySpec : GordianCoreNTRUSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianNTRUEncodedParser(myBuilder.ntru(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * NTRUPrime Encoded parser.
     */
    private static class GordianNTRUPrimeEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianNTRUPrimeEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreNTRUPrimeParams myParams : GordianCoreNTRUPrimeParams.values()) {
                pIdManager.registerParser(myParams.getNTRULIdentifier(),
                        new GordianNTRUPrimeEncodedParser(myBuilder.ntruprime(GordianNewNTRUPrimeType.NTRUL, myParams.getParams())));
                pIdManager.registerParser(myParams.getSNTRUIdentifier(),
                        new GordianNTRUPrimeEncodedParser(myBuilder.ntruprime(GordianNewNTRUPrimeType.SNTRU, myParams.getParams())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * Falcon Encoded parser.
     */
    private static class GordianFalconEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianFalconEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreFalconSpec mySpec : GordianCoreFalconSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianFalconEncodedParser(myBuilder.falcon(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * Mayo Encoded parser.
     */
    private static class GordianMayoEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianMayoEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreMayoSpec mySpec : GordianCoreMayoSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianMayoEncodedParser(myBuilder.mayo(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * Snova Encoded parser.
     */
    private static class GordianSnovaEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianSnovaEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCoreSnovaSpec mySpec : GordianCoreSnovaSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianSnovaEncodedParser(myBuilder.snova(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
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
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianPicnicEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            for (GordianCorePicnicSpec mySpec : GordianCorePicnicSpec.values()) {
                pIdManager.registerParser(mySpec.getIdentifier(), new GordianPicnicEncodedParser(myBuilder.picnic(mySpec.getSpec())));
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * NewHope Encoded parser.
     */
    private static final class GordianNewHopeEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.newHope, new GordianNewHopeEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.newHope();
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            return myBuilder.newHope();
        }
    }

    /**
     * XMSS Encoded parser.
     */
    private static final class GordianXMSSEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.xmss, new GordianXMSSEncodedParser());
            pIdManager.registerParser(IsaraObjectIdentifiers.id_alg_xmss, new GordianXMSSEncodedParser());
        }


        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
            if (myParms != null) {
                return determineKeyPairSpec(myParms);
            }

            /* Protect against exceptions */
            try {
                final byte[] keyEnc = Objects.requireNonNull(ASN1OctetString.getInstance(pInfo.parsePublicKey())).getOctets();
                final int myOID = Pack.bigEndianToInt(keyEnc, 0);
                final XMSSParameters myParams = XMSSParameters.lookupByOID(myOID);
                return GordianKeyPairSpecBuilder.xmss(determineKeyType(myParams.getTreeDigestOID()),
                        determineHeight(myParams.getHeight()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to resolve key", e);
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final XMSSKeyParams pParms) throws GordianException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            final GordianNewXMSSHeight myHeight = determineHeight(pParms.getHeight());
            return GordianKeyPairSpecBuilder.xmss(determineKeyType(myDigest), myHeight);
        }

        /**
         * Obtain keyType from digest.
         *
         * @param pDigest the treeDigest
         * @return the keyType
         * @throws GordianException on error
         */
        static GordianNewXMSSDigestType determineKeyType(final ASN1ObjectIdentifier pDigest) throws GordianException {
            if (pDigest.equals(NISTObjectIdentifiers.id_sha256)) {
                return GordianNewXMSSDigestType.SHA256;
            }
            if (pDigest.equals(NISTObjectIdentifiers.id_sha512)) {
                return GordianNewXMSSDigestType.SHA512;
            }
            if (pDigest.equals(NISTObjectIdentifiers.id_shake128)) {
                return GordianNewXMSSDigestType.SHAKE128;
            }
            if (pDigest.equals(NISTObjectIdentifiers.id_shake256)) {
                return GordianNewXMSSDigestType.SHAKE256;
            }

            /* Tree Digest is not supported */
            throw new GordianDataException(ERROR_TREEDIGEST + pDigest);
        }

        /**
         * Obtain height.
         *
         * @param pHeight the height
         * @return the xmssHeight
         * @throws GordianException on error
         */
        static GordianNewXMSSHeight determineHeight(final int pHeight) throws GordianException {
            /* Loo through the heights */
            for (GordianNewXMSSHeight myHeight : GordianNewXMSSHeight.values()) {
                if (myHeight.getHeight() == pHeight) {
                    return myHeight;
                }
            }

            /* Height is not supported */
            throw new GordianDataException("Inavlid height: " + pHeight);
        }
    }

    /**
     * XMSSMT Encoded parser.
     */
    private static final class GordianXMSSMTEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.xmss_mt, new GordianXMSSMTEncodedParser());
            pIdManager.registerParser(IsaraObjectIdentifiers.id_alg_xmssmt, new GordianXMSSMTEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
            if (myParms != null) {
                return determineKeyPairSpec(myParms);
            }

            /* Protect against exceptions */
            try {
                final byte[] keyEnc = Objects.requireNonNull(ASN1OctetString.getInstance(pInfo.parsePublicKey())).getOctets();
                final int myOID = Pack.bigEndianToInt(keyEnc, 0);
                final XMSSMTParameters myParams = XMSSMTParameters.lookupByOID(myOID);
                return GordianKeyPairSpecBuilder.xmssmt(GordianXMSSEncodedParser.determineKeyType(myParams.getTreeDigestOID()),
                        GordianXMSSEncodedParser.determineHeight(myParams.getHeight()), determineLayers(myParams.getLayers()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to resolve key", e);
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         *
         * @param pParms the parameters
         * @return the keySpec
         * @throws GordianException on error
         */
        private static GordianNewKeyPairSpec determineKeyPairSpec(final XMSSMTKeyParams pParms) throws GordianException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            final GordianNewXMSSHeight myHeight = GordianXMSSEncodedParser.determineHeight(pParms.getHeight());
            final GordianNewXMSSMTLayers myLayers = determineLayers(pParms.getLayers());
            return GordianKeyPairSpecBuilder.xmssmt(GordianXMSSEncodedParser.determineKeyType(myDigest), myHeight, myLayers);
        }

        /**
         * Obtain layers.
         *
         * @param pLayers the layers
         * @return the xmssMTLayers
         * @throws GordianException on error
         */
        static GordianNewXMSSMTLayers determineLayers(final int pLayers) throws GordianException {
            /* Loo through the heights */
            for (GordianNewXMSSMTLayers myLayers : GordianNewXMSSMTLayers.values()) {
                if (myLayers.getLayers() == pLayers) {
                    return myLayers;
                }
            }

            /* Layers is not supported */
            throw new GordianDataException("Invalid layers: " + pLayers);
        }
    }

    /**
     * Edwards Encoded parser.
     */
    private static class GordianEdwardsEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianNewKeyPairSpec theKeySpec;

        /**
         * Constructor.
         *
         * @param pKeySpec the keySpec
         */
        GordianEdwardsEncodedParser(final GordianNewKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
            pIdManager.registerParser(EdECObjectIdentifiers.id_X25519, new GordianEdwardsEncodedParser(myBuilder.x25519()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_X448, new GordianEdwardsEncodedParser(myBuilder.x448()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_Ed25519, new GordianEdwardsEncodedParser(myBuilder.ed25519()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_Ed448, new GordianEdwardsEncodedParser(myBuilder.ed448()));
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            return theKeySpec;
        }
    }

    /**
     * LMS Encoded parser.
     */
    private static final class GordianLMSEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new GordianLMSEncodedParser());
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
                final HSSPublicKeyParameters myPublic = (HSSPublicKeyParameters) PublicKeyFactory.createKey(pInfo);
                final int myDepth = myPublic.getL();
                final LMSPublicKeyParameters myLMSPublicKey = myPublic.getLMSPublicKey();
                final GordianNewLMSSpec myKeySpec = determineKeyPairSpec(myLMSPublicKey);
                return myBuilder.hss(myKeySpec.getHash(), myKeySpec.getHeight(), myKeySpec.getWidth(), myKeySpec.getLength(), myDepth);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final GordianCoreKeyPairSpecBuilder myBuilder = GordianCoreKeyPairSpecBuilder.newInstance();
                final HSSPrivateKeyParameters myPrivate = (HSSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
                final int myDepth = myPrivate.getL();
                final LMSPublicKeyParameters myLMSPublicKey = myPrivate.getPublicKey().getLMSPublicKey();
                final GordianNewLMSSpec myKeySpec = determineKeyPairSpec(myLMSPublicKey);
                return myBuilder.hss(myKeySpec.getHash(), myKeySpec.getHeight(), myKeySpec.getWidth(), myKeySpec.getLength(), myDepth);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        /**
         * Obtain keySpec from public key.
         *
         * @param pPublic the publicKeyParams
         * @return the LMSKeySpec
         */
        static GordianNewLMSSpec determineKeyPairSpec(final LMSPublicKeyParameters pPublic) {
            return GordianCoreLMSSpec.determineSpec(pPublic.getSigParameters(), pPublic.getOtsParameters());
        }
    }

    /**
     * Composite Encoded parser.
     */
    private static class GordianCompositeEncodedParser implements GordianEncodedParser {
        /**
         * The KeyPairFactory.
         */
        private final GordianKeyPairAlgId theIdManager;

        /**
         * Constructor.
         *
         * @param pIdManager the idManager
         */
        GordianCompositeEncodedParser(final GordianKeyPairAlgId pIdManager) {
            theIdManager = pIdManager;
        }

        /**
         * Registrar.
         *
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(MiscObjectIdentifiers.id_alg_composite, new GordianCompositeEncodedParser(pIdManager));
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws GordianException {
            /* Protect against exceptions */
            try {
                final ASN1Sequence myKeys = ASN1Sequence.getInstance(pInfo.getPublicKeyData().getBytes());
                final List<GordianNewKeyPairSpec> mySpecs = new ArrayList<>();

                /* Build the list from the keys sequence */
                final Enumeration<?> en = myKeys.getObjects();
                while (en.hasMoreElements()) {
                    final SubjectPublicKeyInfo myPKInfo = SubjectPublicKeyInfo.getInstance(en.nextElement());
                    mySpecs.add(theIdManager.determineKeyPairSpec(new X509EncodedKeySpec(myPKInfo.getEncoded())));
                }
                return GordianKeyPairSpecBuilder.composite(mySpecs);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        @Override
        public GordianNewKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws GordianException {
            /* Protect against exceptions */
            try {
                final ASN1Sequence myKeys = ASN1Sequence.getInstance(pInfo.getPrivateKey().getOctets());
                final List<GordianNewKeyPairSpec> mySpecs = new ArrayList<>();

                /* Build the list from the keys sequence */
                final Enumeration<?> en = myKeys.getObjects();
                while (en.hasMoreElements()) {
                    final PrivateKeyInfo myPKInfo = PrivateKeyInfo.getInstance(en.nextElement());
                    mySpecs.add(theIdManager.determineKeyPairSpec(new PKCS8EncodedKeySpec(myPKInfo.getEncoded())));
                }
                return GordianKeyPairSpecBuilder.composite(mySpecs);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }
    }
}
