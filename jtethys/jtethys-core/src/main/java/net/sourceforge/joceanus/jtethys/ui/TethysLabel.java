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
 * Label.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysLabel<N, I>
        implements TethysNode<N> {
    /**
     * The id.
     */
    private final Integer theId;

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
    public abstract void setText(final String pText);

    /**
     * Set the Border Title.
     * @param pTitle the title text
     */
    public abstract void setBorderTitle(final String pTitle);

    /**
     * Set Alignment.
     * @param pAlign the alignment
     */
    public abstract void setAlignment(final TethysAlignment pAlign);

    /**
     * Alignment selection.
     */
    public enum TethysAlignment {
        /**
         * Leading.
         */
        LEADING,

        /**
         * Centre.
         */
        CENTRE,

        /**
         * Trailing.
         */
        TRAILING;
    }
}
