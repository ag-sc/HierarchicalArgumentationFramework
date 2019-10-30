package core;


public interface BasicArgument extends HAF_Node {

	public String evaluate(String dimension);
	
	public String getSuperiorOption();
	public String getInferiorOption();
	
	public boolean matches(String dimension);
	
	public String getDimension();
	
	
}
