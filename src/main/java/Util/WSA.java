package Util;

import norsys.netica.*;
import norsys.neticaEx.NodeEx;
import norsys.neticaEx.NodeListEx;

public class WSA {

    public static void fillChildConfigurations(Node childNode, float[] weights) throws NeticaException{
        float[][] elicitedConfigsChild = new float[(int) NodeListEx.sizeCartesianProduct(childNode.getParents())]
                [childNode.getNumStates()];
        float[][] filledConfigsChild = new float[(int) NodeListEx.sizeCartesianProduct(childNode.getParents())]
                [childNode.getNumStates()];
        int[][] parentalConfigurations = Common.getAllParentalConfigurations(childNode);
        NodeEx.getNodeAllProbs(childNode, elicitedConfigsChild);

        WSA.fillChildConfigurations(elicitedConfigsChild, filledConfigsChild, parentalConfigurations, weights);
        NodeEx.setNodeAllProbs(childNode, filledConfigsChild);

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
}
