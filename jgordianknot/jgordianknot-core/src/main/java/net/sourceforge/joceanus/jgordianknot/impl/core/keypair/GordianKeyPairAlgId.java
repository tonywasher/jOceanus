/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypair;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
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
import org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.util.Pack;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianBIKESpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianCMCESpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDILITHIUMSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianFALCONSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianFRODOSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianHQCSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKYBERSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeParams;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianNTRUSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianPICNICSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRainbowSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSABERSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSPHINCSPlusSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSMTLayers;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
        GordianSPHINCSPlusEncodedParser.register(this);
        GordianXMSSEncodedParser.register(this);
        GordianXMSSMTEncodedParser.register(this);
        GordianLMSEncodedParser.register(this);
        GordianCMCEEncodedParser.register(this);
        GordianFrodoEncodedParser.register(this);
        GordianSABEREncodedParser.register(this);
        GordianKyberEncodedParser.register(this);
        GordianDilithiumEncodedParser.register(this);
        GordianHQCEncodedParser.register(this);
        GordianBIKEEncodedParser.register(this);
        GordianNTRUEncodedParser.register(this);
        GordianNTRUPrimeEncodedParser.register(this);
        GordianFalconEncodedParser.register(this);
        GordianPicnicEncodedParser.register(this);
        GordianRainbowEncodedParser.register(this);
        GordianCompositeEncodedParser.register(this);
    }

    /**
     * Obtain KeySpec from X509KeySpec.
     * @param pEncoded X509 keySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    public GordianKeyPairSpec determineKeyPairSpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
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
     * @param pEncoded X509 keySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    public GordianKeyPairSpec determineKeyPairSpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
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
     * @param pAlgId the algorithm Id.
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
         * @param pInfo keySpec
         * @return the keySpec
         * @throws OceanusException on error
         */
        GordianKeyPairSpec determineKeyPairSpec(SubjectPublicKeyInfo pInfo) throws OceanusException;

        /**
         * Obtain KeySpec from SubjectPublicKeyInfo.
         * @param pInfo keySpec
         * @return the keySpec
         * @throws OceanusException on error
         */
        GordianKeyPairSpec determineKeyPairSpec(PrivateKeyInfo pInfo) throws OceanusException;
    }

    /**
     * RSA Encoded parser.
     */
    private static final class GordianRSAEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.rsaEncryption, new GordianRSAEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
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
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
         * @param pModulus the modulus
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final BigInteger pModulus) throws OceanusException {
            final GordianRSAModulus myModulus = GordianRSAModulus.getModulusForInteger(pModulus);
            if (myModulus == null) {
                throw new GordianDataException("RSA strength not supported: " + pModulus.bitLength());
            }
            return  GordianKeyPairSpec.rsa(myModulus);
        }
    }

    /**
     * DSA Encoded parser.
     */
    private static final class GordianDSAEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(X9ObjectIdentifiers.id_dsa, new GordianDSAEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final DSAParameter pParms) throws OceanusException {
            final GordianDSAKeyType myKeyType = GordianDSAKeyType.getDSATypeForParms(pParms);
            if (myKeyType == null) {
                throw new GordianDataException("Unsupported DSA parameters: "
                        + pParms.getP().bitLength() + ":" + pParms.getQ().bitLength());
            }
            return  GordianKeyPairSpec.dsa(myKeyType);
        }
    }

    /**
     * DH Encoded parser.
     */
    public static class GordianDHEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.dhKeyAgreement, new GordianDHEncodedParser());
            pIdManager.registerParser(X9ObjectIdentifiers.dhpublicnumber, new GordianDHEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final DHParameters myParms = determineParameters(pInfo.getAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final DHParameters myParms = determineParameters(pInfo.getPrivateKeyAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain parameters from encoded sequence.
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
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final DHParameters pParms) throws OceanusException {
            final GordianDHGroup myGroup = GordianDHGroup.getGroupForParams(pParms);
            if (myGroup == null) {
                throw new GordianDataException("Unsupported DH parameters: "
                        + pParms.getP().bitLength());
            }
            return  GordianKeyPairSpec.dh(myGroup);
        }
    }

    /**
     * ElGamal Encoded parser.
     */
    public static class GordianElGamalEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(OIWObjectIdentifiers.elGamalAlgorithm, new GordianElGamalEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final DHParameters myParms = determineParameters(pInfo.getAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final DHParameters myParms = determineParameters(pInfo.getPrivateKeyAlgorithm());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain parameters from encoded sequence.
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
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final DHParameters pParms) throws OceanusException {
            final GordianDHGroup myGroup = GordianDHGroup.getGroupForParams(pParms);
            if (myGroup == null) {
                throw new GordianDataException("Unsupported DH parameters: "
                        + pParms.getP().bitLength());
            }
            return  GordianKeyPairSpec.elGamal(myGroup);
        }
    }

    /**
     * EC Encoded parser.
     */
    private static final class GordianECEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(X9ObjectIdentifiers.id_ecPublicKey, new GordianECEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final X962Parameters pParms) throws OceanusException {
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
                final GordianDSAElliptic myDSACurve = GordianDSAElliptic.getCurveForName(myName);
                if (myDSACurve != null) {
                    return GordianKeyPairSpec.ec(myDSACurve);
                }
                final GordianSM2Elliptic mySM2Curve = GordianSM2Elliptic.getCurveForName(myName);
                if (mySM2Curve != null) {
                    return GordianKeyPairSpec.sm2(mySM2Curve);
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
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(UAObjectIdentifiers.dstu4145be, new GordianDSTUEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final DSTU4145Params  myParms = DSTU4145Params.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final DSTU4145Params pParms) throws OceanusException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }
            return determineKeyPairSpec(pParms.getNamedCurve());
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final X962Parameters pParms) throws OceanusException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }
            return determineKeyPairSpec((ASN1ObjectIdentifier) pParms.getParameters());
        }

        /**
         * Obtain keySpec from curveId.
         * @param pId the curveId
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final ASN1ObjectIdentifier pId) throws OceanusException {
            /* Check for EC named surve */
            final String myName = pId.toString();
            final ECDomainParameters myParms = DSTU4145NamedCurves.getByOID(pId);
            final GordianDSTU4145Elliptic myCurve = GordianDSTU4145Elliptic.getCurveForName(myName);
            if (myParms == null || myCurve == null) {
                throw new GordianDataException(ERROR_UNSUPCURVE + myName);
            }
            return GordianKeyPairSpec.dstu4145(myCurve);
        }
    }

    /**
     * GOST Encoded parser.
     */
    private static final class GordianGOSTEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            final GordianGOSTEncodedParser myParser = new GordianGOSTEncodedParser();
            pIdManager.registerParser(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, myParser);
            pIdManager.registerParser(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, myParser);
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return determineKeyPairSpec(pInfo.getAlgorithm());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return determineKeyPairSpec(pInfo.getPrivateKeyAlgorithm());
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pId the algorithmId
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final AlgorithmIdentifier pId) throws OceanusException {
            /* Determine the curve name */
            final GOST3410PublicKeyAlgParameters myParms = GOST3410PublicKeyAlgParameters.getInstance(pId.getParameters());
            final ASN1ObjectIdentifier myId = myParms.getPublicKeyParamSet();
            final String myName = ECGOST3410NamedCurves.getName(myId);

            /* Determine curve */
            if (myName != null) {
                final GordianGOSTElliptic myCurve = GordianGOSTElliptic.getCurveForName(myName);
                if (myCurve == null) {
                    throw new GordianDataException(ERROR_UNSUPCURVE + myName);
                }
                return GordianKeyPairSpec.gost2012(myCurve);
            }

            /* Curve is not supported */
            throw new GordianDataException(ERROR_UNSUPCURVE + myParms);
        }
    }

    /**
     * SPHINCSPlus Encoded parser.
     */
    private static class GordianSPHINCSPlusEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianSPHINCSPlusEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_128f,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA128F)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_128s,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA128S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_192f,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA192F)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_192s,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA192S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_256f,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA256F)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_256s,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA256S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_128f,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE128F)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_128s,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE128S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_192f,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE192F)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_192s,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE192S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_256f,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE256F)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_256s,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE256S)));

            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA128F_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA128S_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA192F_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA192S_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA256F_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHA256S_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_128f_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE128F_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_128s_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE128S_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_192f_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE192F_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_192s_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE192S_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_256f_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE256F_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_shake_256s_r3,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.SHAKE256S_R)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.HARAKA128F_S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.HARAKA128S_S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.HARAKA192F_S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.HARAKA192S_S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.HARAKA256F_S)));
            pIdManager.registerParser(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple,
                    new GordianSPHINCSPlusEncodedParser(GordianKeyPairSpec.sphincsPlus(GordianSPHINCSPlusSpec.HARAKA256S_S)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianCMCEEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.mceliece348864_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.BASE3488)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece348864f_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.PIVOT3488)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece460896_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.BASE4608)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece460896f_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.PIVOT4608)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece6688128_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.BASE6688)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece6688128f_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.PIVOT6688)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece6960119_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.BASE6960)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece6960119f_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.PIVOT6960)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece8192128_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.BASE8192)));
            pIdManager.registerParser(BCObjectIdentifiers.mceliece8192128f_r3, new GordianCMCEEncodedParser(GordianKeyPairSpec.cmce(GordianCMCESpec.PIVOT8192)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianFrodoEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.frodokem640aes, new GordianFrodoEncodedParser(GordianKeyPairSpec.frodo(GordianFRODOSpec.AES640)));
            pIdManager.registerParser(BCObjectIdentifiers.frodokem640shake, new GordianFrodoEncodedParser(GordianKeyPairSpec.frodo(GordianFRODOSpec.SHAKE640)));
            pIdManager.registerParser(BCObjectIdentifiers.frodokem976aes, new GordianFrodoEncodedParser(GordianKeyPairSpec.frodo(GordianFRODOSpec.AES976)));
            pIdManager.registerParser(BCObjectIdentifiers.frodokem976shake, new GordianFrodoEncodedParser(GordianKeyPairSpec.frodo(GordianFRODOSpec.SHAKE976)));
            pIdManager.registerParser(BCObjectIdentifiers.frodokem1344aes, new GordianFrodoEncodedParser(GordianKeyPairSpec.frodo(GordianFRODOSpec.AES1344)));
            pIdManager.registerParser(BCObjectIdentifiers.frodokem1344shake, new GordianFrodoEncodedParser(GordianKeyPairSpec.frodo(GordianFRODOSpec.SHAKE1344)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianSABEREncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.lightsaberkem128r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.LIGHT128)));
            pIdManager.registerParser(BCObjectIdentifiers.saberkem128r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.BASE128)));
            pIdManager.registerParser(BCObjectIdentifiers.firesaberkem128r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.FIRE128)));
            pIdManager.registerParser(BCObjectIdentifiers.lightsaberkem192r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.LIGHT192)));
            pIdManager.registerParser(BCObjectIdentifiers.saberkem192r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.BASE192)));
            pIdManager.registerParser(BCObjectIdentifiers.firesaberkem192r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.FIRE192)));
            pIdManager.registerParser(BCObjectIdentifiers.lightsaberkem256r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.LIGHT256)));
            pIdManager.registerParser(BCObjectIdentifiers.saberkem256r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.BASE256)));
            pIdManager.registerParser(BCObjectIdentifiers.firesaberkem256r3, new GordianSABEREncodedParser(GordianKeyPairSpec.saber(GordianSABERSpec.FIRE256)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }
    }

    /**
     * Kyber Encoded parser.
     */
    private static class GordianKyberEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianKyberEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.kyber512, new GordianKyberEncodedParser(GordianKeyPairSpec.kyber(GordianKYBERSpec.KYBER512)));
            pIdManager.registerParser(BCObjectIdentifiers.kyber768, new GordianKyberEncodedParser(GordianKeyPairSpec.kyber(GordianKYBERSpec.KYBER768)));
            pIdManager.registerParser(BCObjectIdentifiers.kyber1024, new GordianKyberEncodedParser(GordianKeyPairSpec.kyber(GordianKYBERSpec.KYBER1024)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }
    }

    /**
     * Dilithium Encoded parser.
     */
    private static class GordianDilithiumEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianDilithiumEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.dilithium2, new GordianDilithiumEncodedParser(GordianKeyPairSpec.dilithium(GordianDILITHIUMSpec.DILITHIUM2)));
            pIdManager.registerParser(BCObjectIdentifiers.dilithium3, new GordianDilithiumEncodedParser(GordianKeyPairSpec.dilithium(GordianDILITHIUMSpec.DILITHIUM3)));
            pIdManager.registerParser(BCObjectIdentifiers.dilithium5, new GordianDilithiumEncodedParser(GordianKeyPairSpec.dilithium(GordianDILITHIUMSpec.DILITHIUM5)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianHQCEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.hqc128, new GordianDilithiumEncodedParser(GordianKeyPairSpec.hqc(GordianHQCSpec.HQC128)));
            pIdManager.registerParser(BCObjectIdentifiers.hqc192, new GordianDilithiumEncodedParser(GordianKeyPairSpec.hqc(GordianHQCSpec.HQC192)));
            pIdManager.registerParser(BCObjectIdentifiers.hqc256, new GordianDilithiumEncodedParser(GordianKeyPairSpec.hqc(GordianHQCSpec.HQC256)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianBIKEEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.bike128, new GordianBIKEEncodedParser(GordianKeyPairSpec.bike(GordianBIKESpec.BIKE128)));
            pIdManager.registerParser(BCObjectIdentifiers.bike192, new GordianBIKEEncodedParser(GordianKeyPairSpec.bike(GordianBIKESpec.BIKE192)));
            pIdManager.registerParser(BCObjectIdentifiers.bike256, new GordianBIKEEncodedParser(GordianKeyPairSpec.bike(GordianBIKESpec.BIKE256)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianNTRUEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.ntruhps2048509, new GordianNTRUEncodedParser(GordianKeyPairSpec.ntru(GordianNTRUSpec.HPS509)));
            pIdManager.registerParser(BCObjectIdentifiers.ntruhps2048677, new GordianNTRUEncodedParser(GordianKeyPairSpec.ntru(GordianNTRUSpec.HPS677)));
            pIdManager.registerParser(BCObjectIdentifiers.ntruhps4096821, new GordianNTRUEncodedParser(GordianKeyPairSpec.ntru(GordianNTRUSpec.HPS821)));
            pIdManager.registerParser(BCObjectIdentifiers.ntruhrss701, new GordianNTRUEncodedParser(GordianKeyPairSpec.ntru(GordianNTRUSpec.HRSS701)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianNTRUPrimeEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.ntrulpr653,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR653))));
            pIdManager.registerParser(BCObjectIdentifiers.ntrulpr761,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR761))));
            pIdManager.registerParser(BCObjectIdentifiers.ntrulpr857,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR857))));
            pIdManager.registerParser(BCObjectIdentifiers.ntrulpr953,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR953))));
            pIdManager.registerParser(BCObjectIdentifiers.ntrulpr1013,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR1013))));
            pIdManager.registerParser(BCObjectIdentifiers.ntrulpr1277,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR1277))));
            pIdManager.registerParser(BCObjectIdentifiers.sntrup653,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.SNTRU, GordianNTRUPrimeParams.PR653))));
            pIdManager.registerParser(BCObjectIdentifiers.sntrup761,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.SNTRU, GordianNTRUPrimeParams.PR761))));
            pIdManager.registerParser(BCObjectIdentifiers.sntrup857,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.SNTRU, GordianNTRUPrimeParams.PR857))));
            pIdManager.registerParser(BCObjectIdentifiers.sntrup953,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.SNTRU, GordianNTRUPrimeParams.PR953))));
            pIdManager.registerParser(BCObjectIdentifiers.sntrup1013,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.SNTRU, GordianNTRUPrimeParams.PR1013))));
            pIdManager.registerParser(BCObjectIdentifiers.sntrup1277,
                new GordianNTRUPrimeEncodedParser(GordianKeyPairSpec.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.SNTRU, GordianNTRUPrimeParams.PR1277))));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianFalconEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.falcon_512, new GordianFalconEncodedParser(GordianKeyPairSpec.falcon(GordianFALCONSpec.FALCON512)));
            pIdManager.registerParser(BCObjectIdentifiers.falcon_1024, new GordianFalconEncodedParser(GordianKeyPairSpec.falcon(GordianFALCONSpec.FALCON1024)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
         * @param pKeySpec the keySpec
         */
        GordianPicnicEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.picnicl1ur, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L1UR)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl1fs, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L1FS)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl1full, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L1FULL)));
            pIdManager.registerParser(BCObjectIdentifiers.picnic3l1, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L13)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl3ur, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L3UR)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl3fs, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L3FS)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl3full, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L3FULL)));
            pIdManager.registerParser(BCObjectIdentifiers.picnic3l3, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L33)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl5ur, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L5UR)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl5fs, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L5FS)));
            pIdManager.registerParser(BCObjectIdentifiers.picnicl5full, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L5FULL)));
            pIdManager.registerParser(BCObjectIdentifiers.picnic3l5, new GordianPicnicEncodedParser(GordianKeyPairSpec.picnic(GordianPICNICSpec.L53)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }
    }

    /**
     * Rainbow Encoded parser.
     */
    private static class GordianRainbowEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianRainbowEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(BCObjectIdentifiers.rainbow_III_classic, new GordianRainbowEncodedParser(GordianKeyPairSpec.rainbow(GordianRainbowSpec.CLASSIC3)));
            pIdManager.registerParser(BCObjectIdentifiers.rainbow_III_circumzenithal, new GordianRainbowEncodedParser(GordianKeyPairSpec.rainbow(GordianRainbowSpec.CIRCUM3)));
            pIdManager.registerParser(BCObjectIdentifiers.rainbow_III_compressed, new GordianRainbowEncodedParser(GordianKeyPairSpec.rainbow(GordianRainbowSpec.COMPRESSED3)));
            pIdManager.registerParser(BCObjectIdentifiers.rainbow_V_classic, new GordianRainbowEncodedParser(GordianKeyPairSpec.rainbow(GordianRainbowSpec.CLASSIC5)));
            pIdManager.registerParser(BCObjectIdentifiers.rainbow_V_circumzenithal, new GordianRainbowEncodedParser(GordianKeyPairSpec.rainbow(GordianRainbowSpec.CIRCUM5)));
            pIdManager.registerParser(BCObjectIdentifiers.rainbow_V_compressed, new GordianRainbowEncodedParser(GordianKeyPairSpec.rainbow(GordianRainbowSpec.COMPRESSED5)));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }
    }

    /**
     * XMSS Encoded parser.
     */
    private static final class GordianXMSSEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.xmss, new GordianXMSSEncodedParser());
            pIdManager.registerParser(IsaraObjectIdentifiers.id_alg_xmss, new GordianXMSSEncodedParser());
        }


        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
            if (myParms != null) {
                return determineKeyPairSpec(myParms);
            }

            /* Protect against exceptions */
            try {
                final byte[] keyEnc = ASN1OctetString.getInstance(pInfo.parsePublicKey()).getOctets();
                final int myOID = Pack.bigEndianToInt(keyEnc, 0);
                final XMSSParameters myParams = XMSSParameters.lookupByOID(myOID);
                return GordianKeyPairSpec.xmss(determineKeyType(myParams.getTreeDigestOID()),
                        determineHeight(myParams.getHeight()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to resolve key", e);
            }
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final XMSSKeyParams pParms) throws OceanusException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            final GordianXMSSHeight myHeight = determineHeight(pParms.getHeight());
            return GordianKeyPairSpec.xmss(determineKeyType(myDigest), myHeight);
        }

        /**
         * Obtain keyType from digest.
         * @param pDigest the treeDigest
         * @return the keyType
         * @throws OceanusException on error
         */
        static GordianXMSSDigestType determineKeyType(final ASN1ObjectIdentifier pDigest) throws OceanusException {
            if (pDigest.equals(NISTObjectIdentifiers.id_sha256)) {
                return GordianXMSSDigestType.SHA256;
            }
            if (pDigest.equals(NISTObjectIdentifiers.id_sha512)) {
                return GordianXMSSDigestType.SHA512;
            }
            if (pDigest.equals(NISTObjectIdentifiers.id_shake128)) {
                return GordianXMSSDigestType.SHAKE128;
            }
            if (pDigest.equals(NISTObjectIdentifiers.id_shake256)) {
                return GordianXMSSDigestType.SHAKE256;
            }

            /* Tree Digest is not supported */
            throw new GordianDataException(ERROR_TREEDIGEST + pDigest);
        }

        /**
         * Obtain height.
         * @param pHeight the height
         * @return the xmssHeight
         * @throws OceanusException on error
         */
        static GordianXMSSHeight determineHeight(final int pHeight) throws OceanusException {
            /* Loo through the heights */
            for (GordianXMSSHeight myHeight : GordianXMSSHeight.values()) {
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
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.xmss_mt, new GordianXMSSMTEncodedParser());
            pIdManager.registerParser(IsaraObjectIdentifiers.id_alg_xmssmt, new GordianXMSSMTEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
            if (myParms != null) {
                return determineKeyPairSpec(myParms);
            }

            /* Protect against exceptions */
            try {
                final byte[] keyEnc = ASN1OctetString.getInstance(pInfo.parsePublicKey()).getOctets();
                final int myOID = Pack.bigEndianToInt(keyEnc, 0);
                final XMSSMTParameters myParams = XMSSMTParameters.lookupByOID(myOID);
                return GordianKeyPairSpec.xmssmt(GordianXMSSEncodedParser.determineKeyType(myParams.getTreeDigestOID()),
                        GordianXMSSEncodedParser.determineHeight(myParams.getHeight()), determineLayers(myParams.getLayers()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to resolve key",  e);
            }
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
            return determineKeyPairSpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static GordianKeyPairSpec determineKeyPairSpec(final XMSSMTKeyParams pParms) throws OceanusException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            final GordianXMSSHeight myHeight = GordianXMSSEncodedParser.determineHeight(pParms.getHeight());
            final GordianXMSSMTLayers myLayers = determineLayers(pParms.getLayers());
            return GordianKeyPairSpec.xmssmt(GordianXMSSEncodedParser.determineKeyType(myDigest), myHeight, myLayers);
        }

        /**
         * Obtain layers.
         * @param pLayers the layers
         * @return the xmssMTLayers
         * @throws OceanusException on error
         */
        static GordianXMSSMTLayers determineLayers(final int pLayers) throws OceanusException {
            /* Loo through the heights */
            for (GordianXMSSMTLayers myLayers : GordianXMSSMTLayers.values()) {
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
        private final GordianKeyPairSpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianEdwardsEncodedParser(final GordianKeyPairSpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(EdECObjectIdentifiers.id_X25519, new GordianEdwardsEncodedParser(GordianKeyPairSpec.x25519()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_X448, new GordianEdwardsEncodedParser(GordianKeyPairSpec.x448()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_Ed25519, new GordianEdwardsEncodedParser(GordianKeyPairSpec.ed25519()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_Ed448, new GordianEdwardsEncodedParser(GordianKeyPairSpec.ed448()));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }
    }

    /**
     * LMS Encoded parser.
     */
    private static final class GordianLMSEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new GordianLMSEncodedParser());
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final LMSKeyParameters myParms = (LMSKeyParameters) PublicKeyFactory.createKey(pInfo);
                if (myParms instanceof HSSPublicKeyParameters) {
                    final HSSPublicKeyParameters myPublic = (HSSPublicKeyParameters) myParms;
                    final int myDepth = myPublic.getL();
                    final LMSPublicKeyParameters myLMSPublicKey = myPublic.getLMSPublicKey();
                    final GordianLMSKeySpec myKeySpec = determineKeyPairSpec(myLMSPublicKey);
                    return GordianKeyPairSpec.hss(myKeySpec, myDepth);

                } else {
                    final LMSPublicKeyParameters myPublic = (LMSPublicKeyParameters) myParms;
                    final GordianLMSKeySpec myKeySpec = determineKeyPairSpec(myPublic);
                    return GordianKeyPairSpec.lms(myKeySpec);
                }

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
         }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final LMSKeyParameters myParms = (LMSKeyParameters) PrivateKeyFactory.createKey(pInfo);
                if (myParms instanceof HSSPrivateKeyParameters) {
                    final HSSPrivateKeyParameters myPrivate = (HSSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
                    final int myDepth = myPrivate.getL();
                    final LMSPublicKeyParameters myLMSPublicKey = myPrivate.getPublicKey().getLMSPublicKey();
                    final GordianLMSKeySpec myKeySpec = determineKeyPairSpec(myLMSPublicKey);
                    return GordianKeyPairSpec.hss(myKeySpec, myDepth);

                } else {
                    final LMSPrivateKeyParameters myPrivate = (LMSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
                    return GordianKeyPairSpec.lms(GordianLMSKeySpec.determineKeySpec(myPrivate.getSigParameters(), myPrivate.getOtsParameters()));
                }

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        /**
         * Obtain keySpec from public key.
         * @param pPublic the publicKeyParams
         * @return the LMSKeySpec
         */
        static GordianLMSKeySpec determineKeyPairSpec(final LMSPublicKeyParameters pPublic) {
            return GordianLMSKeySpec.determineKeySpec(pPublic.getSigParameters(), pPublic.getOtsParameters());
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
         * @param pIdManager the idManager
         */
        GordianCompositeEncodedParser(final GordianKeyPairAlgId pIdManager) {
            theIdManager = pIdManager;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianKeyPairAlgId pIdManager) {
            pIdManager.registerParser(MiscObjectIdentifiers.id_alg_composite, new GordianCompositeEncodedParser(pIdManager));
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
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
                return GordianKeyPairSpec.composite(mySpecs);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }

        @Override
        public GordianKeyPairSpec determineKeyPairSpec(final PrivateKeyInfo pInfo) throws OceanusException {
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
                return GordianKeyPairSpec.composite(mySpecs);

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianIOException(ERROR_PARSE, e);
            }
        }
    }
}
