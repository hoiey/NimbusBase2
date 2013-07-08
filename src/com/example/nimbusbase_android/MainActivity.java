package com.example.nimbusbase_android;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
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
		
//		   final Intent intent = getIntent();
//		   final String action = intent.getAction();
//
//		   // Make sure the Action is DRIVE_OPEN.
//		   if (ACTION_DRIVE_OPEN.equals(action)) {
//		     // Get the Drive file ID.
//		     mFileId = intent.getStringExtra(EXTRA_FILE_ID);
//		     getUserAccountAndProcessFile();
//		   }
		
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
//		 throw new UnsupportedOperationException(
//				 "The getUserAccountAndProcessFile method has not been implemented");
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
	           saveFileToDrive();
	           //startCameraIntent();//readFileFromDrive();
	           tv.setText("REQUEST_ACCOUNT_PICKER");
	           
	         }
	       }
	       break;
	     case REQUEST_AUTHORIZATION:
	         if (resultCode == Activity.RESULT_OK) {
	        	 tv.setText("REQUEST_AUTHORIZATION");
	        	 saveFileToDrive();
	           } else {
	             startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	           }	    	
	    	break;
	     case CAPTURE_IMAGE:
	         if (resultCode == Activity.RESULT_OK) {
	           saveFileToDrive();
	         }
	   }
	   
	}
	 
	 
	  private void readFileFromDrive(){
		  Thread t = new Thread(new Runnable() {
			  @Override
		      public void run() {
				  try {
					  com.google.api.services.drive.model.File file = service.files().get("new.txt").execute();
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
	  
//	  private void startCameraIntent() {
//		    String mediaStorageDir = Environment.getExternalStoragePublicDirectory(
//		        Environment.DIRECTORY_PICTURES).getPath();
//		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//		    fileUri = Uri.fromFile(new java.io.File(mediaStorageDir + java.io.File.separator + "IMG_"
//		        + timeStamp + ".jpg"));
//
//		    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//		    startActivityForResult(cameraIntent, CAPTURE_IMAGE);
//		  }
	  
	private void saveFileToDrive() {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // File's binary content
        	
            File body = new File();
            body.setTitle("My document");
            body.setDescription("A test document");
            body.setMimeType("text/plain");
            
            java.io.File fileContent = new java.io.File("document.txt");
            FileContent mediaContent = new FileContent("text/plain", fileContent);

          File file = service.files().insert(body, mediaContent).execute();
//          if (file != null) {
//            showToast("Photo uploaded: " + file.getTitle());
//            startCameraIntent();
//          }
        } catch (UserRecoverableAuthIOException e) {
          startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
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
