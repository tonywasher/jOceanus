/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.swing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Simple button that displays icon.
 * @param <T> the object type
 */
public class JIconButton<T>
        extends JButton {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4943316534086830953L;

    /**
     * Button value.
     */
    private T theValue;

    /**
     * Icon Map.
     */
    private final Map<T, Icon> theIconMap;

    /**
     * ToolTip Map.
     */
    private final Map<T, String> theTipMap;

    /**
     * Obtain value.
     * @return the value
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain icon for value.
     * @param pValue the value
     * @return the icon
     */
    public Icon getIconForValue(final T pValue) {
        return theIconMap.get(pValue);
    }

    /**
     * Constructor.
     */
    public JIconButton() {
        /* Allocate the maps */
        theIconMap = new HashMap<T, Icon>();
        theTipMap = new HashMap<T, String>();
    }

    /**
     * Map value.
     * @param pValue the value
     * @param pIcon the mapped Icon
     */
    public void setIconForValue(final T pValue,
                                final Icon pIcon) {
        /* Put value into map */
        theIconMap.put(pValue, pIcon);
    }

    /**
     * Map ToolTip.
     * @param pValue the value
     * @param pTip the mapped toolTip
     */
    public void mapValue(final T pValue,
                         final String pTip) {
        /* Put value into map */
        theTipMap.put(pValue, pTip);
    }

    /**
     * Set value of button.
     * @param pValue the value to set
     */
    public void setValue(final T pValue) {
        /* Store value */
        theValue = pValue;

        /* Access Icon */
        Icon myIcon = getIconForValue(pValue);
        setIcon(myIcon);

        /* Access ToolTip */
        String myTip = theTipMap.get(pValue);
        setToolTipText(myTip);
    }
}
