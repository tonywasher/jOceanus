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
package net.sourceforge.joceanus.jtethys.ui;

/**
 * ProgressBar.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysProgressBar<N, I>
        implements TethysNode<N> {
    /**
     * Maximum Value for double.
     */
    protected static final int MAX_DOUBLE_VALUE = 1000;

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
    protected TethysProgressBar(final TethysGuiFactory<N, I> pFactory) {
        theId = pFactory.getNextId();
    }

    /**
     * Set progress.
     * @param pValue the progress value
     * @param pMaximum the maximum value
     */
    public abstract void setProgress(final int pValue,
                                     final int pMaximum);

    /**
     * Set progress.
     * @param pValue the progress value in (0,1) range
     */
    public abstract void setProgress(final double pValue);

    @Override
    public Integer getId() {
        return theId;
    }

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
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(final Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(final Integer pHeight);

}