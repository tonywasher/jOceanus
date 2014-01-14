/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jspreadsheetmanager;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

/**
 * WorkBook types.
 */
public enum WorkBookType {
    /**
     * Excel xls.
     */
    EXCELXLS("xls"),

    /**
     * Oasis ods.
     */
    OASISODS("ods");

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(WorkBookType.class.getName());

    /**
     * The String name.
     */
    private String theName;

    /**
     * File extension.
     */
    private final String theExtension;

    /**
     * Obtain the extension.
     * @return the extension
     */
    public String getExtension() {
        return "."
               + theExtension;
    }

    /**
     * Constructor.
     * @param pExtension the extension code
     */
    private WorkBookType(final String pExtension) {
        theExtension = pExtension;
    }

    /**
     * Determine workBookType of file.
     * @param pFileName the filename
     * @return the workBookType
     * @throws JDataException on error
     */
    public static WorkBookType determineType(final String pFileName) throws JDataException {
        /* Loop through all values */
        for (WorkBookType myType : WorkBookType.values()) {
            /* If we have matched the type */
            if (pFileName.endsWith(myType.getExtension())) {
                /* Return it */
                return myType;
            }
        }

        /* Unrecognised type */
        throw new JDataException(ExceptionClass.EXCEL, "Unrecognised file type "
                                                       + pFileName);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }
}
