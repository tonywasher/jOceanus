/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.newfield.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.newfield.MetisFieldSetPanelPair;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabManager.TethysSwingTabItem;

/**
 * Swing FieldSet Panel Pair.
 */
public class MetisSwingFieldSetPanelPair
        extends MetisFieldSetPanelPair<JComponent, Color, Font, Icon> {
    /**
     * The Node.
     */
    private final JPanel theNode;

    /**
     * Constructor.
     * @param pAttributes the attribute set
     * @param pFormatter the data formatter
     */
    protected MetisSwingFieldSetPanelPair(final MetisSwingFieldAttributeSet pAttributes,
                                          final MetisDataFormatter pFormatter) {
        /* Initialise underlying set */
        super(pAttributes, pFormatter);

        /* Declare the main panel and tab manager */
        declareMainPanel(new MetisSwingFieldSetPanel(this));
        declareTabManager(new TethysSwingTabManager());

        /* Create the new node */
        theNode = new JPanel();
        theNode.setLayout(new GridLayout(0, 2));
        theNode.add(getMainPanel().getNode());
        theNode.add(getTabManager().getNode());
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    @Override
    public MetisSwingFieldAttributeSet getAttributeSet() {
        return (MetisSwingFieldAttributeSet) super.getAttributeSet();
    }

    @Override
    public MetisSwingFieldSetPanel getMainPanel() {
        return (MetisSwingFieldSetPanel) super.getMainPanel();
    }

    @Override
    protected TethysSwingTabManager getTabManager() {
        return (TethysSwingTabManager) super.getTabManager();
    }

    @Override
    public MetisSwingFieldSetPanel addSubPanel(final String pName) {
        /* Create a new subPanel and add to tab manager */
        MetisSwingFieldSetPanel myPanel = new MetisSwingFieldSetPanel(this);
        TethysSwingTabItem myItem = getTabManager().addTabItem(pName, myPanel.getNode());

        /* Declare the sub panel */
        declareSubPanel(myItem, myPanel);

        /* return the panel */
        return myPanel;
    }
}
