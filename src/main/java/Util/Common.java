package Util;

import norsys.netica.NeticaException;
import norsys.netica.Node;
import norsys.netica.NodeList;
import norsys.neticaEx.NodeListEx;
import norsys.netica.Net;
import java.util.Map;

public class Common {

    static int[][] getAllParentalConfigurations(Node node) throws NeticaException {
        int[][] parentStates = new int[(int) NodeListEx.sizeCartesianProduct(node.getParents())][node.getParents().size()];
        int[] parentState = new int[node.getParents().size()];

        for (int i = 0; i < parentStates.length; i++) {
            parentStates[i] = parentState.clone();
            NodeListEx.nextStates(parentState, node.getParents());
        }
        return parentStates;
    }

    static void printMatrix(float[][] matrix) {
        for (float[] row : matrix) {
            for (float element : row) {
                if (element < 0)
                    System.out.print("  *  ");
                else
                    System.out.printf("%5.2f", element);
            }
            System.out.println();
        }
    }

    public static void printNodes(NodeList nodes) throws NeticaException {
        System.out.println("Read Nodes: ");
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.getNode(i);
            System.out.println(node.getName());
        }
    }

    public static float[] doInference(Net net, Node childNode, Map<String, String> factorStates) throws NeticaException {
        for (String factor : factorStates.keySet()) {
            Node node = net.getNode(factor);
            node.finding().enterState(factorStates.get(factor));
        }
        net.compile();

        return childNode.getBeliefs();
    }
}
