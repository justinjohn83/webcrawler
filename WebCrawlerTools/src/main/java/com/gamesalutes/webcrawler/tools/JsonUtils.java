package com.gamesalutes.webcrawler.tools;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;

import org.apache.commons.io.IOUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

public final class JsonUtils {

	private JsonUtils() {}
	
	public static void toJson(Object object,Writer jsonOut) {
		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat(DateFormat.FULL);
//		builder.serializeNulls();
		builder.disableHtmlEscaping();
		builder.setPrettyPrinting();
	    builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		//builder.setVersion(1.0);
		
		Gson gson = builder.create();
		try {
			JsonWriter jsonWriter = new JsonWriter(jsonOut);
			jsonWriter.setIndent("    ");
			
			gson.toJson(object,object.getClass(),jsonWriter);
			
			jsonOut.flush();
		}
		catch(IOException e) {
			throw new IllegalArgumentException(e);
		}
		finally {
			IOUtils.closeQuietly(jsonOut);
		}
	}
}
