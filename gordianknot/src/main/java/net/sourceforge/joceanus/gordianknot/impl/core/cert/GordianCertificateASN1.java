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
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Certificate;

import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of KeySetSpec.
 * <pre>
 * GordianCertificateASN1 ::= SEQUENCE {
 *      fullCertificate [1] Certificate OPTIONAL
 *      miniCertificate [2] GordianMiniCertificateASN1 OPTIONAL
 * }
 * </pre>
 */
public class GordianCertificateASN1
        extends GordianASN1Object {
    /**
     * The fullCertificate tag.
     */
    private static final int TAG_FULL = 1;

    /**
     * The miniCertificate tag.
     */
    private static final int TAG_MINI = 2;

    /**
     * The full Certificate.
     */
    private final Certificate theFull;

    /**
     * The MiniCertificate.
     */
    private final GordianMiniCertificateASN1 theMini;

    /**
     * Create the ASN1 sequence.
     *
     * @param pCertificate the certificate
     * @throws GordianException on error
     */
    public GordianCertificateASN1(final GordianCertificate pCertificate) throws GordianException {
        /* Handle core Certificate */
        if (pCertificate instanceof GordianCoreCertificate myCore) {
            theFull = myCore.getCertificate();
            theMini = null;

            /* Handle miniCertificate */
        } else if (pCertificate instanceof GordianMiniCertificate myMini) {
            theFull = null;
            theMini = myMini.getASN1();

        } else {
            throw new GordianLogicException("Unexpected certificate type");
        }
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertificateASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
            theFull = myTagged.getTagNo() == TAG_FULL ? Certificate.getInstance(myTagged, false) : null;
            theMini = myTagged.getTagNo() == TAG_MINI ? GordianMiniCertificateASN1.getInstance(myTagged, false) : null;

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     *
     * @param pObject  the tagged object
     * @param explicit is the tagging explicit
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCertificateASN1 getInstance(final ASN1TaggedObject pObject,
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
    public static GordianCertificateASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertificateASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertificateASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the subject.
     *
     * @param pFactory the factory
     * @return the certificate
     */
    public GordianCertificate getCertificate(final GordianFactory pFactory) throws GordianException {
        return theFull != null
                ? new GordianCoreCertificate((GordianBaseFactory) pFactory, theFull)
                : new GordianMiniCertificate(pFactory, theMini);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        if (theFull != null) {
            v.add(new DERTaggedObject(false, TAG_FULL, theFull));
        }
        if (theMini != null) {
            v.add(new DERTaggedObject(false, TAG_MINI, theMini));
        }

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
        return pThat instanceof GordianCertificateASN1 myThat
                && Objects.equals(theFull, myThat.theFull)
                && Objects.equals(theMini, myThat.theMini);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theFull, theMini);
    }
}
