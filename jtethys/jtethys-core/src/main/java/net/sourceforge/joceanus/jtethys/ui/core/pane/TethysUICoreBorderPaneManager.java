/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.pane;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreFactory.TethysUIParentComponent;

/**
 * Core Border Pane Manager.
 */
public abstract class TethysUICoreBorderPaneManager
        extends TethysUICoreComponent
        implements TethysUIBorderPaneManager, TethysUIParentComponent {
    /**
     * Border Gap default.
     */
    private static final int BORDER_GAP = 4;

    /**
     * The Factory.
     */
    private final TethysUICoreFactory theFactory;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Horizontal Gap.
     */
    private Integer theHGap;

    /**
     * The Vertical Gap.
     */
    private Integer theVGap;

    /**
     * Id Map.
     */
    private final Map<Integer, TethysUIBorderLocation> theIdMap;

    /**
     * Node Map.
     */
    private final Map<TethysUIBorderLocation, TethysUIComponent> theNodeMap;

    /**
     * Constructor.
     * @param pFactory the Factory
     */
    protected TethysUICoreBorderPaneManager(final TethysUICoreFactory pFactory) {
        theFactory = pFactory;
        theId = theFactory.getNextId();
        theIdMap = new HashMap<>();
        theNodeMap = new EnumMap<>(TethysUIBorderLocation.class);
        theHGap = BORDER_GAP;
        theVGap = BORDER_GAP;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public Integer getHGap() {
        return theHGap;
    }

    @Override
    public Integer getVGap() {
        return theVGap;
    }

    @Override
    public void setHGap(final Integer pGap) {
        theHGap = pGap;
    }

    @Override
    public void setVGap(final Integer pGap) {
        theVGap = pGap;
    }

    @Override
    public void setCentre(final TethysUIComponent pNode) {
        setNodeLocation(TethysUIBorderLocation.CENTRE, pNode);
    }

    @Override
    public void setNorth(final TethysUIComponent pNode) {
        setNodeLocation(TethysUIBorderLocation.NORTH, pNode);
    }

    @Override
    public void setSouth(final TethysUIComponent pNode) {
        setNodeLocation(TethysUIBorderLocation.SOUTH, pNode);
    }

    @Override
    public void setWest(final TethysUIComponent pNode) {
        setNodeLocation(TethysUIBorderLocation.WEST, pNode);
    }

    @Override
    public void setEast(final TethysUIComponent pNode) {
        setNodeLocation(TethysUIBorderLocation.EAST, pNode);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (final TethysUIComponent myNode : theNodeMap.values()) {
            myNode.setEnabled(pEnabled);
        }
    }

    @Override
    public void setChildVisible(final TethysUIComponent pChild,
                                final boolean pVisible) {
        /* Look for location */
        final TethysUIBorderLocation myLoc = theIdMap.get(pChild.getId());
        if (myLoc != null) {
            setLocationVisibility(myLoc, pVisible);
        }
    }

    /**
     * Set Visible at location.
     * @param pLocation the location
     * @param pVisible true/false
     */
    protected abstract void setLocationVisibility(TethysUIBorderLocation pLocation,
                                                  boolean pVisible);

    /**
     * Set node at location.
     * @param pLocation the location
     * @param pNode the Node
     */
    protected void setNodeLocation(final TethysUIBorderLocation pLocation,
                                   final TethysUIComponent pNode) {
        /* Look for existing node */
        final TethysUIComponent myNode = theNodeMap.get(pLocation);
        if (myNode != null) {
            /* Remove node details */
            removeNode(myNode);

            /* DeRegister the child */
            theFactory.deRegisterChild(myNode);
        }

        /* If we have a node */
        if (pNode != null) {
            /* Update maps */
            theIdMap.put(pNode.getId(), pLocation);
            theNodeMap.put(pLocation, pNode);

            /* Register the child */
            theFactory.registerChild(this, pNode);

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
    protected TethysUIComponent getNodeForLocation(final TethysUIBorderLocation pLocation) {
        return theNodeMap.get(pLocation);
    }

    /**
     * Remove node.
     * @param pNode the node to remove
     */
    protected void removeNode(final TethysUIComponent pNode) {
        theIdMap.remove(pNode.getId());
    }

    /**
     * Node locations.
     */
    protected enum TethysUIBorderLocation {
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
