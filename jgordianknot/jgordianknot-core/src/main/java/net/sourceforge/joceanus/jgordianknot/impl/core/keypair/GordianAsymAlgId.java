/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
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
import org.bouncycastle.pqc.asn1.McElieceCCA2PrivateKey;
import org.bouncycastle.pqc.asn1.McElieceCCA2PublicKey;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.asn1.SPHINCS256KeyParams;
import org.bouncycastle.pqc.asn1.XMSSKeyParams;
import org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.util.Pack;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianLMSKeySpec.GordianLMSOtsType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianLMSKeySpec.GordianLMSSigType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec.GordianMcElieceDigestType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianQTESLAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianSPHINCSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSMTLayers;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Mappings from EncodedId to AsymKeySpec.
 */
public class GordianAsymAlgId {
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
    public GordianAsymAlgId() {
        /* Create the map */
        theParserMap = new HashMap<>();

        /* Register the parsers */
        GordianRSAEncodedParser.register(this);
        GordianDSAEncodedParser.register(this);
        GordianDHEncodedParser.register(this);
        GordianECEncodedParser.register(this);
        GordianDSTUEncodedParser.register(this);
        GordianGOSTEncodedParser.register(this);
        GordianEdwardsEncodedParser.register(this);
        GordianRainbowEncodedParser.register(this);
        GordianNewHopeEncodedParser.register(this);
        GordianSPHINCSEncodedParser.register(this);
        GordianXMSSEncodedParser.register(this);
        GordianXMSSMTEncodedParser.register(this);
        GordianMcElieceEncodedParser.register(this);
        GordianMcElieceCCA2EncodedParser.register(this);
        GordianQTESLAEncodedParser.register(this);
        GordianLMSEncodedParser.register(this);
    }

