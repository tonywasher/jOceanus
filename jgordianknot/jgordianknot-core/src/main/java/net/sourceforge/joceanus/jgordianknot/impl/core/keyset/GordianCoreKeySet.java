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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * A full set of symmetric keys, subject to the relevant predicate.
 */
public final class GordianCoreKeySet
    implements GordianKeySet {
    /**
     * Initialisation Vector size.
     */
    private static final int BLOCKLEN = GordianLength.LEN_128.getByteLength();

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Map of KeySpec to symKey.
     */
    private final Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> theSymKeyMap;

    /**
     * The underlying Cipher.
     */
    private final GordianMultiCipher theCipher;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected GordianCoreKeySet(final GordianCoreFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create maps */
        theSymKeyMap = new HashMap<>();

        /* Create the cipher */
        theCipher = new GordianMultiCipher(this);
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    public GordianCoreFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the symKeySet.
     *
     * @return the keySet
     */
    Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> getSymKeyMap() {
        return theSymKeyMap;
    }

    /**
     * Encryption length.
     *
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(final int pDataLength) {
        final int iBlocks = 1 + ((pDataLength - 1) % BLOCKLEN);
        return iBlocks
                * BLOCKLEN;
    }

    /**
     * Encryption overhead.
     *
     * @return the encryption overhead
     */
    public static int getEncryptionOverhead() {
        return GordianKeySetRecipe.SALTLEN + GordianKeySetRecipe.RECIPELEN;
    }

    /**
     * Obtain keyWrapExpansion for this keySet.
     *
     * @return the keyWrap expansion
     */
    public int getKeyWrapExpansion() {
        return getKeyWrapExpansion(theFactory.getNumCipherSteps() - 1);
    }

    /**
     * Obtain keyWrapExpansion for # of steps.
     *
     * @param pNumSteps the number of wrap steps
     * @return the keyWrap expansion
     */
    public static int getKeyWrapExpansion(final int pNumSteps) {
        final int myExpansion = GordianWrapper.getKeyWrapExpansion(GordianLength.LEN_128) * pNumSteps;
        return myExpansion + getEncryptionOverhead();
    }

    @Override
    public byte[] encryptBytes(final byte[] pBytes) throws OceanusException {
        /* Generate set of keys and initialisation vector */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Encrypt the bytes */
        theCipher.initCiphers(myParams, true);
        final byte[] myBytes = theCipher.finish(pBytes);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public byte[] decryptBytes(final byte[] pBytes) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pBytes);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Decrypt the bytes and return them */
        theCipher.initCiphers(myParams, false);
        return theCipher.finish(myBytes);
    }

    @Override
    public byte[] secureKey(final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureKey(myParams, pKeyToSecure);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> deriveKey(final byte[] pSecuredKey,
                                                              final T pKeyType) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pSecuredKey);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.deriveKey(myParams, myBytes, pKeyType);
    }

    @Override
    public byte[] secureBytes(final byte[] pBytesToSecure) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* secure the key */
        final byte[] myBytes = theCipher.secureBytes(myParams, pBytesToSecure);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public byte[] deriveBytes(final byte[] pSecuredBytes) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pSecuredBytes);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the bytes and return them */
        return theCipher.deriveBytes(myParams, myBytes);
    }

    @Override
    public byte[] securePrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Generate set of keys */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory);
        final GordianKeySetParameters myParams = myRecipe.getParameters();

        /* Wrap the key */
        final byte[] myBytes = theCipher.securePrivateKey(myParams, pKeyPair);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(myBytes);
    }

    @Override
    public GordianKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Access the PKCS8Encoding */
        final PKCS8EncodedKeySpec myPrivate = derivePrivateKeySpec(pSecuredPrivateKey);

        /* Determine and check the keySpec */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianAsymKeySpec myKeySpec = myAsym.determineKeySpec(pPublicKeySpec);
        if (!myKeySpec.equals(myAsym.determineKeySpec(myPrivate))) {
            throw new GordianLogicException("Mismatch on keySpecs");
        }

        /* Derive the keyPair */
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(myKeySpec);
        return myGenerator.deriveKeyPair(pPublicKeySpec, myPrivate);
    }

    /**
     * derive privateKeySpec.
     * @param pSecuredPrivateKey the secured private keySpec
     * @return the private keySpec
     * @throws OceanusException on error
     */
    private PKCS8EncodedKeySpec derivePrivateKeySpec(final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Parse the bytes into the separate parts */
        final GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pSecuredPrivateKey);
        final GordianKeySetParameters myParams = myRecipe.getParameters();
        final byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.derivePrivateKeySpec(myParams, myBytes);
    }

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void declareSymKey(final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        declareKey(pKey, myFactory.supportedKeySetSymKeySpecs(), theSymKeyMap);
    }

    /**
     * Declare Key.
     * @param <T> the keyType
     * @param pKey the key
     * @param pPredicate the predicate
     * @param pKeyMap the keyMap
     * @throws OceanusException on error
     */
    private static <T extends GordianKeySpec> void declareKey(final GordianKey<T> pKey,
                                                              final Predicate<T> pPredicate,
                                                              final Map<T, GordianKey<T>> pKeyMap) throws OceanusException {
        /* Access keyType */
        final T myKeyType = pKey.getKeyType();

        /* Check that the key is supported */
        if (!pPredicate.test(myKeyType)) {
            throw new GordianDataException("invalid keyType");
        }

        /* Look for existing key of this type */
        final GordianKey<T> myExisting = pKeyMap.get(myKeyType);
        if (myExisting != null) {
            /* Must be same as existing key */
            if (!myExisting.equals(pKey)) {
                throw new GordianDataException("keyType already declared");
            }

            /* else new key */
        } else {
            /* Store into map */
            pKeyMap.put(myKeyType, pKey);
        }
    }

    /**
     * Build key set from secret.
     * @param pSecret the secret.
     * @param pInitVector the initialisation vector.
     * @throws OceanusException on error
     */
    public void buildFromSecret(final byte[] pSecret,
                                final byte[] pInitVector) throws OceanusException {
        /* Loop through the symmetricKeys values */
        final GordianKeySetFactory myFactory = theFactory.getKeySetFactory();
        final Predicate<GordianSymKeyType> mySymPredicate = myFactory.supportedKeySetSymKeyTypes();
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (mySymPredicate.test(myType)) {
                /* Generate the key and add to map */
                final GordianSymKeySpec mySpec = new GordianSymKeySpec(myType);
                theSymKeyMap.put(mySpec, generateKey(mySpec, pSecret, pInitVector));
            }
        }
    }

    /**
     * Generate key for a Key Type from the secret and initVector.
     * @param <T> the class of key
     * @param pKeyType the keyType
     * @param pSecret the derived Secret
     * @param pInitVector the initialisation vector.
     * @return the generated key
     * @throws OceanusException on error
     */
    private <T extends GordianKeySpec> GordianKey<T> generateKey(final T pKeyType,
                                                                 final byte[] pSecret,
                                                                 final byte[] pInitVector) throws OceanusException {
        /* Generate a new Secret Key from the secret */
        final GordianCipherFactory myFactory = theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myFactory.getKeyGenerator(pKeyType);
        return myGenerator.generateKeyFromSecret(pSecret, pInitVector);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianCoreKeySet)) {
            return false;
        }

        /* Access the target field */
        final GordianCoreKeySet myThat = (GordianCoreKeySet) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getFactory())
                && theSymKeyMap.equals(myThat.getSymKeyMap());
    }

    @Override
    public int hashCode() {
        return GordianParameters.HASH_PRIME * theFactory.hashCode()
                + theSymKeyMap.hashCode();
    }
}
