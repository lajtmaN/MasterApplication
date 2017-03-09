package Model;

import Helpers.FileHelper;
import Helpers.GUIHelper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.graphstream.graph.Graph;
import parsers.RegexHelper;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by lajtman on 13-02-2017.
 */
public class Simulation implements Serializable {
    private UPPAALModel model;
    private final List<? extends SimulationPoint> run;
    private String query;
    private int currentSimulationIndex = 0;

    public Simulation(UPPAALModel uppModel, String uppQuery, List<SimulationPoint> points) {
        query = uppQuery;
        model = uppModel;
        model.getTopology().updateGraph();
        model.getTopology().unmarkAllEdges();

        if (getModelTimeUnit() != 1) {
            for (SimulationPoint p : points) {
                p.setClock(p.getClock() * getModelTimeUnit());
            }
        }

        run = points;
    }

    public Graph getGraph() {
        return getTopology().getGraph();
    }

    public UPPAALTopology getTopology() { return model.getTopology(); }

    public double getModelTimeUnit() {
        return model.getModelTimeUnit();
    }

    public List<? extends SimulationPoint> getRun() {
        return run;
    }

    public void markGraphAtTime(Number oldTimeValue, Number newTimeValue, GridPane globalVarGridPane) {
        double newTime = newTimeValue.doubleValue();
        double oldTime = oldTimeValue.doubleValue();
        if (newTime > oldTime)
            markGraphForward(newTime, oldTime, globalVarGridPane);
        else
            markGraphBackwards(newTime, oldTime, globalVarGridPane);
    }

    private void markGraphForward(double newTimeValue, double oldTime, GridPane globalVarGridPane) {
        SimulationPoint sp;
        while((sp = run.get(currentSimulationIndex)).getClock() <= newTimeValue) {
            if(sp.getClock() >= oldTime) {
                if(sp.getType() == SimulationPoint.SimulationPointType.Variable) {
                    updateGlobalVariableInGridPane(sp.getIdentifier(), String.valueOf(sp.getValue()), globalVarGridPane);
                }
                else
                    model.getTopology().handleUpdate(sp, sp.getValue() > 0);
            }
            if (currentSimulationIndex + 1 >= run.size())
                break;
            if (run.get(currentSimulationIndex + 1).getClock() <= newTimeValue)
                currentSimulationIndex++;
            else break;
        }
    }

    private void markGraphBackwards(double newTimeValue, double oldTime, GridPane globalVarGridPane) {
        SimulationPoint sp;
        while((sp = run.get(currentSimulationIndex)).getClock() >= newTimeValue) {
            if(sp.getClock() <= oldTime) {
                if(sp.getType() == SimulationPoint.SimulationPointType.Variable) {
                    updateGlobalVariableInGridPane(sp.getIdentifier(), String.valueOf(sp.getPreviousValue()), globalVarGridPane);
                }
                else
                    model.getTopology().handleUpdate(sp, sp.getPreviousValue() > 0);
            }
            if (currentSimulationIndex - 1 < 0)
                break;
            if (run.get(currentSimulationIndex - 1).getClock() >= newTimeValue)
                currentSimulationIndex--;
            else break;
        }
    }

    public int queryTimeBound() {
        return Integer.parseInt(RegexHelper.getFirstMatchedValueFromRegex("\\[<=(\\d+)\\]", query));
    }

    public void save(String fileName){
        try {
            File file = new File(FileHelper.simulationFileName(fileName));
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file,false);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

    public static Simulation load(String fileName) {
        return load(new File(FileHelper.simulationFileName(fileName)));
    }
    public static Simulation load(File file){
        if (!Objects.equals(FileHelper.getExtension(file.getName()), ".sim"))
            throw new IllegalArgumentException("Only files with named *.sim can be loaded");

        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Simulation sim = (Simulation) in.readObject();
            in.close();
            fileIn.close();
            sim.model.getTopology().updateGraph();
            sim.model.getTopology().unmarkAllEdges();
            return sim;
        } catch(InvalidClassException i) {
            GUIHelper.showAlert(Alert.AlertType.ERROR, "The simulation that you tried to load was created by an older version of this program.");
        } catch (Exception ignored) {

        }
        return null;
    }

    private void addGlobalVariableToGridPane(String name, String value, GridPane globalVarGridPane) {
        int nrRows = globalVarGridPane.getChildren().size() / 2;
        Label labelName = new Label(name);
        Label labelValue = new Label(value);
        labelName.setPadding(new Insets(0,10, 0, 0));

        globalVarGridPane.add(labelName, 0, nrRows);
        globalVarGridPane.add(labelValue, 1, nrRows);
    }

    private void updateGlobalVariableInGridPane(String name, String value, GridPane globalVarGridPane) {
        boolean foundLabel = false;
        for(Node n : globalVarGridPane.getChildren()){
            Label label = (Label) n;
            if(foundLabel) {
                label.setText(value);
                break;
            }
            if(label.getText().equals(name)) {
                foundLabel = true;
            }
        }
        if(!foundLabel) {
            addGlobalVariableToGridPane(name, value, globalVarGridPane);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Simulation that = (Simulation) o;

        if (!model.equals(that.model)) return false;
        if (!run.equals(that.run)) return false;
        return query.equals(that.query);
    }

    @Override
    public int hashCode() {
        int result = model.hashCode();
        result = 31 * result + run.hashCode();
        result = 31 * result + query.hashCode();
        return result;
    }

}
