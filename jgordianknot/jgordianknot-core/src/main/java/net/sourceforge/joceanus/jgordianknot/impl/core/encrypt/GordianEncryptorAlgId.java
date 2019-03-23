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
package net.sourceforge.joceanus.jgordianknot.impl.core.encrypt;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianMcElieceKeySpec.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianDigestAlgId;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Mappings from EncodedId to EncryptorSpec.
 */
public class GordianEncryptorAlgId {
    /**
     * Base our encryptors off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier ENCRYPTOID = GordianCoreFactory.BASEOID.branch("22");

    /**
     * Map of EncryptorSpec to Identifier.
     */
    private final Map<GordianEncryptorSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to EncryptorSpec.
     */
    private final Map<AlgorithmIdentifier, GordianEncryptorSpec> theIdentifierMap;

    /**
     * The factory.
     */
    private final GordianEncryptorFactory theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianEncryptorAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the encryptorFactory  */
        theFactory = pFactory.getAsymmetricFactory().getEncryptorFactory();

        /* Populate with the public standards */
        //addSHADigests();
        //addBlakeDigests();
        //addGOSTDigests();
        //addSundryDigests();

        /* Loop through the possible AsymKeys */
        for (GordianAsymKeyType myKeyType : GordianAsymKeyType.values()) {
            /* Add any non-standard encryptorSpecs */
            addEncryptors(myKeyType);
        }
    }

    /**
     * Obtain Identifier for EncryptorSpec.
     *
     * @param pSpec the encryptorSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianEncryptorSpec pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain EncryptorSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the encryptorSpec
     * @throws OceanusException on error
     */
    public GordianEncryptorSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) throws OceanusException {
        final GordianEncryptorSpec mySpec = theIdentifierMap.get(pIdentifier);
        if (mySpec == null) {
            throw new GordianDataException("Invalid identifier " + pIdentifier);
        }
        return mySpec;
    }

    /**
     * Add SHA digests.
     */
    //private void addSHADigests() {
    //  addToMaps(GordianDigestSpec.sha1(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
    //}

    /**
     * Create Identifiers for all valid EncryptorTypes.
     * @param pKeyType the keyType
     */
    private void addEncryptors(final GordianAsymKeyType pKeyType) {
        for (GordianEncryptorSpec mySpec : theFactory.listAllSupportedEncryptors(pKeyType)) {
            ensureEncryptor(mySpec);
        }
    }

    /**
     * Add encryptorSpec to map if supported and not already present.
     * @param pSpec the encryptorSpec
     */
    private void ensureEncryptor(final GordianEncryptorSpec pSpec) {
        /* If the encryptor is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addEncryptor(pSpec);
        }
    }

    /**
     * Create Identifier for an encryptorSpec.
     *
     * @param pSpec the macSpec
     */
    private void addEncryptor(final GordianEncryptorSpec pSpec) {
        /* Create a branch for encryptor based on the keyType */
        final GordianAsymKeyType myKeyType = pSpec.getKeyType();
        ASN1ObjectIdentifier myId = ENCRYPTOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Obtain the encryptor */
        final Object myEncryptor = pSpec.getEncryptorType();
        if (myEncryptor instanceof GordianDigestSpec) {
            myId = GordianDigestAlgId.appendDigestOID(myId.branch("1"), (GordianDigestSpec) myEncryptor);
        } else if (myEncryptor instanceof GordianMcElieceEncryptionType) {
            myId = myId.branch("3").branch(Integer.toString(((GordianMcElieceEncryptionType) myEncryptor).ordinal() + 1));
        } else {
            myId = myId.branch("1");
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId));
    }

    /**
     * Add encryptor to maps.
     * @param pSpec the encryptorSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianEncryptorSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
