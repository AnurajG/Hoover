package com.hoover.util;

import com.hoover.util.Hoov;

public class QueryBuilder {

	/**
	 * Specify your database name here
	 * @return
	 */
	public String getDatabaseName() {
		return "hoover";
	}

	/**
	 * Specify your MongoLab API here
	 * @return
	 */
	public String getApiKey() {
		return "zvbjTNUW6COSTIZxJcPIW7_tniVCnDKC";
	}

	/**
	 * This constructs the URL that allows you to manage your database,
	 * collections and documents
	 * @return
	 */
	public String getBaseUrl()
	{
		return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
	}

	/**
	 * Completes the formating of your URL and adds your API key at the end
	 * @return
	 */
	public String docApiKeyUrl()
	{
		return "?apiKey="+getApiKey();
	}

	/**
	 * Returns the docs101 collection
	 * @return
	 */
	public String documentRequest()
	{
		return "hoov";
	}

	/**
	 * Builds a complete URL using the methods specified above
	 * @return
	 */
	public String buildContactsSaveURL()
	{
		return getBaseUrl()+documentRequest()+docApiKeyUrl();
	}

	/**
	 * Formats the contact details for MongoHQ Posting
	 * @param contact: Details of the person
	 * @return
	 */
	public String createContact(Hoov hoov)
	{
		return String
				.format("{\"document\" : {\"id\": \"%s\", "
						+ "\"company\": \"%s\", \"city\": \"%s\", "
						+ "\"hoov\": \"%s\"}, \"safe\" : true}",
						hoov.id, hoov.company, hoov.city, hoov.hoov);
	}

}