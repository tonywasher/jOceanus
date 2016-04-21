/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * A full set of symmetric keys, subject to the relevant predicate.
 */
public final class GordianKeySet {
    /**
     * Initialisation Vector size.
     */
    private static final int IVSIZE = GordianFactory.IVSIZE;

    /**
     * Maximum number of encryption steps.
     */
    public static final int MAXSTEPS = GordianSymKeyType.values().length - 1;

    /**
     * Maximum wrapped KeySize.
     */
    public static final int WRAPPED_KEYSIZE = GordianFactory.BIG_KEYLEN
                                              + GordianKeySetRecipe.RECIPELEN;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * Map of KeyType to key.
     */
    private final Map<GordianSymKeyType, GordianKey<GordianSymKeyType>> theKeyMap;

    /**
     * The underlying Cipher.
     */
    private final GordianMultiCipher theCipher;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianKeySet(final GordianFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create map */
        theKeyMap = new EnumMap<>(GordianSymKeyType.class);

        /* Create the cipher */
        theCipher = new GordianMultiCipher(this);
    }

    /**
     * Obtain the factory.
     * @return a new MultiCipher
     */
    public GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Obtain the keySet.
     * @return the keySet
     */
    protected Map<GordianSymKeyType, GordianKey<GordianSymKeyType>> getKeyMap() {
        return theKeyMap;
    }

    /**
     * Obtain a multiCipher.
     * @return a new MultiCipher
     */
    public GordianMultiCipher getNewCipher() {
        return new GordianMultiCipher(this);
    }

    /**
     * Encryption length.
     * @param pDataLength the length of data to be encrypted
     * @return the length of encrypted data
     */
    public static int getEncryptionLength(final int pDataLength) {
        int iBlocks = 1 + ((pDataLength - 1) % IVSIZE);
        return iBlocks
               * IVSIZE;
    }

    /**
     * Encryption overhead.
     * @return the encryption overhead
     */
    public static int getEncryptionOverhead() {
        return IVSIZE + GordianKeySetRecipe.RECIPELEN;
    }

    /**
     * Encrypt bytes.
     * @param pBytes the bytes to encrypt
     * @return the encrypted bytes
     * @throws OceanusException on error
     */
    public byte[] encryptBytes(final byte[] pBytes) throws OceanusException {
        /* Generate set of keys and initialisation vector */
        GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, true);
        GordianSymKeyType[] myKeyTypes = myRecipe.getSymKeyTypes();
        byte[] myVector = myRecipe.getInitVector();

