package com.example.fileexplorer;

import android.annotation.SuppressLint;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentFiles extends Fragment implements FileAdapter.ClickListener{

    RecyclerView rv_files;
    ImageView btn_back;
    TextView text_path;
    public String path;
    List<File>fileList;

    FileAdapter fileAdapter;

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
        File filePath=new File(path);
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
            if(!file.exists()){
                if(file.createNewFile()){
                    fileAdapter.addFile(file);
                    rv_files.smoothScrollToPosition(0);
                }
            }
        }
        if(!file.exists()){
            if(file.mkdir()){
                fileAdapter.addFile(file);
                rv_files.smoothScrollToPosition(0);
            }
        }
    }

    @Override
    public void onClickFile(File file) {
        if(file.isDirectory()){
            ((MainActivity)requireActivity()).displayFiles(path+File.separator+file.getName());
        }else {
            OpenFile.openFile(getContext(),file);
        }

    }
}