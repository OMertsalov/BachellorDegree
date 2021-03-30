package com.example.demo.model;

public class Results {

	private String imageBase64;
	private String testText;
	private String resultText;

	public Results(String imageBase64, String testText, String resultText) {
		this.imageBase64 = imageBase64;
		this.testText = testText;
		this.resultText = resultText;
	}

	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}

	public String getTestText() {
		return testText;
	}

	public void setTestText(String testText) {
		this.testText = testText;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

}