    /**
     * Obtain KeySpec from X509KeySpec.
     * @param pEncoded X509 keySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    public GordianAsymKeySpec determineKeySpec(final PKCS8EncodedKeySpec pEncoded) throws OceanusException {
        /* Determine the algorithm Id. */
        final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pEncoded.getEncoded());
        final AlgorithmIdentifier myId = myInfo.getPrivateKeyAlgorithm();
        final ASN1ObjectIdentifier myAlgId = myId.getAlgorithm();

        /* Obtain the parser */
        final GordianEncodedParser myParser = theParserMap.get(myAlgId);
        if (myParser != null) {
            return myParser.determineKeySpec(myInfo);
        }
        throw new GordianDataException(ERROR_ALGO);
    }

    /**
     * Obtain KeySpec from X509KeySpec.
     * @param pEncoded X509 keySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    public GordianAsymKeySpec determineKeySpec(final X509EncodedKeySpec pEncoded) throws OceanusException {
        /* Determine the algorithm Id. */
        final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pEncoded.getEncoded());
        final AlgorithmIdentifier myId = myInfo.getAlgorithm();
        final ASN1ObjectIdentifier myAlgId = myId.getAlgorithm();

        /* Obtain the parser */
        final GordianEncodedParser myParser = theParserMap.get(myAlgId);
        if (myParser != null) {
            return myParser.determineKeySpec(myInfo);
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
        GordianAsymKeySpec determineKeySpec(SubjectPublicKeyInfo pInfo) throws OceanusException;

        /**
         * Obtain KeySpec from SubjectPublicKeyInfo.
         * @param pInfo keySpec
         * @return the keySpec
         * @throws OceanusException on error
         */
        GordianAsymKeySpec determineKeySpec(PrivateKeyInfo pInfo) throws OceanusException;
    }

    /**
     * RSA Encoded parser.
     */
    private static class GordianRSAEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.rsaEncryption, new GordianRSAEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse the publicKey */
                final RSAPublicKey myPublic = RSAPublicKey.getInstance(pInfo.parsePublicKey());
                return determineKeySpec(myPublic.getModulus());

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianDataException(ERROR_PARSE);
            }
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse the publicKey */
                final RSAPrivateKey myPrivate = RSAPrivateKey.getInstance(pInfo.parsePrivateKey());
                return determineKeySpec(myPrivate.getModulus());

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianDataException(ERROR_PARSE);
            }
        }

        /**
         * Obtain keySpec from Modulus.
         * @param pModulus the modulus
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final BigInteger pModulus) throws OceanusException {
            final GordianRSAModulus myModulus = GordianRSAModulus.getModulusForInteger(pModulus);
            if (myModulus == null) {
                throw new GordianDataException("RSA strength not supported: " + pModulus.bitLength());
            }
            return  GordianAsymKeySpec.rsa(myModulus);
        }
    }

    /**
     * DSA Encoded parser.
     */
    private static class GordianDSAEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(X9ObjectIdentifiers.id_dsa, new GordianDSAEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final DSAParameter myParms = DSAParameter.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final DSAParameter pParms) throws OceanusException {
            final GordianDSAKeyType myKeyType = GordianDSAKeyType.getDSATypeForParms(pParms);
            if (myKeyType == null) {
                throw new GordianDataException("Unsupported DSA parameters: "
                        + pParms.getP().bitLength() + ":" + pParms.getQ().bitLength());
            }
            return  GordianAsymKeySpec.dsa(myKeyType);
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
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.dhKeyAgreement, new GordianDHEncodedParser());
            pIdManager.registerParser(X9ObjectIdentifiers.dhpublicnumber, new GordianDHEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final DHParameters myParms = determineParameters(pInfo.getAlgorithm());
            return determineKeySpec(myParms);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final DHParameters myParms = determineParameters(pInfo.getPrivateKeyAlgorithm());
            return determineKeySpec(myParms);
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
        private static  GordianAsymKeySpec determineKeySpec(final DHParameters pParms) throws OceanusException {
            final GordianDHGroup myGroup = GordianDHGroup.getGroupForParams(pParms);
            if (myGroup == null) {
                throw new GordianDataException("Unsupported DH parameters: "
                        + pParms.getP().bitLength());
            }
            return  GordianAsymKeySpec.dh(myGroup);
        }
    }

    /**
     * EC Encoded parser.
     */
    private static class GordianECEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(X9ObjectIdentifiers.id_ecPublicKey, new GordianECEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final X962Parameters pParms) throws OceanusException {
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
                    return GordianAsymKeySpec.ec(myDSACurve);
                }
                final GordianSM2Elliptic mySM2Curve = GordianSM2Elliptic.getCurveForName(myName);
                if (mySM2Curve != null) {
                    return GordianAsymKeySpec.sm2(mySM2Curve);
                }
                throw new GordianDataException(ERROR_UNSUPCURVE + myName);
            }

            /* Curve is not supported */
            throw new GordianDataException(ERROR_UNSUPCURVE + pParms.toString());
        }
    }

    /**
     * DSTU Encoded parser.
     */
    private static class GordianDSTUEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(UAObjectIdentifiers.dstu4145be, new GordianDSTUEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final DSTU4145Params  myParms = DSTU4145Params.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final X962Parameters myParms = X962Parameters.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final DSTU4145Params pParms) throws OceanusException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }
            return determineKeySpec(pParms.getNamedCurve());
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final X962Parameters pParms) throws OceanusException {
            /* Reject if not a named curve */
            if (!pParms.isNamedCurve()) {
                throw new GordianDataException(ERROR_NAMEDCURVE);
            }
            return determineKeySpec((ASN1ObjectIdentifier) pParms.getParameters());
        }

        /**
         * Obtain keySpec from curveId.
         * @param pId the curveId
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final ASN1ObjectIdentifier pId) throws OceanusException {
            /* Check for EC named surve */
            final String myName = pId.toString();
            final ECDomainParameters myParms = DSTU4145NamedCurves.getByOID(pId);
            final GordianDSTU4145Elliptic myCurve = GordianDSTU4145Elliptic.getCurveForName(myName);
            if (myParms == null || myCurve == null) {
                throw new GordianDataException(ERROR_UNSUPCURVE + myName);
            }
            return GordianAsymKeySpec.dstu4145(myCurve);
        }
    }

    /**
     * GOST Encoded parser.
     */
    private static class GordianGOSTEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            final GordianGOSTEncodedParser myParser = new GordianGOSTEncodedParser();
            pIdManager.registerParser(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, myParser);
            pIdManager.registerParser(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, myParser);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return determineKeySpec(pInfo.getAlgorithm());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return determineKeySpec(pInfo.getPrivateKeyAlgorithm());
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pId the algorithmId
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final AlgorithmIdentifier pId) throws OceanusException {
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
                return GordianAsymKeySpec.gost2012(myCurve);
            }

            /* Curve is not supported */
            throw new GordianDataException(ERROR_UNSUPCURVE + myParms.toString());
        }
    }

    /**
     * Rainbow Encoded parser.
     */
    private static class GordianRainbowEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.rainbow, new GordianRainbowEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.rainbow();
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.rainbow();
        }
    }

    /**
     * NewHope Encoded parser.
     */
    private static class GordianNewHopeEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.newHope, new GordianNewHopeEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.newHope();
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.newHope();
        }
    }

    /**
     * SPHINCS Encoded parser.
     */
    private static class GordianSPHINCSEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.sphincs256, new GordianSPHINCSEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final SPHINCS256KeyParams myParms = SPHINCS256KeyParams.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final SPHINCS256KeyParams myParms = SPHINCS256KeyParams.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final SPHINCS256KeyParams pParms) throws OceanusException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            if (myDigest.equals(NISTObjectIdentifiers.id_sha512_256)) {
                return GordianAsymKeySpec.sphincs(GordianSPHINCSDigestType.SHA2);
            }
            if (myDigest.equals(NISTObjectIdentifiers.id_sha3_256)) {
                return GordianAsymKeySpec.sphincs(GordianSPHINCSDigestType.SHA3);
            }

            /* Tree Digest is not supported */
            throw new GordianDataException(ERROR_TREEDIGEST + myDigest.toString());
        }


    }

    /**
     * XMSS Encoded parser.
     */
    private static class GordianXMSSEncodedParser implements GordianEncodedParser {
        /**
         * From Isara.
         */
        private static final ASN1ObjectIdentifier ISARA_XMSS = new ASN1ObjectIdentifier("0.4.0.127.0.15.1.1.13.0");

        /**
         * # of standard configs per digestType.
         */
        private static final int NUM_CONFIGS = 3;

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.xmss, new GordianXMSSEncodedParser());
            pIdManager.registerParser(ISARA_XMSS, new GordianXMSSEncodedParser());
        }


        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
            if (myParms != null) {
                return determineKeySpec(myParms);
            }

            /* Protect against exceptions */
            try {
                final byte[] keyEnc = ASN1OctetString.getInstance(pInfo.parsePublicKey()).getOctets();
                final int myOID = Pack.bigEndianToInt(keyEnc, 0);
                final GordianXMSSDigestType myDigest = GordianXMSSDigestType.class.getEnumConstants()[(myOID - 1) / NUM_CONFIGS];
                final XMSSParameters myParams = XMSSParameters.lookupByOID(myOID);
                return GordianAsymKeySpec.xmss(myDigest, determineHeight(myParams.getHeight()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to resolve key",  e);
            }
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final XMSSKeyParams myParms = XMSSKeyParams.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final XMSSKeyParams pParms) throws OceanusException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            final GordianXMSSHeight myHeight = determineHeight(pParms.getHeight());
            return GordianAsymKeySpec.xmss(determineKeyType(myDigest), myHeight);
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
            throw new GordianDataException(ERROR_TREEDIGEST + pDigest.toString());
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
    private static class GordianXMSSMTEncodedParser implements GordianEncodedParser {
        /**
         * From Isara.
         */
        private static final ASN1ObjectIdentifier ISARA_XMSSMT = new ASN1ObjectIdentifier("0.4.0.127.0.15.1.1.14.0");

        /**
         * # of standard configs per digestType.
         */
        private static final int NUM_CONFIGS = 8;

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.xmss_mt, new GordianXMSSMTEncodedParser());
            pIdManager.registerParser(ISARA_XMSSMT, new GordianXMSSMTEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getAlgorithm();
            final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
            if (myParms != null) {
                return determineKeySpec(myParms);
            }

            /* Protect against exceptions */
            try {
                final byte[] keyEnc = ASN1OctetString.getInstance(pInfo.parsePublicKey()).getOctets();
                final int myOID = Pack.bigEndianToInt(keyEnc, 0);
                final GordianXMSSDigestType myDigest = GordianXMSSDigestType.class.getEnumConstants()[(myOID - 1) / NUM_CONFIGS];
                final XMSSMTParameters myParams = XMSSMTParameters.lookupByOID(myOID);
                return GordianAsymKeySpec.xmssmt(myDigest, GordianXMSSEncodedParser.determineHeight(myParams.getHeight()),
                        determineLayers(myParams.getLayers()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to resolve key",  e);
            }
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            final AlgorithmIdentifier myId = pInfo.getPrivateKeyAlgorithm();
            final XMSSMTKeyParams myParms = XMSSMTKeyParams.getInstance(myId.getParameters());
            return determineKeySpec(myParms);
        }

        /**
         * Obtain keySpec from Parameters.
         * @param pParms the parameters
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final XMSSMTKeyParams pParms) throws OceanusException {
            final ASN1ObjectIdentifier myDigest = pParms.getTreeDigest().getAlgorithm();
            final GordianXMSSHeight myHeight = GordianXMSSEncodedParser.determineHeight(pParms.getHeight());
            final GordianXMSSMTLayers myLayers = determineLayers(pParms.getLayers());
            return GordianAsymKeySpec.xmssmt(GordianXMSSEncodedParser.determineKeyType(myDigest), myHeight, myLayers);
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
     * McEliece Encoded parser.
     */
    private static class GordianMcElieceEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.mcEliece, new GordianMcElieceEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.mcEliece(GordianMcElieceKeySpec.standard());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.mcEliece(GordianMcElieceKeySpec.standard());
        }
    }

    /**
     * McEliece Encoded parser.
     */
    private static class GordianMcElieceCCA2EncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.mcElieceCca2, new GordianMcElieceCCA2EncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final McElieceCCA2PublicKey myPublic = McElieceCCA2PublicKey.getInstance(pInfo.parsePublicKey());
                return determineKeySpec(myPublic.getDigest());

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianDataException(ERROR_PARSE);
            }
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final McElieceCCA2PrivateKey myPrivate = McElieceCCA2PrivateKey.getInstance(pInfo.parsePrivateKey());
                return determineKeySpec(myPrivate.getDigest());

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianDataException(ERROR_PARSE);
            }
        }

        /**
         * Obtain keySpec from digestId.
         * @param pDigest the digest
         * @return the keySpec
         * @throws OceanusException on error
         */
        private static  GordianAsymKeySpec determineKeySpec(final AlgorithmIdentifier pDigest) throws OceanusException {
            final GordianMcElieceDigestType myDigest = determineDigestType(pDigest);
            return GordianAsymKeySpec.mcEliece(GordianMcElieceKeySpec.cca2(myDigest));
        }

        /**
         * Obtain digestType from digest.
         * @param pDigest the treeDigest
         * @return the keyType
         * @throws OceanusException on error
         */
        static  GordianMcElieceDigestType determineDigestType(final AlgorithmIdentifier pDigest) throws OceanusException {
            final ASN1ObjectIdentifier myId = pDigest.getAlgorithm();
            if (myId.equals(OIWObjectIdentifiers.idSHA1)) {
                return GordianMcElieceDigestType.SHA1;
            }
            if (myId.equals(NISTObjectIdentifiers.id_sha224)) {
                return GordianMcElieceDigestType.SHA224;
            }
            if (myId.equals(NISTObjectIdentifiers.id_sha256)) {
                return GordianMcElieceDigestType.SHA256;
            }
            if (myId.equals(NISTObjectIdentifiers.id_sha384)) {
                return GordianMcElieceDigestType.SHA384;
            }
            if (myId.equals(NISTObjectIdentifiers.id_sha512)) {
                return GordianMcElieceDigestType.SHA512;
            }

            /* Tree Digest is not supported */
            throw new GordianDataException(ERROR_TREEDIGEST + pDigest.toString());
        }
    }

    /**
     * Edwards Encoded parser.
     */
    private static class GordianEdwardsEncodedParser implements GordianEncodedParser {
        /**
         * AsymKeySpec.
         */
        private final GordianAsymKeySpec theKeySpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         */
        GordianEdwardsEncodedParser(final GordianAsymKeySpec pKeySpec) {
            theKeySpec = pKeySpec;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(EdECObjectIdentifiers.id_X25519, new GordianEdwardsEncodedParser(GordianAsymKeySpec.x25519()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_X448, new GordianEdwardsEncodedParser(GordianAsymKeySpec.x448()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_Ed25519, new GordianEdwardsEncodedParser(GordianAsymKeySpec.ed25519()));
            pIdManager.registerParser(EdECObjectIdentifiers.id_Ed448, new GordianEdwardsEncodedParser(GordianAsymKeySpec.ed448()));
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return theKeySpec;
        }
    }

    /**
     * QTESLA Encoded parser.
     */
    private static class GordianQTESLAEncodedParser implements GordianEncodedParser {
        /**
         * QTESLA KeyType.
         */
        private final GordianQTESLAKeyType theKeyType;

        /**
         * Constructor.
         * @param pKeyType the keyType
         */
        GordianQTESLAEncodedParser(final GordianQTESLAKeyType pKeyType) {
            theKeyType = pKeyType;
        }

        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PQCObjectIdentifiers.qTESLA_p_I, new GordianQTESLAEncodedParser(GordianQTESLAKeyType.PROVABLY_SECURE_I));
            pIdManager.registerParser(PQCObjectIdentifiers.qTESLA_p_III, new GordianQTESLAEncodedParser(GordianQTESLAKeyType.PROVABLY_SECURE_III));
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.qTESLA(theKeyType);
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            return GordianAsymKeySpec.qTESLA(theKeyType);
        }
    }

    /**
     * LMS Encoded parser.
     */
    private static class GordianLMSEncodedParser implements GordianEncodedParser {
        /**
         * Registrar.
         * @param pIdManager the idManager
         */
        static void register(final GordianAsymAlgId pIdManager) {
            pIdManager.registerParser(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new GordianLMSEncodedParser());
        }

        @Override
        public GordianAsymKeySpec determineKeySpec(final SubjectPublicKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final LMSKeyParameters myParms = (LMSKeyParameters) PublicKeyFactory.createKey(pInfo);
                if (myParms instanceof HSSPublicKeyParameters) {
                    final HSSPublicKeyParameters myPublic = (HSSPublicKeyParameters) myParms;
                    final int myDepth = myPublic.getL();
                    final LMSPublicKeyParameters myLMSPublicKey = myPublic.getLMSPublicKey();
                    final GordianLMSKeySpec myKeySpec = determineKeySpec(myLMSPublicKey);
                    return GordianAsymKeySpec.hss(myKeySpec, myDepth);

                } else {
                    final LMSPublicKeyParameters myPublic = (LMSPublicKeyParameters) myParms;
                    final GordianLMSKeySpec myKeySpec = determineKeySpec(myPublic);
                    return GordianAsymKeySpec.lms(myKeySpec);
                }

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianDataException(ERROR_PARSE);
            }
         }

        @Override
        public GordianAsymKeySpec determineKeySpec(final PrivateKeyInfo pInfo) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Parse public key */
                final LMSKeyParameters myParms = (LMSKeyParameters) PrivateKeyFactory.createKey(pInfo);
                if (myParms instanceof HSSPrivateKeyParameters) {
                    final HSSPrivateKeyParameters myPrivate = (HSSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
                    final int myDepth = myPrivate.getL();
                    final LMSPublicKeyParameters myLMSPublicKey = myPrivate.getPublicKey().getLMSPublicKey();
                    final GordianLMSKeySpec myKeySpec = determineKeySpec(myLMSPublicKey);
                    return GordianAsymKeySpec.hss(myKeySpec, myDepth);

                } else {
                    final LMSPrivateKeyParameters myPrivate = (LMSPrivateKeyParameters) PrivateKeyFactory.createKey(pInfo);
                    final GordianLMSSigType mySigType = determineSigType(myPrivate.getSigParameters());
                    final GordianLMSOtsType myOtsType = determineOtsType(myPrivate.getOtsParameters());
                    return GordianAsymKeySpec.lms(new GordianLMSKeySpec(mySigType, myOtsType));
                }

                /* Handle exceptions */
            } catch (IOException e) {
                throw new GordianDataException(ERROR_PARSE);
            }
        }

        /**
         * Obtain keySpec from public key.
         * @param pPublic the publicKeyParams
         * @return the LMSKeySpec
         * @throws OceanusException on error
         */
        static GordianLMSKeySpec determineKeySpec(final LMSPublicKeyParameters pPublic) throws OceanusException {
            final GordianLMSSigType mySigType = determineSigType(pPublic.getSigParameters());
            final GordianLMSOtsType myOtsType = determineOtsType(pPublic.getOtsParameters());
            return new GordianLMSKeySpec(mySigType, myOtsType);
        }

        /**
         * Obtain sigType from params.
         * @param pParams the Params
         * @return the sigType
         * @throws OceanusException on error
         */
        static GordianLMSSigType determineSigType(final LMSigParameters pParams) throws OceanusException {
            /* Loop through the sigTypes */
            for (final GordianLMSSigType myType : GordianLMSSigType.values()) {
                if (myType.getParameter().equals(pParams)) {
                    return myType;
                }
            }

            /* Parameter not recognised */
            throw new GordianDataException(ERROR_PARSE);
        }

        /**
         * Obtain otsType from params.
         * @param pParams the Params
         * @return the sigType
         * @throws OceanusException on error
         */
        static GordianLMSOtsType determineOtsType(final LMOtsParameters pParams) throws OceanusException {
            /* Loop through the sigTypes */
            for (final GordianLMSOtsType myType : GordianLMSOtsType.values()) {
                if (myType.getParameter().equals(pParams)) {
                    return myType;
                }
            }

            /* Parameter not recognised */
            throw new GordianDataException(ERROR_PARSE);
        }
    }
}
