package edu.ubi.sc.haf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import edu.ubi.sc.haf.core.BasicArgument;
import edu.ubi.sc.haf.core.BasicArgumentFactory;
import edu.ubi.sc.haf.core.Filter;
import edu.ubi.sc.haf.core.RangeFilter;
import edu.ubi.sc.haf.ClinicalTrial;

public class DiabetesBasicArgumentFactory implements BasicArgumentFactory{
	public static String diabetesEndpointDesc = "HbA1c";
    
    public static String diabetesAdvEffName = "Nocturnal_hypoglycemia";
    
	List<MedicalBasicArgument> basicArguments;

	String file;
	
	public DiabetesBasicArgumentFactory()
	{
		basicArguments = new ArrayList<MedicalBasicArgument>();
	}
	
	public void setFile(String RDF_file)
	{
		file = RDF_file;
	}
	
	public String createQuery()
	{
		
		//String d1 ="Timolol";
		ParameterizedSparqlString pss = new ParameterizedSparqlString();	   	   
		   
		pss.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		pss.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		pss.setNsPrefix("","http://www.semanticweb.org/root/ontologies/2018/6/ctro#");
		//pss.setLiteral("drug1", "Timolol");
		//pss.setLiteral("drug2", "Latanoprost");
		//pss.setLiteral("drug1", "Glargine_Insuline");
		//pss.setLiteral("drug2", "NHP_Insuline");
		
	     //String drug1 = "Glargine_Insulin";
	     //String drug2 = "NHP_Insulin";
	     //String EndpointDesc = "HbA1c";
	     //String AdvEff = "Nocturnal_hypoglycemia";
	     
	     String drug1 = "Glargine_Insulin";
	     String drug2 = "NPH_Insulin";
	     String EndpointDesc = DiabetesBasicArgumentFactory.diabetesEndpointDesc;
	     String AdvEff = DiabetesBasicArgumentFactory.diabetesAdvEffName;
		  
		String queryDyn="";	
			
		pss.setCommandText("SELECT DISTINCT ?ct ?pmid ?country_l ?duration ?numPatients ?avgAge ?drugName1 ?drugName2 ?endpointDesc ?reduction1 ?reduction2 ?AEName ?numAffected1 ?numAffected2\n" + 
				"WHERE{\n" + 
				"?medic1 :hasDrug :" + drug1 +" . \n" + 
				"?medic2 :hasDrug :" + drug2 +" . \n" + 
				"  ?interv1 :hasMedication ?medic1. \n" + 
				"  ?interv2 :hasMedication ?medic2.\n" + 
				"  ?medic1 :hasDrug ?drugName1. \n" + 
				"  ?medic2 :hasDrug ?drugName2. \n" + 
				"  ?arm1 :hasIntervention ?interv1. \n" + 
				"  ?arm2 :hasIntervention ?interv2.\n" + 
				"  ?ct :hasArm ?arm1 .\n" + 
				"  ?ct :hasArm ?arm2 .\n" + 
				"  ?pub :describes ?ct. \n" + 
				"  ?pub :hasPMID ?pmid.  \n" + 
				"  ?ct :hasPopulation ?population .\n" + 
				"  ?population :hasCountry ?country .\n" + 
				"  ?country rdfs:label ?country_l. \n" +
				"  ?population :hasAvgAge ?avgAge .\n" + 
				"  ?ct :hasNumberPatientsCT ?numPatients . \n" + 
				"  ?ct :hasCTduration ?duration .\n" + 
				"#starts endpoint \n" + 
				"  ?arm1 :hasOutcome ?outcome1. \n" + 
				"  ?arm2 :hasOutcome ?outcome2.  \n" + 
				"  ?outcome1 :hasEndPoint ?endpoint1. \n" + 
				"  ?outcome2 :hasEndPoint ?endpoint2.\n" + 
				"  ?endpoint1 :hasEndpointDescription :" + EndpointDesc + ". \n" + 
				"  ?endpoint2 :hasEndpointDescription :" + EndpointDesc + ". \n" + 
				"  ?endpoint2 :hasEndpointDescription ?endpointDesc . \n" + 
				"  ?outcome1 :hasResult ?res1.  \n" + 
				"  ?outcome2 :hasResult ?res2.\n" + 
				"  ?res1 :hasAbsoluteValue ?result1.\n" + 
				"  ?res2 :hasAbsoluteValue ?result2.\n" + 
				"  bind(str(?result1) as ?reduction1) \n" + 
				"  bind(str(?result2) as ?reduction2)\n" + 
				"#Adverse effects\n" + 
				"  ?arm1 :hasAdverseEffect ?AEff1 .\n" + 
				"  ?AEff1 :hasAEname :"+ AdvEff + ". \n" + 
				"  ?AEff1 :hasAEname ?AEName .\n" + 
				"  ?AEff1 :hasNumAffectedAE ?numAffected1. \n" + 
				"  ?arm2 :hasAdverseEffect ?AEff2 .\n" + 
				"  ?AEff2 :hasAEname :"+ AdvEff + ". \n" +  
				"  ?AEff2 :hasAEname ?AEName .\n" + 
				"  ?AEff2 :hasNumAffectedAE ?numAffected2.  \n" + 
				"} order by ?ct");
		 
		
		/*pss.setCommandText("SELECT DISTINCT ?ct ?drug1 ?drug2 ?result1 ?result2 WHERE{\n" +
		   "?drug1 rdfs:label ?drug1Name. ?drug2 rdfs:label ?drug2Name. \n" + 
		   "?medic1 :hasDrug ?drug1. ?medic2 :hasDrug ?drug2. \n"+
	       "?interv1 :hasMedication ?medic1. ?interv2 :hasMedication ?medic2. \n"+
	       "?arm1 :hasIntervention ?interv1. ?arm2 :hasIntervention ?interv2. \n" +
	       "?ct :hasArm ?arm1. ?ct :hasArm ?arm2.");*/
	                			
		//pss.append("?ct :hasCTDesign ?design. \n"); 
		   //pss.append("?ct :hasCTDesign ?design. \n");
		   //pss.append("?design rdfs:label ?designDesc. \n");
		  		
		   //pss.append("}");
	            
		   queryDyn = pss.toString();
		   
		   System.out.println(queryDyn);
		
		return queryDyn;
	}

