import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import norsys.netica.*;

import Util.WSA;
import Util.WSAUnbb;
import Util.Common;
import unbbayes.io.BaseIO;
import unbbayes.io.DneIO;
import unbbayes.prs.bn.ProbabilisticNetwork;

public class TestUtil {

    static Environ env;

    // USE THIS FUNCTION TO OBTAIN A .DNE BN FILE
    public static File WSANetica_Main(File file, String targetNames[], float[][] weights) throws NeticaException {
        System.out.println("--WSA Prototype tool--");

        Net net = new Net(new Streamer(file.getAbsolutePath()));
        Common.printNodes(net.getNodes());

        int targetNamesIndex = 0;
        for (String targetName : targetNames) {
            Node childNode = net.getNode(targetName);
            System.out.println("Infering configurations with WSA for child node: " + childNode.getName());
            WSA.fillChildConfigurations(childNode, weights[targetNamesIndex]);

            targetNamesIndex++;
        }
        File wsa_file = new File(file.getParent(), "WSA_" + file.getName());
        net.write(new Streamer(wsa_file.getAbsolutePath()));

        System.out.println("Net with inflicted configurations has been saved to " + wsa_file.getAbsolutePath());

        return wsa_file;
    }

    // USE THIS OTHER FUNCTION TO OBTAIN A UNBBAYES BN FILE
    public static File WSAUnbb_Main(File file, String SInodeName, float[] weights) throws IOException {
        System.out.println("--WSA Prototype tool--");

        BaseIO io = new DneIO();
        ProbabilisticNetwork net = (ProbabilisticNetwork) io.load(file);
        System.out.println(net.getNodes().toString());

        unbbayes.prs.Node childNode = net.getNode(SInodeName);
        System.out.println("Infering configurations with WSA for child node: " + childNode.getName());

        WSAUnbb.fillChildConfigurations(net, childNode, weights);

        File wsa_file = new File(file.getParent(), "WSA_" + file.getName());
        io.save(wsa_file, net);

        System.out.println("Net with inflicted configurations has been saved to " + wsa_file.getAbsolutePath());

        return wsa_file;
    }

    public static void main(String[] args) throws NeticaException {
        try {
            // VARIABLES TO TEST THE WSA CUSTOM IMPLEMENTATION
            String pathname = "C:/Factors_step 3.dne";
            File file = new File(pathname);
            String ftargetNames[] = {"activitycompletion", "productstability"};

            float fweights[][] = {{0.7f, 0.3f}, {0.2f, 0.4f, 0.4f}};

            String sitargetName[] = {"productreadiness"};
                                    // AC    KRD   PST
            float siweights[][] =   {{0.6f, 0.1f, 0.3f}};
                              //


            env = new Environ(null);
            File wsa_file = WSANetica_Main(file, sitargetName, siweights);


            // VARIABLES FOR ASSESSMENT TESTING
//            Map<String, String> factorStates = new HashMap<>();
//            factorStates.put("blockingcode", "High");
//            factorStates.put("softwarestability", "Medium");
//            factorStates.put("codequality", "Medium");
//
//            Net net = new Net(new Streamer(wsa_file.getAbsolutePath()));
//            Node SInode = net.getNode(SInodeName);
//            float[] assessmentTest = Common.doInference(net, SInode, factorStates);
//
//            System.out.print("Probabilites after entering beliefs for node " + SInodeName + ": ");
//            System.out.println(factorStates.toString());
//            System.out.println(Arrays.toString(assessmentTest));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            env.finalize();
        }
    }


}
