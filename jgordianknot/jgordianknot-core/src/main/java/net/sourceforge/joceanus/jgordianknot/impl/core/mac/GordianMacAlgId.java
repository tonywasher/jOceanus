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
package net.sourceforge.joceanus.jgordianknot.impl.core.mac;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCipherAlgId;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianDigestAlgId;

/**
 * Mappings from EncodedId to MacSpec.
 */
public class GordianMacAlgId {
    /**
     * Base our macs off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier MACOID = GordianCoreFactory.BASEOID.branch("12");

    /**
     * Map of MacSpec to Identifier.
     */
    private final Map<GordianMacSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to MacSpec.
     */
    private final Map<AlgorithmIdentifier, GordianMacSpec> theIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianMacAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the macFactory  */
        final GordianMacFactory myFactory = pFactory.getMacFactory();

        /* Populate with the public standards */
        addWellKnownMacs();

        /* For each KeyLength */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Loop through the possible MacSpecs */
            for (GordianMacSpec mySpec : myFactory.listAllSupportedSpecs(myKeyLen)) {
                /* Add any non-standard macs */
                ensureMac(mySpec);
            }
        }
    }

    /**
     * Obtain Identifier for MacSpec.
     *
     * @param pSpec the macSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianMacSpec pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain MacSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the macSpec (or null if not found)
     */
    public GordianMacSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add well-known macs.
     */
    private void addWellKnownMacs() {
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.gost()), new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3411Hmac));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.streebog(GordianLength.LEN_256)),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_256));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.streebog(GordianLength.LEN_512)),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_512));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha1()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_224)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_256)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_384)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_512)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_224)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_256)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_384)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_512)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512));
        addToMaps(GordianMacSpec.cMac(GordianSymKeySpec.aria(GordianLength.LEN_128)), new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cmac));
        addToMaps(GordianMacSpec.cMac(GordianSymKeySpec.aria(GordianLength.LEN_192)), new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cmac));
        addToMaps(GordianMacSpec.cMac(GordianSymKeySpec.aria(GordianLength.LEN_256)), new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cmac));
        addToMaps(GordianMacSpec.kupynaMac(GordianLength.LEN_256, GordianLength.LEN_256), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564mac_256));
        addToMaps(GordianMacSpec.kupynaMac(GordianLength.LEN_256, GordianLength.LEN_384), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564mac_384));
        addToMaps(GordianMacSpec.kupynaMac(GordianLength.LEN_256, GordianLength.LEN_512), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564mac_512));
    }

    /**
     * Add macSpec to map if supported and not already present.
     *
     * @param pSpec the macSpec
     */
    private void ensureMac(final GordianMacSpec pSpec) {
        /* If the mac is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addMac(pSpec);
        }
    }

    /**
     * Create Identifier for a macSpec.
     *
     * @param pSpec the macSpec
     */
    private void addMac(final GordianMacSpec pSpec) {
        /* Create a branch for mac based on the MacType */
        final GordianMacType myType = pSpec.getMacType();
        ASN1ObjectIdentifier myId = MACOID.branch(Integer.toString(myType.ordinal() + 1));
        myId = myId.branch(Integer.toString(pSpec.getKeyLength().ordinal() + 1));

        /* Obtain the subSpec */
        final Object mySubSpec = pSpec.getSubSpec();
        if (mySubSpec instanceof GordianDigestSpec) {
            myId = GordianDigestAlgId.appendDigestOID(myId, (GordianDigestSpec) mySubSpec);
        } else if (mySubSpec instanceof GordianSymKeySpec) {
            myId = GordianCipherAlgId.appendSymKeyOID(myId, false, (GordianSymKeySpec) mySubSpec);
        } else if (mySubSpec instanceof GordianLength) {
            myId = myId.branch(Integer.toString(((GordianLength) mySubSpec).ordinal() + 1));
        } else if (mySubSpec instanceof Boolean) {
            myId = myId.branch(((Boolean) mySubSpec) ? "1" : "2");
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId));
    }

    /**
     * Add mac to maps.
     * @param pSpec the macSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianMacSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
