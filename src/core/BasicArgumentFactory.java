package core;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;


public class BasicArgumentFactory<T> {

	List<BasicArgument> basicArguments;

	String file;
	
	public BasicArgumentFactory()
	{
		basicArguments = new ArrayList<BasicArgument>();
	}
	
	public void setFile(String RDF_file)
	{
		file = RDF_file;
	}
	
	public void CreateBasicArguments(String queryString, List<String> properties) throws RDFParseException, RepositoryException, IOException
	{
	
		// Create a new Repository.
		Repository db = new SailRepository(new MemoryStore());
		db.init();

		// Open a connection to the database
		try (RepositoryConnection conn = db.getConnection()) {
		    String filename = "example-data-artists.ttl";
		    try (InputStream input = this.getClass().getResourceAsStream("/" + filename)) {
			// add the RDF data from the inputstream directly to our database
			conn.add(input, "", RDFFormat.TURTLE );
		    }
;
		    TupleQuery query = conn.prepareTupleQuery(queryString); 
		    // A QueryResult is also an AutoCloseable resource, so make sure it gets 
		    // closed when done.
		    try (TupleQueryResult result = query.evaluate()) {
			// we just iterate over all solutions in the result...
			while (result.hasNext()) {
			    BindingSet solution = result.next();
			    // construct argument from each binding and add to basicArguments!
			    
			}
		    }
		}
		finally {
		    // Before our program exits, make sure the database is properly shut down.
		    db.shutDown();
		}
	}
	
	public List<BasicArgument> getBasicArguments(String dimension)
	{
		List<BasicArgument> arguments = new ArrayList<BasicArgument>();
		
		for (BasicArgument argument: basicArguments)
		{
		    if (argument.matches(dimension))
		    {
		    	arguments.add(argument);
		    }
		}
		return arguments;
	}
	
}
