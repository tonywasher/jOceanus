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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROutputStream;
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

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
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
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
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
    private final GordianCoreKeyPair theKeyPair;

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
                           final GordianCoreKeyPair pKeyPair,
                           final X500Name pSubject) throws OceanusException {
        /* Check that the keyPair is OK */
        if (pKeyPair.isPublicOnly()) {
            throw new GordianLogicException("Invalid keyPair");
        }

        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = pKeyPair.getPublicOnly();

        /* Determine the signatureSpec */
        theSigSpec = GordianSignatureSpec.defaultForKey(theKeyPair.getKeySpec());

        /* Determine the algorithm Id for the signatureSpec */
        final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) theFactory.getAsymmetricFactory().getSignatureFactory();
        theSigAlgId = mySignFactory.getIdentifierForSpecAndKeyPair(theSigSpec, theKeyPair);

        /* Create the TBSCertificate */
        theKeyUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        theCAStatus = new GordianCAStatus(true);
        theTbsCertificate = buildCertificate(null, pSubject);

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
                           final GordianCoreKeyPair pKeyPair,
                           final X500Name pSubject,
                           final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = pKeyPair.getPublicOnly();
        theKeyUsage = pUsage;

        /* Check that the signer is allowed to sign certificates */
        final GordianKeyPair mySignerPair = pSigner.getKeyPair();
        final GordianCoreCertificate mySignerCert = (GordianCoreCertificate) pSigner.getCertificateChain()[0];
        if (!mySignerCert.getUsage().hasUse(GordianKeyPairUse.CERTIFICATE)
                || !mySignerCert.isValidNow()
                || mySignerPair.isPublicOnly()) {
            throw new GordianLogicException("Invalid signer");
        }

        /* Determine CA Status */
        theCAStatus = new GordianCAStatus(theKeyUsage, mySignerCert.theCAStatus);

        /* Determine the signatureSpec */
        theSigSpec = GordianSignatureSpec.defaultForKey(pSigner.getKeyPair().getKeySpec());

        /* Determine the algorithm Id for the signatureSpec */
        final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) theFactory.getAsymmetricFactory().getSignatureFactory();
        theSigAlgId = mySignFactory.getIdentifierForSpecAndKeyPair(theSigSpec, pSigner.getKeyPair());

        /* Create the TBSCertificate */
        theTbsCertificate = buildCertificate(mySignerCert, pSubject);

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
    public GordianCoreCertificate(final GordianCoreFactory pFactory,
                                  final byte[] pSequence) throws OceanusException {
        /* Store the parameters */
        theFactory = pFactory;

        /* Parse the certificate */
        final Certificate myCert = Certificate.getInstance(pSequence);

        /* Extract the details */
        theTbsCertificate = myCert.getTBSCertificate();
        theSigAlgId = myCert.getSignatureAlgorithm();
        theSignature = myCert.getSignature().getBytes();

        /* Determine the signatureSpec for the algorithmId */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) theFactory.getAsymmetricFactory().getSignatureFactory();
        theSigSpec = mySignFactory.getSpecForIdentifier(theSigAlgId);
        if (theSigSpec == null) {
            throw new GordianDataException("Unsupported Signature AlgorithmId: " + theSigAlgId);
        }

        /* Derive the keyPair */
        final X509EncodedKeySpec myX509 = getX509KeySpec();
        final GordianAsymKeySpec myKeySpec = myAsym.determineKeySpec(myX509);
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(myKeySpec);
        theKeyPair = (GordianCoreKeyPair) myGenerator.derivePublicOnlyKeyPair(myX509);

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

        /* Store the encoded representation */
        theEncoded = pSequence;
    }


    /**
     * Determine usage.
     *
     * @param pExtensions the extensions.
     * @return the usage
     */
    private static GordianKeyPairUsage determineUsage(final Extensions pExtensions) {
        /* Access details */
        final KeyUsage myUsage = KeyUsage.fromExtensions(pExtensions);
        final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);
        final GordianKeyPairUsage myResult = new GordianKeyPairUsage();

        /* Check for CERTIFICATE */
        if (myConstraint.isCA() && myUsage.hasUsages(KeyUsage.keyCertSign)) {
            myResult.addUse(GordianKeyPairUse.CERTIFICATE);
        }

        /* Check for signer. */
        if (myUsage.hasUsages(KeyUsage.digitalSignature)) {
            myResult.addUse(GordianKeyPairUse.SIGNATURE);
        }

        /* Check for nonRepudiation. */
        if (myUsage.hasUsages(KeyUsage.nonRepudiation)) {
            myResult.addUse(GordianKeyPairUse.NONREPUDIATION);
        }

        /* Check for keyAgreement. */
        if (myUsage.hasUsages(KeyUsage.keyAgreement)) {
            myResult.addUse(GordianKeyPairUse.AGREEMENT);
        }

        /* Check for keyEncryption. */
        if (myUsage.hasUsages(KeyUsage.keyEncipherment)) {
            myResult.addUse(GordianKeyPairUse.KEYENCRYPT);
        }

        /* Check for dataEncryption. */
        if (myUsage.hasUsages(KeyUsage.dataEncipherment)) {
            myResult.addUse(GordianKeyPairUse.DATAENCRYPT);
        }

        /* Check for encipherOnly. */
        if (myUsage.hasUsages(KeyUsage.encipherOnly)) {
            myResult.addUse(GordianKeyPairUse.ENCRYPTONLY);
        }

        /* Check for decipherOnly. */
        if (myUsage.hasUsages(KeyUsage.decipherOnly)) {
            myResult.addUse(GordianKeyPairUse.DECRYPTONLY);
        }

        /* Return the result */
        return myResult;
    }

    @Override
    public boolean isValidOnDate(final Date pDate) {
        /* Access the date */
        return pDate.compareTo(theTbsCertificate.getStartDate().getDate()) >= 0
                && pDate.compareTo(theTbsCertificate.getEndDate().getDate()) <= 0;
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
    DERBitString getSubjectId() {
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
    DERBitString getIssuerId() {
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
        final DERBitString mySignerId = pSigner.getSubjectId();
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
     * Validate that the keyPair public Key matches.
     * @param pPair the key pair
     * @return matches true/false
     */
    boolean checkMatchingPublicKey(final GordianKeyPair pPair) {
        final GordianCoreKeyPair myPair = (GordianCoreKeyPair) pPair;
        return myPair.getPublicKey().equals(theKeyPair.getPublicKey());
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

        /* Access the keyPair generator */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(theKeyPair.getKeySpec());

        /* Obtain the publicKey Info */
        final byte[] myPublicKeyEncoded = myGenerator.getX509Encoding(theKeyPair).getEncoded();
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
            myCertBuilder.setIssuerUniqueID(pSigner.getSubjectId());
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
        final GordianDigestSpec mySpec = theSigSpec.getDigestSpec();
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
            final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) theFactory.getAsymmetricFactory().getSignatureFactory();
            final GordianSignature mySigner = mySigns.createSigner(theSigSpec);
            mySigner.initForSigning(pSigner);
            final GordianStreamConsumer myConsumer = new GordianStreamConsumer(mySigner);
            final DEROutputStream myOut = new DEROutputStream(myConsumer);
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
            final GordianCoreSignatureFactory mySigns = (GordianCoreSignatureFactory) theFactory.getAsymmetricFactory().getSignatureFactory();
            final GordianSignature myValidator = mySigns.createSigner(theSigSpec);
            myValidator.initForVerify(pSigner);
            final GordianStreamConsumer myConsumer = new GordianStreamConsumer(myValidator);
            final DEROutputStream myOut = new DEROutputStream(myConsumer);
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
            if (myConstraint.isCA()) {
                return new GordianCAStatus(myConstraint.getPathLenConstraint());
            }

            /* Not CA */
            return new GordianCAStatus(false);
        }
    }
}
