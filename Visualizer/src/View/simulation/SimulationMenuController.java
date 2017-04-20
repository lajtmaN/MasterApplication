package View.simulation;


import Helpers.GUIHelper;
import Helpers.OptionsHelper;
import Model.OutputVariable;
import Model.Simulations;
import View.DoubleTextField;
import View.Options.*;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import parsers.VQParser.VQParse;

import java.util.Arrays;

/**
 * Created by lajtman on 08-03-2017.
 */
public class SimulationMenuController {
    final PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    @FXML private TextArea txtNewVQ;
    @FXML private ListView<EnableDisableSimulationOption> lstSimulationOptions;
    @FXML private ListView<EnableDisableSimulationOption> lstDisplayOptions;
    @FXML private ListView<SimulationOption> lstExportOptions;

    private Simulations currentSimulations;

    public void loadWithSimulation(Simulations currentSimulations) {
        this.currentSimulations = currentSimulations;
        addOptionsToListViews();
        initializeExportOptions();
        initializeDisplayOptions();
        initializeSimulationOptions();
        setupVQValidationParser();
    }

    private void setupVQValidationParser() {
        txtNewVQ.textProperty().addListener((observable, oldValue, newValue) -> {
            validateVQ(newValue);
        });
        txtNewVQ.focusedProperty().addListener((observable, oldValue, newValue) -> {
            validateVQ(txtNewVQ.getText());
        });
        validateVQ("");
    }

    private void addOptionsToListViews() {
        lstExportOptions.getItems().add(new ExportTopologyOption(currentSimulations));

        for (int i = 0; i < currentSimulations.getNumberOfSimulations(); i++) {
            lstSimulationOptions.getItems().add(new ShowHideSimulationOption(currentSimulations, i));
        }
    }

    private void initializeExportOptions() {
        lstExportOptions.setCellFactory(param -> new SimulationOptionCell());
        lstExportOptions.setOnMouseClicked(p -> {
            lstExportOptions.getSelectionModel().getSelectedItem().startAction();
        });
    }

    private void initializeDisplayOptions() {
        //TODO Description is not updated when the onProperty changes.
        lstDisplayOptions.setCellFactory(OptionsHelper.optionListCell());
    }

    private void initializeSimulationOptions() {
        lstSimulationOptions.setCellFactory(OptionsHelper.optionListCell());

        for(EnableDisableSimulationOption o : lstSimulationOptions.getItems()) {
            o.onProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue) {
                    for(EnableDisableSimulationOption edso : lstSimulationOptions.getItems()) {
                        if(edso != o) {
                            edso.onProperty().setValue(false);
                        }
                    }
                }
            });
        }
    }

    public void addNewVQ(ActionEvent actionEvent) {
        String newVQ = txtNewVQ.getText();

        if (newVQ.length() > 0 && !txtNewVQ.getPseudoClassStates().contains(errorClass)) {
            VQOption option = new VQOption(currentSimulations, newVQ);
            lstDisplayOptions.getItems().add(option);
            txtNewVQ.setText("");

            option.onProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue) {
                    for (EnableDisableSimulationOption eds : this.lstDisplayOptions.getItems()) {
                        VQOption vqOption = null;
                        if(eds instanceof VQOption)
                            vqOption = (VQOption)eds;
                        if(vqOption != option && vqOption.getType() == option.getType()) {
                            eds.onProperty().set(false);
                        }
                    }
                }
            });
        }
    }

    private void validateVQ(String vqString) {
        txtNewVQ.pseudoClassStateChanged(errorClass, vqString.length() > 0 && !validVQ(vqString));
    }

    private boolean validVQ(String vqString) {
        return VQParse.validVQ(vqString, currentSimulations.getOutputVariables());
    }
}
