package com.heliumhq.inttest_api;

import org.junit.Test;

import static com.heliumhq.API.*;
import static com.heliumhq.Environment.getIntegrationtestResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FileUploadIT extends BrowserAT {

	private final String fileToUpload = getIntegrationtestResource(
			"inttest_file_upload/upload_this.png"
	);

	@Override
	protected String getPage() {
		return "inttest_file_upload/inttest_file_upload.html";
	}

	@Test
	public void testNormalFileUploadIsNotTextField() {
		assertFalse(TextField("Normal file upload").exists());
	}

	@Test
	public void testAttachFileToNormalFileUpload() throws InterruptedException {
		attachFile(fileToUpload, to("Normal file upload"));
		assertEquals("Success!", readResultFromBrowser());
	}

	@Test
	public void testAttachFileNoTo() throws InterruptedException {
		attachFile(fileToUpload);
		assertEquals("Success!", readResultFromBrowser());
	}

	@Test
	public void testAttachFileToPoint() throws InterruptedException {
		attachFile(
				fileToUpload,
				to(Text("Normal file upload").getTopLeft().withOffset(200, 10))
		);
		assertEquals("Success!", readResultFromBrowser());
	}

	@Test
	public void testDragFileToAppearingDropArea() throws InterruptedException {
		dragFile(fileToUpload, to("Drop the file here!"));
		assertEquals("Success!", readResultFromBrowser());
	}

}