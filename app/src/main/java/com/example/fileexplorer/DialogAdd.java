package com.example.fileexplorer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Objects;

public class DialogAdd extends DialogFragment {

    AddNewFolder addNewFolder;
    TextInputLayout inputLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addNewFolder=(AddNewFolder) context;
    }

    TextInputEditText et_add;
    MaterialButton btn_add;
    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add,null);
        builder.setView(view);

        et_add=view.findViewById(R.id.et_add);
        btn_add=view.findViewById(R.id.btn_add);
        inputLayout=view.findViewById(R.id.input_layout_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_add.length() > 0){
                    try {
                        addNewFolder.add_folder(Objects.requireNonNull(et_add.getText()).toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    dismiss();
                }else {
                    inputLayout.setError("Name box is empty!");
                }
            }
        });

        return builder.create();
    }
}

interface AddNewFolder{
    void add_folder(String name) throws IOException;
}
