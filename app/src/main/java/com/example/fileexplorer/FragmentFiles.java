package com.example.fileexplorer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.fileexplorer.Adapter.FileAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FragmentFiles extends Fragment implements FileAdapter.ClickListener{

    RecyclerView rv_files;
    ImageView btn_back;
    TextView text_path;
    public String path;
    List<File>fileList;

    File filePath;
  public FileAdapter fileAdapter;
    String currentPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getArguments().getString("path");
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_files, container, false);


        rv_files = view.findViewById(R.id.rv_files);
        btn_back = view.findViewById(R.id.btn_back);
        text_path = view.findViewById(R.id.text_path);

        rv_files.setHasFixedSize(true);
        rv_files.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        fileList=new ArrayList<>();
        filePath=new File(path);
        fileList.addAll(FindFiles(filePath));
        fileAdapter=new FileAdapter(fileList,getContext(),this);
        rv_files.setAdapter(fileAdapter);

        text_path.setText(filePath.getName());


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public ArrayList<File> FindFiles(File file) {

        ArrayList<File> Listfiles = new ArrayList<File>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory()) {
                Listfiles.add(singleFile);
            }
        }
        for (File singleFile : files) {
            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".png") ||
                    singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                    singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".pdf") ||
                    singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                    singleFile.getName().toLowerCase().endsWith(".apk") || singleFile.getName().toLowerCase().endsWith(".txt"))
            {

                Listfiles.add(singleFile);
            }
        }
        return Listfiles;
    }


    public void CreateFile(String name_file) throws IOException {
        File file = new File(path+File.separator+name_file);
        if(file.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".mp4") || file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".wav") ||
                file.getName().toLowerCase().endsWith(".pdf") || file.getName().toLowerCase().endsWith(".doc") ||
                file.getName().toLowerCase().endsWith(".txt"))
        {
            if(file.exists()){

                FileExists(file);

            }else {
                add(file);
            }
        }

        if (file.exists()){
            FileExists(file);
        }
        else {
            add(file);
        }
    }

    @Override
    public void onClickFile(File file) {
        if(file.isDirectory()){
            ((MainActivity)requireActivity()).displayFiles(path+File.separator+file.getName());
        }else {
            OpenFile.openFile(getContext(),file);
        }

        currentPath=file.getAbsolutePath();
    }

    @Override
    public void DeleteFile(File file) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

            builder.setTitle("Are you sure you want to delete?!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(file.isDirectory()){
                        deleteDirectory(file);
                        fileAdapter.deleteFile(file);
                    }
                    else {
                        if (file.delete()) {
                            fileAdapter.deleteFile(file);
                        }
                    }
                }
            });
            builder.setIcon(R.drawable.baseline_delete_outline_24);

            builder.create().show();
    }

    @Override
    public void CopyFile(File file) {

        Snackbar snackbar = Snackbar.make(getView(),"where you want to copy",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Copy", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File current = new File(currentPath+File.separator+file.getName());
                try {
                    copyDirectory(file,current);
                    CreateFile(file.getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        snackbar.show();
    }

    @Override
    public void MoveFile(File file) {

        Snackbar snackbar = Snackbar.make(getView(),"where you want to move",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Move", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File current = new File(currentPath+File.separator+file.getName());
                try {
                    moveDirectory(file,current);
                    CreateFile(file.getName());
                    if(file.delete())
                        fileAdapter.deleteFile(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        snackbar.show();
    }


    public static void copyDirectory(File source, File destination) throws IOException {
        if (!destination.exists()) {
            destination.mkdirs();
        }

        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    copyDirectory(file, new File(destination, file.getName()));
                } else {
                    copyFile(file, new File(destination, file.getName()));
                }
            }
        }
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
             FileChannel destChannel = new FileOutputStream(destFile).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }

    public static void moveDirectory(File sourceDir, File destDir) throws IOException {
        copyDirectory(sourceDir, destDir);
        deleteDirectory(sourceDir);
    }

    public static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    public void FileExists(File file){
        if (file.exists()) {
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext(),R.style.MyDialogTheme);

            builder.setTitle(" You can not this file is Exists!!");
            builder.setPositiveButton("I know", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        add(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            builder.create().show();
        }
    }

    public void add(File file) throws IOException {
        if(file.createNewFile()) {
            fileAdapter.addFile(file);
            rv_files.smoothScrollToPosition(0);
        }
        if (file.mkdir()){
            fileAdapter.addFile(file);
            rv_files.smoothScrollToPosition(0);
        }
    }
}