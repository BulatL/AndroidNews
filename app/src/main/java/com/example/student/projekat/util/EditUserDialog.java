package com.example.student.projekat.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.student.projekat.R;

public class EditUserDialog extends AppCompatDialogFragment {
    private EditText userNameEdit;
    private EditText nameEdit;
    private EditText passwordEdit;
    private EditUserDialogListener editUserDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_user_dialog, null);

        builder.setView(view)
                .setTitle("Edit user")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String userName = userNameEdit.getText().toString();
                        String name = nameEdit.getText().toString();
                        String password = passwordEdit.getText().toString();
                        editUserDialogListener.applayTextS(userName,name,password);
                    }
                });
        userNameEdit = view.findViewById(R.id.userNameEdit);
        nameEdit = view.findViewById(R.id.nameEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);

        userNameEdit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        nameEdit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});
        passwordEdit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(250)});

        Bundle args = getArguments();
        if(args!= null) {
            String prosledjenUserName = getArguments().getString("userName");
            String prosledjenName = getArguments().getString("name");
            String prosledjenPassword = getArguments().getString("password");

            userNameEdit.setText(prosledjenUserName);
            nameEdit.setText(prosledjenName);
            passwordEdit.setText(prosledjenPassword);
        }
        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            editUserDialogListener = (EditUserDialogListener) context;
        }catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "must implement EditUserDialogListener");
        }
    }

    public interface EditUserDialogListener{
        void applayTextS(String userName,String name,String password);
    }
}
