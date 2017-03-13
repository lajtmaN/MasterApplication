package Helpers;

import javafx.stage.FileChooser;


/**
 * Created by lajtman on 13-03-2017.
 */
public class ExtensionFilters {
    public static FileChooser.ExtensionFilter UPPAALModelExtensionFilter = new FileChooser.ExtensionFilter("UPPAAL Model", "*.xml");
    public static FileChooser.ExtensionFilter SimulationExtensionFilter = new FileChooser.ExtensionFilter("Simulation", "*.sim");
    public static FileChooser.ExtensionFilter TopologyExtensionFilter = new FileChooser.ExtensionFilter("Topology", "*.topo");
    public static FileChooser.ExtensionFilter VerifytaExtensionFilter = new FileChooser.ExtensionFilter("Verifyta executable file", "verifyta.exe");
}
