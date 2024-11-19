/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.oceanus.logger;

/**
 * Logger instance.
 */
public class OceanusLogger {
    /**
     * The log manager instance.
     */
    private final OceanusLogManager theManager;

    /**
     * The class for which this is the logger.
     */
    private final Class<?> theOwner;

    /**
     * Constructor.
     * @param pManager the manager
     * @param pOwner the owning class
     */
    public OceanusLogger(final OceanusLogManager pManager,
                         final Class<?> pOwner) {
        theManager = pManager;
        theOwner = pOwner;
    }

    /**
     * Write a debug message with parameters.
     * @param pFormat the format
     * @param pArgs the arguments
     */
    public void debug(final String pFormat,
                      final Object... pArgs) {
        final String myMessage = String.format(pFormat, pArgs);
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.DEBUG, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write a debug message with hex data.
     * @param pMessage the message
     * @param pData the data
     */
    public void debug(final String pMessage,
                      final byte[] pData) {
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.DEBUG, pMessage);
        final String myLogData = OceanusLogManager.formatData(pData);
        theManager.writeLogMessage(myLogMessage + myLogData);
    }

    /**
     * Write a debug message with hex data.
     * @param pMessage the message
     * @param pData the data
     * @param pOffset the offset
     * @param pLength the length of data
     */
    public void debug(final String pMessage,
                      final byte[] pData,
                      final int pOffset,
                      final int pLength) {
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.DEBUG, pMessage);
        final String myLogData = OceanusLogManager.formatData(pData, pOffset, pLength);
        theManager.writeLogMessage(myLogMessage + myLogData);
    }
    /**
     * Write an information message with parameters.
     * @param pFormat the format
     * @param pArgs the arguments
     */
    public void info(final String pFormat,
                     final Object... pArgs) {
        final String myMessage = String.format(pFormat, pArgs);
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.INFO, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write an error message with parameters.
     * @param pFormat the format
     * @param pArgs the arguments
     */
    public void error(final String pFormat,
                      final Object... pArgs) {
        final String myMessage = String.format(pFormat, pArgs);
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.ERROR, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write an error message with exception.
     * @param pMessage the message
     * @param pException the exception
     */
    public void error(final String pMessage,
                      final Throwable pException) {
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.ERROR, pMessage);
        theManager.writeLogMessage(myLogMessage, pException);
    }

    /**
     * Write a fatal error message with parameters.
     * @param pFormat the format
     * @param pArgs the arguments
     */
    public void fatal(final String pFormat,
                      final Object... pArgs) {
        final String myMessage = String.format(pFormat, pArgs);
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.FATAL, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write a fatal error message with exception.
     * @param pMessage the message
     * @param pException the exception
     */
    public void fatal(final String pMessage,
                      final Throwable pException) {
        final String myLogMessage = theManager.formatMessage(theOwner, OceanusLogLevel.FATAL, pMessage);
        theManager.writeLogMessage(myLogMessage, pException);
    }
}
