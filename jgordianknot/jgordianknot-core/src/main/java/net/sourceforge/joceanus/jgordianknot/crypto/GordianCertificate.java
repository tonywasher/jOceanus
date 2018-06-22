/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

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

import net.sourceforge.joceanus.jgordianknot.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Certificate representation.
 */
public class GordianCertificate {
    /**
     * The Factory.
     */
    private final GordianFactory theFactory;

    /**
     * The KeyPair to which this Certificate belongs.
     */
    private final GordianKeyPair theKeyPair;

    /**
     * The Signing Certificate.
     */
    private final GordianCertificate theSigner;

    /**
     * The SignatureSpec.
     */
    private final GordianSignatureSpec theSigSpec;

    /**
     * The TBS Certificate.
     */
    private final TBSCertificate theTbsCertificate;

    /**
     * The Signature Algorithm.
     */
    private final AlgorithmIdentifier theSigAlgId;

    /**
     * The signature.
     */
    private final byte[] theSignature;

    /**
     * Create a new self-signed certificate.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @throws OceanusException on error
     */
    protected GordianCertificate(final GordianFactory pFactory,
                                 final GordianKeyPair pKeyPair,
                                 final String pSubject) throws OceanusException {
        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = pKeyPair;
        theSigner = null;

        /* Determine the signatureSpec */
        theSigSpec = GordianSignatureSpec.defaultForKey(theKeyPair.getKeySpec());

        /* Determine the algorithm Id for the signatureSpec */
        final GordianSignatureAlgId mySigIdMgr = theFactory.getSignatureIdManager();
        theSigAlgId = mySigIdMgr.getIdentifierForSpec(theSigSpec);

        /* Create the TBSCertificate */
        theTbsCertificate = buildCertificate(pSubject, GordianKeyPairUsage.CERTIFICATE);

        /* Create the signature */
        theSignature = createSignature(pKeyPair);
    }

    /**
     * Create a new certificate, signed by the relevant authority.
     * @param pFactory the factory
     * @param pSigner the signing certificate
     * @param pKeyPair the keyPair
     * @param pSubject the name of the entity
     * @param pUsage the key usage
     * @throws OceanusException on error
     */
    protected GordianCertificate(final GordianFactory pFactory,
                                 final GordianCertificate pSigner,
                                 final GordianKeyPair pKeyPair,
                                 final String pSubject,
                                 final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = pKeyPair;
        theSigner = pSigner;

        /* Determine the signatureSpec */
        theSigSpec = GordianSignatureSpec.defaultForKey(pSigner.getKeyPair().getKeySpec());

        /* Determine the algorithm Id for the signatureSpec */
        final GordianSignatureAlgId mySigIdMgr = theFactory.getSignatureIdManager();
        theSigAlgId = mySigIdMgr.getIdentifierForSpec(theSigSpec);

        /* Create the TBSCertificate */
        theTbsCertificate = buildCertificate(pSubject, pUsage);

        /* Create the signature */
        theSignature = createSignature(theSigner.getKeyPair());
    }

    /**
     * Parse a certificate.
     * @param pFactory the factory
     * @param pSequence the DER representation of the certificate
     * @throws OceanusException on error
     */
    protected GordianCertificate(final GordianFactory pFactory,
                                 final DERSequence pSequence) throws OceanusException {
        /* Store the parameters */
        theFactory = pFactory;
        theKeyPair = null;
        theSigner = null;

        /* Parse the certificate */
        final Certificate myCert = Certificate.getInstance(pSequence);

        /* Extract the details */
        theTbsCertificate = myCert.getTBSCertificate();
        theSigAlgId = myCert.getSignatureAlgorithm();
        theSignature = myCert.getSignature().getBytes();

        /* Determine the signatureSpec for the algorithmId */
        final GordianSignatureAlgId mySigIdMgr = theFactory.getSignatureIdManager();
        theSigSpec = mySigIdMgr.getSpecForIdentifier(theSigAlgId);
    }

    /**
     * Obtain the subject of the certificate.
     * @return the subject
     */
    public X500Name getSubject() {
        return theTbsCertificate.getSubject();
    }

    /**
     * Obtain the subjectId of the certificate.
     * @return the subjectId
     */
    public DERBitString getSubjectId() {
        return theTbsCertificate.getSubjectUniqueId();
    }

    /**
     * Obtain the issuer of the certificate.
     * @return the issuer name
     */
    public X500Name getIssuer() {
        return theTbsCertificate.getIssuer();
    }

    /**
     * Obtain the issuerId of the certificate.
     * @return the issuerId
     */
    public DERBitString getIssuerId() {
        return theTbsCertificate.getIssuerUniqueId();
    }