        /* Encrypt the bytes */
        theCipher.initCiphers(myKeyTypes, myVector, true);
        byte[] myBytes = theCipher.finish(pBytes);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(theFactory, myBytes);
    }

    /**
     * Decrypt bytes.
     * @param pBytes the bytes to decrypt
     * @return the decrypted bytes
     * @throws OceanusException on error
     */
    public byte[] decryptBytes(final byte[] pBytes) throws OceanusException {
        /* Parse the bytes into the separate parts */
        GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pBytes, true);
        GordianSymKeyType[] myKeyTypes = myRecipe.getSymKeyTypes();
        byte[] myVector = myRecipe.getInitVector();
        byte[] myBytes = myRecipe.getBytes();

        /* Decrypt the bytes and return them */
        theCipher.initCiphers(myKeyTypes, myVector, false);
        return theCipher.finish(myBytes);
    }

    /**
     * secure Key.
     * @param pKey the key to wrap
     * @return the wrapped key
     * @throws OceanusException on error
     */
    public byte[] secureKey(final GordianKey<?> pKey) throws OceanusException {
        /* Generate set of keys */
        GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, false);
        GordianSymKeyType[] myKeyTypes = myRecipe.getSymKeyTypes();

        /* Wrap the key */
        byte[] myBytes = theCipher.wrapKey(myKeyTypes, pKey);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(theFactory, myBytes);
    }

    /**
     * derive Key.
     * @param <T> the keyType class
     * @param pKeySpec the wrapped key
     * @param pKeyType the key type
     * @return the key
     * @throws OceanusException on error
     */
    public <T> GordianKey<T> deriveKey(final byte[] pKeySpec,
                                       final T pKeyType) throws OceanusException {
        /* Parse the bytes into the separate parts */
        GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pKeySpec, false);
        GordianSymKeyType[] myKeyTypes = myRecipe.getSymKeyTypes();
        byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.unwrapKey(myKeyTypes, myBytes, pKeyType);
    }

    /**
     * secure Key.
     * @param pKey the key to wrap
     * @return the wrapped key
     * @throws OceanusException on error
     */
    public byte[] secureKey(final GordianPrivateKey pKey) throws OceanusException {
        /* Generate set of keys */
        GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, false);
        GordianSymKeyType[] myKeyTypes = myRecipe.getSymKeyTypes();

        /* Wrap the key */
        byte[] myBytes = theCipher.wrapKey(myKeyTypes, pKey);

        /* Package and return the encrypted bytes */
        return myRecipe.buildExternal(theFactory, myBytes);
    }

    /**
     * derive Key.
     * @param pKeySpec the wrapped key
     * @param pKeyType the key type
     * @return the key
     * @throws OceanusException on error
     */
    public GordianPrivateKey deriveKey(final byte[] pKeySpec,
                                       final GordianAsymKeyType pKeyType) throws OceanusException {
        /* Parse the bytes into the separate parts */
        GordianKeySetRecipe myRecipe = new GordianKeySetRecipe(theFactory, pKeySpec, false);
        GordianSymKeyType[] myKeyTypes = myRecipe.getSymKeyTypes();
        byte[] myBytes = myRecipe.getBytes();

        /* Unwrap the key and return it */
        return theCipher.unwrapKey(myKeyTypes, myBytes, pKeyType);
    }

    /**
     * derive externalId from type.
     * @param <T> the Type class
     * @param pType the type
     * @return the externalId
     * @throws OceanusException on error
     */
    public <T> int deriveExternalIdForType(final T pType) throws OceanusException {
        GordianIdManager myManager = theFactory.getIdManager();
        return myManager.deriveExternalIdFromType(pType);
    }

    /**
     * derive Type from externalId.
     * @param <T> the Type class
     * @param pId the externalId
     * @param pTypeClass the class
     * @return the type
     * @throws OceanusException on error
     */
    public <T> T deriveTypeFromExternalId(final int pId,
                                          final Class<T> pTypeClass) throws OceanusException {
        GordianIdManager myManager = theFactory.getIdManager();
        return myManager.deriveTypeFromExternalId(pId, pTypeClass);
    }

    /**
     * Declare Key.
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void declareKey(final GordianKey<GordianSymKeyType> pKey) throws OceanusException {
        /* Access keyType */
        GordianSymKeyType myKeyType = pKey.getKeyType();

        /* Check that the key is supported */
        Predicate<GordianSymKeyType> myPredicate = theFactory.standardSymKeys();
        if (!myPredicate.test(myKeyType)) {
            throw new GordianDataException("invalid keyType");
        }

        /* Look for existing key of this type */
        GordianKey<GordianSymKeyType> myExisting = theKeyMap.get(myKeyType);
        if (myExisting != null) {
            /* Must be same as existing key */
            if (!myExisting.equals(pKey)) {
                throw new GordianDataException("keyType already declared");
            }

            /* else new key */
        } else {
            /* Store into map */
            theKeyMap.put(myKeyType, pKey);
        }
    }

    /**
     * Build key set from secret.
     * @param pSecret the secret.
     * @param pInitVector the initialisation vector.
     * @throws OceanusException on error
     */
    protected void buildFromSecret(final byte[] pSecret,
                                   final byte[] pInitVector) throws OceanusException {
        /* Loop through the Cipher values */
        Predicate<GordianSymKeyType> myPredicate = theFactory.standardSymKeys();
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* If this is supported for a keySet */
            if (myPredicate.test(myType)) {
                /* Build the Cipher */
                buildCipher(myType, pSecret, pInitVector);
            }
        }
    }

    /**
     * Build Secret Cipher for a Key Type.
     * @param pKeyType the Key type
     * @param pSecret the derived Secret
     * @param pInitVector the initialisation vector.
     * @throws OceanusException on error
     */
    private void buildCipher(final GordianSymKeyType pKeyType,
                             final byte[] pSecret,
                             final byte[] pInitVector) throws OceanusException {
        /* Generate a new Secret Key from the secret */
        GordianKeyGenerator<GordianSymKeyType> myGenerator = theFactory.getKeyGenerator(pKeyType);
        GordianKey<GordianSymKeyType> myKey = myGenerator.generateKeyFromSecret(pSecret, pInitVector);

        /* Add it to the map */
        theKeyMap.put(pKeyType, myKey);
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
        if (!(pThat instanceof GordianKeySet)) {
            return false;
        }

        /* Access the target field */
        GordianKeySet myThat = (GordianKeySet) pThat;

        /* Check differences */
        return theFactory.equals(myThat.getFactory())
               && theKeyMap.equals(myThat.getKeyMap());
    }

    @Override
    public int hashCode() {
        return GordianFactory.HASH_PRIME * theFactory.hashCode()
               + theKeyMap.hashCode();
    }
}
