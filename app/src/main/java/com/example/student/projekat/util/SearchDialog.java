package com.example.student.projekat.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.student.projekat.R;

public class SearchDialog extends AppCompatDialogFragment {
    private EditText searchByEdit;
    private CheckBox searchUserCheck;
    private CheckBox searchTagCheck;
    private SearchDialogListener searchDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_dialog, null);

        builder.setView(view)
                .setTitle("Search")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String searchBy = searchByEdit.getText().toString();
                        boolean searchUser = searchUserCheck.isChecked();
                        boolean searchTag = searchTagCheck.isChecked();
                        searchDialogListener.applayTextS(searchBy,searchUser,searchTag);
                    }
                });
        searchByEdit = view.findViewById(R.id.searchBy);
        searchUserCheck = view.findViewById(R.id.checkboxSearchUser);
        searchTagCheck = view.findViewById(R.id.checkboxSearchTag);

        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            searchDialogListener = (SearchDialogListener)context;
        }catch (ClassCastException e){
            throw  new ClassCastException(context.toString() + "must implement SearchDialogListener");
        }
    }

    public interface SearchDialogListener{
        void applayTextS(String searchBy,boolean searchUser, boolean searchTag);
    }
}
