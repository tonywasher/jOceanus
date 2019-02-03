/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.zip;

/**
 * GordianKnot File Entry API.
 */
public interface GordianZipFileEntry {
    /**
     * Obtain the name of the file.
     *
     * @return the name of the file
     */
    String getFileName();

    /**
     * Obtain the parent contents.
     *
     * @return the parent of the fileEntry
     */
    GordianZipFileContents getParent();

    /**
     * Set User String property.
     *
     * @param pPropertyName  the property name
     * @param pPropertyValue the property value
     */
    void setUserStringProperty(String pPropertyName,
                               String pPropertyValue);

    /**
     * Set User Long property.
     *
     * @param pPropertyName  the property name
     * @param pPropertyValue the property value
     */
    void setUserLongProperty(String pPropertyName,
                             Long pPropertyValue);

    /**
     * Get User String property.
     *
     * @param pPropertyName the property name
     * @return the property value (or null)
     */
    String getUserStringProperty(String pPropertyName);

    /**
     * Get User Long property.
     *
     * @param pPropertyName the property name
     * @return the property value (or null)
     */
    Long getUserLongProperty(String pPropertyName);
}
