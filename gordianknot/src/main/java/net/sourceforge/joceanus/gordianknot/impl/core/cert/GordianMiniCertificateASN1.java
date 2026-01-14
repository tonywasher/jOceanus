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

package net.sourceforge.joceanus.gordianknot.impl.core.cert;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of MiniCertificate.
 * <pre>
 * GordianMiniCertificateASN1 ::= SEQUENCE {
 *      subject                 Name,
 *      publicKeyInfo           SubjectPublicKeyInfo,
 *      usage                   Extensions
 * }
 * </pre>
 */
public class GordianMiniCertificateASN1
        extends GordianASN1Object {
    /**
     * The Subject.
     */
    private final X500Name theSubject;

    /**
     * The SubjectPublicKey.
     */
    private final X509EncodedKeySpec thePublicKey;

    /**
     * The extensions.
     */
    private final Extensions theExtensions;

    /**
     * Create the ASN1 sequence.
     *
     * @param pSubject   the subject
     * @param pPublicKey the publicKey
     * @param pUsage     the keyPairUsage
     * @throws GordianException on error
     */
    public GordianMiniCertificateASN1(final X500Name pSubject,
                                      final X509EncodedKeySpec pPublicKey,
                                      final GordianKeyPairUsage pUsage) throws GordianException {
        theSubject = pSubject;
        thePublicKey = pPublicKey;
        theExtensions = GordianCertUtils.createExtensions(pUsage);
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianMiniCertificateASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theSubject = X500Name.getInstance(en.nextElement());
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(en.nextElement());
            final byte[] myBytes = myInfo.getEncoded(ASN1Encoding.DER);
            thePublicKey = new X509EncodedKeySpec(myBytes);
            theExtensions = Extensions.getInstance(en.nextElement());

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* handle exceptions */
        } catch (IllegalArgumentException
                 | IOException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object
     *
     * @param pObject  the tagged object
     * @param explicit is the tagging explicit
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianMiniCertificateASN1 getInstance(final ASN1TaggedObject pObject,
                                                         final boolean explicit) throws GordianException {
        return getInstance(ASN1Sequence.getInstance(pObject, explicit));
    }

    /**
     * Parse the ASN1 object.
     *
     * @param pObject the object to parse
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianMiniCertificateASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianMiniCertificateASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianMiniCertificateASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the subject.
     *
     * @return the subject
     */
    public X500Name getSubject() {
        return theSubject;
    }

    /**
     * Obtain the publicKey.
     *
     * @return the publicKey
     */
    public X509EncodedKeySpec getPublicKey() {
        return thePublicKey;
    }

    /**
     * Obtain the keyPairUsage.
     *
     * @return the usage
     */
    public GordianKeyPairUsage getUsage() {
        return GordianCertUtils.determineUsage(theExtensions);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theSubject);
        v.add(SubjectPublicKeyInfo.getInstance(thePublicKey.getEncoded()));
        v.add(theExtensions);

        return new DERSequence(v);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check that the fields are equal */
        return pThat instanceof GordianMiniCertificateASN1 myThat
                && Objects.equals(theSubject, myThat.getSubject())
                && Objects.equals(thePublicKey, myThat.getPublicKey())
                && Objects.equals(theExtensions, myThat.theExtensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSubject, thePublicKey, theExtensions);
    }
}
