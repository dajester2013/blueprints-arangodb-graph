package com.tinkerpop.blueprints.impls.arangodb;

import java.util.HashSet;
import java.util.Set;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.arangodb.client.ArangoDBBaseDocument;
import com.tinkerpop.blueprints.impls.arangodb.client.ArangoDBException;
import com.tinkerpop.blueprints.impls.arangodb.utils.ArangoDBUtil;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;

abstract public class ArangoDBElement implements Element {

	/**
	 * the graph of the document
	 */
	
	protected ArangoDBGraph graph;

	/**
	 * the vertex/edge document
	 */
	
	protected ArangoDBBaseDocument document;
	
	/**
	 * true if the element was changed
	 */
	
	protected boolean changed = false; 
	
	/**
	 * Save the vertex or the edge in ArangoDB 
	 */
	
	abstract public void save () throws ArangoDBException;
	
	/**
	 * Delete the vertex or the edge in ArangoDB
	 */
	
	abstract public void delete () throws ArangoDBException;
	
	/**
	 * @inheritDoc
	 */
	
	public Object getProperty(String key) {
		return document.getProperty(ArangoDBUtil.normalizeKey(key));
	}
	
	/**
	 * Set/Reset the vertex/edge document
	 */
	
	public void setDocument (ArangoDBBaseDocument document) {
		this.document = document;
	}

	/**
	 * @inheritDoc
	 */
	
	public Set<String> getPropertyKeys() {
		Set<String> ps = document.getPropertyKeys();		
		HashSet<String> result = new HashSet<String>(); 
		for (String key: ps) {
			result.add(ArangoDBUtil.denormalizeKey(key));
		}
		return result;
	}

	/**
	 * @inheritDoc
	 */
	
	public void setProperty(String key, Object value) {
		
        if (key == null || key.equals(StringFactory.EMPTY_STRING))
            throw ExceptionFactory.elementKeyCanNotBeEmpty();        
        if (key.equals(StringFactory.ID))
            throw ExceptionFactory.propertyKeyIdIsReserved();
		
		try {
			document.setProperty(ArangoDBUtil.normalizeKey(key), value);	
			changed = true;
		} catch (ArangoDBException e) {
            throw ExceptionFactory.propertyKeyIdIsReserved();
		}
	}

	/**
	 * @inheritDoc
	 */
	
	public Object removeProperty(String key) {
        if (key == null || key.equals(StringFactory.EMPTY_STRING))
            throw ExceptionFactory.elementKeyCanNotBeEmpty();        
        if (key.equals(StringFactory.ID))
            throw ExceptionFactory.propertyKeyIdIsReserved();
        if (key.equals(StringFactory.LABEL) && this instanceof Edge)
            throw ExceptionFactory.propertyKeyLabelIsReservedForEdges();
        
		Object o = null;
		try {
			o = document.removeProperty(ArangoDBUtil.normalizeKey(key));
			changed = true;
		} catch (ArangoDBException e) {
            throw ExceptionFactory.propertyKeyIdIsReserved();
		}
		return o;
	}

	/**
	 * @inheritDoc
	 */
	
	public Object getId() {
		return document.getDocumentKey();
	}
	
	public ArangoDBBaseDocument getRaw () {
		return document;
	}
	
}
