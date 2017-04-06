package Model;

import Model.topology.generator.CellNode;
import View.MainWindowController;
import View.simulation.SimulationMenuController;
import javafx.scene.image.Image;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rasmu on 09/02/2017.
 */
public class UPPAALTopology extends ArrayList<UPPAALEdge> implements Serializable {
    private int _numberOfNodes;
    private transient Graph _graphInstance;
    private List<CellNode> nodes;
    private String backgroundFilePath;

    public UPPAALTopology(Graph graph) {
        _graphInstance = graph;
        initializeGraph();
        this._numberOfNodes = graph.getNodeCount();
        for(Edge edge : _graphInstance.getEachEdge()){
            add(new UPPAALEdge(edge.getSourceNode().getId(), edge.getTargetNode().getId()));
        }
    }

    public UPPAALTopology(int numberOfNodes) {
        _numberOfNodes = numberOfNodes;
    }

    public UPPAALTopology(List<CellNode> cellNodes, String backgroundFilePath) {
        this(cellNodes.size());
        nodes = cellNodes;
        this.backgroundFilePath = backgroundFilePath;
    }

    public UPPAALTopology() {}

    public int getNumberOfNodes() {
        return _numberOfNodes;
    }

    public void setNumberOfNodes(int _numberOfNodes) {
        this._numberOfNodes = _numberOfNodes;
    }

    public void updateGraph() {
        for (int i = 0; i < getNumberOfNodes(); i++) {
            addNode(i);
        }
        for(UPPAALEdge s : this){
            SimulationEdgePoint sep = new SimulationEdgePoint(0, s.getSource(), s.getDestination(), 1);
            addEdge(sep);
        }
    }

    private Edge addEdge(SimulationEdgePoint s){
        Edge e = getGraph().getEdge(s.getIdentifier());
        if(e != null)
            return e;

        Edge newEdge = getGraph(false).addEdge(s.getIdentifier(), s.getSource(), s.getDestination(), true);
        return newEdge;
    }
    private Edge removeEdge(SimulationEdgePoint s) {
        Graph g = getGraph(false);
        Edge e = g.getEdge(s.getIdentifier());
        if(e == null){
            return e;
        }
        return g.removeEdge(e);
    }

    public void updateVariableGradient(SimulationPoint s, double min, double max) {
        switch (s.getType()) {
            case EdgePoint:
                handleEdgeEdit((SimulationEdgePoint) s, min, max);
                break;
            case NodePoint:
                handleNodeEdit((SimulationNodePoint) s, min, max);
                break;
        }
    }

    private void handleNodeEdit(SimulationNodePoint point, double min, double max) {
        Node node = getGraph().getNode(point.getNodeId());
        if (node == null)
            return;

        //TODO: Consider negative values
        double diff = max - min;
        if(diff != 0) {
            double gradientValue = point.getValue() / diff;
            if(gradientValue >= 1.0)
                markGradientNode(node, 1.0);
            else if(gradientValue <= 0.0)
                markGradientNode(node, 0.0);
            else
                markGradientNode(node, gradientValue);
        }
    }

    private void markGradientNode(Node node, double gradientValue) {
        node.setAttribute("ui.color", gradientValue);
    }

    private void handleEdgeEdit(SimulationEdgePoint sp, double min, double max) {
        Edge edge = getGraph().getEdge(sp.getEdgeIdentifier());
        if (edge == null)
            return;

        //TODO: Consider negative values
        double diff = max - min;
        if(diff != 0) {
            double gradientValue = sp.getValue() / diff;
            if(gradientValue >= 1.0)
                markGradientEdge(edge, 1.0);
            else if(gradientValue <= 0.0)
                markGradientEdge(edge, 0.0);
            else
                markGradientEdge(edge, gradientValue);
        }
    }

    private void markGradientEdge(Edge edge, double gradientValue) {
        edge.setAttribute("ui.color", gradientValue);
    }

    private void markEdge(Edge edge) {
        edge.setAttribute("ui.color", 1.0);
    }

    protected void unmarkAllEdges() {
        for (Edge e : getGraph().getEdgeSet())
            unmarkEdge(e);
    }

    private void unmarkEdge(Edge edge) {
        edge.setAttribute("ui.color", 0.0);
    }

    public void unmarkAllNodes() {
        for(Node n : getGraph().getNodeSet())
            unmarkNode(n);
    }

    private Node addNode(Integer id){
        Node graphNode = getGraph(false).addNode(String.valueOf(id));
        showLabelOnNode(graphNode, String.valueOf(id));
        if (nodesHasSpecificLocations()) {
            graphNode.addAttribute("layout.frozen");
            graphNode.setAttribute("xyz", nodes.get(id).getX(), nodes.get(id).getY(), 0);
        }
        return graphNode;
    }

    private void showLabelOnNode(Node n, String nodeName) {
        n.addAttribute("ui.label", nodeName);
    }

    private void markNode(Node node) {
        node.setAttribute("ui.color", 1.0);
    }

    private void unmarkNode(Node node) {
        node.setAttribute("ui.color", 0.0);
    }

    public Graph getGraph() { return getGraph(false); }
    public Graph getGraph(Boolean updateGraph) {
        if (_graphInstance == null) {
            _graphInstance = new MultiGraph("Topology with " + _numberOfNodes + " nodes");
            initializeGraph();
        }

        if (updateGraph)
            updateGraph();

        return _graphInstance;
    }

    public String getBackgroundFilePath() {
        return backgroundFilePath;
    }

    public Image getBackgroundImage() throws IOException {
        return new Image(new File(backgroundFilePath).toURI().toString());
    }

    public boolean nodesHasSpecificLocations() {
        return nodes != null;
    }

    /**
     * If the topology has been generated with nodes on a special location, they will be returned in this method
     * @return List of Nodes which has range, x, y, all measured in meters
     */
    public List<CellNode> getNodesWithSpecificLocation() {
        return nodes;
    }

    private void initializeGraph() {
        _graphInstance.setStrict(false);
        _graphInstance.addAttribute("ui.stylesheet", styleSheet);
        _graphInstance.setAutoCreate(true);
        _graphInstance.addAttribute("ui.quality");
        _graphInstance.addAttribute("ui.antialias");
    }

    protected String styleSheet =
            "graph {" +
                    "fill-color: rgba(0,0,0,0);" +
                    "padding: 0,0;" +
                    "}" +
            "node {" +
                    "   fill-mode: dyn-plain;" +
                    "   fill-color: black, red;" +
                    "   text-alignment: under;" +
                    "   text-background-mode: rounded-box;" +
                    "}" +
            "edge {" +
                    "fill-mode: dyn-plain;" +
                    "fill-color: white, red;" +
                    "}";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UPPAALTopology that = (UPPAALTopology) o;

        if (_numberOfNodes != that._numberOfNodes) return false;
        return nodes != null ? nodes.equals(that.nodes) : that.nodes == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + _numberOfNodes;
        result = 31 * result + (nodes != null ? nodes.hashCode() : 0);
        return result;
    }
}