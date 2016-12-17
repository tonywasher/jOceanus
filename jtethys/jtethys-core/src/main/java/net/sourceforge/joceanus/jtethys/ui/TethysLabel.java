/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

/**
 * Label.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysLabel<N, I>
        implements TethysNode<N> {
    /**
     * The colon indicator.
     */
    public static final String STR_COLON = ":";

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysLabel(final TethysGuiFactory<N, I> pFactory) {
        theId = pFactory.getNextId();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Set Text.
     * @param pText the text
     */
    public abstract void setText(String pText);

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set error text colour.
     */
    public abstract void setErrorText();

    /**
     * Obtain the width.
     * @return the width
     */
    public abstract Integer getWidth();

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

    /**
     * Set Alignment.
     * @param pAlign the alignment
     */
    public abstract void setAlignment(TethysAlignment pAlign);
}
