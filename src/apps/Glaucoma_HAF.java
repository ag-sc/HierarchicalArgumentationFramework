package apps;
import java.util.HashMap;
import java.util.List;

import core.BasicArgumentFactory;
import core.DimensionNode;
import core.HAF;


public class Glaucoma_HAF {

	public static void main(String[] args) {
		
		DimensionNode top = new DimensionNode("top");
		DimensionNode safety = new DimensionNode("safety");
		DimensionNode efficiency = new DimensionNode("efficiency");
		DimensionNode iop_reduction = new DimensionNode("IOP reduction");
		DimensionNode sideeffect = new DimensionNode("ConjunctivalHyperemia reduction");
		
		safety.addChild(sideeffect);
		efficiency.addChild(iop_reduction);
		top.addChild(safety);
		top.addChild(efficiency);
		
		
		HAF<GlaucomaBasicArgument> medical_HAF = new HAF<GlaucomaBasicArgument>(top);
		
		BasicArgumentFactory<GlaucomaBasicArgument> factory = new BasicArgumentFactory<GlaucomaBasicArgument>();

		medical_HAF.voidSetFactory(factory);
	
		// medical_HAF.evaluate("latanoprost", "liraglitude");
		
		System.out.print(medical_HAF.getTextualArgument("latanoprost", "timolol",""));
		
		HashMap<String,Double> solution = medical_HAF.renderTrueSuperior("latanoprost","timolol",1.0,3.0);
		
		if (solution == null) System.out.println("Can not render!");
		
		System.out.print(solution);

	}

}
