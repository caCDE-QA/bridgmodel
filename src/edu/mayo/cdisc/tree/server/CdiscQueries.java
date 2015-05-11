package edu.mayo.cdisc.tree.server;

import java.util.Hashtable;

public class CdiscQueries {

	private static final String BRIDG_ROOT = "DataEntity";
	private static String PREFIXES;
		
	public static String getChildrenQueryString(String uri) {
		String queryString = "SELECT DISTINCT  ?child ?childTitle"
				+ " { GRAPH <http://who.int/icd> "
				+ " {  "
				+ "    ?child <http://www.w3.org/2000/01/rdf-schema#subClassOf>   <"+ uri + "> ."
				+ "    ?child <http://who.int/icd#icdTitle> ?childTitleObj ."
				+ "    ?childTitleObj <http://who.int/icd#label> ?childTitle ."
				+ "} } ";  

		return queryString;
	}
	
	public static String getCategoryParentsQueryString(String uri) {

		String queryString = "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
				+ "    SELECT DISTINCT *"
				+ "      { GRAPH <http://who.int/icd>"
				+ "        { <"
				+ uri
				+ "> <http://localhost:2020/vocab/resource/DIRECT-SUPERCLASSES> ?parentId. "
				+ "           ?parentId rdfs:label ?label"
				+ "      } }  ORDER BY ?label ";

		return queryString;
	}
	
	public static String getMetadataQueryString(String object) {

		String queryString = getPrefixes() 
				+ " SELECT ?predicate ?object { \n"
				+ "	   GRAPH <http://www.bridgmodel.org/owl/3.2/>  \n"
				+ "    { \n";
		
			System.out.println("Incoming object=" + object);
			String prf4val = CdiscQueries.getPrefixKeyForValue(object.split("#")[0]);
			if (prf4val != null)
				queryString += " " + prf4val + ":" + object.split("#")[1];
			else
				queryString += "       bridg:" + object;
			
			queryString += " ?predicate ?object . \n"	
						 + "     } \n"
						 + "}"
						 + "ORDER BY ASC(?object)";
				
		System.out.println(queryString);
		
		return queryString;
	}
			
	public static String getCategoryQueryString(String parent) {
				
		String queryString = getPrefixes() 
				+ " SELECT DISTINCT ?child ?childLabel \n"
				+ "	WHERE { ?child rdfs:subClassOf ?parent . \n"
				+ "        ?child rdfs:label ?childLabel . \n"
				+ "        ?parent owl:intersectionOf ?o . \n"
				+ "        ?o  rdf:first bridg:" + parent +" . \n"
				+ "}"
				+ "ORDER BY ASC(?childLabel)";
				
		System.out.println(queryString);
		
		return queryString;
	}
	
	public static String getCategoryQueryString2(String parent) {
		
		// a different query to try to get children when the first query 
		// returns no results.
		String queryString = getPrefixes() 
				+ " SELECT DISTINCT ?child ?childLabel ?parent ?parentLabel \n"
				+ "	WHERE { ?child rdfs:subClassOf bridg:" + parent + " . \n"
				+ "        ?child rdfs:label ?childLabel . \n"
				+ "        bridg:" + parent + " rdfs:label ?parentLabel .  \n"
				+ "}"
				+ "ORDER BY ASC(?childLabel)";
				
		System.out.println(queryString);
		
		return queryString;
	}
	
	public static String getAssociationsQueryString(String parent) {
		
		String queryString = getPrefixes() 
				+ " SELECT DISTINCT  ?associationLabel ?childLabel ?targetClassLabel  ?association \n"
				+ "	WHERE {  \n"
				+ "        bridg:" + parent +" bridg:associationProperty ?association .\n"
				+ "        ?association rdfs:label ?associationLabel . \n"
				+ "        ?association bridg:roleName ?childLabel . \n"
				+ "        ?association rdfs:range ?targetClass . \n"
				+ "        ?targetClass rdfs:label ?targetClassLabel .\n"
				+ "}\n"
				+ "ORDER BY DESC(?association)";
		
		System.out.println(queryString);
		
		return queryString;
	}
	
	public static String getAttributesQueryString(String parent) {
		
		String queryString = getPrefixes() 
				+ " SELECT   * { \n"
				+ "	GRAPH <http://www.bridgmodel.org/owl/3.2/>  \n"
				+ "    {"
				+ "        ?association rdfs:domain bridg:" + parent +" .\n"
				+ "        ?association rdfs:label ?childLabel . \n"
				+ "        ?association skos:definition ?definition . \n"
				+ "        ?association rdfs:range ?datatype . \n"
				+ "        ?association rdfs:subPropertyOf bridg:attributeProperty .\n"
				+ "     } \n"
				+ "}\n"
				+ "ORDER BY ASC(?childLabel)";
				
		System.out.println(queryString);
		
		return queryString;
	}
		
