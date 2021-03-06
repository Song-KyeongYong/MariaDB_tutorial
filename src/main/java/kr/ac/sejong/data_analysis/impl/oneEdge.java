package kr.ac.sejong.data_analysis.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import kr.ac.sejong.data_analysis.graph.Direction;
import kr.ac.sejong.data_analysis.graph.Edge;
import kr.ac.sejong.data_analysis.graph.Graph;
import kr.ac.sejong.data_analysis.graph.Vertex;


public class oneEdge implements Edge {

	private Vertex out;
	private String label;
	private Vertex in;
    private String id;
    private oneGraph graph;
    Statement stmt = null;
    ResultSet rs = null;
    Connection connection = null;

    
    public oneEdge(Vertex out, String label, Vertex in, oneGraph graph) { 
    	this.out = out;
    	this.label = label;
    	this.in = in;
    	this.id = (String)out.getId() + '|' + label + '|' + in.getId();
    	this.graph = graph;
    	this.connection = this.graph.getConnection();
    }

    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
		
    	if (direction == Direction.IN) {
    		return this.in;
    	}
    	else {
    		return this.out;
    	}
    }


    @Override
    public Object getProperty(String key) { 
    	JSONObject job = null;
    	try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT properties FROM edge WHERE OutVertex = "+out.getId()+" && "
					+ "InVertex = "+in.getId()+" && label = '"+label+"' AND properties IS NOT NULL");
			if (rs.next() == false) {
				return null;
			}
			else {
				try {
					job = new JSONObject(rs.getString(1));
					return job.get(key);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
        return null;
    }

    @Override
    public Set<String> getPropertyKeys() { 
    	Set<String> s = new HashSet<String>();
    	JSONObject job = null;
    	try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT properties FROM edge WHERE OutVertex = "+out.getId()+" && "
					+ "InVertex = "+in.getId()+" && label = '"+label+"' AND properties IS NOT NULL");		
			
			if (rs.next() == false) {
				return null; 
			}
			else {
				try {
					job = new JSONObject(rs.getString(1));
					Iterator<String> it = job.keys();
					while(it.hasNext()) {
						s.add(it.next());
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
        return s;
    }

    @Override
    public void setProperty(String key, Object value) { 
    	JSONObject job = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT properties FROM edge WHERE OutVertex = "+out.getId()+" && "
					+ "InVertex = "+in.getId()+" && label = '"+label+"' AND properties IS NOT NULL");	
			if (rs.next() == false) {
				job = new JSONObject();
			}
			else {
				try {
					job = new JSONObject(rs.getString(1));
					job.remove(key);
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
			try {
				job.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			stmt.executeUpdate("UPDATE edge SET properties = \'"+job.toString()+"\' WHERE OutVertex = "+out.getId()+" &&"
					+ "InVertex = "+in.getId()+" && label = '"+label+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
    @Override
	public String toString() { 
		return "e["+out.getId()+"-"+label+"->"+in.getId()+"]";
	}

	@Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public Object getId() { 
        return this.id;
    }
}