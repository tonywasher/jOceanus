# GordianKnot

## Overview
**GordianKnot** provides an layer over the BouncyCastle encryption library,
and adds significant additional functionality, via a **GordianFactory** object.
Two flavours of factory are provided, one that accesses BouncyCastle via the JCA provider interface, and
one that accesses the BouncyCastle lightweight API. This second flavour supports additional algorithms
not currently available in the JCA interface, or for that matter in BouncyCastle itself.

The factory provides access to a number of subFactories (e.g. **GordianDigestFactory**) to simplify the API.
        
## Vanilla API
The Vanilla API provides access to cryptographic primitives. Each primitive is identified by a
**Specification** object such as **GordianDigestSpec** which identifies the algorithm variant
that is required.

### Symmetric API

1. <a href="blockCipher.html">Block Ciphers</a>
2. <a href="streamCipher.html">Stream Ciphers</a>
3. <a href="pbe.html">Password Based Ciphers</a>
4. <a href="digest.html">Digests</a>
5. <a href="mac.html">MACs</a>
6. <a href="random.html">Randoms</a>
                
### Asymmetric API
                
1. <a href="keyPair.html">KeyPairs</a>
2. <a href="signature.html">Signatures</a>
3. <a href="agreement.html">Agreements</a>
4. <a href="encryptor.html">Encryptors</a>
                  
## Extended API
The Extended API provides access to additional features that are not provided by BouncyCastle.

1. <a href="personal.html">Personalisation</a>
2. <a href="keySet.html">KeySets</a>
3. <a href="lock.html">Locks</a>
4. <a href="zip.html">Secure ZipFile</a>
5. <a href="keyStore.html">KeyStore</a>
6. <a href="keyGateway.html">KeyGateway</a>