	public static String getDataTypesQueryString(String parent) {
		
		String queryString = getPrefixes() 
				+ " SELECT distinct  * { \n"
				+ "	GRAPH <http://www.bridgmodel.org/owl/3.2/>  \n"
				+ "    {"
			    + "      bridg:" + parent + " rdfs:range ?datatype . \n"
				+ "     } . \n"
			    + " GRAPH <http://www.hl7.org/owl/iso-dt-2.0/> \n"
				+ "     { \n"
			    + "       ?component rdfs:domain ?datatype . \n"
				+ "       OPTIONAL { ?component rdfs:range ?datatypeOfComponent . }"
				+ "     } \n"
				+ "}\n"
				+ "ORDER BY ASC(?component)";
				
		System.out.println(queryString);
		
		return queryString;
	}
	
	public static String getSingleDataTypeQueryString(String parent) {
		
		String queryString = getPrefixes() 
				+ " SELECT distinct  * { \n"
				+ "	GRAPH <http://www.bridgmodel.org/owl/3.2/>  \n"
				+ "    {"
			    + "      bridg:" + parent + " rdfs:range ?datatype . \n"
				+ "     } . \n"
			    + "}\n";
				
		System.out.println(queryString);
		
		return queryString;
	}
	
	public static String getSubComponentsQueryString(String component) {
		
		String queryString = getPrefixes() 
				+ " SELECT distinct  * { \n"
				+ "	GRAPH <http://www.hl7.org/owl/iso-dt-2.0/>   \n"
				+ "    {"
				+ "        ?component rdfs:domain dt:" + component + " .\n"
				+ "        OPTIONAL { ?component rdfs:range ?datatypeOfComponent . }\n"
				+ "     } \n"
				+ "}\n"
				+ "ORDER BY ASC(?component)";
				
		System.out.println(queryString);
		
		return queryString;
	}
	

	public static String getRootCategoryQueryString() {
		return getCategoryQueryString(BRIDG_ROOT);
	}
	
	public static String getPrefixes() {
		
		System.out.println("### Using HashMap for Prefixes... ###");
		if (PREFIXES == null) {
			
			StringBuilder sb = new StringBuilder();
			
			for (String k : getPrefixHashMap().keySet())
				sb.append("PREFIX " + k + ": <" + getPrefixHashMap().get(k) + "> \n");
			
//			sb.append("PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>\n");  
//			sb.append("PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>\n"); 
//			sb.append("PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"); 
//			sb.append("PREFIX owl:     <http://www.w3.org/2002/07/owl#> \n");
//
//			sb.append("PREFIX bridg:   <http://www.bridgmodel.org/owl#> \n");
//			sb.append("PREFIX dct:     <http://purl.org/dc/terms/> \n");
//			sb.append("PREFIX dt:      <http://www.hl7.org/owl/iso-dt-2.0#> \n");
//			sb.append("PREFIX foaf:    <http://xmlns.com/foaf/0.1/> \n");
//			sb.append("PREFIX skos:    <http://www.w3.org/2004/02/skos/core#> \n");
//			sb.append("PREFIX xml:     <http://www.w3.org/XML/1998/namespace> \n");
//			sb.append("PREFIX xs:      <http://www.w3.org/2001/XMLSchema> \n");
			
			PREFIXES = sb.toString();
			
		}
		return PREFIXES;
	}
	
	public static Hashtable<String, String> getPrefixHashMap()
	{
		if (CdiscQueries.prefixesWithNS.isEmpty())
		{
			CdiscQueries.prefixesWithNS.put("xsd","http://www.w3.org/2001/XMLSchema#");  
			CdiscQueries.prefixesWithNS.put("rdfs","http://www.w3.org/2000/01/rdf-schema#"); 
			CdiscQueries.prefixesWithNS.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#"); 
			CdiscQueries.prefixesWithNS.put("owl","http://www.w3.org/2002/07/owl#");
			CdiscQueries.prefixesWithNS.put("bridg","http://www.bridgmodel.org/owl#");
			CdiscQueries.prefixesWithNS.put("dct","http://purl.org/dc/terms/");
			CdiscQueries.prefixesWithNS.put("dt","http://www.hl7.org/owl/iso-dt-2.0#");
			CdiscQueries.prefixesWithNS.put("foaf","http://xmlns.com/foaf/0.1/");
			CdiscQueries.prefixesWithNS.put("skos","http://www.w3.org/2004/02/skos/core#");
			CdiscQueries.prefixesWithNS.put("xml","http://www.w3.org/XML/1998/namespace");
			CdiscQueries.prefixesWithNS.put("xs","http://www.w3.org/2001/XMLSchema");
		}
		
		return CdiscQueries.prefixesWithNS;
	}
	
	public static String getPrefixKeyForValue(String value)
	{
		System.out.println("looking for:" + value);
		
		if ((value == null)||("".equalsIgnoreCase(value.trim())))
			return null;
		
		for (String k : getPrefixHashMap().keySet())
			if (value.equalsIgnoreCase(getPrefixHashMap().get(k)))
				return k;
		
		if (!value.endsWith("#"))
			return CdiscQueries.getPrefixKeyForValue(value + "#");
		
		return null;
	}
	
	private static Hashtable<String, String> prefixesWithNS = new Hashtable<String, String>();
	
}