	public List<ClinicalTrial> getTrials() {
		List<ClinicalTrial> trials = new ArrayList<ClinicalTrial>();
		
		String queryString = createQuery();
		
		Model model= FileManager.get().loadModel("ctro_v4.ttl");
		
		QueryExecution qexec = QueryExecutionFactory.create(queryString, model);
		  	 	  
		ResultSet resultSet = qexec.execSelect();		  
		
		List<String> varNames = resultSet.getResultVars();
		for(String s : varNames) {
			System.out.println(s);
		}
		
		while(resultSet.hasNext()) {
			QuerySolution row = resultSet.next();
			
			ClinicalTrial trial = new ClinicalTrial();
			
			String id = row.get("ct").toString();
			trial.id = id.substring(id.lastIndexOf("#")+1);
			
			trial.title = "title";
			trial.authors = "authors";
			trial.link = "link";
			trial.include = true;
			
			trials.add(trial);
		}
		
		return trials;
	}
	
	public void createBasicArguments()
	{
		//String query=createQuery();
		String queryString = createQuery();
		Model model= FileManager.get().loadModel("/home/cwitte/eclipse-workspace/HAF_backend/ctro_v4.ttl");
		
		QueryExecution qexec = QueryExecutionFactory.create(queryString, model);
		  	 	  
		ResultSet resultSet = qexec.execSelect();		
		ResultSetFormatter.out(resultSet);
		
		List<String> varNames = resultSet.getResultVars();
		for(String s : varNames) {
			System.out.println(s);
		}
		
		while(resultSet.hasNext()) {
			QuerySolution row = resultSet.next();
			
			// efficacy //////////////////////////////////////////////////
			String dimension = DiabetesBasicArgumentFactory.diabetesEndpointDesc;
			String disease = "Type 2 Diabetes";
			String comparator = ">";
			String trialId = row.get("ct").toString();
			
			MedicalBasicArgument basicArgumentEfficacy = new MedicalBasicArgument(disease, dimension, comparator);
			basicArgumentEfficacy.setTrialId(trialId);
			
			String drugName1 = row.get("drugName1").toString();
			drugName1 = drugName1.substring(drugName1.lastIndexOf('#')+1);
			double reduction1 = Double.valueOf(row.get("reduction1").toString());
			basicArgumentEfficacy.addEvidence(drugName1, reduction1);
			
			String drugName2 = row.get("drugName2").toString();
			drugName2 = drugName2.substring(drugName2.lastIndexOf('#')+1);
			double reduction2 = Double.valueOf(row.get("reduction2").toString());
			basicArgumentEfficacy.addEvidence(drugName2, reduction2);
			
			this.basicArguments.add(basicArgumentEfficacy);
			
			
			// safety //////////////////////////////////////////////////
			dimension = DiabetesBasicArgumentFactory.diabetesAdvEffName;
			disease = "Type 2 Diabetes";
			comparator = "<";
			
			MedicalBasicArgument basicArgumentSafety = new MedicalBasicArgument(disease, dimension, comparator);
			basicArgumentSafety.setTrialId(trialId);
			
			double numAffectedAdverseEvent1 = Double.valueOf(row.get("numAffected1").toString());
			basicArgumentSafety.addEvidence(drugName1, numAffectedAdverseEvent1);
						
			double numAffectedAdverseEvent2 = Double.valueOf(row.get("numAffected2").toString());
			basicArgumentSafety.addEvidence(drugName2, numAffectedAdverseEvent2);
			
			this.basicArguments.add(basicArgumentSafety);
			
			
			System.out.println("drugName1: " + drugName1);
			System.out.println("redcution1: " + reduction1);
			System.out.println("numAffected1: " + numAffectedAdverseEvent1);
			System.out.println("drugName2: " + drugName2);
			System.out.println("redcution2: " + reduction2);
			System.out.println("numAffected2: " + numAffectedAdverseEvent2);
		}
	
	    ResultSetFormatter.out(resultSet);
	    qexec.close();	
	}

