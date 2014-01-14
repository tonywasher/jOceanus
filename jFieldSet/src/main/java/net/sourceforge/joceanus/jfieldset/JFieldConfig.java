/*******************************************************************************
 * jFieldSet: Java Swing Field Set
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
package net.sourceforge.joceanus.jfieldset;

import java.awt.Color;

/**
 * Render configuration.
 * @author Tony Washer
 */
public class JFieldConfig {
    /**
     * The error colour.
     */
    private Color theErrorColor = Color.red;

    /**
     * The changed colour.
     */
    private Color theChangedColor = Color.magenta.darker();

    /**
     * The new colour.
     */
    private Color theNewColor = Color.blue;

    /**
     * The deleted colour.
     */
    private Color theDeletedColor = Color.lightGray;

    /**
     * The recovered colour.
     */
    private Color theRecoveredColor = Color.darkGray;

    /**
     * The standard colour.
     */
    private Color theStandardColor = Color.black;

    /**
     * The background colour.
     */
    private Color theBackgroundColor = Color.white;

    /**
     * The link colour.
     */
    private Color theLinkColor = Color.blue;

    /**
     * The changed link colour.
     */
    private Color theChgLinkColor = Color.blue;

    /**
     * Get colour for render state.
     * @param pState the render state
     * @return the colour
     */
    public Color getColorForState(final JFieldState pState) {
        switch (pState) {
            case ERROR:
                return theErrorColor;
            case NEW:
                return theNewColor;
            case CHANGED:
                return theChangedColor;
            case DELETED:
                return theDeletedColor;
            case RESTORED:
                return theRecoveredColor;
            case NORMAL:
            default:
                return theStandardColor;
        }
    }

    /**
     * Get standard colour.
     * @return the colour
     */
    public Color getStandardColor() {
        return theStandardColor;
    }

    /**
     * Get changed colour.
     * @return the colour
     */
    public Color getChangedColor() {
        return theChangedColor;
    }

    /**
     * Get link colour.
     * @return the colour
     */
    public Color getLinkColor() {
        return theLinkColor;
    }

    /**
     * Get changed link colour.
     * @return the colour
     */
    public Color getChangedLinkColor() {
        return theChgLinkColor;
    }

    /**
     * Get background colour.
     * @return the colour
     */
    public Color getBackgroundColor() {
        return theBackgroundColor;
    }

    /**
     * Get error colour.
     * @return the colour
     */
    public Color getErrorColor() {
        return theErrorColor;
    }

    /**
     * Set error colour.
     * @param pColor the colour
     */
    public void setErrorColor(final Color pColor) {
        theErrorColor = pColor;
    }

    /**
     * Set changed colour.
     * @param pColor the colour
     */
    public void setChangedColor(final Color pColor) {
        theChangedColor = pColor;
    }

    /**
     * Set new colour.
     * @param pColor the colour
     */
    public void setNewColor(final Color pColor) {
        theNewColor = pColor;
    }

    /**
     * Set deleted colour.
     * @param pColor the colour
     */
    public void setDeletedColor(final Color pColor) {
        theDeletedColor = pColor;
    }

    /**
     * Set recovered colour.
     * @param pColor the colour
     */
    public void setRecoveredColor(final Color pColor) {
        theRecoveredColor = pColor;
    }

    /**
     * Set standard colour.
     * @param pColor the colour
     */
    public void setStandardColor(final Color pColor) {
        theStandardColor = pColor;
    }

    /**
     * Set link colour.
     * @param pColor the colour
     */
    public void setLinkColor(final Color pColor) {
        theLinkColor = pColor;
    }

    /**
     * Set changed link colour.
     * @param pColor the colour
     */
    public void setChgLinkColor(final Color pColor) {
        theChgLinkColor = pColor;
    }

    /**
     * Set background colour.
     * @param pColor the colour
     */
    public void setBackgroundColor(final Color pColor) {
        theBackgroundColor = pColor;
    }
}
