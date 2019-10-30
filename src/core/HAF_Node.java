package core;

public interface HAF_Node {

	public String getDimension();
	
	public Double evaluate(String superior, String inferior);
	
	public String getTextualArgument(String superior, String inferior,String string);
	
}
