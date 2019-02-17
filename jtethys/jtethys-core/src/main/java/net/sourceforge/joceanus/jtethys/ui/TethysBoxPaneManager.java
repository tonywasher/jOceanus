/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory.TethysParentComponent;

/**
 * Box Pane Manager.
 */
public abstract class TethysBoxPaneManager
        implements TethysComponent, TethysParentComponent {
    /**
     * Strut Size.
     */
    private static final int STRUT_SIZE = 4;

    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Node List.
     */
    private final List<TethysComponent> theNodeList;

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
    protected TethysBoxPaneManager(final TethysGuiFactory pFactory) {
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
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

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
    protected void addSpacerNode(final TethysComponent pNode) {
        theNodeList.add(pNode);
    }

    /**
     * Add Node.
     * @param pNode the node
     */
    public void addNode(final TethysComponent pNode) {
        theNodeList.add(pNode);
        theGuiFactory.registerChild(this, pNode);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysComponent myNode : theNodeList) {
            myNode.setEnabled(pEnabled);
        }
    }

    /**
     * Obtain List iterator.
     * @return the iterator
     */
    protected Iterator<TethysComponent> iterator() {
        return theNodeList.iterator();
    }
}
