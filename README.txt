Before running the main class TestUtil, modify the following variables:


1. pathname
Path to the DNE file containing the BN in which the WSA will be applied.
Example: "C:/Users/Martí/Strategic Indicators Protocol/workshop_productquality.dne";

2. SInodeName
Exact name of the node for which the CPT is going to be inflicted. Can be found in the node properties inside Netica
Example: "productquality";
                               
3. weights
Weights of the parent nodes of the child node for which the CPT is going to be inflicted (SInodeName), quantifying the 
relative impact of each factor on the child node. The weights must add up to 1 and should be in float format.

The weights have to be ordered in the same way they are ordered in the CPT of the child node (In Netica, right click on child node/Table)
Example 1:
			// BC     SS     CQ
weights[] = {0.68f, 0.24f, 0.08f};

Example 2:
                              // CQ   SW
            float weights[] = {0.3f, 0.7f};