/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.dialog;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import net.sourceforge.joceanus.tethys.api.dialog.TethysUIAlert;

/**
 * javaFX Alert.
 */
public class TethysUIFXAlert
        implements TethysUIAlert {
    /**
     * The Alert.
     */
    private final Alert theAlert;

    /**
     * Constructor.
     * @param pStage the Stage
     */
    TethysUIFXAlert(final Stage pStage) {
        if (pStage == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }
        theAlert = new Alert(AlertType.CONFIRMATION);
        theAlert.initStyle(StageStyle.UTILITY);
        theAlert.initOwner(pStage);
    }

    @Override
    public void setTitle(final String pTitle) {
        theAlert.setTitle(pTitle);
    }

    @Override
    public void setMessage(final String pMessage) {
        theAlert.setContentText(pMessage);
    }

    @Override
    public boolean confirmYesNo() {
        theAlert.setAlertType(AlertType.CONFIRMATION);
        theAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        final Optional<ButtonType> myResult = theAlert.showAndWait();
        return myResult.isPresent() && myResult.get() == ButtonType.YES;
    }

    @Override
    public boolean confirmOKCancel() {
        theAlert.setAlertType(AlertType.CONFIRMATION);
        theAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        final Optional<ButtonType> myResult = theAlert.showAndWait();
        return myResult.isPresent() && myResult.get() == ButtonType.OK;
    }

    @Override
    public void showError() {
        theAlert.setAlertType(AlertType.ERROR);
        theAlert.getButtonTypes().setAll(ButtonType.OK);
        theAlert.showAndWait();
    }

    @Override
    public void showWarning() {
        theAlert.setAlertType(AlertType.WARNING);
        theAlert.getButtonTypes().setAll(ButtonType.OK);
        theAlert.showAndWait();
    }

    @Override
    public void showInfo() {
        theAlert.setAlertType(AlertType.INFORMATION);
        theAlert.getButtonTypes().setAll(ButtonType.OK);
        theAlert.showAndWait();
    }
}
