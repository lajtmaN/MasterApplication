package parsers.GPSLog;

import Helpers.Pair;
import Model.TemplateUpdate;

import java.util.*;
import java.util.function.Function;

/**
 * Created by lajtman on 01-05-2017.
 */
public class GPSLogNode extends ArrayList<GPSLogEntry> {

    private int nodeId;
    private boolean isSorted = true;

    public GPSLogNode(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public boolean add(GPSLogEntry gpsLogEntry) {
        if (nodeId != gpsLogEntry.nodeId)
            throw new IllegalArgumentException("Cannot add that GPS Log Entry. The node id does not match.");

        isSorted = false;
        return super.add(gpsLogEntry);
    }

    public double min(Function<GPSLogEntry, Double> mapper) {
        double minScore = Double.MAX_VALUE;
        for (GPSLogEntry node : this) {
            if (mapper.apply(node) < minScore)
                minScore = mapper.apply(node);
        }
        return minScore;
    }

    public double max(Function<GPSLogEntry, Double> mapper) {
        double maxScore = Double.MIN_VALUE;
        for (GPSLogEntry node : this) {
            if (mapper.apply(node) > maxScore)
                maxScore = mapper.apply(node);
        }
        return maxScore;
    }

    @Override
    public void sort(Comparator<? super GPSLogEntry> c) {
        if (!isSorted)
            super.sort(c);
        isSorted = true;
    }

    public GPSLogEntry first() {
        this.sort(GPSLogEntry::compareTo);
        return this.get(0);
    }

    public List<TemplateUpdate> calculateTopologyChanges() {
        this.sort(GPSLogEntry::compareTo);

        List<TemplateUpdate> updates = new ArrayList<>();
        List<Integer> currentNeighbors = new ArrayList<>();
        for (GPSLogEntry entry : this) {
            List<Pair<Integer, String>> neighborUpdates = newNeighborConnections(entry, currentNeighbors);
            for (Pair<Integer, String> p : neighborUpdates) {
                String variableName = String.format("CONFIG_connected[%d][%d]", this.nodeId, p.getFirst());
                updates.add(new TemplateUpdate(variableName, p.getSecond(), entry.timestamp));
            }
            currentNeighbors = entry.neighbors;
        }
        return updates;
    }

    private List<Pair<Integer, String>> newNeighborConnections(GPSLogEntry entry, List<Integer> lastTimeNeighbors) {
        List<Pair<Integer, String>> neighborsToUpdate = new ArrayList<>();

        for(int curNeighbor : entry.neighbors) {
            if (!lastTimeNeighbors.contains(curNeighbor))
                neighborsToUpdate.add(new Pair<>(curNeighbor, "1"));
        }
        for (int lastNeighbor : lastTimeNeighbors) {
            if (!entry.neighbors.contains(lastNeighbor))
                neighborsToUpdate.add(new Pair<>(lastNeighbor, "0"));
        }

        return neighborsToUpdate;
    }
}