# GordianKnot keyPair Gateway.

## Overview 
GordianKnot provides a gateway to facilitate the processing of Certificate Request Messages.

It provides the capability of creating a PEM encoded CRM, processing and validating it 
and sending/processing the various response messages.

### Sample for signing keyPair  
```
/* Access the factory and store */
final GordianFactory myFactory = ...;
final GordianKeyStore myStore = ...;

/* Create a gateway for the store */
final GordianKeyStoreFactory myKSFactory = myFactory.getKeyPairFactory().getKeyStoreFactory();
final GordianKeyStoreManager myMgr = myKSFactory.createKeyStoreManager(myStore);
final GordianKeyStoreGateway myGateway = myKSFactory.createKeyStoreGateway(myMgr);

/* Configure gateway */
myGateway.setPasswordResolver(name -> passwordForNameInStore);
myGateway.setCertifier(nameOfCertifierKeyPairInStore);
myGateway.setMACSecretResolver(name -> "Some secret to be shared between gateways");

/* Create a signature keyPair */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.ed448();
final X500Name myX500Name = ...;
final String myName = ...; 
final byte[] myPassword = ...;
final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
myMgr.createKeyPair(mySpec, myX500Name, myUsage, mySigner, myName, myPassword);

/* Build the CertificateRequest */
final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
myGateway.createCertificateRequest(myName, myOutStream);

/* At server, process the certificateRequest (Response is now in outStream) */
ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
myOutStream.reset();
myGateway.processCertificateRequest(myInputStream, myOutStream);

/* Server keyStore will have been updated with certified entry */

/* At client, process the certificate response */
myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
myOutStream.reset();
final Integer myRespId = myGateway.processCertificateResponse(myInputStream, myOutStream);

/* Client keyStore will have been updated with certified entry */
```

### Sample for agreement keyPair 
```
/* Access the factory and store */
final GordianFactory myFactory = ...;
final GordianKeyStore myStore = ...;

/* Create a gateway for the store */
final GordianKeyStoreFactory myKSFactory = myFactory.getKeyPairFactory().getKeyStoreFactory();
final GordianKeyStoreManager myMgr = myKSFactory.createKeyStoreManager(myStore);
final GordianKeyStoreGateway myGateway = myKSFactory.createKeyStoreGateway(myMgr);

/* Configure gateway */
myGateway.setPasswordResolver(name -> passwordForNameInStore);
myGateway.setCertifier(nameOfCertifierKeyPairInStore);
myGateway.setMACSecretResolver(name -> "Some secret to be shared between gateways");

/* Create an agreement keyPair */
final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.x448();
final X500Name myX500Name = ...;
final String myName = ...; 
final byte[] myPassword = ...;
final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
myMgr.createKeyPair(mySpec, myX500Name, myUsage, mySigner, myName, myPassword);

/* Build the CertificateRequest */
final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
myGateway.createCertificateRequest(myName, myOutStream);

/* At server, process the certificateRequest (Response is now in outStream) */
ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
myOutStream.reset();
myGateway.processCertificateRequest(myInputStream, myOutStream);

/* Record certificate in local cache so that update can be made on successful ACK */

/* At client, process the certificate response (Ack is now in outStream) */
myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
myOutStream.reset();
final Integer myRespId = myGateway.processCertificateResponse(myInputStream, myOutStream);

/* Client keyStore will have been updated with new certificate chain */

/* At server, process the certificateAck */
myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
myOutStream.reset();
myGateway.processCertificateAck(myInputStream);

/* Server keyStore will have been updated with certified entry */
```