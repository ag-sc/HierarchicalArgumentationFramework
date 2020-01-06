package edu.ubi.sc.haf.core;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import edu.ubi.sc.haf.ClinicalTrial;
import edu.ubi.sc.haf.DiabetesBasicArgumentFactory;
import edu.ubi.sc.haf.GlaucomaBasicArgumentFactory;


public class HAF<T> implements HAF_Node{

    DimensionNode Node;
	
	Map<String,Double> Weights;
	
	HashMap<String,RangeFilter> RangeFilters;
	
	List<ClinicalTrial> trials;
	
	BasicArgumentFactory Factory;
	
	public HAF(DimensionNode node)
	{
		Weights = new HashMap<String,Double>();
		RangeFilters = new HashMap<String,RangeFilter>();	
		Node = node;
		
		List<String> dimensions = node.getAllDimensions();
		
		for (String dimension: dimensions)
		{
			// System.out.println("I have found dimension "+dimension);
			Weights.put(dimension, 1.0);
		}
		
	}
	
	public HAF(DimensionNode node, Map<String,Double> weights, HashMap<String,RangeFilter> RangeFilters, 
			BasicArgumentFactory factory, List<ClinicalTrial> trials)
	{
		Weights = weights;
		this.RangeFilters = RangeFilters;
		Factory = factory;
		Node = node;
		
		if(trials == null) {
			this.trials = factory.getTrials();
		} else {
			this.trials = trials;
		}
	}
	
	public List<String> getComparables() {
		return null;
	}
	
	public Map<String, Object> getTextualArgument(String superior, String inferior)
	{
		Map<String,Object> verbalization = new HashMap<String,Object>();
		
		List<Map<String, Object>> subarguments = new ArrayList<Map<String, Object>>();
		
		String Top = Node.getDimension();
		
		System.out.println("Top: "+Top);
		
		List<DimensionNode> subdimensions = Node.getSubDimensions();
		
		HAF<T> haf;
		
		List<HAF_Node> subnodes = new ArrayList<HAF_Node>();
		
		// generate textual argument for current node ////////////////////////////////
		double score = this.evaluate(superior, inferior);
		double weight;
		String superiorityString;
		String textualArgument = null;
		String superiorString;
		
		Locale locale  = new Locale("en", "UK");
		String pattern = "###.##";
		DecimalFormat df = (DecimalFormat)
		        NumberFormat.getNumberInstance(locale);
		df.applyPattern(pattern);
		
		// check is basic arguments exist
		if(Factory.getBasicArguments(GlaucomaBasicArgumentFactory.glaucomaEndpointDesc, RangeFilters, trials).size() == 0
				&& Factory.getBasicArguments(DiabetesBasicArgumentFactory.diabetesEndpointDesc, RangeFilters, trials).size() == 0) {;
			verbalization.put("text", "No basic arguments left!");
			verbalization.put("children", null);
			return verbalization;
		}
		
		// generate subarguments for all children of the current node
		if (subdimensions.size() > 0)
		{
			
			for (DimensionNode node: subdimensions)
			{
				System.out.println("Subdimension text: "+node.getDimension());
		
				haf = new HAF<T>(node,Weights,RangeFilters,Factory,trials);		
				subnodes.add(haf);
			}
			System.out.println(subnodes);
			
			for (HAF_Node subnode: subnodes)
			{
				System.out.println("subnode text: "+subnode.getDimension());
			}
			
			for (HAF_Node subnode: subnodes)
			{
				subarguments.add(subnode.getTextualArgument(superior, inferior));
			}
			
			verbalization.put("children", subarguments);		
		} else {
			verbalization.put("children", null);
		}
		
		// generate verbalization for current node ////////////////////
		if (subdimensions.size() > 0)
		{	
			if(score > 0.5) {
				superiorityString = "shown to be superior";
			} else {
				superiorityString = "not shown to be superior";
			}
			
			if(this.getDimension().equalsIgnoreCase("top")) {
				textualArgument = "Overall, the " + superior + " treatment has " + superiorityString
						+ " to the " + inferior + " treatment with respect to " + subdimensions.get(0).getDimension()
						+ " (weight " + Weights.get(subdimensions.get(0).getDimension()) + ") and "
						+ subdimensions.get(1).getDimension() + " (weight " + Weights.get(subdimensions.get(1).getDimension())
						+ "). The overall score is " + df.format(score) + ".";
			} else {
				textualArgument = "The " + superior + " treatment has " + superiorityString
					+ " to the " + inferior + " treatment with respect to " + Top
					+ " with a score of " + df.format(score) + ".";
			}
		} else { // textual argument for leaf node
			List<BasicArgument> bas = Factory.getBasicArguments(Top, RangeFilters, trials);
			double numBasicArguments = bas.size();
			double count = 0;
			
			for (BasicArgument ba:bas)
			{
				if (ba.evaluate(superior, inferior) == 1.0)
				{
					count=count+1.0;
				}
			}
			
			// check superiority
			score = count / numBasicArguments;
			
			if(score > 0.5) {
				superiorityString = "shown to be superior";
			} else {
				superiorityString = "not shown to be superior";
			}
			
			textualArgument = "The " + superior + " treatment has " + superiorityString
					+ " to the " + inferior + " treatment with respect to " + Top
					+ " with a score of " + df.format(score) + ".";
		}
		
		verbalization.put("text", textualArgument);
		
		return verbalization;
	}
	
