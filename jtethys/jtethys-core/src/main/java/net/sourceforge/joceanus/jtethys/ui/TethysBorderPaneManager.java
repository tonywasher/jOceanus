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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory.TethysParentNode;

/**
 * Border Pane Manager.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public abstract class TethysBorderPaneManager<N, I>
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
     * Id Map.
     */
    private final Map<Integer, TethysBorderLocation> theIdMap;

    /**
     * Node Map.
     */
    private final Map<TethysBorderLocation, TethysNode<N>> theNodeMap;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysBorderPaneManager(final TethysGuiFactory<N, I> pFactory) {
        theGuiFactory = pFactory;
        theId = theGuiFactory.getNextId();
        theIdMap = new HashMap<>();
        theNodeMap = new EnumMap<>(TethysBorderLocation.class);
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Set the Centre Node.
     * @param pNode the node
     */
    public void setCentre(final TethysNode<N> pNode) {
        setNodeLocation(TethysBorderLocation.CENTRE, pNode);
    }

    /**
     * Set the North Node.
     * @param pNode the node
     */
    public void setNorth(final TethysNode<N> pNode) {
        setNodeLocation(TethysBorderLocation.NORTH, pNode);
    }

    /**
     * Set the South Node.
     * @param pNode the node
     */
    public void setSouth(final TethysNode<N> pNode) {
        setNodeLocation(TethysBorderLocation.SOUTH, pNode);
    }

    /**
     * Set the West Node.
     * @param pNode the node
     */
    public void setWest(final TethysNode<N> pNode) {
        setNodeLocation(TethysBorderLocation.WEST, pNode);
    }

    /**
     * Set the East Node.
     * @param pNode the node
     */
    public void setEast(final TethysNode<N> pNode) {
        setNodeLocation(TethysBorderLocation.EAST, pNode);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysNode<N> myNode : theNodeMap.values()) {
            myNode.setEnabled(pEnabled);
        }
    }

    @Override
    public void setChildVisible(final TethysNode<N> pChild,
                                final boolean pVisible) {
        /* Look for location */
        TethysBorderLocation myLoc = theIdMap.get(pChild.getId());
        if (myLoc != null) {
            setLocationVisibility(myLoc, pVisible);
        }
    }

    /**
     * Set Visible at location.
     * @param pLocation the location
     * @param pVisible true/false
     */
    protected abstract void setLocationVisibility(final TethysBorderLocation pLocation,
                                                  final boolean pVisible);

    /**
     * Set node at location.
     * @param pLocation the location
     * @param pNode the Node
     */
    protected void setNodeLocation(final TethysBorderLocation pLocation,
                                   final TethysNode<N> pNode) {
        /* Look for existing node */
        TethysNode<N> myNode = theNodeMap.get(pLocation);
        if (myNode != null) {
            /* Remove node details */
            removeNode(myNode);

            /* DeRegister the child */
            theGuiFactory.deRegisterChild(myNode);
        }

        /* If we have a node */
        if (pNode != null) {
            /* Update maps */
            theIdMap.put(pNode.getId(), pLocation);
            theNodeMap.put(pLocation, pNode);

            /* Register the child */
            theGuiFactory.registerChild(this, pNode);

            /* else clear the location */
        } else {
            theNodeMap.remove(pLocation);
        }
    }

    /**
     * Obtain node for location.
     * @param pLocation the location
     * @return the node or null
     */
    protected TethysNode<N> getNodeForLocation(final TethysBorderLocation pLocation) {
        return theNodeMap.get(pLocation);
    }

    /**
     * Remove node.
     * @param pNode the node to remove
     */
    protected void removeNode(final TethysNode<N> pNode) {
        theIdMap.remove(pNode.getId());
    }

    /**
     * Node locations.
     */
    protected enum TethysBorderLocation {
        /**
         * Centre.
         */
        CENTRE,

        /**
         * North.
         */
        NORTH,

        /**
         * South.
         */
        SOUTH,

        /**
         * West.
         */
        WEST,

        /**
         * East.
         */
        EAST;
    }
}
