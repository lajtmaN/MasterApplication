package Helpers;

import Model.UPPAALTopology;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

/**
 * Created by batto on 13-Feb-17.
 */
public class ConnectedGraphGenerator {
    public static void matrix(int size) {
        String s = "int CONFIG_connected[CONFIG_NR_NODES][CONFIG_NR_NODES] = {";
        for(int i= 0 ; i < size; i++) {
            s += "\n{";
            for(int j= 0 ; j < size; j++) {
                if(square(i,j, (int)Math.sqrt(size))) {
                    s += "1";
                } else {
                    s += "0";
                }
                if(j < size-1)
                    s+=",";
            }
            s += "}";
            if(i < size-1) {
                s += ",";
            }
        }
        s+= "\n};";
        System.out.println(s);
    }

    public static boolean square(int my_id, int other_id, int NR_NODES_SQR_ROOT) {
        if(my_id % NR_NODES_SQR_ROOT == 0)
            return my_id-other_id == -1 || my_id-other_id == NR_NODES_SQR_ROOT || my_id-other_id == - NR_NODES_SQR_ROOT;
        if(my_id % NR_NODES_SQR_ROOT == NR_NODES_SQR_ROOT - 1)
            return my_id-other_id == 1 || my_id-other_id == NR_NODES_SQR_ROOT || my_id-other_id == - NR_NODES_SQR_ROOT;
        return my_id-other_id == -1 || my_id-other_id == 1 || my_id-other_id == NR_NODES_SQR_ROOT || my_id-other_id == - NR_NODES_SQR_ROOT;
    }

    public static UPPAALTopology generateRandomTopology(int numberOfNodes) {
        //TODO generate trivial topology for one and two nodes
        if (numberOfNodes < 3)
            throw new IllegalArgumentException("Number of nodes should be at least 3");

        Graph graph = new MultiGraph("Random graph with " + numberOfNodes + " nodes");
        Generator gen = new DorogovtsevMendesGenerator();
        gen.addSink(graph);
        gen.begin();
        for(int i=3; i < numberOfNodes; i++)
            gen.nextEvents();
        gen.end();
        return new UPPAALTopology(graph);
    }

    public static UPPAALTopology generateSquareTopology(int squareSize) throws InterruptedException {
        int numEdgesToAddOnEachRow = squareSize-1;

        Graph graph = new MultiGraph("Random graph with " + numEdgesToAddOnEachRow + " nodes");
        Generator gen = new SquareGraphGenerator( true, squareSize);
        gen.addSink(graph);
        gen.begin();

        for(int i=0; i < numEdgesToAddOnEachRow; i++)
            gen.nextEvents();
        gen.end();

        int initEdgeCount = graph.getEdgeCount();
        for (int i = 0; i < initEdgeCount; i++) {
            Edge curEdge = graph.getEdge(i);
            graph.addEdge(String.valueOf(graph.getEdgeCount()), (Node)curEdge.getTargetNode(),
                    (Node)curEdge.getSourceNode(), true);
        }
        return new UPPAALTopology(graph);
    }
}