	public Double evaluate(String superior, String inferior)
	{
		String Top = Node.getDimension();
		
		System.out.println("Top: "+Top);
		
		List<DimensionNode> subdimensions = Node.getSubDimensions();
		
		HAF<T> haf;
		
		List<HAF_Node> subnodes = new ArrayList<HAF_Node>();
		
		if (subdimensions.size() > 0)
		{
			
			for (DimensionNode node: subdimensions)
			{
				System.out.println("Subdimension: "+node.getDimension());
		
				haf = new HAF<T>(node,Weights,RangeFilters,Factory, trials);		
				subnodes.add(haf);
			}
		
			double score = 0.0;
			double weight = 0.0;
			double sumWeights = 0.0;
			
			for (HAF_Node node: subnodes)
			{
				// System.out.println("Trying to get weight for "+node.getDimension()+"\n");
				
				if (Weights.containsKey(node.getDimension()))
					weight = Weights.get(node.getDimension());
				else {
					System.out.println("No weight for: "+node.getDimension());
					weight = 1.0;
					Weights.put(node.getDimension(), 1.0);
					
				}
				sumWeights = sumWeights + weight;
				score = score + node.evaluate(superior, inferior) * weight;
			}
			
			score = score / sumWeights;
			System.out.println("Node: " + Top + " ; Score: " + score);
			
			return score;
			
		}
		else
		{
			List<BasicArgument> bas = Factory.getBasicArguments(Top, RangeFilters, trials);
			
			System.out.println("Getting basic arguments for superiority of "+superior+ " vs. "+inferior+" with respect to "+Top);
			
			/*
			for (BasicArgument ba:bas)
			{
				subnodes.add(ba);
			}
			
			double count = 0;
			
			for (HAF_Node node: subnodes)
			{
				if (node.evaluate(superior, inferior) == 1.0)
				{
					count=count+1.0;
				}
			}
			*/
			
			double count = 0;
			
			for (BasicArgument ba:bas)
			{
				subnodes.add(ba);
				if (ba.evaluate(superior, inferior) == 1.0)
				{
					count=count+1.0;
				}
			}
			
			return (count / (new Integer(subnodes.size()).doubleValue()));
			
		}
 
	}
	
	
	private void getBasicArguments(String top2) {
		// TODO Auto-generated method stub
		
	}



	public void setRangeFilter(String property, String value)
	{
		
	}
	
	public void removeRangeFilter(String property)
	{
		
	}
	
	public void removeAllRangeFilters()
	{
		
	}
	
	
	public void voidSetFactory(BasicArgumentFactory factory)
	{
		this.Factory = factory;
	}

	public String getDimension() {
		
		return Node.getDimension();
	}

	public Map<String,Double> renderTrueSuperior(String superior, String inferior, Double step, double max) {
		
		List<Map<String,Double>> list = renderTrueSuperior(superior, inferior, Weights, Node,step,max, 0);
		
		return getMinimalWeights(list);
	}
		
	
	
	private Map<String, Double> getMinimalWeights(
			List<Map<String, Double>> list) {
		
		Map<String,Double> minimum = null;
		
		int min = Integer.MAX_VALUE;
		
		for (Map<String,Double> weights: list)
		{
			if (getSize(weights) < min)
			{
				minimum = weights;
				min = getSize(weights);
			}
		}
			
		return minimum;
	}

	private int getSize(Map<String, Double> weights) {
		int size = 0;
		
		for (String dimension: weights.keySet())
		{
			size += weights.get(dimension);
		}
		
		return size;
	}

	public List<Map<String,Double>> renderTrueSuperior(String superior, String inferior, Map<String,Double> map, DimensionNode node, Double step, Double max, int pos) {
		
		List<String> subdimensions = node.getAllDimensions();
		
		List<Map<String,Double>> list = new ArrayList<Map<String,Double>>();
		
		System.out.println(map);
		
		HAF haf;
		
		Map<String,Double> copy;
		
		if (pos < subdimensions.size())
		{
			String dimension = subdimensions.get(pos);
	
			for (int i =1; i <= max; i++)
			{
				copy = getCopy(map);
				copy.put(dimension,copy.get(dimension)+step);
			
				haf = new HAF<T>(node,copy,RangeFilters,Factory, trials);
			
				if (haf.evaluate(superior, inferior) > 0.5)
				{
					list.add(copy);
				}
					
				list.addAll(renderTrueSuperior(superior, inferior, copy, node, step, max, pos+1));
			}
		}
		
	return list;
		
	}

	private int getMaximumDimensions(HashMap<String, Double> map, Double max) {
		
		int count = 0;
		for (String key: map.keySet())
		{
			if (map.get(key) - max == 0)
			{
				count ++;
			}
		}
		return count;
	}

	private Map<String, Double> getCopy(Map<String, Double> map) {
		
		Map<String,Double> copy = new HashMap<String,Double>();
		HAF haf;
		
		for (String key: map.keySet())
		{
			copy.put(key, map.get(key));
		}
		return copy;
	}
	
	public List<ClinicalTrial> getTrials() {
		return this.trials;
	}
}
