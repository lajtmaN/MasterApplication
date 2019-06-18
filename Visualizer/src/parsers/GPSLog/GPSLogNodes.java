package parsers.GPSLog;

import Helpers.GoogleMapsHelper;
import Model.*;
import Model.topology.LatLng;
import Model.topology.LatLngBounds;
import Model.topology.generator.CellNode;
import exceptions.GPSLogParseException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by lajtman on 25-04-2017.
 */
public class GPSLogNodes {
    private Map<Integer, GPSLogNode> nodes = new HashMap<>();
    private LatLngBounds bounds;

    public GPSLogNodes() {}

    public void add(GPSLogEntry node) {
        if (node == null)
            return;

        if (!nodes.containsKey(node.nodeId)) {
            nodes.put(node.nodeId, new GPSLogNode(node.nodeId));
        }

        nodes.get(node.nodeId).add(node);

        invalidateBounds();
    }

    private void invalidateBounds() {
        bounds = null;
    }

    public void addRange(Collection<GPSLogEntry> nodeCollection) {
        nodeCollection.forEach(this::add);
    }

    public GPSLogNode getNode(int nodeId) {
        return nodes.get(nodeId);
    }

    public LatLngBounds getBounds() throws Exception {
        if (nodes.isEmpty())
            throw new Exception("Please add nodes before calculating the bounds");

        if (bounds == null)
            bounds = new LatLngBounds(southWestBounds(), northEastBounds());
        return bounds;
    }

    public void setEarliestTimestampAsZeroOnAllEntries() {
        if (nodes.values().isEmpty())
            return;

        int lowestTimestamp = minValueFromList(n -> n.timestamp).intValue();
        if (lowestTimestamp > 0) {
            nodes.values().forEach(nodes -> nodes.forEach(entry -> entry.timestamp -= lowestTimestamp));
        }
    }

    private LatLng southWestBounds() {
        //Lat is y
        //Lng is x

        Double south = minValueFromList(n -> n.location.lat);
        Double west = minValueFromList(n -> n.location.lng);
        return new LatLng(south, west);
    }

    private LatLng northEastBounds() {
        Double north = maxValueFromList(n -> n.location.lat);
        Double east = maxValueFromList(n -> n.location.lng);
        return new LatLng(north, east);
    }

    private Double minValueFromList(Function<GPSLogEntry, Number> mapper) {
        double minScore = Double.MAX_VALUE;
        for (GPSLogNode nodeList : nodes.values()) {
            double minInList = nodeList.min(mapper);
            if (minScore > minInList)
                minScore = minInList;
        }
        return minScore;
    }

    private Double maxValueFromList(Function<GPSLogEntry, Number> mapper) {
        double maxScore = Double.MIN_VALUE;
        for (GPSLogNode nodeList : nodes.values()) {
            double maxInList = nodeList.max(mapper);
            if (maxScore < maxInList)
                maxScore = maxInList;
        }
        return maxScore;
    }

    public UPPAALTopology generateUPPAALTopologyWithBounds(LatLngBounds latLongBounds) throws Exception {
        bounds = latLongBounds;
        List<CellNode> cellNodes = generateCellNodes();
        UPPAALTopology uppaalTopology = new UPPAALTopology(cellNodes);

        addEdgesBetweenNeighborsToUPPAALTopology(uppaalTopology);
        invalidateBounds();

        return uppaalTopology;
    }

    private List<CellNode> generateCellNodes() throws Exception {
        List<CellNode> cellNodes = new ArrayList<>();
        List<GPSLogEntry> seedEntries = nodes.values().stream().map(nodes -> nodes.first()).collect(Collectors.toList());
        for (GPSLogEntry node : seedEntries) {
            Point xy = getLocationRelativeToBounds(node);
            cellNodes.add(new CellNode(0, xy.x, xy.y));
        }

        return cellNodes;
    }

    private void addEdgesBetweenNeighborsToUPPAALTopology(UPPAALTopology uppaalTopology) {
        List<GPSLogEntry> seedEntries = nodes.values().stream().map(nodes -> nodes.first()).collect(Collectors.toList());
        seedEntries.forEach(source ->
            source.neighbors.forEach(neighbor ->
                uppaalTopology.add(new UPPAALEdge(String.valueOf(source.nodeId), String.valueOf(neighbor.neighborNodeID)))));
    }

    private Point getLocationRelativeToBounds(GPSLogEntry node) throws Exception {
        //Lat is y
        //Lng is x
        LatLng southWest = getBounds().getSouthWest();

        LatLng leftForNode = new LatLng(node.location.lat, southWest.lng);
        double x = GoogleMapsHelper.distanceBetween(node.location, leftForNode);

        LatLng belowNode = new LatLng(southWest.lat, node.location.lng);
        double y = GoogleMapsHelper.distanceBetween(node.location, belowNode);

        return new Point(x,y);
    }

    public void forEach(Consumer<? super GPSLogEntry> action) {
        nodes.values().forEach(nodeList -> nodeList.forEach(action));
    }

    public List<TemplateUpdate> getTopologyChanges() {
        List<TemplateUpdate> updates = new ArrayList<>();
        for (GPSLogNode nodeList : nodes.values()) {
            updates.addAll(nodeList.calculateTopologyChanges());
        }
        return updates;
    }

    public List<SimulationMoveNodePoint> generateSimulationMoveNodePoints() {
        List<SimulationMoveNodePoint> points = new ArrayList<>();

        for(GPSLogNode node : nodes.values()) {
            List<SimulationMoveNodePoint> currentNodePoints = new ArrayList<>();
            for(GPSLogEntry entry : node) {
                try {
                    currentNodePoints.add(generateSimulationMoveNodePoint(entry));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setPreviousValues(currentNodePoints);
            points.addAll(currentNodePoints);
        }

        return points;
    }

    private void setPreviousValues(List<SimulationMoveNodePoint> nodes) {
        nodes.sort(SimulationPoint::compareTo);
        for (int i = 0; i < nodes.size(); i++) {
            Point prevValue = nodes.get(i).getPointValue();
            int j = i-1;
            while(j >= 0) {
                if(nodes.get(i).getIdentifier().equals(nodes.get(j).getIdentifier())) {
                    prevValue = nodes.get(j).getPointValue();
                    break;
                }
                j--;
            }
            nodes.get(i).setPreviousPointValue(prevValue);
        }
    }

    private SimulationMoveNodePoint generateSimulationMoveNodePoint(GPSLogEntry n) throws Exception {
        Point p = getLocationRelativeToBounds(n);
        return new SimulationMoveNodePoint(String.valueOf(n.nodeId), n.timestamp, p, null);
    }

    /**
     * Check that node ids start from 0, and that they follow pattern 0,1,2,3...N-1. and that neighbor IDs all refer to valid node ids
     */

    public void validateNodeIds() throws GPSLogParseException {
        List<Integer> nodeIds = new ArrayList<>(nodes.keySet());
        for (int i =0; i< nodeIds.size(); i++) {
            if (!nodes.keySet().contains(i)) //node id did not exist
                throw new GPSLogParseException(String.format("Node IDs are not consequtive - Found %d nodes: with the following Ids: ",nodeIds.size()) + nodeIds.toString());
        }
        for (Integer i : nodes.keySet()) {
            for (Integer neighbor : nodes.get(i).getAllNeighborsOverTime()) {
                if(!nodes.keySet().contains(neighbor))
                    throw new GPSLogParseException(String.format("Node %d has node %d as neigbor, but the latter was not defined as a node!", i, neighbor));
            }
        }
    }
}