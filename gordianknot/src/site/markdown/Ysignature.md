# GordianKnot Asymmetric Signatures

## Overview
Signatures are supported via the <strong>GordianSignatureFactory</strong> interface.

Algorithms are represented by **GordianSignatureSpec**. A **GordianSignature** is obtained via the signatureSpec,
and then signatures are generated or verified  by the signer.

The **LMS** and **XMSS** signature schemes are stateful signature schemes and as such their keyPairs support the
**GordianStateAwareKeyPair** interface which allows sharding of the privateKey
        
### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianKeyPairFactory myKeyPairFactory = myBase.getKeyPairFactory();
final GordianSignatureFactory mySignatureFactory = myKeyPairFactory.getSignatureFactory();

/* Access keyPairGenerator */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048);
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myKeyPair = myGenerator.generateKeyPair();
GordianKeyPair mySigningPair = myKeyPair;

/* If the pair is StateAware */
if (myKeyPair instanceof GordianStateAwareKeyPair) {
    /* Extract a shard that can sign a single entity */
    final GordianKeyPair mySigningPair = ((GordianStateAwareKeyPair) myKeyPair).getKeyPairShard(1);

    /* Store updated keyPair back to store BEFORE signing to ensure this shard is not reused */
}

/* Access signer */
final GordianSignatureSpec mySignSpec = GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1,
                                                                        GordianDigestSpecBuilder.sha2(GordianLength.LEN_256));
final GordianSignature mySigner = mySignatureFactory.createSigner(mySignSpec);

/* Sign message */
final byte[] message = ....;
mySigner.initForSigning(mySigningPair);
mySigner.update(message);
final byte[] mySignature = mySigner.sign();

/* Verify signature */
mySigner.initForVerify(myKeyPair);
mySigner.update(message);
final boolean verified = mySigner.verify(mySignature);
```

## Composite Signatures
Composite signatures may be created by a composite keyPair, as long as each element of the composite keyPair is assigned a valid signatureSpec.

The first signature is calculated as a standard signature, subsequent signature take as a trailing input the preceding signature. 

The resulting signatures are encoded as an ASN1Sequence.

### Sample
```
/* Access factory */
final GordianFactory myBaseFactory = GordianGenerator.createFactory();
final GordianKeyPairFactory myKeyPairFactory = myBase.getKeyPairFactory();
final GordianSignatureFactory mySignatureFactory = myKeyPairFactory.getSignatureFactory();

/* Access keyPairGenerator */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                                                                      GordianKeyPairSpecBuilder.ed25519());
final GordianKeyPairGenerator myGenerator = myKeyPairFactory.getKeyPairGenerator(mySpec);
final GordianKeyPair myKeyPair = myGenerator.generateKeyPair();
GordianKeyPair mySigningPair = myKeyPair;

/* Access signer */
final GordianSignatureSpec mySignSpec = GordianSignatureSpecBuilder.composite(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1,
                                                                                                              GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)),
                                                                              GordianSignatureSpecBuilder.edDSA());
final GordianSignature mySigner = mySignatureFactory.createSigner(mySignSpec);

/* Sign message */
final byte[] message = ....;
mySigner.initForSigning(mySigningPair);
mySigner.update(message);
final byte[] mySignature = mySigner.sign();

/* Verify signature */
mySigner.initForVerify(myKeyPair);
mySigner.update(message);
final boolean verified = mySigner.verify(mySignature);
```

## Algorithms
The following signature algorithms are supported.
<table class="defTable">
  <tr><th class="defHdr">Algorithm</th><th class="defHdr">Variants</th></tr>
  <tr><td>RSA</td><td>PSSMGF1, PSS128, PSS256, X931, ISO9796D2, PreHash</td></tr>
  <tr><td>DSA</td><td>DSA, DetDSA</td></tr>
  <tr><td>EC</td><td>ECDSA, ECDetDSA, ECNR</td></tr>
  <tr><td>DSTU4145</td><td>Native</td></tr>
  <tr><td>GOST2012</td><td>Native</td></tr>
  <tr><td>SM2</td><td>Native</td></tr>
  <tr><td>EdDSA</td><td>Native</td></tr>
  <tr><td>SLHDSA</td><td>Native</td></tr>
  <tr><td>MLDSA</td><td>Native</td></tr>
  <tr><td>FALCON</td><td>Native</td></tr>
  <tr><td>PICNIC</td><td>Native</td></tr>
  <tr><td>XMSS</td><td>Native, PreHash</td></tr>
  <tr><td>LMS</td><td>Native</td></tr>
</table>
