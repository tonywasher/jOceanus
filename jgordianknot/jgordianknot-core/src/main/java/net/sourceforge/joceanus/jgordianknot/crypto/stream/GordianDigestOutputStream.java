/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;

/**
 * Output stream digest implementation.
 */
public class GordianDigestOutputStream
        extends GordianOutputStream {
    /**
     * The digest.
     */
    private final GordianDigest theDigest;

    /**
     * The result.
     */
    private byte[] theResult;

    /**
     * The data length.
     */
    private long theDataLen;

    /**
     * Constructor.
     * @param pDigest the digest
     * @param pOutput the underlying output stream
     */
    public GordianDigestOutputStream(final GordianDigest pDigest,
                                     final OutputStream pOutput) {
        super(pOutput);
        theDigest = pDigest;
    }

    /**
     * Obtain the Digest.
     * @return the digest
     */
    public GordianDigest getDigest() {
        return theDigest;
    }

    /**
     * Obtain the Result.
     * @return the result
     */
    public byte[] getResult() {
        return theResult;
    }

    /**
     * Obtain the dataLength.
     * @return the length
     */
    public long getDataLen() {
        return theDataLen;
    }

    @Override
    protected void processData(final byte[] pBytes,
                               final int pOffset,
                               final int pLength) throws IOException {
        /* Update the digest and write bytes to underlying stream */
        theDigest.update(pBytes, pOffset, pLength);
        theDataLen += pLength;
        writeToStream(pBytes, pOffset, pLength);
    }

    @Override
    protected void finishData() {
        theResult = theDigest.finish();
    }
}