    /**
     * Obtain the keyPair of the certificate.
     * @return the keyPair
     */
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    /**
     * Create a certificate.
     * @param pSubject the name of the certificate
     * @param pUsage the KeyPair Usage
     * @return the theCertificate
     * @throws OceanusException on error
     */
    private TBSCertificate buildCertificate(final String pSubject,
                                            final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Create the name of the certificate */
        final X500Name mySubject = new X500Name(pSubject);
        final X500Name myIssuer = theSigner == null
                                  ? mySubject
                                  : theSigner.getSubject();

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
        final GordianKeyPairGenerator myGenerator = theFactory.getKeyPairGenerator(theKeyPair.getKeySpec());

        /* Obtain the publicKey Info */
        final byte[] myPublicKeyEncoded = myGenerator.getX509Encoding(theKeyPair).getEncoded();
        final SubjectPublicKeyInfo myPublicKeyInfo = SubjectPublicKeyInfo.getInstance(myPublicKeyEncoded);

        /* Build basic information */
        final V3TBSCertificateGenerator myCertBuilder = new V3TBSCertificateGenerator();
        myCertBuilder.setSubject(mySubject);
        myCertBuilder.setIssuer(myIssuer);
        myCertBuilder.setStartDate(new Time(myStart));
        myCertBuilder.setEndDate(new Time(myEnd));
        myCertBuilder.setSerialNumber(new ASN1Integer(mySerialNo));
        myCertBuilder.setSubjectPublicKeyInfo(myPublicKeyInfo);
        myCertBuilder.setSignature(theSigAlgId);
        myCertBuilder.setSubjectUniqueID(createSubjectId(myPublicKeyEncoded, mySerialNo));
        if (theSigner != null) {
            myCertBuilder.setIssuerUniqueID(theSigner.getSubjectId());
        }

        /* Create extensions for the certificate */
        myCertBuilder.setExtensions(createExtensions(pUsage));

        /* Generate the TBS Certificate */
        return myCertBuilder.generateTBSCertificate();
    }

    /**
     * Create extensions for tbsCertificate.
     * @param pUsage the KeyPair Usage
     * @return the extensions
     */
    private Extensions createExtensions(final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create extensions for the certificate */
            final ExtensionsGenerator myExtGenerator = new ExtensionsGenerator();
            myExtGenerator.addExtension(Extension.keyUsage, true, new KeyUsage(pUsage.getUsage()));
            myExtGenerator.addExtension(Extension.basicConstraints, false, new BasicConstraints(pUsage.isCA()));
            return myExtGenerator.generate();
        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }

    /**
     * Create the subjectId
     * @param pEncodedPublicKey the publicKey
     * @param pSerialNo the certificate Serial#
     * @return the subjectId
     * @throws OceanusException on error
     */
    private DERBitString createSubjectId(final byte[] pEncodedPublicKey,
                                         final BigInteger pSerialNo) throws OceanusException {
        /* Build the hash */
        final GordianDigestSpec mySpec = theSigSpec.getDigestSpec();
        final GordianDigest myDigest = theFactory.createDigest(mySpec);
        myDigest.update(pEncodedPublicKey);
        myDigest.update(pSerialNo.toByteArray());

        /* Create the subjectId */
        return new DERBitString(myDigest.finish());
     }

    /**
     * Create the signature
     * @param pSigner the signer
     * @return the generated signature
     * @throws OceanusException on error
     */
    private byte[] createSignature(final GordianKeyPair pSigner) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Build the signature */
            final GordianSigner mySigner = theFactory.createSigner(pSigner, theSigSpec);
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
     * Validate the signature
     * @param pSigner the signer
     * @return the generated signature
     * @throws OceanusException on error
     */
    private boolean validateSignature(final GordianKeyPair pSigner) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Build the signature */
            final GordianValidator myValidator = theFactory.createValidator(pSigner, theSigSpec);
            final GordianStreamConsumer myConsumer = new GordianStreamConsumer(myValidator);
            final DEROutputStream myOut = new DEROutputStream(myConsumer);
            myOut.writeObject(theTbsCertificate);
            myOut.close();

            /* Create the signature */
            return myValidator.verify(theSignature);
        } catch (IOException e) {
            throw new GordianIOException("Failed to validate signature", e);
        }
    }

    /**
     * Create the DERSequence for a certificate.
     * @return the DERSequence
     */
    public DERSequence createDERSequence() {
        /* Create the DERSequence */
        final ASN1EncodableVector myVector = new ASN1EncodableVector();
        myVector.add(theTbsCertificate);
        myVector.add(theSigAlgId);
        myVector.add(new DERBitString(theSignature));
        return new DERSequence(myVector);
    }

    /**
     * KeyUsage.
     */
    public enum GordianKeyPairUsage {
        /**
         * Certificates.
         */
        CERTIFICATE(KeyUsage.keyCertSign),

        /**
         * Signatures.
         */
        SIGNATURE(KeyUsage.digitalSignature),

        /**
         * KeyExchange.
         */
        EXCHANGE(KeyUsage.keyAgreement),

        /**
         * Encryption.
         */
        ENCRYPT(KeyUsage.dataEncipherment);

        /**
         * The KeyUsage.
         */
        private final int theUsage;

        /**
         * Constructor.
         * @param pUsage the usage.
         */
        GordianKeyPairUsage(final int pUsage) {
            theUsage = pUsage;
        }

        /**
         * Obtain the usage.
         * @return the usage
         */
        int getUsage() {
            return theUsage;
        }

        /**
         * Is this a Certificate Authority Key?
         * @return true/false
         */
        public boolean isCA() {
            return this == CERTIFICATE;
        }
    }
}