	@Override
	public List<BasicArgument> getBasicArguments(String dimension, HashMap<String,Filter> filters, List<ClinicalTrial> trials)
	{
		List<BasicArgument> arguments = new ArrayList<BasicArgument>();
		
		for (MedicalBasicArgument argument: basicArguments)
		{
		    if (argument.matches(dimension))
		    {
		    	// filtering
		    	boolean filterSuccess = true;
		    	
		    	if(filters.containsKey(BackendInput.AVG_AGE_STR)) {
		    		RangeFilter filter = (RangeFilter) filters.get(BackendInput.AVG_AGE_STR);
		    		double avgAge = argument.getAvgAge();
		    		if( !(avgAge >= filter.getMin() && avgAge <= filter.getMax()) ) {
		    			filterSuccess = false;
		    		}
		    	}
		    	
		    	if(filters.containsKey(BackendInput.DURATION_STR)) {
		    		RangeFilter filter = (RangeFilter) filters.get(BackendInput.DURATION_STR);
		    		double duration = argument.getStudyDuration();
		    		if( !(duration >= filter.getMin() && duration <= filter.getMax()) ) {
		    			filterSuccess = false;
		    		}
		    	}
		    	
		    	if(filters.containsKey(BackendInput.NUM_PATIENTS_STR)) {
		    		RangeFilter filter = (RangeFilter) filters.get(BackendInput.NUM_PATIENTS_STR);
		    		double numPatients = argument.getNumPatients();
		    		if( !(numPatients >= filter.getMin() && numPatients <= filter.getMax()) ) {
		    			filterSuccess = false;
		    		}
		    	}
		    	
		    	for(ClinicalTrial trial : trials) {
		    		if(argument.getTrialId().equalsIgnoreCase(trial.id)) {
		    			if(!trial.include) {
		    				filterSuccess = false;
		    			}
		    			
		    			break;
		    		}
		    	}
		    	
		    	if(filterSuccess) {
		    		arguments.add(argument);
		    	}
		    }
		}
		return arguments;
	}
}
