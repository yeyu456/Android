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
import java.util.Date;
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
        } else{
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
            } else{
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
                                      .getMimeTypeFromExtension(fileExt(f.toString())));
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                showMessages(R.string.onNewIntent_handleFileFailed, "");
            }
        } else{
            if(f.canExecute()&&f.canRead()){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("path", file.get("path"));
                startActivity(intent);
            } else{
                showMessages(R.string.onNewIntent_noDirectoryPermission, "");
            }
        }
    }
    
    private String fileExt(String url) {
        if (url.indexOf("/")>-1) {
            url = url.substring(url.lastIndexOf("/")+1);
        }
        if (url.lastIndexOf(".") == -1||url.lastIndexOf(".")+1 == -1) {
            return "text";
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
                          showMessages(R.string.onOptionsDialog_exceptionItem,
                                       "method:onOptionsDialog file:" + path);
                          return;
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
                               if(deleteFile(f)){
                                   mAdapter.remove(position);
                                   showMessages(R.string.delete_deleteSuccessed, "");
                               } else{
                                   showMessages(R.string.delete_deleteFailed, 
                                                "method:delete file:" + f.getPath());
                               }
                       }
                   })
                   .setNegativeButton(R.string.dialog_cancel, null)
                   .show();
        } else{
            showMessages(R.string.delete_noDeletePermission, "");
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
                           } else{
                               showMessages(R.string.rename_renameFailed, "");
                           }
                       }
            })
            .setNegativeButton(R.string.dialog_cancel, null)
            .show();
        } else{
            showMessages(R.string.rename_noRenamePermission, "");
        }
    }
    
    private void copyOrMove(String path, Action copyOrMoveFlag){
        File src = new File(path);
        if(src.canRead()&&src.canWrite()){
            mBottomBarVisiable = View.VISIBLE;
            ((View) findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
            mPasteSourceFilePath = path;
            mCopyOrMove = copyOrMoveFlag;
        } else{
            if(copyOrMoveFlag.equals(Action.COPY)){
                showMessages(R.string.copyOrMove_noCopyPermission, 
                             "method:copyOrMove file:" + path);
            } else{
                showMessages(R.string.copyOrMove_noMovePermission, 
                             "method:copyOrMove file:" + path);
            }
        }
    }
    
    public void onBottomBarPasteClicked(View v){
        File srcFile = new File(mPasteSourceFilePath);
        String dstFileName = mRootFile + "/" + srcFile.getName();
        File dstFile = new File(dstFileName);
        while(dstFile.exists()){
            if(mCopyOrMove.equals(Action.MOVE)){
                initializePasteStatus();
                return;
            }
            dstFile = new File(dstFile.getParent() + "/copy-" + dstFile.getName());
        }
        
        if(mCopyOrMove.equals(Action.MOVE)){
            if(!srcFile.renameTo(dstFile)){
                showMessages(R.string.onBottomBarPasteClicked_moveFailed, 
                             "method:onBottomBarPasteClicked file:" + srcFile.getPath());
            } else {
                mAdapter.add(createFileData(dstFile));
            }
        } else{
            if(mCopyOrMove.equals(Action.COPY)){
                if(!copyFile(srcFile, dstFile)){
                    showMessages(R.string.onBottomBarPasteClicked_FilesCopyFailed, 
                                 "method:onBottomBarPasteClicked file:" + srcFile.getPath());
                } else {
                    mAdapter.add(createFileData(dstFile));
                }
            } else {
                showMessages(R.string.onBottomBarPasteClicked_exceptionItem, 
                             "method:onBottomBarPasteClicked file:" + srcFile.getPath());
            }
        }
        initializePasteStatus();
    }
    
    public void onBottomBarCancelClicked(View v){
        initializePasteStatus();
    }
    
    private boolean copyFile(File src, File dst){
        boolean flag = true;
        if(src.isDirectory()){
            if(!dst.mkdir()){
                flag = false;
            }
            if(src.canExecute()&&src.canRead()){
                for(File f: src.listFiles()){
                    flag = flag && copyFile(f, new File(dst.getPath() + "/" + f.getName()));
                }
            } else {
                flag = false;
            }
        } else{
            try{
                if(!dst.createNewFile()){
                    flag = false;
                    return flag;
                }
                InputStream  in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dst);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            }catch(IOException e){
                showMessages(0, e.getMessage());
                flag = false;
            }
        }
        return flag;
    }
    
    private boolean deleteFile(File srcFile){
        boolean b = true;
        if(srcFile.isDirectory()){
            for(File f : srcFile.listFiles()){
                b = b && deleteFile(f);
            }
        }
        if(b){
            return srcFile.delete();
        } else {
            return b;
        }
    }
    
    private void initializePasteStatus(){
        mCopyOrMove = Action.NONE;
        mBottomBarVisiable = View.GONE;
        ((View) this.findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
    }
    
    private void showMessages(int messages, String errors){
        if(messages!=0){
            Toast.makeText(this, 
                           messages, 
                           Toast.LENGTH_SHORT)
                 .show();
            errors = getResources().getString(messages) + errors; 
        }
        if(!errors.equals("")){
            Log.e(this.toString(), sdf.format(new Date()) + " " + errors);
        }
    }
}
