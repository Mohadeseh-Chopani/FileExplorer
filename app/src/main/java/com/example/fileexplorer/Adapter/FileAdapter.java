package com.example.fileexplorer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fileexplorer.R;
import com.example.fileexplorer.ViewType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    ClickListener clickListener;
    boolean status=false;

    ViewType viewType=ViewType.ROW;
    List<File>fileFilter=new ArrayList<>();
    List<File> list=new ArrayList<>();
    Context context;

    public FileAdapter(List<File> list, Context context,ClickListener clickListener) {
        this.list = list;
        this.fileFilter=this.list;
        this.context = context;
        this.clickListener=clickListener;
    }

    public void searchFile(String query){
        if(query.length() > 0){
            List<File>result=new ArrayList<>();
            for (File item:list) {
                if(item.getName().toLowerCase().contains(query)){
                    result.add(item);
                }
            }
            this.fileFilter=result;
            notifyDataSetChanged();
        }
        else {
            this.fileFilter=this.list;
            notifyDataSetChanged();
        }
    }

    public void deleteFile(File file) {
        int index = list.indexOf(file);
        list.remove(index);
        notifyItemRemoved(index);
    }
    public void addFile(File file){
        list.add(0,file);
        notifyItemInserted(0);
    }
    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                viewType == ViewType.ROW.getValue() ? R.layout.item_recyclearview_row : R.layout.item_recyclearview_grid,parent,false));
    }

    @Override
    public int getItemCount() {
        return fileFilter.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {

        holder.bind(fileFilter.get(position));
    }

    public class FileViewHolder extends RecyclerView.ViewHolder{

        ImageView img_icon,btn_more;
        TextView title;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            img_icon=itemView.findViewById(R.id.icon_file);
            btn_more=itemView.findViewById(R.id.btn_more);
            title=itemView.findViewById(R.id.file_name);
        }


         void bind(File file){

            TypeFile(file);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClickFile(file);
                }
            });

            btn_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                    popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if(item.getItemId() == R.id.btn_delete){
                                clickListener.DeleteFile(file);
                            } else if (item.getItemId() == R.id.btn_copy) {
                                clickListener.CopyFile(file);
                                status = true;
                            } else if (item.getItemId() == R.id.btn_move) {
                                clickListener.MoveFile(file);
                                status = true;
                            }
                            return false;
                        }
                    });
                }
            });
        }



        @SuppressLint("ResourceAsColor")
        void TypeFile(File file){
            if(file.isDirectory()){
                img_icon.setImageResource(R.drawable.ic_folder_black_32dp);
                img_icon.setImageTintList(ColorStateList.valueOf(R.color.colorPrimaryDark));
            }
            else if(file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpeg") ||
                    file.getName().toLowerCase().endsWith(".jpg")){
                img_icon.setImageResource(R.drawable.image);
            } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                img_icon.setImageResource(R.drawable.pdf);
            }
            else if (file.getName().toLowerCase().endsWith(".doc")) {
                img_icon.setImageResource(R.drawable.doc);
            }
            else if (file.getName().toLowerCase().endsWith(".mp3")) {
                img_icon.setImageResource(R.drawable.mp3);
            }
            else if (file.getName().toLowerCase().endsWith(".mp4")) {
                img_icon.setImageResource(R.drawable.mp4);
            }
            else if (file.getName().toLowerCase().endsWith(".wav")) {
                img_icon.setImageResource(R.drawable.wav);
            }
            else if (file.getName().toLowerCase().endsWith(".txt")) {
                img_icon.setImageResource(R.drawable.text);
            }
            else if (file.getName().toLowerCase().endsWith(".apk")) {
                img_icon.setImageResource(R.drawable.apk);
            }
            title.setText(file.getName());
        }
    }


    public void setViewType(ViewType viewType){
        this.viewType=viewType;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType.getValue();
    }

    public interface ClickListener{
        void onClickFile(File file);
        void DeleteFile(File file);
        void CopyFile(File file);
        void MoveFile(File file);
    }
}
