package com.example.fileexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddNewFolder{

    ImageView btn_add;
    File path;
    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add = findViewById(R.id.btn_add_folder);

        path = getExternalFilesDir(null);


        runtimepermissions();


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAdd dialogAdd=new DialogAdd();
                dialogAdd.show(getSupportFragmentManager(),null);
            }
        });
    }

    private void runtimepermissions(){
        Dexter.withContext(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                displayFiles(String.valueOf(path),false);
            }


            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void displayFiles(String path , boolean onBackToStack) {
        Bundle bundle=new Bundle();
        FragmentFiles fragmentFiles=new FragmentFiles();
        bundle.putString("path",path);
        fragmentFiles.setArguments(bundle);

        FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_folder,fragmentFiles);
        fragmentTransaction.commit();

        if(onBackToStack)
            fragmentTransaction.addToBackStack(null);
    }

    public void displayFiles(String path){
        this.displayFiles(path,true);
    }

    @Override
    public void add_folder(String name) throws IOException {
        Fragment fragment=getSupportFragmentManager().findFragmentById(R.id.fragment_folder);
        if(fragment instanceof FragmentFiles) {
            ((FragmentFiles)fragment).CreateFile(name);
        }
    }
}