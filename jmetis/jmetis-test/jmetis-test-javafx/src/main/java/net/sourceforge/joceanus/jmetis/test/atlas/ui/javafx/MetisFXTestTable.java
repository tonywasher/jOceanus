/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.test.atlas.ui.javafx;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.test.atlas.ui.MetisTestDataTable;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXNode;

/**
 * Table Tester.
 */
public class MetisFXTestTable extends Application {
    /**
     * Toolkit.
     */
    private final MetisFXToolkit theToolkit;

    /**
     * GUI factory.
     */
    private final TethysFXGuiFactory theGuiFactory;

    /**
     * The Table.
     */
    private final MetisTestDataTable theTable;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisFXTestTable() throws OceanusException {
        /* Create toolkit */
        theToolkit = new MetisFXToolkit();

        /* Access components */
        theGuiFactory = theToolkit.getGuiFactory();

        /* Create table */
        theTable = new MetisTestDataTable(theToolkit);
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage pStage) {
        /* Create parent panel */
        final TethysFXBorderPaneManager myMainPanel = theGuiFactory.newBorderPane();
        myMainPanel.setCentre(theTable);

        /* Create scene */
        final Scene myScene = new Scene((Region) TethysFXNode.getNode(myMainPanel));
        pStage.setTitle("MetisFXTable Demo");
        pStage.setScene(myScene);

        /* Configure factory */
        theGuiFactory.setStage(pStage);
        theGuiFactory.registerScene(myScene);
        pStage.show();
    }
}
