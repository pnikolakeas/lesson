package com.education.lessons.ui.server;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private boolean isMultipart;
	private String filePath;
	private int maxMemSize = 12 * 1024;
	private File file ;

	@Override
	public void init() throws ServletException {
		filePath = getServletContext().getInitParameter("file-upload");
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		      isMultipart = ServletFileUpload.isMultipartContent(request);
		      response.setContentType("text/html");
		      if( isMultipart ){
		        
			      DiskFileItemFactory factory = new DiskFileItemFactory();
			      // maximum size that will be stored in memory
			      factory.setSizeThreshold(maxMemSize);
			      // Location to save data that is larger than maxMemSize.
			      factory.setRepository(new File(filePath));
			      // Create a new file upload handler
			      ServletFileUpload upload = new ServletFileUpload(factory);
			      // maximum file size to be uploaded.
			      try{ 
				      // Parse the request to get file items.
				    @SuppressWarnings("unchecked")
					List<FileItem> fileItems = upload.parseRequest(request);
				   
				    // Process the uploaded file items
				    String fileInputParameter=null;
				    Iterator<FileItem> i = fileItems.iterator();
				    while ( i.hasNext () ) {
			          FileItem fi = (FileItem)i.next();
			          if ( !fi.isFormField () )	{
			            // Get the uploaded file parameters
			            String fileName = fi.getName();
			            // Write the file
			            if( fileName.lastIndexOf("\\") >= 0 ){
			            	fileInputParameter=filePath + fileName.substring( fileName.lastIndexOf("\\"));
			            	file = new File(fileInputParameter) ;
			            }else{
			               fileInputParameter=filePath + fileName.substring(fileName.lastIndexOf("\\")+1);
			               file = new File(fileInputParameter) ;
			            }
			            fi.write( file ) ;
			          }
				    }
				    response.setContentType("text/html");
			        response.getWriter().print(fileInputParameter);
			        
			   }catch(Exception ex) {
			       System.out.println(ex);
			   }
		 }
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
