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
package net.sourceforge.joceanus.tethys.ui.api.control;

import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;

/**
 * Non-editable text area.
 */
public interface TethysUITextArea
        extends TethysUIComponent {
    /**
     * Set the text.
     * @param pText the text
     */
    void setText(String pText);

    /**
     * Append the text.
     * @param pText the text
     */
    void appendText(String pText);

    /**
     * Insert the text at position.
     * @param pText the text
     * @param pPos the position
     */
    void insertText(String pText,
                    int pPos);

    /**
     * replace the text at position.
     * @param pText the text
     * @param pStart the start position
     * @param pEnd the end position
     */
    void replaceText(String pText,
                     int pStart,
                     int pEnd);

    /**
     * SetCaretPosition.
     * @param pPos the position
     */
    void setCaretPosition(int pPos);

    /**
     * GetCaretPosition.
     * @return the position
     */
    int getCaretPosition();

    /**
     * Obtain textLength.
     * @return the length
     */
    int getTextLength();
}
