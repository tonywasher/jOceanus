/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.javafx;

import java.util.Arrays;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sourceforge.joceanus.jcoeus.ui.panels.CoeusMainPanel;
import net.sourceforge.joceanus.jmetis.atlas.threads.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXMenuBarManager;

/**
 * Coeus javaFX Main Panel.
 */
public class CoeusFXMainPanel
        extends CoeusMainPanel<Node, Node> {
    /**
     * The Toolkit.
     */
    private final MetisFXToolkit theToolkit;

    /**
     * The Panel.
     */
    private final BorderPane thePane;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected CoeusFXMainPanel(final MetisFXToolkit pToolkit) throws OceanusException {
        /* Initialise underlying class */
        super(pToolkit);

        /* Store the toolkit */
        theToolkit = pToolkit;

        /* Create the borderPane */
        thePane = new BorderPane();
        thePane.setTop(getMenuBar().getNode());
        thePane.setCenter(getTabs().getNode());
    }

    /**
     * Attach to stage.
     * @param pStage the stage
     */
    protected void attachToStage(final Stage pStage) {
        /* Access the GUI factory and program definitions */
        TethysFXGuiFactory myFactory = theToolkit.getGuiFactory();
        TethysProgram myApp = theToolkit.getProgramDefinitions();

        /* Create the scene */
        Scene myScene = new Scene(thePane);
        myFactory.registerScene(myScene);

        /* Configure the stage */
        myFactory.setStage(pStage);
        pStage.setTitle(myApp.getName());
        pStage.setScene(myScene);

        /* Add the icons to the frame */
        TethysIconId[] myIds = myApp.getIcons();
        Image[] myIcons = TethysFXGuiUtils.getIcons(myIds);
        pStage.getIcons().addAll(Arrays.asList(myIcons));

        /* Record startUp completion */
        theToolkit.getActiveProfile().end();
    }

    @Override
    protected TethysFXMenuBarManager getMenuBar() {
        return (TethysFXMenuBarManager) super.getMenuBar();
    }
}
