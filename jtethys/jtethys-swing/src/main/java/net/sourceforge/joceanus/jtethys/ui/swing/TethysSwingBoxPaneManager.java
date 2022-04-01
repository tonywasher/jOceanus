/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;

/**
 * Swing Box Pane Manager.
 */
public class TethysSwingBoxPaneManager
        extends TethysBoxPaneManager {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * NodeMap.
     */
    private final Map<Integer, JPanel> theNodeMap;

    /**
     * Is the panel horizontal?
     */
    private final boolean isHorizontal;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pHorizontal horizontal box true/false
     */
    protected TethysSwingBoxPaneManager(final TethysSwingGuiFactory pFactory,
                                        final boolean pHorizontal) {
        super(pFactory);
        isHorizontal = pHorizontal;
        theNodeMap = new HashMap<>();

        /* Create the panel */
        thePanel = new JPanel();
        setLayout(thePanel);

        /* Create the node */
        theNode = new TethysSwingNode(thePanel);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysComponent pNode) {
        super.addNode(pNode);
        final JPanel myPanel = createContainer(TethysSwingNode.getComponent(pNode));
        theNodeMap.put(pNode.getId(), myPanel);
        thePanel.add(myPanel);
    }

    /**
     * Create container panel.
     * @param pNode the panel
     * @return the container panel
     */
    private JPanel createContainer(final JComponent pNode) {
        final JPanel myPanel = new JPanel();
        setLayout(myPanel);
        if (isHorizontal) {
            pNode.setAlignmentY(Component.CENTER_ALIGNMENT);
        } else {
            pNode.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        myPanel.add(createStrut());
        myPanel.add(pNode);
        myPanel.add(createStrut());
        return myPanel;
    }

    /**
     * Create strut.
     * @return the strut
     */
    private Component createStrut() {
        final Integer myGap = getGap() >> 1;
        return isHorizontal
                            ? Box.createHorizontalStrut(myGap)
                            : Box.createVerticalStrut(myGap);
    }

    /**
     * Set layout.
     * @param pPanel the panel
     */
    private void setLayout(final JPanel pPanel) {
        pPanel.setLayout(new BoxLayout(pPanel, isHorizontal
                                                            ? BoxLayout.X_AXIS
                                                            : BoxLayout.Y_AXIS));
    }

    @Override
    public void setChildVisible(final TethysComponent pChild,
                                final boolean pVisible) {
        /* Obtain the required node */
        final JPanel myPanel = theNodeMap.get(pChild.getId());

        /* Set status */
        if (myPanel != null) {
            myPanel.setVisible(pVisible);
        }
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void addSpacer() {
        thePanel.add(isHorizontal
                                  ? Box.createHorizontalGlue()
                                  : Box.createVerticalGlue());
    }

    @Override
    public void addStrut() {
        final JPanel myPanel = new JPanel();
        setLayout(myPanel);
        myPanel.add(createStrut());
        myPanel.add(createStrut());
        myPanel.add(createStrut());
        myPanel.add(createStrut());
        thePanel.add(myPanel);
    }
}
