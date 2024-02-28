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
package net.sourceforge.joceanus.jtethys.ui.api.control;

/**
 * Control Factory.
 */
public interface TethysUIControlFactory {
    /**
     * Obtain a new label.
     * @return the new label
     */
    TethysUILabel newLabel();

    /**
     * Obtain a new label.
     * @param pText the label text
     * @return the new label
     */
    default TethysUILabel newLabel(String pText) {
        final TethysUILabel myLabel = newLabel();
        myLabel.setText(pText);
        return myLabel;
    }

    /**
     * Obtain a check box.
     * @return the new check box
     */
    TethysUICheckBox newCheckBox();

    /**
     * Obtain a check box.
     * @param pText the checkBox text
     * @return the new check box
     */
    default TethysUICheckBox newCheckBox(String pText) {
        final TethysUICheckBox myCheckBox = newCheckBox();
        myCheckBox.setText(pText);
        return myCheckBox;
    }

    /**
     * Obtain a new textArea.
     * @return the new textArea
     */
    TethysUITextArea newTextArea();

    /**
     * Obtain a new password field.
     * @return the new password field
     */
    TethysUIPasswordField newPasswordField();

    /**
     * Obtain a new progressBar.
     * @return the new progressBar
     */
    TethysUIProgressBar newProgressBar();

    /**
     * Obtain a new slider.
     * @return the new slider
     */
    TethysUISlider newSlider();

    /**
     * Obtain a new HTML manager.
     * @return the new manager
     */
    TethysUIHTMLManager newHTMLManager();

    /**
     * Obtain a new Tree manager.
     * @param <T> the item type
     * @return the new manager
     */
    <T> TethysUITreeManager<T> newTreeManager();

    /**
     * Obtain a new splitTree manager.
     * @param <T> the item type
     * @return the new manager
     */
    <T> TethysUISplitTreeManager<T> newSplitTreeManager();
}
