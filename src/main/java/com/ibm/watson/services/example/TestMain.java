package com.ibm.watson.services.example;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class TestMain {
	
	OkHttpClient client = new OkHttpClient();
	final String URL = "https://gateway.watsonplatform.net/language-translation/api/v2/";
	
	//You need to substitute the user, password associated with the language translation service binded to the app on bluemix
	String apiKey = Credentials.basic("service user", "service password"); 
	
	// post request code here
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
	public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
	public static final MediaType URLENC = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

	// code request code here
	String doGetRequest(String resource) throws IOException {
		Request request = new Request.Builder()
				.url(URL + resource)
				.header("Authorization", apiKey)
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	String doGetRequestIdentify(String text) throws IOException {

		HttpUrl url = HttpUrl.parse(URL).newBuilder()
                .addPathSegment("identify")
                .addQueryParameter("text", text)
                .build();
		
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", apiKey)
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	String doPostRequestIdentify(String text) throws IOException {

		HttpUrl url = HttpUrl.parse(URL).newBuilder()
                .addPathSegment("identify")
                .build();
		
		RequestBody body = RequestBody.create(TEXT, text);
		
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", apiKey)
				.header("Accept", "application/json")
				.post(body)
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	String doGetRequestTranslate(String text, String source, String target) throws IOException {

		HttpUrl url = HttpUrl.parse(URL).newBuilder()
                .addPathSegment("translate")
                .addQueryParameter("text", text)
                .addQueryParameter("source", source)
                .addQueryParameter("target", target)
                .build();
		
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", apiKey)
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}


	String doPostRequestTranslate(String text, String model) throws Exception {

		HttpUrl url = HttpUrl.parse(URL).newBuilder()
                .addPathSegment("translate")
                .build();
		
		RequestBody body = RequestBody.create(JSON, languageJson(text, model));
		
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", apiKey)
				.header("Accept", "application/json")
				.post(body)
				.build();


		Response response = client.newCall(request).execute();

		return response.body().string();
	}
	
	String doPostRequestCreateModel(String base, String file) throws IOException {

		HttpUrl url = HttpUrl.parse(URL).newBuilder()
                .addPathSegment("models")
                .build();
		
		RequestBody body = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
                .addFormDataPart("base_model_id",  base)
                .addFormDataPart("forced_glossary", file, 
                		RequestBody.create(XML, new File( 
                				this.getClass().getClassLoader().getResource(file).getFile())))
                .build();
		
		Request request = new Request.Builder()
				.url(url)
				.header("Authorization", apiKey)
				.post(body)
				.build();


		Response response = client.newCall(request).execute();
//		JSONObject json = new JSONObject(response.body().toString());
		
		return response.body().string();
	}
	
	// code request code here
	String doGetRequestPollModel(String id) throws IOException {
		
		return doGetRequest("models/" + id);
	}
	
	String doDeleteRequestDeleteModel(String id) throws IOException {
		Request request = new Request.Builder()
				.url(URL + "models/" + id)
				.header("Authorization", apiKey)
				.delete()
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}
	
	// test data
	String languageJson(String text, String model) throws JSONException {

		JSONObject obj = new JSONObject();
		obj.put("model_id", model);
		obj.put("text", text);
		return obj.toString();
	}



	public static void main(String[] args) throws Exception {

		// issue the Get request
		TestMain example = new TestMain();
		
		//identifiable_languages
		String getResponse = example.doGetRequest("identifiable_languages");
		System.out.println(getResponse);

		//identify
		getResponse = example.doGetRequestIdentify( "hola, gracias");
		System.out.println(getResponse);

		getResponse = example.doPostRequestIdentify( "bonjour");
		System.out.println(getResponse);
		
		//available models
		getResponse = example.doGetRequest("models");
		System.out.println(getResponse);

		//translate
		getResponse = example.doGetRequestTranslate("hola, gracias", "es", "en");
		System.out.println(getResponse);
		
		getResponse = example.doPostRequestTranslate("bonjour", "fr-en");
		System.out.println(getResponse);
		
		//create profile
		getResponse = example.doPostRequestCreateModel("en-es", "esen.tmx");
		System.out.println(getResponse);
		String id = new JSONObject(getResponse).getString("model_id");
		
		//poll profile
		getResponse = example.doGetRequestPollModel(id);
		System.out.println(getResponse);
		
		//delete profile
		getResponse = example.doDeleteRequestDeleteModel(id);
		System.out.println(getResponse);

		
	}
} 