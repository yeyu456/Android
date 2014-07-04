package com.simplefileexplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
        
        if(getIntent().hasExtra("path")){
            mRootFile = new File(getIntent().getExtras().getString("path"));
        } else{
            mRootFile = new File("/");
        }
        
        String actionbarTitle = mRootFile.getPath();
        getActionBar().setDisplayShowTitleEnabled(true);
        if(actionbarTitle.length()>19){
            actionbarTitle = "..." + actionbarTitle.substring(actionbarTitle.length() - 15);
        }
        getActionBar().setTitle(actionbarTitle);
        setContentView(R.layout.main_activity);
        sdf.setTimeZone(TimeZone.getDefault());
        ListView v = (ListView) findViewById(R.id.main_activity_listview);
        
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
    protected void onResume(){
        super.onResume();
        ((View) this.findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
        refreshView();
    }
    
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //In case of mRootFile directory is empty
        if(mAdapter!=null){
            mAdapter.empty();
            mAdapter = null;
        }
        mRootFile = null;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        this.getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.refresh :
                refreshView();
                return true;
            case R.id.createFile :
                addFileOrDir(false);
                return true;
            case R.id.createDirectory :
                addFileOrDir(true);
                return true;
            case R.id.search :
                search();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void addFileOrDir(final boolean flag){
        if(!mRootFile.canWrite()){
            showMessages(R.string.addFileOrDir_addFileFailed, "");
            return;
        }
        View dialogView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                        .inflate(R.layout.dialog, null);
        int title;
        if(flag){
            title = R.string.addFileOrDir_createDirectory;
        } else {
            title = R.string.addFileOrDir_createFile;
        }
        final EditText newName 
                        = (EditText) dialogView.findViewById(R.id.dialog);
        popDialog(title, dialogView, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       File newFile = new File(mRootFile, newName.getText().toString());
                       if(newFile.exists()){
                           showMessages(R.string.addFileOrDir_duplicatedFile, "");
                           return;
                       }
                       if(flag){
                           if(!newFile.mkdir()){
                               showMessages(R.string.addFileOrDir_createNewDirectoryFailed, "");
                           } else{
                               refreshView();
                           }
                       } else {
                           try{
                               newFile.createNewFile();
                               refreshView();
                           } catch(IOException e){
                               showMessages(R.string.addFileOrDir_createNewFileFailed, "");
                           }
                       }
                   }
        });
    }
    
    private void search(){
        View dialogView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog, null);
        final EditText keyString = (EditText) dialogView.findViewById(R.id.dialog);
        popDialog(R.string.dialog_search, dialogView, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                   imm.hideSoftInputFromWindow(keyString.getWindowToken(), 0);
                   search(keyString.getText().toString());
               }
        });
    }
    
    public void search(final String keyword){
        final ProgressDialog processDialog = new ProgressDialog(MainActivity.this);
        processDialog.setCancelable(true);
        processDialog.setTitle("查找文件中...");
        processDialog.show();
        
        if(mAdapter!=null){
            mAdapter.empty();
            mAdapter.notifyDataSetChanged();
        }
        
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                    String f = msg.getData().getString("msg");
                    if(msg.getData().containsKey("yes")){
                        mAdapter.add(createFileData(new File(f)));
                    }
                    processDialog.setMessage(f);
               }
           };
        new Thread(new Runnable(){
            @Override
            public void run(){
                FileFilter ff = new FileFilter(){
                    @Override
                    public boolean accept(File pathname){
                        if(pathname.getName().contains(keyword)
                           ||(pathname.isDirectory()
                              &&pathname.canExecute()
                              &&pathname.canRead()
                              &&pathname.listFiles().length!=0)){
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
                LinkedList<File> dir = new LinkedList<File>();
                dir.add(mRootFile);
                while(!dir.isEmpty()){
                    if(!processDialog.isShowing()){
                        return;
                    }
                    File f = dir.removeFirst();
                    Bundle bd = new  Bundle();
                    bd.putString("msg", f.getPath());
                    Message msg = handler.obtainMessage();
                    if(f.getName().contains(keyword)){
                        bd.putString("yes", "");
                    } 
                    msg.setData(bd);
                    handler.sendMessage(msg);
                    if(f.isDirectory()){
                        File[] fff = f.listFiles(ff);
                        if(fff.length!=0){
                            dir.addAll(Arrays.asList(fff));
                        }
                    }
                }
                processDialog.dismiss();
            }
        }).start();
    }
    
    private void refreshView(){
        ListView v = (ListView) findViewById(R.id.main_activity_listview);
        if(v.getCount()!=mRootFile.list().length){
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
            for(File f:mRootFile.listFiles()){
                list.add(createFileData(f));
            }

            SimpleButtonAdapter simpleButtonAdapter 
                                    = new SimpleButtonAdapter(this, 
                                                              list,
                                                              R.layout.file_list,
                                                              new String[]{"name", "image", "atr", "path"},
                                                              new int[]{R.id.file_list_button});
            mAdapter = simpleButtonAdapter;
            v.setAdapter(simpleButtonAdapter);
        }
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
            } else {
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
                showMessages(R.string.onNewIntent_noDirectoryPermission, 
                             "method:onNewIntent file:"+f.getPath());
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
    
    //done
    private void delete(String path, final int position){
        final File f = new File(path);
        if(f.canWrite()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_delete)
                   .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                               if(FileOperation.deleteFile(f)){
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
    
    //done
    private void rename(String path, final int position){
        final File f = new File(path);
        if(f.canWrite()&&f.getParentFile().canWrite()){
            View dialogRenameView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                                          .inflate(R.layout.dialog, null);
            final EditText newName = (EditText) dialogRenameView.findViewById(R.id.dialog);
            
            popDialog(R.string.dialog_rename, dialogRenameView, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       File newFile = new File(f.getParent(), newName.getText().toString());
                       if(newFile.exists()){
                           showMessages(R.string.rename_renameExist, "");
                           return;
                       }
                       if(f.renameTo(newFile)){
                           mAdapter.edit(position, 
                                         f.getParent() + "/" + newName.getText().toString(), 
                                         newName.getText().toString());
                       } else{
                           showMessages(R.string.rename_renameFailed, "");
                       }
                   }
            });
        } else{
            showMessages(R.string.rename_noRenamePermission, "");
        }
    }
    
    private void copyOrMove(String path, Action copyOrMoveFlag){
        File src = new File(path);
        if(copyOrMoveFlag.equals(Action.COPY)&&!src.canRead()){
            showMessages(R.string.copyOrMove_noCopyPermission, 
                         "method:copyOrMove file:" + path);
            return;
        } 
        if(copyOrMoveFlag.equals(Action.MOVE)&&!src.canWrite()){
            showMessages(R.string.copyOrMove_noMovePermission, 
                         "method:copyOrMove file:" + path);
            return;
        }
        mBottomBarVisiable = View.VISIBLE;
        ((View) findViewById(R.id.bottom_bar)).setVisibility(mBottomBarVisiable);
        mPasteSourceFilePath = path;
        mCopyOrMove = copyOrMoveFlag;
    }
    
    public void onBottomBarPasteClicked(View v){
        File srcFile = new File(mPasteSourceFilePath);
        String dstFileName = mRootFile + "/" + srcFile.getName();
        File dstFile = new File(dstFileName);
        if(dstFile.exists()){
            if(srcFile.equals(dstFile)){
                if(mCopyOrMove.equals(Action.MOVE)){
                    return;
                }
                if(mCopyOrMove.equals(Action.COPY)){
                    FileOperation.copyNotCoverExistFile(srcFile, dstFile);
                }
            } else {
                existFileProcess(srcFile, dstFile);
            }
        } else {
            if(mCopyOrMove.equals(Action.MOVE)){
                if(!srcFile.renameTo(dstFile)){
                    showMessages(R.string.onBottomBarPasteClicked_moveFailed, 
                                 "method:onBottomBarPasteClicked file:" + srcFile.getPath());
                }
            }
            if(mCopyOrMove.equals(Action.COPY)){
                boolean flag = srcFile.isFile() ? 
                               FileOperation.copyFile(srcFile, dstFile) : 
                               FileOperation.copyDirectory(srcFile, dstFile);
                if(!flag){
                    showMessages(R.string.onBottomBarPasteClicked_FilesCopyFailed, 
                                 "method:onBottomBarPasteClicked file:" + srcFile.getPath());
                }
            }
            refreshView();
            initializePasteStatus();
        }
    }
    
    private void existFileProcess(final File src, final File dst){
        View dialogView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                                .inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_existFileProcess_title)
               .setView(dialogView);
        builder.setItems(R.array.dialog_existFileProcess, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0 : {
                        if(!FileOperation.copyDirectory(src, dst)){
                            showMessages(R.string.existFileProcess_copyDirectory,
                                         "method:existFileProcess file:" + src.getPath());
                         }
                         break;
                     }
                    case 1 : {
                        if(!FileOperation.copyNotCoverExistFile(src, dst)){
                            showMessages(R.string.existFileProcess_copyNotCoverExistFile,
                                         "method:existFileProcess file:" + src.getPath());
                        }
                        break;
                    }
                    default : {}
                }
                if(mCopyOrMove.equals(Action.MOVE)){
                    if(!FileOperation.deleteFile(src)){
                        showMessages(R.string.existFileProcess_deleteFile,
                                     "method:existFileProcess file:" + src.getPath());
                    }
                }
                refreshView();
                initializePasteStatus();
            }
        });
        builder.show();
    }
    
    public void onBottomBarCancelClicked(View v){
        initializePasteStatus();
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

    private void popDialog(int title, View v, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
               .setView(v);
        builder.setPositiveButton(R.string.dialog_ok, listener)
               .setNegativeButton(R.string.dialog_cancel, null)
               .show();
    }
}