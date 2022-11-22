/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;

import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCompositeKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Certificate implementation.
 */
public class GordianCoreCertificate
    implements GordianCertificate {
    /**
     * The Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Subject.
     */
    private final GordianCertificateId theSubject;

    /**
     * The Issuer.
     */
    private final GordianCertificateId theIssuer;

    /**
     * The (public only) KeyPair to which this Certificate belongs.
     */
    private final GordianKeyPair theKeyPair;

    /**
     * The KeyUsage.
     */
    private final GordianKeyPairUsage theKeyUsage;

    /**
     * The CAStatus.
     */
    private final GordianCAStatus theCAStatus;

    /**
     * The Signature Algorithm.
     */
    private final AlgorithmIdentifier theSigAlgId;

    /**
     * The SignatureSpec.
     */
    private final GordianSignatureSpec theSigSpec;

    /**
     * The serial#.
     */
    private final BigInteger theSerialNo;

    /**
     * The TBS Certificate.
     */
    private final TBSCertificate theTbsCertificate;

    /**
     * The signature.
     */
    private final byte[] theSignature;

    /**
     * The encoded representation.
     */
    private final byte[] theEncoded;

    /**
     * Is the certificate self-signed?
     */
    private final boolean isSelfSigned;

    /**
     * Create a new self-signed certificate.
     *
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @throws OceanusException on error
     */
    GordianCoreCertificate(final GordianCoreFactory pFactory,
                           final GordianKeyPair pKeyPair,
                           final X500Name pSubject) throws OceanusException {
        /* Check that the keyPair is OK */
        if (isPublicOnly(pKeyPair)) {
            throw new GordianLogicException("Invalid keyPair");
        }

        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = getPublicOnly(pKeyPair);

        /* Determine the signatureSpec */
        theSigSpec = determineSignatureSpecForKeyPair(theKeyPair);

        /* Determine the algorithm Id for the signatureSpec */
        theSigAlgId = determineAlgIdForSignatureSpec(theSigSpec, theKeyPair);

        /* Create the TBSCertificate */
        theKeyUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        theCAStatus = new GordianCAStatus(true);
        theTbsCertificate = buildCertificate(null, pSubject);
        theSerialNo = theTbsCertificate.getSerialNumber().getValue();

        /* Create the signature */
        theSignature = createSignature(pKeyPair);
        isSelfSigned = true;

        /* Create the ids */
        theSubject = GordianCoreCertificateId.getSubjectId(this);
        theIssuer = GordianCoreCertificateId.getIssuerId(this);

        /* Store the encoded representation */
        theEncoded = encodeCertificate();
    }

    /**
     * Create a new certificate, signed by the relevant authority.
     *
     * @param pFactory the factory
     * @param pSigner  the signing keyPair/certificate
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @param pUsage   the key usage
     * @throws OceanusException on error
     */
    GordianCoreCertificate(final GordianCoreFactory pFactory,
                           final GordianKeyStorePair pSigner,
                           final GordianKeyPair pKeyPair,
                           final X500Name pSubject,
                           final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = getPublicOnly(pKeyPair);
        theKeyUsage = pUsage;

        /* Check that the signer is allowed to sign certificates */
        final GordianKeyPair mySignerPair = pSigner.getKeyPair();
        final GordianCoreCertificate mySignerCert = (GordianCoreCertificate) pSigner.getCertificateChain().get(0);
        if (!mySignerCert.getUsage().hasUse(GordianKeyPairUse.CERTIFICATE)
                || !mySignerCert.isValidNow()
                || isPublicOnly(mySignerPair)) {
            throw new GordianLogicException("Invalid signer");
        }

        /* Determine CA Status */
        theCAStatus = new GordianCAStatus(theKeyUsage, mySignerCert.theCAStatus);

        /* Determine the signatureSpec */
        theSigSpec = determineSignatureSpecForKeyPair(pSigner.getKeyPair());

        /* Determine the algorithm Id for the signatureSpec */
        theSigAlgId = determineAlgIdForSignatureSpec(theSigSpec, pSigner.getKeyPair());

        /* Create the TBSCertificate */
        theTbsCertificate = buildCertificate(mySignerCert, pSubject);
        theSerialNo = theTbsCertificate.getSerialNumber().getValue();

        /* Create the signature */
        theSignature = createSignature(mySignerPair);
        isSelfSigned = false;

        /* Create the ids */
        theSubject = GordianCoreCertificateId.getSubjectId(this);
        theIssuer = GordianCoreCertificateId.getIssuerId(this);

        /* Store the encoded representation */
        theEncoded = encodeCertificate();
    }

    /**
     * Parse a certificate.
     *
     * @param pFactory    the factory
     * @param pSequence   the DER representation of the certificate
     * @throws OceanusException on error
     */
    protected GordianCoreCertificate(final GordianCoreFactory pFactory,
                                     final byte[] pSequence) throws OceanusException {
        this(pFactory, Certificate.getInstance(pSequence));
    }

    /**
     * Parse a certificate.
     *
     * @param pFactory     the factory
     * @param pCertificate the certificate
     * @throws OceanusException on error
     */
    public GordianCoreCertificate(final GordianCoreFactory pFactory,
                                  final Certificate pCertificate) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Store the parameters */
            theFactory = pFactory;

            /* Extract the details */
            theTbsCertificate = pCertificate.getTBSCertificate();
            theSigAlgId = pCertificate.getSignatureAlgorithm();
            theSignature = pCertificate.getSignature().getBytes();

            /* Determine the signatureSpec for the algorithmId */
            theSigSpec = determineSignatureSpecForAlgId(theSigAlgId);
            if (theSigSpec == null) {
                throw new GordianDataException("Unsupported Signature AlgorithmId: " + theSigAlgId);
            }

            /* Derive the keyPair */
            theKeyPair = parseEncodedKey();

            /* Access the extensions */
            final Extensions myExtensions = theTbsCertificate.getExtensions();
            theKeyUsage = determineUsage(myExtensions);
            theCAStatus = GordianCAStatus.determineStatus(myExtensions);

            /* Determine whether we are self-signed */
            final X500Name mySignerName = getSubjectName();
            isSelfSigned = mySignerName.equals(getIssuerName());

            /* Create the ids */
            theSubject = GordianCoreCertificateId.getSubjectId(this);
            theIssuer = GordianCoreCertificateId.getIssuerId(this);
            theSerialNo = theTbsCertificate.getSerialNumber().getValue();

            /* Store the encoded representation */
            theEncoded = pCertificate.getEncoded();
        } catch (IOException e) {
            throw new GordianIOException("FAiled to parse certificate", e);
        }
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public boolean isValidOnDate(final Date pDate) {
        /* Access the date */
        return pDate.compareTo(theTbsCertificate.getStartDate().getDate()) >= 0
                && pDate.compareTo(theTbsCertificate.getEndDate().getDate()) <= 0;
    }

    /**
     * Obtain the certificate.
     * @return the certificate
     */
    public Certificate getCertificate() {
        return Certificate.getInstance(getEncoded());
    }

    @Override
    public byte[] getEncoded() {
        return Arrays.copyOf(theEncoded, theEncoded.length);
    }

    @Override
    public GordianCertificateId getSubject() {
        return theSubject;
    }

    @Override
    public GordianCertificateId getIssuer() {
        return theIssuer;
    }

    /**
     * Obtain the subject of the certificate.
     *
     * @return the subject
     */
    X500Name getSubjectName() {
        return theTbsCertificate.getSubject();
    }

    /**
     * Obtain the subjectId of the certificate.
     *
     * @return the subjectId
     */
    ASN1BitString getSubjectId() {
        return theTbsCertificate.getSubjectUniqueId();
    }

    /**
     * Obtain the issuer of the certificate.
     *
     * @return the issuer name
     */
    X500Name getIssuerName() {
        return theTbsCertificate.getIssuer();
    }

    /**
     * Obtain the issuerId of the certificate.
     *
     * @return the issuerId
     */
    ASN1BitString getIssuerId() {
        return isSelfSigned
               ? getSubjectId()
               : theTbsCertificate.getIssuerUniqueId();
    }

    @Override
    public boolean isSelfSigned() {
        return isSelfSigned;
    }

    @Override
    public GordianKeyPairUsage getUsage() {
        return theKeyUsage;
    }

    @Override
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    /**
     * Obtain the serial#.
     * @return the serial number.
     */
    public BigInteger getSerialNo() {
        return theSerialNo;
    }

    /**
     * Obtain the signatureSpec.
     * @return the signatureSpec
     */
    protected GordianSignatureSpec getSignatureSpec() {
        return theSigSpec;
    }

    /**
     * Is the keyPair publicOnly?
     * @param pKeyPair the keyPair
     * @return true/false
     */
    protected boolean isPublicOnly(final GordianKeyPair pKeyPair) {
        return pKeyPair.isPublicOnly();
    }

    /**
     * get public only version of key.
     * @param pKeyPair the key
     * @return the publicOnly version
     */
    protected GordianKeyPair getPublicOnly(final GordianKeyPair pKeyPair) {
        return pKeyPair instanceof GordianCompositeKeyPair ? ((GordianCompositeKeyPair) pKeyPair).getPublicOnly()
                                                           : ((GordianCoreKeyPair) pKeyPair).getPublicOnly();
    }

    /**
     * Determine the signatureSpec for the key.
     *
     * @param pKeyPair the keyPair
     * @return the signatureSpec
     */
    GordianSignatureSpec determineSignatureSpecForKeyPair(final GordianKeyPair pKeyPair) {
        return theFactory.getKeyPairFactory().getSignatureFactory().defaultForKeyPair(pKeyPair.getKeyPairSpec());
    }

    /**
     * Determine the signatureSpec for the algorithmId.
     *
     * @param pAlgId the algorithmId
     * @return the signatureSpec
     */
    GordianSignatureSpec determineSignatureSpecForAlgId(final AlgorithmIdentifier pAlgId) {
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getKeyPairFactory().getSignatureFactory();
        return mySigns.getSpecForIdentifier(pAlgId);
    }

    /**
     * Determine the algorithmId for the signatureSpec.
     *
     * @param pSpec the signatureSpec
     * @param pSigner the signer
     * @return the algorithmId
     */
    AlgorithmIdentifier determineAlgIdForSignatureSpec(final GordianSignatureSpec pSpec,
                                                       final GordianKeyPair pSigner) {
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getKeyPairFactory().getSignatureFactory();
        return mySigns.getIdentifierForSpecAndKeyPair(getSignatureSpec(), pSigner);
    }

    /**
     * Validate that the keyPair public Key matches.
     * @param pKeyPair the key pair
     * @return matches true/false
     */
    boolean checkMatchingPublicKey(final GordianKeyPair pKeyPair) {
        return pKeyPair instanceof GordianCompositeKeyPair ? ((GordianCompositeKeyPair) pKeyPair).checkMatchingPublicKey(getKeyPair())
                                                           : ((GordianCoreKeyPair) pKeyPair).checkMatchingPublicKey(getKeyPair());
    }

    /**
     * parse encodedKey.
     * @return the parsed key
     * @throws OceanusException on error
     */
    protected GordianKeyPair parseEncodedKey() throws OceanusException {
        /* Derive the keyPair */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final X509EncodedKeySpec myX509 = getX509KeySpec();
        final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509);
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
        return myGenerator.derivePublicOnlyKeyPair(myX509);
    }

    /**
     * Obtain the encoded publicKey.
     * @return the encoded bytes
     * @throws OceanusException on error
     */
    protected byte[] getPublicKeyEncoded() throws OceanusException {
        /* Access the keyPair */
        final GordianKeyPair myPair = getKeyPair();

        /* Access the keyPair generator */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myPair.getKeyPairSpec());

        /* Obtain the publicKey Info */
        return myGenerator.getX509Encoding(myPair).getEncoded();
    }

    /**
     * Obtain the digestSpec.
     *
     * @return the digestSpec
     */
    protected GordianDigestSpec getDigestSpec() {
        return getSignatureSpec().getDigestSpec();
    }

    /**
     * Create the signer.
     *
     * @return the signer
     * @throws OceanusException on error
     */
    protected GordianSignature createSigner() throws OceanusException {
        /* Create the signer */
        final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) getFactory().getKeyPairFactory().getSignatureFactory();
        return mySigns.createSigner(getSignatureSpec());
    }

    /**
     * Determine usage.
     *
     * @param pExtensions the extensions.
     * @return the usage
     */
    static GordianKeyPairUsage determineUsage(final Extensions pExtensions) {
        /* Access details */
        final KeyUsage myUsage = KeyUsage.fromExtensions(pExtensions);
        final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);
        final GordianKeyPairUsage myResult = new GordianKeyPairUsage();

        /* Check for CERTIFICATE */
        final boolean isCA = myConstraint != null && myConstraint.isCA();
        if (isCA && checkUsage(myUsage, KeyUsage.keyCertSign)) {
            myResult.addUse(GordianKeyPairUse.CERTIFICATE);
        }

        /* Check for signer. */
        if (checkUsage(myUsage, KeyUsage.digitalSignature)) {
            myResult.addUse(GordianKeyPairUse.SIGNATURE);
        }

        /* Check for nonRepudiation. */
        if (checkUsage(myUsage, KeyUsage.nonRepudiation)) {
            myResult.addUse(GordianKeyPairUse.NONREPUDIATION);
        }

        /* Check for keyAgreement. */
        if (checkUsage(myUsage, KeyUsage.keyAgreement)) {
            myResult.addUse(GordianKeyPairUse.AGREEMENT);
        }

        /* Check for keyEncryption. */
        if (checkUsage(myUsage, KeyUsage.keyEncipherment)) {
            myResult.addUse(GordianKeyPairUse.KEYENCRYPT);
        }

        /* Check for dataEncryption. */
        if (checkUsage(myUsage, KeyUsage.dataEncipherment)) {
            myResult.addUse(GordianKeyPairUse.DATAENCRYPT);
        }

        /* Check for encipherOnly. */
        if (checkUsage(myUsage, KeyUsage.encipherOnly)) {
            myResult.addUse(GordianKeyPairUse.ENCRYPTONLY);
        }

        /* Check for decipherOnly. */
        if (checkUsage(myUsage, KeyUsage.decipherOnly)) {
            myResult.addUse(GordianKeyPairUse.DECRYPTONLY);
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Check for usage.
     * @param pUsage the usage control
     * @param pRequired  the required usage
     * @return true/false
     */
    private static boolean checkUsage(final KeyUsage pUsage,
                                      final int pRequired) {
        return pUsage == null || pUsage.hasUsages(pRequired);
    }

    /**
     * Validate a certificate.
     *
     * @param pSigner the signer of the certiicate
     * @return valid? true/false
     * @throws OceanusException on error
     */
    public boolean validateCertificate(final GordianCoreCertificate pSigner) throws OceanusException {
        /* Check that the certificate is not self-signed */
        if (isSelfSigned) {
            throw new GordianDataException("Root certificate used as intermediary");
        }

        /* Check that the signing certificate is correct */
        final X500Name mySignerName = pSigner.getSubjectName();
        final DERBitString mySignerId = DERBitString.convert(pSigner.getSubjectId());
        if (!mySignerName.equals(getIssuerName())
                || !Objects.equals(mySignerId, getIssuerId())) {
            throw new GordianDataException("Incorrect signer certificate");
        }

        /* Check that the signing certificate is valid */
        if (!pSigner.getUsage().hasUse(GordianKeyPairUse.CERTIFICATE)
                || !pSigner.isValidNow()) {
            throw new GordianDataException("Invalid signer certificate");
        }

        /* Check that the signature is valid */
        return validateSignature(pSigner.getKeyPair());
     }

    /**
     * Validate a root certificate.
     *
     * @return valid? true/false
     * @throws OceanusException on error
     */
    public boolean validateRootCertificate() throws OceanusException {
        /* Check that the certificate is self-signed */
        if (!isSelfSigned) {
            throw new GordianDataException("Non-root certificate used as root");
        }

        /* Check that the certificate is valid self-signed */
        if (!theKeyUsage.hasUse(GordianKeyPairUse.CERTIFICATE)
                || theCAStatus.getPathLen() != null) {
            throw new GordianDataException("Invalid root certificate");
        }

        /* Check that the issuerId is null */
        if (theTbsCertificate.getIssuerUniqueId() != null) {
            throw new GordianDataException("Root certificate has distinct issuerUniqueId");
        }

        /* Check that the signature is valid */
        return validateSignature(theKeyPair);
    }

    /**
     * Create a certificate.
     * @param pSigner the signing certificate
     * @param pSubject the name of the certificate
     * @return the theCertificate
     * @throws OceanusException on error
     */
    private TBSCertificate buildCertificate(final GordianCoreCertificate pSigner,
                                            final X500Name pSubject) throws OceanusException {
        /* Create the name of the certificate */
        final X500Name myIssuer = pSigner == null
                                  ? pSubject
                                  : pSigner.getSubjectName();

        /* Using the current timestamp as the certificate serial number */
        final long myNow = System.currentTimeMillis();
        final BigInteger mySerialNo = BigInteger.valueOf(myNow);

        /* Create the startDate and endDate for the certificate */
        final Date myStart = new Date(myNow);
        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime(myStart);
        myCalendar.add(Calendar.YEAR, 1);
        final Date myEnd = myCalendar.getTime();

        /* Obtain the publicKey Info */
        final byte[] myPublicKeyEncoded = getPublicKeyEncoded();
        final SubjectPublicKeyInfo myPublicKeyInfo = SubjectPublicKeyInfo.getInstance(myPublicKeyEncoded);

        /* Build basic information */
        final V3TBSCertificateGenerator myCertBuilder = new V3TBSCertificateGenerator();
        myCertBuilder.setSubject(pSubject);
        myCertBuilder.setIssuer(myIssuer);
        myCertBuilder.setStartDate(new Time(myStart));
        myCertBuilder.setEndDate(new Time(myEnd));
        myCertBuilder.setSerialNumber(new ASN1Integer(mySerialNo));
        myCertBuilder.setSubjectPublicKeyInfo(myPublicKeyInfo);
        myCertBuilder.setSignature(theSigAlgId);
        myCertBuilder.setSubjectUniqueID(createSubjectId(myPublicKeyEncoded, mySerialNo));
        if (pSigner != null) {
            myCertBuilder.setIssuerUniqueID(DERBitString.convert(pSigner.getSubjectId()));
        }

        /* Create extensions for the certificate */
        myCertBuilder.setExtensions(createExtensions());

        /* Generate the TBS Certificate */
        return myCertBuilder.generateTBSCertificate();
    }

    /**
     * Create extensions for tbsCertificate.
     *
     * @return the extensions
     * @throws OceanusException on error
     */
    private Extensions createExtensions() throws OceanusException {
        /* Create extensions for the certificate */
        final ExtensionsGenerator myExtGenerator = new ExtensionsGenerator();
        createKeyUseExtensions(myExtGenerator);
        theCAStatus.createExtensions(myExtGenerator);
        return myExtGenerator.generate();
    }

    /**
     * Create extensions.
     * @param pGenerator the extensions generator
     * @throws OceanusException on error
     */
    private void createKeyUseExtensions(final ExtensionsGenerator pGenerator) throws OceanusException {
        /* Protect against exceptions */
        try {
            pGenerator.addExtension(Extension.keyUsage, true, theKeyUsage.getKeyUsage());
        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }

    /**
     * Create the subjectId.
     *
     * @param pEncodedPublicKey the publicKey
     * @param pSerialNo         the certificate Serial#
     * @return the subjectId
     * @throws OceanusException on error
     */
    private DERBitString createSubjectId(final byte[] pEncodedPublicKey,
                                         final BigInteger pSerialNo) throws OceanusException {
        /* Build the hash */
        final GordianDigestSpec mySpec = getDigestSpec();
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianDigest myDigest = myDigests.createDigest(mySpec);
        myDigest.update(pEncodedPublicKey);
        myDigest.update(pSerialNo.toByteArray());

        /* Create the subjectId */
        return new DERBitString(myDigest.finish());
    }

    /**
     * Create the signature.
     *
     * @param pSigner the signer
     * @return the generated signature
     * @throws OceanusException on error
     */
    private byte[] createSignature(final GordianKeyPair pSigner) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Build the signature */
            final GordianSignature mySigner = createSigner();
            mySigner.initForSigning(pSigner);
            final GordianStreamConsumer myConsumer = new GordianStreamConsumer(mySigner);
            final ASN1OutputStream myOut = ASN1OutputStream.create(myConsumer);
            myOut.writeObject(theTbsCertificate);
            myOut.close();

            /* Create the signature */
            return mySigner.sign();
        } catch (IOException e) {
            throw new GordianIOException("Failed to create signature", e);
        }
    }

    /**
     * Obtain the X509EncodedKeySpec.
     *
     * @return the keySpec
     * @throws OceanusException on error
     */
    X509EncodedKeySpec getX509KeySpec() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Obtain the X509 keySpec */
            final SubjectPublicKeyInfo myInfo = theTbsCertificate.getSubjectPublicKeyInfo();
            return new X509EncodedKeySpec(myInfo.getEncoded());
        } catch (IOException e) {
            throw new GordianIOException("Failed to extract keySpec", e);
        }
    }

    /**
     * Validate the signature.
     *
     * @param pSigner the signer
     * @return true/false is the signature valid?
     * @throws OceanusException on error
     */
    private boolean validateSignature(final GordianKeyPair pSigner) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Build the signature */
            final GordianSignature myValidator = createSigner();
            myValidator.initForVerify(pSigner);
            final GordianStreamConsumer myConsumer = new GordianStreamConsumer(myValidator);
            final ASN1OutputStream myOut = ASN1OutputStream.create(myConsumer);
            myOut.writeObject(theTbsCertificate);
            myOut.close();

            /* Verify the signature */
            return myValidator.verify(theSignature);
        } catch (IOException e) {
            throw new GordianIOException("Failed to validate signature", e);
        }
    }

    /**
     * Create the DERSequence for a certificate.
     *
     * @return the DERSequence
     * @throws OceanusException on error
     */
    private byte[] encodeCertificate() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the DERSequence */
            final ASN1EncodableVector myVector = new ASN1EncodableVector();
            myVector.add(theTbsCertificate);
            myVector.add(theSigAlgId);
            myVector.add(new DERBitString(theSignature));
            final DERSequence mySeq = new DERSequence(myVector);
            return mySeq.getEncoded();
        } catch (IOException e) {
            throw new GordianIOException("Failed to generate encoding", e);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial case */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Ensure object is correct class */
        if (!(pThat instanceof GordianCoreCertificate)) {
            return false;
        }
        final GordianCoreCertificate myThat = (GordianCoreCertificate) pThat;

        /* Compare fields */
        return Arrays.equals(theEncoded, myThat.theEncoded);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(theEncoded);
    }

    /**
     * CA Status.
     */
    public static class GordianCAStatus {
        /**
         * Is this a CA.
         */
        private final boolean isCA;

        /**
         * The Path Length.
         */
        private final BigInteger thePathLen;

        /**
         * Constructor.
         * @param pCA is this a CA?
         */
        GordianCAStatus(final boolean pCA) {
            isCA = pCA;
            thePathLen = null;
        }

        /**
         * Constructor.
         *
         * @param pPathLen the path length.
         */
        GordianCAStatus(final BigInteger pPathLen) {
            isCA = true;
            thePathLen = pPathLen;
        }

        /**
         * Constructor.
         *
         * @param pUsage the keyPair usage
         * @param pSignerStatus the signerStatus.
         */
        GordianCAStatus(final GordianKeyPairUsage pUsage,
                        final GordianCAStatus pSignerStatus) {
            isCA = pUsage.getUsageSet().contains(GordianKeyPairUse.CERTIFICATE);
            if (isCA) {
                 final BigInteger mySignerPath = pSignerStatus.getPathLen();
                 thePathLen = mySignerPath == null
                             ? BigInteger.ZERO
                             : mySignerPath.add(BigInteger.ONE);
            } else {
                thePathLen = null;
            }
        }

        /**
         * is this a CA?.
         *
         * @return true/false
         */
        boolean isCA() {
            return isCA;
        }

        /**
         * Obtain the pathLen.
         *
         * @return the pathLen
         */
        BigInteger getPathLen() {
            return thePathLen;
        }

        /**
         * Create extensions.
         * @param pGenerator the extensions generator
         * @throws OceanusException on error
         */
        void createExtensions(final ExtensionsGenerator pGenerator) throws OceanusException {
            /* Protect against exceptions */
            try {
                pGenerator.addExtension(Extension.basicConstraints, isCA, thePathLen == null
                                                                               ? new BasicConstraints(isCA)
                                                                               : new BasicConstraints(thePathLen.intValue()));
            } catch (IOException e) {
                throw new GordianIOException("Failed to create extensions", e);
            }
        }

        /**
         * Determine CAStatus.
         *
         * @param pExtensions the extensions.
         * @return the CAStatus
         */
        static GordianCAStatus determineStatus(final Extensions pExtensions) {
            /* Access details */
            final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);

            /* Check for CA */
            if (myConstraint != null && myConstraint.isCA()) {
                return new GordianCAStatus(myConstraint.getPathLenConstraint());
            }

            /* Not CA */
            return new GordianCAStatus(false);
        }
    }
}
