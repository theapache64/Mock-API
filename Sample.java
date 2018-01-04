package com.glowsis.glowmetric.api.responses;

import com.google.gson.annotations.SerializedName;

//Developed using mock api

public class Sample {

	@SerializedName("error")
	private final boolean error;

	@SerializedName("message")
	private final String message;

	@SerializedName("data")
	private final Data data;

	public Sample(boolean error,String message,Data data){
		this.error = error;
		this.message = message;
		this.data = data;
	}