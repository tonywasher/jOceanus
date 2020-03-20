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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * Mappings from EncodedId to KeySpec.
 */
public class GordianKeyAlgId {
    /**
     * CipherOID branch.
     */
    private static final ASN1ObjectIdentifier KEYOID = GordianASN1Util.SYMOID.branch("2");

    /**
     * SymKeysOID branch.
     */
    private static final ASN1ObjectIdentifier SYMKEYOID = KEYOID.branch("1");

    /**
     * StreamKeysOID branch.
     */
    private static final ASN1ObjectIdentifier STREAMKEYOID = KEYOID.branch("2");

    /**
     * Map of SymKeySpec to Identifier.
     */
    private final Map<GordianSymKeySpec, AlgorithmIdentifier> theSymSpecMap;

    /**
     * Map of Identifier to SymKeySpec.
     */
    private final Map<AlgorithmIdentifier, GordianSymKeySpec> theSymIdentifierMap;

    /**
     * Map of StreamKeySpec to Identifier.
     */
    private final Map<GordianStreamKeySpec, AlgorithmIdentifier> theStreamSpecMap;

    /**
     * Map of Identifier to StreamKeySpec.
     */
    private final Map<AlgorithmIdentifier, GordianStreamKeySpec> theStreamIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianKeyAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSymSpecMap = new HashMap<>();
        theSymIdentifierMap = new HashMap<>();
        theStreamSpecMap = new HashMap<>();
        theStreamIdentifierMap = new HashMap<>();

        /* Access the cipherFactory  */
        final GordianCipherFactory myFactory = pFactory.getCipherFactory();

        /* For each KeyLength */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Loop through the possible SymKeySpecs */
            for (GordianSymKeySpec mySpec : myFactory.listAllSupportedSymKeySpecs(myKeyLen)) {
                /* Add any non-standard symKeys */
                ensureSymKey(mySpec);
            }

            /* Loop through the possible StreamKeySpecs */
            for (GordianStreamKeySpec mySpec : myFactory.listAllSupportedStreamKeySpecs(myKeyLen)) {
                /* Add any non-standard streamKeys */
                ensureStreamKey(mySpec);
            }
        }
    }

    /**
     * Obtain Identifier for symKeySpec.
     *
     * @param pSpec the keySpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianSymKeySpec pSpec) {
        return theSymSpecMap.get(pSpec);
    }

    /**
     * Obtain Identifier for streamKeySpec.
     *
     * @param pSpec the keySpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianStreamKeySpec pSpec) {
        return theStreamSpecMap.get(pSpec);
    }

    /**
     * Obtain symKeySpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the keySpec (or null if not found)
     */
    GordianSymKeySpec getSymKeySpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theSymIdentifierMap.get(pIdentifier);
    }

    /**
     * Obtain streamKeySpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the keySpec (or null if not found)
     */
    GordianStreamKeySpec getStreamKeySpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theStreamIdentifierMap.get(pIdentifier);
    }

    /**
     * Add symKeySpec to map if supported and not already present.
     *
     * @param pSpec the symKeySpec
     */
    private void ensureSymKey(final GordianSymKeySpec pSpec) {
        /* If the key is not already known */
        if (!theSymSpecMap.containsKey(pSpec)) {
            addSymKey(pSpec);
        }
    }

    /**
     * Create Identifier for a symKeySpec.
     *
     * @param pSpec the keySpec
     */
    private void addSymKey(final GordianSymKeySpec pSpec) {
        /* Build SymKey id */
        final ASN1ObjectIdentifier myId = appendSymKeyOID(SYMKEYOID, true, pSpec);

        /* Add the spec to the maps */
        addToSymMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Append Identifier for a symKeySpec.
     * @param pBaseOID the base OID
     * @param pAddLength add length to id true/false
     * @param pSpec the keySpec
     * @return the resulting OID
     */
    public static ASN1ObjectIdentifier appendSymKeyOID(final ASN1ObjectIdentifier pBaseOID,
                                                       final boolean pAddLength,
                                                       final GordianSymKeySpec pSpec) {
        /* Create a branch based on the KeyType */
        final GordianSymKeyType myType = pSpec.getSymKeyType();
        ASN1ObjectIdentifier myId = pBaseOID.branch(Integer.toString(myType.ordinal() + 1));

        /* Add blockLength */
        final GordianLength myBlockLength = pSpec.getBlockLength();
        myId = myId.branch(Integer.toString(myBlockLength.ordinal() + 1));

        /* Add length if required */
        if (pAddLength) {
            final GordianLength myKeyLength = pSpec.getKeyLength();
            myId = myId.branch(Integer.toString(myKeyLength.ordinal() + 1));
        }

        /* Return the id */
        return myId;
    }

    /**
     * Add symKeySpec to maps.
     * @param pSpec the keySpec
     * @param pIdentifier the identifier
     */
    private void addToSymMaps(final GordianSymKeySpec pSpec,
                              final AlgorithmIdentifier pIdentifier) {
        theSymSpecMap.put(pSpec, pIdentifier);
        theSymIdentifierMap.put(pIdentifier, pSpec);
    }

    /**
     * Add streamKeySpec to map if supported and not already present.
     *
     * @param pSpec the streamKeySpec
     */
    private void ensureStreamKey(final GordianStreamKeySpec pSpec) {
        /* If the key is not already known */
        if (!theStreamSpecMap.containsKey(pSpec)) {
            addStreamKey(pSpec);
        }
    }

    /**
     * Create Identifier for a streamKeySpec.
     *
     * @param pSpec the keySpec
     */
    private void addStreamKey(final GordianStreamKeySpec pSpec) {
        /* Build StreamKey id */
        final ASN1ObjectIdentifier myId = appendStreamKeyOID(STREAMKEYOID, pSpec);

        /* Add the spec to the maps */
        addToStreamMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Append Identifier for a streamKeySpec.
     * @param pBaseOID the base OID
     * @param pSpec the keySpec
     * @return the resulting OID
     */
    public static ASN1ObjectIdentifier appendStreamKeyOID(final ASN1ObjectIdentifier pBaseOID,
                                                          final GordianStreamKeySpec pSpec) {
        /* Create a branch based on the KeyType */
        final GordianStreamKeyType myType = pSpec.getStreamKeyType();
        ASN1ObjectIdentifier myId = pBaseOID.branch(Integer.toString(myType.ordinal() + 1));

        /* Add keyLength */
        final GordianLength myKeyLength = pSpec.getKeyLength();
        myId = myId.branch(Integer.toString(myKeyLength.ordinal() + 1));

        /* Add subKeyType if required */
        if (pSpec.getSubKeyType() != null) {
            myId = myId.branch(Integer.toString(((Enum<?>) pSpec.getSubKeyType()).ordinal() + 1));
        }

        /* Return the id */
        return myId;
    }

    /**
     * Add streamKeySpec to maps.
     * @param pSpec the keySpec
     * @param pIdentifier the identifier
     */
    private void addToStreamMaps(final GordianStreamKeySpec pSpec,
                                 final AlgorithmIdentifier pIdentifier) {
        theStreamSpecMap.put(pSpec, pIdentifier);
        theStreamIdentifierMap.put(pIdentifier, pSpec);
    }
}
