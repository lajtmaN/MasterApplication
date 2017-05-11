package View.simulation;


import Helpers.GUIHelper;
import Helpers.OptionsHelper;
import Model.OutputVariable;
import Model.Simulations;
import Model.VQ.VQParseTree;
import View.Options.*;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import parsers.VQParser.VQParse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lajtman on 08-03-2017.
 */
public class SimulationMenuController {
    final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    public Button btnAddNewVQ;
    public Accordion root;
    @FXML public TitledPane simulationPane;
    @FXML public TitledPane vqPane;

    @FXML private TextArea txtNewVQ;
    @FXML private ListView<EnableDisableSimulationOption> lstSimulationOptions;
    @FXML private ListView<EnableDisableSimulationOption> lstDisplayOptions;
    @FXML private ListView<SimulationOption> lstExportOptions;

    private Simulations currentSimulations;
    private static double tabHeight = 25;

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
            SimulationOption selected = lstExportOptions.getSelectionModel().getSelectedItem();
            if(selected !=  null){
                selected.startAction();
            }
        });
    }

    private void initializeDisplayOptions() {
        //TODO Description is not updated when the onProperty changes.
        lstDisplayOptions.setCellFactory(OptionsHelper.optionListCell());

        vqPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            lstDisplayOptions.setPrefHeight(root.getHeight()
                    - root.getPanes().size() * tabHeight
                    - txtNewVQ.getHeight()
                    - btnAddNewVQ.getHeight());
        });
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
        simulationPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                lstSimulationOptions.setPrefHeight(root.getHeight()
                        - root.getPanes().size() * tabHeight);
        });
    }

    public void addNewVQ(ActionEvent actionEvent) {
        String newVQ = txtNewVQ.getText();

        if (newVQ.length() <= 0 || txtNewVQ.getPseudoClassStates().contains(errorClass))
            return;

        VQOption option = new VQOption(currentSimulations, newVQ);
        lstDisplayOptions.getItems().add(option);
        txtNewVQ.setText("");

        option.onProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                return;

            for (EnableDisableSimulationOption eds : this.lstDisplayOptions.getItems()) {
                VQOption vqOption = null;
                if(eds instanceof VQOption)
                    vqOption = (VQOption)eds;
                if(vqOption != option && vqOption.getType() == option.getType()) {
                    eds.onProperty().set(false);
                }
            }
        });
    }

    private void validateVQ(String vqString) {
        VQParseTree parsedTree = VQParse.parse(vqString, currentSimulations.getOutputVariables());
        boolean hasError = vqString.length() > 0 && !parsedTree.isValid();
        txtNewVQ.pseudoClassStateChanged(errorClass, hasError);
        btnAddNewVQ.setDisable(hasError || vqString.length() == 0);
        if(hasError)
            txtNewVQ.setTooltip(new Tooltip(parsedTree.getParseError()));
        else
            txtNewVQ.setTooltip(null);
    }

    public void showVQHelp(ActionEvent actionEvent) {
        try {
            String vqHelpDescription = getDetailedVQHelp();

            String vqShortHelp = getShortVQHelp();

            GUIHelper.showExtendedInformation("VQ Help", vqShortHelp, vqHelpDescription);
        } catch (IOException e) {
            GUIHelper.showError("Could not retrieve VQ Help");
            e.printStackTrace();
        }
    }

    private String getShortVQHelp() throws IOException {
        return overviewOfAvailableVariables()
                + System.lineSeparator()
                + examplesOnVQ();
    }

    private String overviewOfAvailableVariables() {
        List<OutputVariable> usedOutputVars = currentSimulations.getOutputVariables().stream().filter(OutputVariable::getIsSelected).collect(Collectors.toList());
        List<OutputVariable> edgeVariables = usedOutputVars.stream().filter(OutputVariable::getIsEdgeData).collect(Collectors.toList());
        List<OutputVariable> nodeVariables = usedOutputVars.stream().filter(OutputVariable::getIsNodeData).collect(Collectors.toList());

        StringBuilder helpTextBuilder = new StringBuilder();
        helpTextBuilder.append("The following variables can be used in our VQ:").append(System.lineSeparator());

        helpTextBuilder.append("Edge variables:").append(System.lineSeparator());
        for (OutputVariable edgeVar : edgeVariables) {
            helpTextBuilder.append("\t").append(edgeVar.toString()).append(System.lineSeparator());
        }

        helpTextBuilder.append("Node variables:").append(System.lineSeparator());
        for (OutputVariable nodeVar : nodeVariables) {
            helpTextBuilder.append("\t").append(nodeVar.toString()).append(System.lineSeparator());
        }

        return helpTextBuilder.toString();
    }

    private String examplesOnVQ() throws IOException {
        File pathToShortVQHelp = new File("resources/vq_help_short.txt");
        return String.join(System.lineSeparator(), Files.readAllLines(pathToShortVQHelp.toPath()));
    }

    private String getDetailedVQHelp() throws IOException {
        File pathToVQHelp = new File("resources/vq_help.txt");
        return String.join(System.lineSeparator(), Files.readAllLines(pathToVQHelp.toPath()));
    }
}
