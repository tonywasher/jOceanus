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
package net.sourceforge.joceanus.jmetis.newfield.javafx;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.newfield.MetisFieldSetPanelPair;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTabManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXTabManager.TethysFXTabItem;

/**
 * JavaFX FieldSet Panel Pair.
 */
public class MetisFXFieldSetPanelPair
        extends MetisFieldSetPanelPair<Node, Color, Font, Node> {
    /**
     * 50% width.
     */
    private static final int COLUMN_WEIGHT = 50;

    /**
     * The Node.
     */
    private final GridPane theNode;

    /**
     * Constructor.
     * @param pAttributes the attribute set
     * @param pFormatter the data formatter
     */
    protected MetisFXFieldSetPanelPair(final MetisFXFieldAttributeSet pAttributes,
                                       final MetisDataFormatter pFormatter) {
        /* Initialise underlying set */
        super(pAttributes, pFormatter);

        /* Declare the main panel and tab manager */
        declareMainPanel(new MetisFXFieldSetPanel(this));
        declareTabManager(new TethysFXTabManager());

        /* Create the new node */
        theNode = new GridPane();
        theNode.add(getMainPanel().getNode(), 0, 0);
        theNode.add(getTabManager().getNode(), 1, 0);

        /* Set equal weights to the columns */
        ColumnConstraints myMainCol = new ColumnConstraints();
        myMainCol.setPercentWidth(COLUMN_WEIGHT);
        ColumnConstraints myTabCol = new ColumnConstraints();
        myTabCol.setPercentWidth(COLUMN_WEIGHT);
        theNode.getColumnConstraints().addAll(myMainCol, myTabCol);
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public MetisFXFieldAttributeSet getAttributeSet() {
        return (MetisFXFieldAttributeSet) super.getAttributeSet();
    }

    @Override
    public MetisFXFieldSetPanel getMainPanel() {
        return (MetisFXFieldSetPanel) super.getMainPanel();
    }

    @Override
    protected TethysFXTabManager getTabManager() {
        return (TethysFXTabManager) super.getTabManager();
    }

    @Override
    public MetisFXFieldSetPanel addSubPanel(final String pName) {
        /* Create a new subPanel and add to tab manager */
        MetisFXFieldSetPanel myPanel = new MetisFXFieldSetPanel(this);
        TethysFXTabItem myItem = getTabManager().addTabItem(pName, myPanel.getNode());

        /* Declare the sub panel */
        declareSubPanel(myItem, myPanel);

        /* return the panel */
        return myPanel;
    }
}
