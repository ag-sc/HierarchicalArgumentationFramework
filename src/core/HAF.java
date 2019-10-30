package core;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HAF<T> implements HAF_Node{

    DimensionNode Node;
	
	HashMap<String,Double> Weights;
	
	HashMap<String,Object> Filters;
	
	List<HAF_Node> subnodes;
	
	BasicArgumentFactory<T> Factory;
	
	public HAF(DimensionNode node)
	{
		Weights = new HashMap<String,Double>();
		Filters = new HashMap<String,Object>();	
		Node = node;
		
		List<String> dimensions = node.getAllDimensions();
		
		for (String dimension: dimensions)
		{
			// System.out.println("I have found dimension "+dimension);
			Weights.put(dimension, 1.0);
		}
		
	}
	
	public HAF(DimensionNode node, HashMap<String,Double> weights, HashMap<String,Object> filters, BasicArgumentFactory<T> factory)
	{
		Weights = weights;
		Filters = filters;
		Factory = factory;
		Node = node;
	}
	
	public List<String> getComparables() {
		return null;
	}
	
	public String getTextualArgument(String superior, String inferior, String indent)
	{
		String textualArgument = "";
		
		if (evaluate(superior,inferior) > 0.5)
		{
			textualArgument += indent+superior + " has been shown to be superior to" + inferior + " with respect to " +Node.getDimension() +"\n";
		}
		else
		{
			textualArgument += indent+superior + " has not been shown to be superior to" + inferior +"with respect to " +Node.getDimension() +"\n";
		}
		
		for (HAF_Node node: subnodes)
		{
			textualArgument += node.getTextualArgument(superior, inferior,indent+"\t");
		}
		return textualArgument;
	}
	
	public Double evaluate(String superior, String inferior)
	{
		String Top = Node.getDimension();
		
		System.out.println("Top: "+Top);
		
		List<DimensionNode> subdimensions = Node.getSubDimensions();
		
		HAF<T> haf;
		
		subnodes = new ArrayList<HAF_Node>();
		
		if (subdimensions.size() > 0)
		{
			
			for (DimensionNode node: subdimensions)
			{
				System.out.println("Subdimension: "+node.getDimension());
		
				haf = new HAF<T>(node,Weights,Filters,Factory);		
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
				else System.out.println("No weight for: "+node.getDimension());
				sumWeights = sumWeights + weight;
				score = score + node.evaluate(superior, inferior) * weight;
			}
			
			score = score / weight;
			
			return score;
			
		}
		else
		{
			List<BasicArgument> bas = Factory.getBasicArguments(Top);
			
			System.out.println("Getting basic arguments for superiority of "+superior+ " vs. "+inferior+" with respect to "+Top);
			
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
			
			return (count / (new Integer(subnodes.size()).doubleValue()));
			
		}
 
	}
	
	
	private void getBasicArguments(String top2) {
		// TODO Auto-generated method stub
		
	}



	public void setFilter(String property, String value)
	{
		
	}
	
	public void removeFilter(String property)
	{
		
	}
	
	public void removeAllFilters()
	{
		
	}
	
	
	public void voidSetFactory(BasicArgumentFactory<T> factory)
	{
		this.Factory = factory;
	}

	public String getDimension() {
		
		return Node.getDimension();
	}

	public HashMap<String,Double> renderTrueSuperior(String superior, String inferior, Double step, double max) {
		
		List<HashMap<String,Double>> list = renderTrueSuperior(superior, inferior, Weights, Node,step,max, 0);
		
		return getMinimalWeights(list);
	}
		
	
	
	private HashMap<String, Double> getMinimalWeights(
			List<HashMap<String, Double>> list) {
		
		HashMap<String,Double> minimum = null;
		
		int min = Integer.MAX_VALUE;
		
		for (HashMap<String,Double> weights: list)
		{
			if (getSize(weights) < min)
			{
				minimum = weights;
				min = getSize(weights);
			}
		}
			
		return minimum;
	}

	private int getSize(HashMap<String, Double> weights) {
		int size = 0;
		
		for (String dimension: weights.keySet())
		{
			size += weights.get(dimension);
		}
		
		return size;
	}

	public List<HashMap<String,Double>> renderTrueSuperior(String superior, String inferior, HashMap<String,Double> map, DimensionNode node, Double step, Double max, int pos) {
		
		List<String> subdimensions = node.getAllDimensions();
		
		List<HashMap<String,Double>> list = new ArrayList<HashMap<String,Double>>();
		
		System.out.println(map);
		
		HAF haf;
		
		HashMap<String,Double> copy;
		
		if (pos < subdimensions.size())
		{
			String dimension = subdimensions.get(pos);
	
			for (int i =1; i <= max; i++)
			{
				copy = getCopy(map);
				copy.put(dimension,copy.get(dimension)+step);
			
				haf = new HAF<T>(node,copy,Filters,Factory);
			
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

	private HashMap<String, Double> getCopy(HashMap<String, Double> map) {
		
		HashMap<String,Double> copy = new HashMap<String,Double>();
		HAF haf;
		
		for (String key: map.keySet())
		{
			copy.put(key, map.get(key));
		}
		return copy;
	}
	
	
}
