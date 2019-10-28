package Util;

import unbbayes.io.BaseIO;
import unbbayes.io.DneIO;
import unbbayes.io.NetIO;
import unbbayes.prs.Node;
import unbbayes.prs.bn.JunctionTreeAlgorithm;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.util.extension.bn.inference.IInferenceAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WSAUnbb {

    public static void fillChildConfigurations(ProbabilisticNetwork net, Node childNode, float[] weights) throws IOException {
        //IInferenceAlgorithm algorithm = new JunctionTreeAlgorithm();
        //algorithm.setNetwork(net);
        //algorithm.run();

        float[][] elicitedConfigsChild = new float[sizeCartesianProduct(childNode.getParents())]
                [childNode.getStatesSize()];
        float[][] filledConfigsChild = new float[sizeCartesianProduct(childNode.getParents())]
                [childNode.getStatesSize()];
        int[][] parentalConfigurations = getAllParentalConfigurations(childNode);
        getNodeAllProbs((ProbabilisticNode) childNode, elicitedConfigsChild);


        WSAUnbb.fillChildConfigurations(elicitedConfigsChild, filledConfigsChild, parentalConfigurations, weights);
        setNodeAllProbs((ProbabilisticNode) childNode, filledConfigsChild);


        System.out.println("Elicited configurations found in the child nodes' CPT: ");
        Common.printMatrix(elicitedConfigsChild);
        System.out.println("Elicited and inflicted configurations: ");
        Common.printMatrix(filledConfigsChild);

    }

    private static void fillChildConfigurations(float[][] elicitedConfigsChild, float[][] filledConfigsChild,
                                                int[][] parentalConfigurations, float[] weights) {
        for(int i = 0; i < elicitedConfigsChild.length; i++) {
            if (!rowIsElicited(elicitedConfigsChild[i])) {
                filledConfigsChild[i] = WSA_Row(elicitedConfigsChild, parentalConfigurations, i, weights);
            } else {
                filledConfigsChild[i] = elicitedConfigsChild[i].clone();
            }
        }
    }

    private static float[] WSA_Row(float[][] elicitedProbsChild, int[][] parentalConfigurations, int i,
                                   float[] weights) {
        int numberOfCategories = elicitedProbsChild[i].length;
        float inferedConfiguration[] = new float[numberOfCategories];

        for (int categ = 0; categ < numberOfCategories; categ++) {
            inferedConfiguration[categ] = WSA_Category(elicitedProbsChild, parentalConfigurations, i, categ, weights);
        }
        return inferedConfiguration;
    }

    private static float WSA_Category(float[][] elicitedProbsChild, int[][] parentalConfigurations, int i, int categ,
                                      float[] weights) {
        int compatibleConfigurations[] = new int[parentalConfigurations[i].length];
        float WSA[] = new float[parentalConfigurations[i].length];
        for (int idx = 0; idx < parentalConfigurations.length; idx++) {
            if (idx != i && rowIsElicited(elicitedProbsChild[idx])) {
                for (int col = 0; col < parentalConfigurations[idx].length; col++) {
                    if (parentalConfigurations[idx][col] == parentalConfigurations[i][col]) {
                        WSA[col] += elicitedProbsChild[idx][categ];
                        compatibleConfigurations[col] += 1;
                    }
                }
            }
        }
        return weighAndSum(WSA, weights, compatibleConfigurations);
    }

    private static float weighAndSum(float[] WSA, float[] weights, int[] compatibleConfigurations) {
        float ret = 0;
        for (int WSAindex = 0; WSAindex < WSA.length; WSAindex++) {
            float weighted = WSA[WSAindex] / compatibleConfigurations[WSAindex];
            weighted *= weights[WSAindex];
            ret += weighted;
        }
        return ret;
    }

    private static boolean rowIsElicited(float[] row) {
        return row[0] >= 0;
    }

    private static int sizeCartesianProduct(ArrayList<Node> nodesList) {
        int ret = 1;
        for (int i = 0; i < nodesList.size(); i++) {
            int nodesStatesNumber = nodesList.get(i).getStatesSize();
            if (nodesStatesNumber == 0) return 0;
            ret *= nodesStatesNumber;
        }
        return ret;
    }

    static int[][] getAllParentalConfigurations(Node node) {
        int[][] parentStates = new int[(int) sizeCartesianProduct(node.getParents())][node.getParents().size()];
        int[] parentState = new int[node.getParents().size()];

        for (int i = 0; i < parentStates.length; i++) {
            parentStates[i] = parentState.clone();
            nextStates(parentState, node.getParents());
        }
        return parentStates;
    }

    public static boolean nextStates(int[] parentState, ArrayList<Node> parents) {
        for(int indexParents = parents.size() - 1; indexParents >= 0; --indexParents) {
            Node var3 = parents.get(indexParents);
            if (++parentState[indexParents] < var3.getStatesSize()) {
                return false;
            }

            parentState[indexParents] = 0;
        }

        return true;
    }

    public static void getNodeAllProbs(ProbabilisticNode childNode, float[][] elicitedConfigsChild) {
        childNode.initMarginalList();
        int i = 0;
        for (int row = 0; row < elicitedConfigsChild.length; row++) {
            for (int column = 0; column < elicitedConfigsChild[row].length; column++, i++) {
                elicitedConfigsChild[row][column] = childNode.getProbabilityFunction().getValue(i);
            }
        }
    }

    public static void setNodeAllProbs(ProbabilisticNode childNode, float[][] filledConfigsChild) {
        childNode.initMarginalList();
        int i = 0;
        for (int row = 0; row < filledConfigsChild.length; row++) {
            for (int column = 0; column < filledConfigsChild[row].length; column++, i++) {
                childNode.getProbabilityFunction().setValue(i, filledConfigsChild[row][column]);
            }
        }
    }
}
