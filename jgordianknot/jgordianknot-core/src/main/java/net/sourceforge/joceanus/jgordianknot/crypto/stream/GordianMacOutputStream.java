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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/src/main/java/net/sourceforge/joceanus/jgordianknot/zip/EncryptionOutputStream.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;

/**
 * Output stream mac implementation.
 */
public class GordianMacOutputStream
        extends GordianOutputStream {
    /**
     * The MAC.
     */
    private final GordianMac theMac;

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
     * @param pMac the MAC
     * @param pOutput the underlying output stream
     */
    protected GordianMacOutputStream(final GordianMac pMac,
                                     final OutputStream pOutput) {
        super(pOutput);
        theMac = pMac;
    }

    /**
     * Obtain the MAC.
     * @return the MAC
     */
    public GordianMac getMac() {
        return theMac;
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
        /* Update the MAC and write bytes to underlying stream */
        theMac.update(pBytes, pOffset, pLength);
        theDataLen += pLength;
        writeToStream(pBytes, pOffset, pLength);
    }

    @Override
    protected void finishData() {
        theResult = theMac.finish();
    }
}
