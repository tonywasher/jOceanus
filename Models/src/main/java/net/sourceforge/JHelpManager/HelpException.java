/*******************************************************************************
 * JHelpManager: Java Help Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JHelpManager;

/**
 * Exception indicating that there was a failure in the help system.
 * @author Tony Washer
 */
public class HelpException extends Exception {
    /**
     * Serial id.
     */
    private static final long serialVersionUID = 716878129927618942L;

    /**
     * Constructor.
     * @param s exception string
     */
    public HelpException(final String s) {
        super(s);
    }

    /**
     * Constructor.
     * @param s exception string
     * @param e cause of exception
     */
    public HelpException(final String s,
                         final Throwable e) {
        super(s, e);
    }
}
