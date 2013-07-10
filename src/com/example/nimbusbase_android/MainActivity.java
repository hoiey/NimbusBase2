package com.example.nimbusbase_android;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	private static final String ACTION_DRIVE_OPEN = "com.google.android.apps.drive.DRIVE_OPEN";
	private static final String EXTRA_FILE_ID = "resourceId";
	private static final int REQUEST_ACCOUNT_PICKER = 1;
	private static final int REQUEST_AUTHORIZATION = 2;
	private static final int CAPTURE_IMAGE = 3;
	private GoogleAccountCredential credential;
	private static Drive service;
	private static Uri fileUri;

	public Button btn;
	public TextView tv;
	
	private String mFileId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView)findViewById(R.id.textView1);
		btn = (Button)findViewById(R.id.button1);
		
		tv.setText(mFileId);
		

		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getUserAccountAndProcessFile();
			}
		});
	}
	
	 @SuppressWarnings("deprecation")
	private void getUserAccountAndProcessFile() {
		 credential = GoogleAccountCredential.usingOAuth2(this, DriveScopes.DRIVE);
		 startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}
	 
	 @Override
	 protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	   switch (requestCode) {
	     case REQUEST_ACCOUNT_PICKER:
	       if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
	         String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
	         if (accountName != null) {
	           credential.setSelectedAccountName(accountName);
	           service = getDriveService(credential);
	           readFileByList();
	           //saveFileToDrive();
	           //startCameraIntent();
	           //readFileFromDrive();
	           tv.setText("REQUEST_ACCOUNT_PICKER");
	           
	         }
	       }
	       break;
	     case REQUEST_AUTHORIZATION:
	         if (resultCode == Activity.RESULT_OK) {
	        	 tv.setText("REQUEST_AUTHORIZATION");
	        	 //readFileFromDrive();
	        	 saveFileToDrive();
	           } else {
	             startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	           }	    	
	    	break;
	     case CAPTURE_IMAGE:
	         if (resultCode == Activity.RESULT_OK) {
	        	 readFileFromDrive();
	           //saveFileToDrive();
	         }
	   }
	   
	}
	 
	 
	  private void readFileFromDrive(){
		  Thread t = new Thread(new Runnable() {
			  @Override
		      public void run() {
				  try {
					  File file = service.files().get("new.txt").execute();
					  if(file.getDownloadUrl()!=null&&file.getDownloadUrl().length()>0){
						  HttpResponse resp = service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
						  InputStream stream = resp.getContent();
						  StringBuilder sb = inputStreamToStringBuilder(stream);
						  showText(sb);
					  }
				  }
					  catch (UserRecoverableAuthIOException e) {
						  startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
					  }
					  catch (IOException e) {
		            e.printStackTrace();
		            }
					  }
				  });
		      t.start();	  
	  }
	  
	  
	  private void readFileByList(){
		  Thread t = new Thread(new Runnable() {
			  @Override
		      public void run() {
				// TODO Auto-generated method stub
				try {
		        	Files.List  request_1 = service.files().list().setQ("mimeType = 'application/vnd.google-apps.folder' and title = 'tweetdiary'");
		        	Map<String, File> textFiles_1 = new HashMap<String, File>();
		        	
					do {
					      try {
					        FileList files = request_1.execute();

					        for (File file : files.getItems()) {
					          textFiles_1.put(file.getId(), file);
					        }
					        request_1.setPageToken(files.getNextPageToken());
					      } catch (IOException e) {
					    	  e.printStackTrace();
					      }
					    } while (request_1.getPageToken() != null && request_1.getPageToken().length() > 0);
					
					
					for(Map.Entry<String, File> entry:textFiles_1.entrySet()){
						File file= service.files().get(entry.getKey()).execute();
						Children.List request_2 = service.children().list(file.getId()).setQ("mimeType = 'application/vnd.google-apps.folder' and title = 'Entry'");
						Map<String, ChildReference> textFiles_2 = new HashMap<String, ChildReference>();
						
						do {
						      try {
						        ChildList files = request_2.execute();

						        for (ChildReference file1 : files.getItems()) {
						          textFiles_2.put(file1.getId(), file1);
						        }
						        request_2.setPageToken(files.getNextPageToken());
						      } catch (IOException e) {
						    	  e.printStackTrace();
						      }
						} while (request_2.getPageToken() != null && request_2.getPageToken().length() > 0);
						
						for(Map.Entry<String, ChildReference> entry2:textFiles_2.entrySet()){
							File file1= service.files().get(entry2.getKey()).execute();
							Children.List request_c = service.children().list(file1.getId());
							
							ChildList children = request_c.execute();
							
						      for (ChildReference child : children.getItems()) {
						    	  
									File file2= service.files().get(child.getId()).execute();
									if(file2.getDownloadUrl()!=null&&file2.getDownloadUrl().length()>0){
										  HttpResponse resp = service.getRequestFactory().buildGetRequest(new GenericUrl(file2.getDownloadUrl())).execute();
										  InputStream stream = resp.getContent();
										  StringBuilder sb = inputStreamToStringBuilder(stream);
										  showText(sb);
									}
						      }
						}

					}						
					
			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
		  });
      t.start();	
	  }
	  
	  
//	  private void readFile(Map<String, File> Filess){
//		 
//		  for(Map.Entry<String, File> entry:Filess.entrySet()){
//			try {
//				File file= service.files().get(entry.getKey()).execute();
//				if(file.getDownloadUrl()!=null&&file.getDownloadUrl().length()>0){
//					  HttpResponse resp = service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
//					  InputStream stream = resp.getContent();
//					  StringBuilder sb = inputStreamToStringBuilder(stream);
//					  showText(sb);
//				}
//			}
//				catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			  			  
//		  }
//	  }
	  
  
	@SuppressLint("SdCardPath")
	private void saveFileToDrive() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // File's binary content
        	Files.List  request_1 = service.files().list().setQ("mimeType = 'application/vnd.google-apps.folder' and title = 'tweetdiary'");
        	Map<String, File> textFiles_1 = new HashMap<String, File>();
        	
			do {
			      try {
			        FileList files = request_1.execute();

			        for (File file : files.getItems()) {
			          textFiles_1.put(file.getId(), file);
			        }
			        request_1.setPageToken(files.getNextPageToken());
			      } catch (IOException e) {
			    	  e.printStackTrace();
			      }
			    } while (request_1.getPageToken() != null && request_1.getPageToken().length() > 0);
			
			
			for(Map.Entry<String, File> entry:textFiles_1.entrySet()){
				File file= service.files().get(entry.getKey()).execute();
				Children.List request_2 = service.children().list(file.getId()).setQ("mimeType = 'application/vnd.google-apps.folder' and title = 'Entry'");
				Map<String, ChildReference> textFiles_2 = new HashMap<String, ChildReference>();
				
				do {
				      try {
				        ChildList files = request_2.execute();

				        for (ChildReference file1 : files.getItems()) {
				          textFiles_2.put(file1.getId(), file1);
				        }
				        request_2.setPageToken(files.getNextPageToken());
				      } catch (IOException e) {
				    	  e.printStackTrace();
				      }
				} while (request_2.getPageToken() != null && request_2.getPageToken().length() > 0);
				
				for(Map.Entry<String, ChildReference> entry2:textFiles_2.entrySet()){
					File file1= service.files().get(entry2.getKey()).execute();
					File body = new File();
		            body.setTitle("My document.txt");
		            body.setDescription("A test document");
		            body.setMimeType("text/plain");
		            body.setParents(Arrays.asList(new ParentReference().setId(file1.getId())));
		            
		            java.io.File fileContent = new java.io.File("/mnt/sdcard/text.txt");
		            FileContent mediaContent = new FileContent("text/plain", fileContent);
		            
		            File file3 = service.files().insert(body, mediaContent).execute();
		            insertFileIntoFolder(service, file1.getId(), file3.getId());	
					
				}			     
			}
				
				
			} catch (UserRecoverableAuthIOException e) {
          startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
  }
	

	
	private static File createPublicFolder(Drive service, String folderName) throws IOException {
		  File body = new File();
		  body.setTitle(folderName);
		  body.setMimeType("application/vnd.google-apps.folder");

		  File file = service.files().insert(body).execute();

		  Permission permission = new Permission();
		  permission.setValue("");
		  permission.setType("anyone");
		  permission.setRole("reader");

		  service.permissions().insert(file.getId(), permission).execute();
		  

		  return file;
		}
	
	
	  private static ChildReference insertFileIntoFolder(Drive service, String folderId, String fileId) {
		    ChildReference newChild = new ChildReference();
		    newChild.setId(fileId);
		    try {
		      return service.children().insert(folderId, newChild).execute();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		    return null;
		  }
	  
	  
	  private StringBuilder inputStreamToStringBuilder(InputStream stream){
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				while((line = br.readLine()) != null){
					sb.append(line);
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  return sb;
	  }
	  
	  private void showText(StringBuilder sb){
		  final String string = sb.toString();
		  runOnUiThread(new Runnable(){
		      @Override
		      public void run() {
		    	  tv.setText(string);
		      } 
		  });
	  }
	 
	 private Drive getDriveService(GoogleAccountCredential credential) {
		    return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		        .build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void showToast(final String toast) {
	    runOnUiThread(new Runnable() {
	      @Override
	      public void run() {
	        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
	      }
	    });
	  }

}
