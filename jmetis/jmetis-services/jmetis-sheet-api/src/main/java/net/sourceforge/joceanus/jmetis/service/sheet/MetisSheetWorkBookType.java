/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * WorkBook types.
 */
public enum MetisSheetWorkBookType {
    /**
     * Excel xls.
     */
    EXCELXLS("xls"),

    /**
     * Excel xlsx.
     */
    EXCELXLSX("xlsx"),

    /**
     * Oasis jOpenDoc.
     */
    OASISJOPEN("ods"),

    /**
     * Oasis ods.
     */
    OASISODS("ods");

    /**
     * The String name.
     */
    private String theName;

    /**
     * File extension.
     */
    private final String theExtension;

    /**
     * Constructor.
     * @param pExtension the extension code
     */
    MetisSheetWorkBookType(final String pExtension) {
        theExtension = pExtension;
    }

    /**
     * Obtain the extension.
     * @return the extension
     */
    public String getExtension() {
        return "."
               + theExtension;
    }

    /**
     * Determine workBookType of file.
     * @param pFileName the filename
     * @return the workBookType
     * @throws OceanusException on error
     */
    public static MetisSheetWorkBookType determineType(final String pFileName) throws OceanusException {
        /* Loop through all values */
        for (MetisSheetWorkBookType myType : MetisSheetWorkBookType.values()) {
            /* If we have matched the type */
            if (pFileName.endsWith(myType.getExtension())) {
                /* Return it */
                return myType;
            }
        }

        /* Unrecognised type */
        throw new MetisSheetException("Unrecognised file type "
                                      + pFileName);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MetisSheetResource.getKeyForWorkBook(this).getValue();
        }

        /* return the name */
        return theName;
    }
}
