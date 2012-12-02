package com.education.lessons.ui.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.education.lessons.ui.client.utils.IConstants;

public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String key = req.getParameter("key");
		String filePath = req.getParameter("filePath");

		if (key == null || key.isEmpty() || filePath == null || filePath.isEmpty())
			return;

		String suggestedFileName = extractSuggestedFileName(filePath);

		if (IConstants.PDF.equals(key)) {
			resp.setContentType("application/pdf");
			resp.setHeader("Content-Disposition", "attachment; filename=\""+ suggestedFileName);
		} else if (IConstants.EXCEL.equals(key)) {
			resp.setContentType("application/ms-excel");
			resp.setHeader("Content-Disposition", "attachment; filename=\""+ suggestedFileName);
		}else if (IConstants.DOC.equals(key)) {
			resp.setContentType("application/ms-doc");
			resp.setHeader("Content-Disposition", "attachment; filename=\""+ suggestedFileName);
		}

		OutputStream out = resp.getOutputStream();
		File f = new File(filePath);
		if (f != null) {
			BufferedInputStream from = new BufferedInputStream(new FileInputStream(f));
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			from.close();
		}
		resp.flushBuffer();
	}

	private String extractSuggestedFileName(String filePath) {
		int lastSlash = filePath.lastIndexOf("/");
		int lastBackSlash = filePath.lastIndexOf("\\");
		int start = lastSlash >= lastBackSlash ? lastSlash:lastBackSlash;

		return filePath.substring(start+1, filePath.length());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
