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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory.TethysParentNode;

/**
 * Flow Pane Manager.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public abstract class TethysBoxPaneManager<N, I>
        implements TethysNode<N>, TethysParentNode<N> {
    /**
     * Strut Size.
     */
    private static final int STRUT_SIZE = 4;

    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Node List.
     */
    private final List<TethysNode<N>> theNodeList;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The Horizontal Gap.
     */
    private Integer theGap;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysBoxPaneManager(final TethysGuiFactory<N, I> pFactory) {
        theGuiFactory = pFactory;
        theId = theGuiFactory.getNextId();
        theNodeList = new ArrayList<>();
        theGap = STRUT_SIZE;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain the Gap.
     * @return the Gap.
     */
    protected Integer getGap() {
        return theGap;
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
     * Set the Gap.
     * @param pGap the Gap.
     */
    public void setGap(final Integer pGap) {
        theGap = pGap;
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

    /**
     * Add spacer.
     */
    public abstract void addSpacer();

    /**
     * Add strut.
     */
    public abstract void addStrut();

    /**
     * Add Spacer Node.
     * @param pNode the node
     */
    protected void addSpacerNode(final TethysNode<N> pNode) {
        theNodeList.add(pNode);
    }

    /**
     * Add Node.
     * @param pNode the node
     */
    public void addNode(final TethysNode<N> pNode) {
        theNodeList.add(pNode);
        theGuiFactory.registerChild(this, pNode);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysNode<N> myNode : theNodeList) {
            myNode.setEnabled(pEnabled);
        }
    }

    /**
     * Obtain List iterator.
     * @return the iterator
     */
    protected Iterator<TethysNode<N>> iterator() {
        return theNodeList.iterator();
    }
}
