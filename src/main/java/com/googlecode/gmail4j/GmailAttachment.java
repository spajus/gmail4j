package com.googlecode.gmail4j;

import java.io.InputStream;

/**
 * Message attachement.
 *
 */
public class GmailAttachment {
	
	private int partIndex;
	private String fileName;
	private String contentType;
	private InputStream data;
	
	public GmailAttachment(int partIndex, String fileName, String contentType, InputStream data) {
		super();
		this.partIndex = partIndex;
		this.fileName = fileName;
		this.contentType = contentType;
		this.data = data;
	}

	public int getPartIndex() {
		return partIndex;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}
	
	public InputStream getData() {
		return data;
	}

	@Override
	public String toString() {
		return "GmailAttachment [partIndex=" + partIndex + ", fileName="
				+ fileName + ", contentType=" + contentType + "]";
	}
}
