package edu.ubi.sc.haf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.sys.JenaSystem;

import edu.ubi.sc.haf.DiabetesBasicArgumentFactory;
import edu.ubi.sc.haf.core.BasicArgumentFactory;
import edu.ubi.sc.haf.core.DimensionNode;
import edu.ubi.sc.haf.core.Filter;
import edu.ubi.sc.haf.core.HAF;
import edu.ubi.sc.haf.core.RangeFilter;


public class DiabetesHAF {

	public static BackendOutput Main(BackendInput backendInput) {
		org.apache.jena.query.ARQ.init();
		JenaSystem.init();
		
		DimensionNode top = new DimensionNode("top");
		DimensionNode safety = new DimensionNode("safety");
		DimensionNode efficiency = new DimensionNode("efficacy");
		DimensionNode hba1c_reduction = new DimensionNode(DiabetesBasicArgumentFactory.diabetesEndpointDesc);
		DimensionNode sideeffect = new DimensionNode(DiabetesBasicArgumentFactory.diabetesAdvEffName);
		
		safety.addChild(sideeffect);
		efficiency.addChild(hba1c_reduction);
		top.addChild(safety);
		top.addChild(efficiency);
		
		GlaucomaBasicArgumentFactory factory = new GlaucomaBasicArgumentFactory();
		
		/*
		List<ClinicalTrial> trials = null;
		HashMap<String,Double> weights = new HashMap<String,Double>();
		weights.put("Safety", 0.5);
		weights.put("Efficacy", 0.5);
		HashMap<String,Filter> filters = new HashMap<String,Filter>();
		*/
		
		HashMap<String,Double> weights = backendInput.weights;
		HashMap<String,Filter> filters = new HashMap<String,Filter>();
		List<ClinicalTrial> trials = backendInput.evidence;
		
		HAF<MedicalBasicArgument> top_HAF = new HAF<MedicalBasicArgument>(top, weights, filters, 
				factory, trials);
		
		
		factory.createBasicArguments();

		top_HAF.voidSetFactory(factory);
		
		Map<String, Object> textualArgument = top_HAF.getTextualArgument(backendInput.drug1, backendInput.drug2);
		double score = top_HAF.evaluate("Glargine_Insulin", "NPH_Insulin");
		
		//System.out.println(textualArgument);
		BackendOutput backendOutput = new BackendOutput();
		backendOutput.verbalization = textualArgument;
		backendOutput.evidence = top_HAF.getTrials();
		return backendOutput;
	}

}
