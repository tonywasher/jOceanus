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
public abstract class TethysFlowPaneManager<N, I>
        implements TethysNode<N>, TethysParentNode<N> {
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
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFlowPaneManager(final TethysGuiFactory<N, I> pFactory) {
        theGuiFactory = pFactory;
        theId = theGuiFactory.getNextId();
        theNodeList = new ArrayList<>();
    }

    @Override
    public Integer getId() {
        return theId;
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
