/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;

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
     * The MacStream.
     */
    private final GordianMacOutputStream theMacStream;

    /**
     * Constructor.
     * @param pDigest the digest
     * @param pOutput the underlying output stream
     * @param pMacStream the MacStream
     */
    GordianDigestOutputStream(final GordianDigest pDigest,
                              final OutputStream pOutput,
                              final GordianMacOutputStream pMacStream) {
        super(pOutput);
        theDigest = pDigest;
        theMacStream = pMacStream;
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
        return theResult == null
               ? null
               : Arrays.copyOf(theResult, theResult.length);
    }

    /**
     * Obtain the dataLength.
     * @return the length
     */
    long getDataLen() {
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
        /* Calculate the digest */
        theResult = theDigest.finish();

        /* Report the expected Digest to the MacStream */
        theMacStream.setDigest(theResult);
    }
}
