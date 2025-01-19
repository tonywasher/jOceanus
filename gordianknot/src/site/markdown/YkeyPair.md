# GordianKnot KeyPairs

## Overview
Asymmetric functions are supported via the **GordianKeyPairFactory** interface.

GordianKnot supports most Asymmetric algorithms that are available from BouncyCastle through the JCA.

A keyPairGenerator can be created for a KeyPairSpec (algorithm plus additional configuration)
This will allow generation of a random keyPair plus translation to/from PKCS8/X509 encodings,
and combines the role of KeyPairGenerator and KeyFactory in JCA.

The various algorithms support Signature/Encryption/Agreement as available
JCA supports all algorithms
        
## Key Management
Algorithms are represented by **GordianKeyPairSpec**. A **GordianKeyPairGenerator** is obtained via the keySpec,
and then keyPairs are generated or derived by the generator. The keyPair may be publicOnly if only the public key is known.

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianKeyPairFactory myKeyPairFactory = myBaseFactory.getKeyPairFactory();

/* Access keyPairGenerator */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048);
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myPair = myGenerator.generateKeyPair();

/* Access encodings */
final PKCS8EncodedKeySpec myPKCS8 = myGenerator.getPKCS8Encoding(myPair);
final X509EncodedKeySpec myX509 = myGenerator.getX509Encoding(myPair);

/* Derive publicOnly KeyPair */
final GordianKeyPair myPublicOnly = myGenerator.derivePublicOnlyKeyPair(myX509);

/* Derive full keyPair */
final GordianKeyPair myDerived = myGenerator.deriveKeyPair(myX509, myPKCS8);
```
            
## Algorithms
The following asymmetric algorithms and variants are supported.
<table class="defTable">
  <tr><th class="defHdr">Algorithm</th><th class="defHdr">Variants</th></tr>
  <tr><td>RSA</td><td>Modulus 1024, 1536, 2048, 3072, 4096, 6144, 8192</td></tr>
  <tr><td>DSA</td><td>Modulus 1024, 2048, 3096</td></tr>
  <tr><td>DiffieHellman</td><td>rfc2409_1024, rfc3526_1536, rfc3526_2048, rfc3526_3072, rfc3526_4096, rfc3526_6144, rfc3526_8192,
      rfc7919_ffdhe2048, rfc7919_ffdhe3072, rfc7919_ffdhe4096, rfc7919_ffdhe6144, rfc7919_ffdhe8192</td></tr>
  <tr><td>ElGamal</td><td>rfc2409_1024, rfc3526_1536, rfc3526_2048, rfc3526_3072, rfc3526_4096, rfc3526_6144, rfc3526_8192,
      rfc7919_ffdhe2048, rfc7919_ffdhe3072, rfc7919_ffdhe4096, rfc7919_ffdhe6144, rfc7919_ffdhe8192</td></tr>
  <tr><td>EC</td><td>sect571k1, sect571r1, secp521r1, sect409k1, sect409r1, secp384r1, sect283k1, sect283r1, secp256k1,
      secp256r1, sect239k1, sect233k1, sect233r1, secp224k1, secp224r1, sect193r1, sect193r2, secp192k1, secp192r1, sect163k1,
      sect163r1, sect163r2, secp160k1, secp160r1, secp160r2, sect131r1, sect131r2, secp128r1, secp128r2, sect113r1, sect113r2,
      secp112r1, secp112r2,
      prime239v1, prime239v2, prime239v3, prime192v2, prime192v3,
      c2tnb431r1, c2pnb368w1, c2tnb359v1, c2pnb304w1, c2pnb272w1, c2tnb239v1, c2tnb239v2, c2tnb239v3, c2pnb208w1, c2tnb191v1,
      c2tnb191v2, c2tnb191v3, c2pnb176w1, c2pnb163v1, c2pnb163v2, c2pnb163v3,
      brainpoolP512r1, brainpoolP512t1, brainpoolP384r1, brainpoolP384t1, brainpoolP320r1, brainpoolP320t1, brainpoolP256r1,
      brainpoolP256t1, brainpoolP224r1, brainpoolP224t1, brainpoolP192r1, brainpoolP192t1, brainpoolP160r1, brainpoolP160t1</td></tr>
  <tr><td>DSTU4145</td><td>Curves 1-9</td></tr>
  <tr><td>GOST2012</td><td>Tc26-Gost-3410-12-512-paramSetA,B,C Tc26-Gost-3410-12-256-paramSetA</td></tr>
  <tr><td>SM2</td><td>sm2p256v1, wapip192v1</td></tr>
  <tr><td>EdDSA</td><td>Curve25519, Curve448</td></tr>
  <tr><td>XDH</td><td>Curve25519, Curve448</td></tr>
  <tr><td>SLHDSA</td><td>(SHA2, SHAKE) * (128, 192, 256) * (F, S) * (pure, hash)</td></tr>
  <tr><td>XMSS</td><td>(SHA256, SHA512, SHAKE128, SHAKE256) * XMSS(H12, H16, H20) or XMSS^MT(H20, H40, H60)</td></tr>
  <tr><td rowspan="2">LMS</td><td>SIG(H5,H10,H25,H20,H25) * OTS(W1,W2,W4,W8)</td></tr>
  <tr><td>HSS(LMS * DEPTH(2..8)</td></tr>
  <tr><td>CMCE</td><td>(BASE, PIVOT) * (3488, 4608, 6688, 6960, 8192)</td></tr>
  <tr><td>FRODO</td><td>(AES, SHAKE) * (19888, 31296, 43088)</td></tr>
  <tr><td>SABER</td><td>(LIGHT, BASE, FIRE) * (128, 192, 256)</td></tr>
  <tr><td>MLKEM</td><td>512, 768, 1024</td></tr>
  <tr><td>MLDSA</td><td>(44, 65, 87) * (pure, hash)</td></tr>
  <tr><td>NTRU</td><td>HPS509, HPS677, HPS821, HPS1229, HRSS701, HRSS1373</td></tr>
  <tr><td>NTRUPRIME</td><td>(NTRUL, SNTRU) * (653, 761, 857, 953, 1013, 1277)</td></tr>
  <tr><td>BIKE</td><td>128, 192, 256</td></tr>
  <tr><td>FALCON</td><td>512, 1024</td></tr>
  <tr><td>HQC</td><td>128, 192, 256</td></tr>
  <tr><td>PICNIC</td><td>(L1, L3, L5) * (FS, UR, FULL, 3)</td></tr>
</table>
             
## Composite KeyPairs
Composite keyPairs can be created as a list of **different** keyPairs. These composite keyPairs can be used
for signatures/agreements/encryption as long as each individual component keyPair can be used for the operation. The only restrictions
are that there must be at least two keyPairs and that all keyPairs must be of a different type

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianKeyPairFactory myKeyPairFactory = myBaseFactory.getKeyPairFactory();

/* Access keyPairGenerator */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                                                                      GordianKeyPairSpecBuilder.elGamal(GordiaDHGroup.rfc7919_ffdhe2048));
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myPair = myGenerator.generateKeyPair();

/* Access encodings */
final PKCS8EncodedKeySpec myPKCS8 = myGenerator.getPKCS8Encoding(myPair);
final X509EncodedKeySpec myX509 = myGenerator.getX509Encoding(myPair);

/* Derive publicOnly KeyPair */
final GordianKeyPair myPublicOnly = myGenerator.derivePublicOnlyKeyPair(myX509);

/* Derive full keyPair */
final GordianKeyPair myDerived = myGenerator.deriveKeyPair(myX509, myPKCS8);
```

