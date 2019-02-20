/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.logger;

/**
 * Logger instance.
 */
public class TethysLogger {
    /**
     * The log manager instance.
     */
    private final TethysLogManager theManager;

    /**
     * The class for which this is the logger.
     */
    private final Class<?> theOwner;

    /**
     * Constructor.
     * @param pManager the manager
     * @param pOwner the owning class
     */
    public TethysLogger(final TethysLogManager pManager,
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
        final String myLogMessage = theManager.formatMessage(theOwner, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write an information message with parameters.
     * @param pFormat the format
     * @param pArgs the arguments
     */
    public void info(final String pFormat,
                     final Object... pArgs) {
        final String myMessage = String.format(pFormat, pArgs);
        final String myLogMessage = theManager.formatMessage(theOwner, myMessage);
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
        final String myLogMessage = theManager.formatMessage(theOwner, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write an error message with exception.
     * @param pMessage the message
     * @param pException the exception
     */
    public void error(final String pMessage,
                      final Throwable pException) {
        final String myLogMessage = theManager.formatMessage(theOwner, pMessage);
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
        final String myLogMessage = theManager.formatMessage(theOwner, myMessage);
        theManager.writeLogMessage(myLogMessage);
    }

    /**
     * Write a fatal error message with exception.
     * @param pMessage the message
     * @param pException the exception
     */
    public void fatal(final String pMessage,
                      final Throwable pException) {
        final String myLogMessage = theManager.formatMessage(theOwner, pMessage);
        theManager.writeLogMessage(myLogMessage, pException);
    }
}
