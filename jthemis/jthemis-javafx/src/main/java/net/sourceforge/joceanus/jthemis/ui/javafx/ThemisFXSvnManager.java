/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui.javafx;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXMenuBarManager;
import net.sourceforge.joceanus.jthemis.ui.ThemisSvnManager;

/**
 * Top level javaFX SvnManager window.
 */
public class ThemisFXSvnManager
        extends ThemisSvnManager<Node, Node> {
    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected ThemisFXSvnManager(final MetisFXToolkit pToolkit) throws OceanusException {
        /* Initialise underlying class */
        super(pToolkit);

        /* Create the borderPane */
        BorderPane myBorderPane = new BorderPane();
        myBorderPane.setTop(getMenuBar().getNode());
        myBorderPane.setCenter(getTabs().getNode());

        /* Create the scene and attach to the stage */
        Stage myStage = pToolkit.getGuiFactory().getStage();
        Scene myScene = new Scene(myBorderPane);
        pToolkit.getGuiFactory().registerScene(myScene);
        myStage.setScene(myScene);

        /* Close application properly */
        myStage.setOnCloseRequest(e -> handleWindowClosed());
    }

    @Override
    protected TethysFXMenuBarManager getMenuBar() {
        return (TethysFXMenuBarManager) super.getMenuBar();
    }
}
