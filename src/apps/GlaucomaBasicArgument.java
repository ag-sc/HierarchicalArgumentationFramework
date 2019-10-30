package apps;
import java.util.HashMap;
import core.BasicArgument;

public class GlaucomaBasicArgument implements BasicArgument{

	String dimension;

	public String getSuperiorOption() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInferiorOption() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean matches(HashMap<String, String> filters) {
		// TODO Auto-generated method stub
		return false;
	}

	public Double evaluate(String superior, String inferior) {
		return 1.0;
	}

	public String getTextualArgument(String superior, String inferior, String indent) {
		return indent+ superior + "has been shown to be superior to" + inferior + "in study X  with respect to" + dimension;
	}


	public boolean matches(String dimension) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTextualArgument(String superior, String inferior) {
		// TODO Auto-generated method stub
		return null;
	}

	public String evaluate(String dimension) {
		// TODO Auto-generated method stub
		return null;
	}

}
