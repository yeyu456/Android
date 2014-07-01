package com.simplefileexplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends Activity {
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    private SimpleButtonAdapter mAdapter;
    private File mRootFile;
    private static int mBottomBarVisiable = View.GONE;
    private static String mPasteSourceFilePath = "";
    public enum Action { NONE, COPY, MOVE };
    private static Action mCopyOrMove = Action.NONE;
    
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main_activity);
        sdf.setTimeZone(TimeZone.getDefault());
        if(getIntent().hasExtra("path")){
            mRootFile = new File(getIntent().getExtras().getString("path"));
        }
        else{
            mRootFile = new File("/");
        }
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	((View) this.findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
        ListView v = (ListView) findViewById(R.id.main_activity_listview);
        
        SimpleButtonAdapter simpleButtonAdapter 
        		= new SimpleButtonAdapter(this, 
        								  generateData(mRootFile),
        								  R.layout.file_list,
        								  new String[]{"name", "image", "atr", "path"},
        								  new int[]{R.id.file_list_button});
        mAdapter = simpleButtonAdapter;
        v.setAdapter(simpleButtonAdapter);
        	
        v.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        		
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Map<String, String> file = (Map<String, String>) parent.getItemAtPosition(position);
                onNewIntent(file);
            }
        });
        	
        v.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onOptionsDialog(parent, position);
                return true;
            }
        });
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	mAdapter.empty();
    	mAdapter = null;
    	mRootFile = null;
    }
    
    private List<Map<String, String>> generateData(File mRootFile){
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
        for(File f:mRootFile.listFiles()){
            list.add(createFileData(f));
        }
        return list;
    }
    
    private Map<String, String> createFileData(File f){
    	Map<String, String> mFile = new HashMap<String, String>();
        
        Integer drawId = f.isDirectory() ? R.drawable.isdirectory : R.drawable.isfile;
        String atr = sdf.format(f.lastModified()) + " " + getFileLen(f.length());
        String path = f.getAbsolutePath();
        
        mFile.put("name", f.getName());
        mFile.put("image", drawId.toString());
        mFile.put("atr", atr);
        mFile.put("path", path);
        return mFile;
    }
    
    private String getFileLen(long len){
        final String[] units = new String[]{"Bytes", "K", "M", "G", "T"};
        double result = (double) len;
        for(int n=0;n<units.length;n++){
            if(result/1024>1){
                result = result / 1024;
                continue;
            }
            else{
                return String.format("%.2f", result) + units[n];
            }
        }
        return Long.toString(len) + units[4];
    }
    
    public void onNewIntent(Map<String, String> file){
        File f = new File(file.get("path"));
        if(f.isFile()){
        	Intent intent = new Intent("android.intent.action.VIEW");
        	intent.setDataAndType(Uri.fromFile(f), 
        						  MimeTypeMap
        						  	.getSingleton()
        						  	.getMimeTypeFromExtension(fileExt(f.toString()).substring(1)));
        	try {
        	    startActivity(intent);
        	} catch (android.content.ActivityNotFoundException e) {
        	    showMessages(R.string.MainActivity_onNewIntent_handleFileFailed);
        	}
        }
        else{
        	if(f.canExecute()&&f.canRead()){
        		Intent intent = new Intent(this, MainActivity.class);
        		intent.putExtra("path", file.get("path"));
        		startActivity(intent);
        	}
        	else{
        		showMessages(R.string.MainActivity_onNewIntent_noDirectoryPermission);
        	}
        }
    }
    
    private String fileExt(String url) {
        if (url.indexOf("/")>-1) {
            url = url.substring(url.lastIndexOf("/")+1);
            System.out.println(url);
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            return url.substring(url.lastIndexOf(".")+1);
        }
    }
    
    public void onOptionsDialog(AdapterView<?> parent, final int position){
    	final String path = ((Map<String, String>) parent.getItemAtPosition(position)).get("path");
    	AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(R.string.dialog_title)
          .setItems(R.array.dialog_options, new DialogInterface.OnClickListener() {
        	  
        	  @Override
              public void onClick(DialogInterface dialog, int which) {
        		  switch(which){
        		  		case 0 : {
        		  			copyOrMove(path, Action.COPY);
        		  			break;
        		  		}
        		  		case 1 : {
        		  			copyOrMove(path, Action.MOVE);
        		  			break;
        		  		}
        		  		case 2 : {
        		  		   rename(path, position);
        		  		   break;
        		  		}
        		  		case 3 : {
                		   delete(path, position);
                		   break;
                	    }
                	    default :{
                		   Log.e("1", "error");
                	    }
                  }
              }
        });
        ab.show();
    }
    
    private void delete(String path, final int position){
    	final File f = new File(path);
    	if(f.canWrite()){
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle(R.string.dialog_delete)
    			   .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		       			if(f.delete()){
    		       				mAdapter.remove(position);
    		       			}
    		           }
    		       })
    		       .setNegativeButton(R.string.dialog_cancel, null)
    		       .show();
    	}
    	else{
    		showMessages(R.string.MainActivity_delete_noDeletePermission);
    	}
    }
    
    private void rename(String path, final int position){
    	final File f = new File(path);
    	if(f.canWrite()&&f.getParentFile().canWrite()){
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		
    		View dialogRenameView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
    									.inflate(R.layout.rename_dialog, null);
    		
    		builder.setTitle(R.string.dialog_rename)
    			   .setView(dialogRenameView);
    		
    		final EditText newName 
    						= (EditText) dialogRenameView.findViewById(R.id.rename_dialog);
    		
    		builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
    		           public void onClick(DialogInterface dialog, int id) {
    		        	   if(f.renameTo(new File(f.getParent(), newName.getText().toString()))){
    		        		   mAdapter.edit(position, 
    		       							 f.getParent() + newName.getText().toString(), 
    		       							 newName.getText().toString());
    		        	   }
    		        	   else{
    		        		   showMessages(R.string.MainActivity_rename_renameFailed);
    		        	   }
    		           }
    		})
    		.setNegativeButton(R.string.dialog_cancel, null)
    		.show();
    	}
    	else{
    		showMessages(R.string.MainActivity_rename_noRenamePermission);
    	}
    }
    
    private void copyOrMove(String path, Action copyOrMoveFlag){
    	File src = new File(path);
    	if(src.canRead()&&src.canWrite()){
    		mBottomBarVisiable = View.VISIBLE;
    		((View) findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
    		mPasteSourceFilePath = path;
    		mCopyOrMove = copyOrMoveFlag;
    	}
    	else{
    		if(mCopyOrMove.equals(Action.COPY)){
    			showMessages(R.string.MainActivity_paste_noCopyPermission);
    		}
    		else{
    			showMessages(R.string.MainActivity_paste_noMovePermission);
    		}
    	}
    }
    
    public void onBottomBarPasteClicked(View v) throws IOException{
    	File srcFile = new File(mPasteSourceFilePath);
    	String dstFileName = mRootFile + "/" + srcFile.getName();
    	if(mPasteSourceFilePath.equals(dstFileName)){
    		if(mCopyOrMove.equals(Action.MOVE)){
    			initializePasteStatus();
    			return;
    		}
    		dstFileName = mRootFile + "/copy-" + srcFile.getName();
    	}
    	File dstFile = new File(dstFileName);
    	dstFile.createNewFile();
    	if(dstFile.canWrite()){
    		InputStream in = new FileInputStream(srcFile);
    		OutputStream out = new FileOutputStream(dstFile);
    		byte[] buf = new byte[1024];
    		int len;
    		while ((len = in.read(buf)) > 0) {
    			out.write(buf, 0, len);
    		}
    		in.close();
    		out.close();
    	}
    	else{
    		showMessages(R.string.MainActivity_paste_noPastePermission);
    		return;
    	}
    	
    	if(mCopyOrMove.equals(Action.MOVE)){
    		if(!srcFile.delete()){
    			Log.e("1", "delete failed");
    		}
    	}
    	
    	initializePasteStatus();
    	mAdapter.add(createFileData(dstFile));
    }
    
    public void onBottomBarCancelClicked(View v){
    	initializePasteStatus();
    }
    
    private void initializePasteStatus(){
    	mCopyOrMove = Action.NONE;
		mBottomBarVisiable = View.GONE;
		((View) this.findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
    }
    
    private void showMessages(int messages){
    	Toast.makeText(this, 
    				   messages, 
    				   Toast.LENGTH_SHORT)
 			 .show();
    }
}
