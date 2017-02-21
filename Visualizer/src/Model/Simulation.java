package Model;

import Helpers.FileHelper;
import org.graphstream.graph.Graph;
import parsers.RegexHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by lajtman on 13-02-2017.
 */
public class Simulation implements Serializable {
    private UPPAALModel model;
    private final ArrayList<SimulationEdgePoint> run;
    private final ArrayList<SimulationEdgePoint> reverseRun;
    private String query;

    public Simulation(UPPAALModel uppModel, String uppQuery, ArrayList<SimulationEdgePoint> points) {
        query = uppQuery;
        model = uppModel;
        run = points;
        reverseRun = new ArrayList<>(points);
        reverseRun.sort( (p1, p2) -> Double.compare(p2.getClock(), p1.getClock()));
        model.getTopology().setEdges(run);
        model.getTopology().updateGraph();
        model.getTopology().unmarkAllEdges();
    }

    public Graph getGraph() {
        return model.getTopology().getGraph();
    }

    public double getModelTimeUnit() {
        return model.getModelTimeUnit();
    }

    public ArrayList<SimulationEdgePoint> getRun() {
        return run;
    }

    public ArrayList<SimulationEdgePoint> getReverseRun() {
        return reverseRun;
    }

    public void markEdgeAtTime(Number oldValue, Number newValue) {
        double newTime = newValue.doubleValue();
        double oldTime = oldValue.doubleValue();
        if (newTime > oldTime)
        {
            run.stream().filter(p -> p.getClock() < newTime && p.getClock() > oldTime)
                    .forEach( p-> model.getTopology().handleEdgeEdit(p, p.getValue() > 0));
        }
        else
        {
            reverseRun.stream().filter(p -> p.getClock() < oldTime && p.getClock() > newTime)
                    .forEach( p-> model.getTopology().handleEdgeEdit(p, p.getValue() == 0));
        }
    }

    public void markEdgesInRealTime() {
        model.getTopology().unmarkAllEdges();
        model.getTopology().startAddingEdgesOverTime(run);
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
            //sim.model.load();
            sim.model.getTopology().setEdges(sim.run);
            sim.model.getTopology().updateGraph();
            sim.model.getTopology().unmarkAllEdges();
            return sim;
        } catch(Exception i) {
            i.printStackTrace();

        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Simulation that = (Simulation) o;

        if (!model.equals(that.model)) return false;
        if (!run.equals(that.run)) return false;
        if (!reverseRun.equals(that.reverseRun)) return false;
        return query.equals(that.query);
    }

    @Override
    public int hashCode() {
        int result = model.hashCode();
        result = 31 * result + run.hashCode();
        result = 31 * result + reverseRun.hashCode();
        result = 31 * result + query.hashCode();
        return result;
    }

}
