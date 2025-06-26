package com.smb116.project.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.smb116.project.model.NContact;

import java.util.ArrayList;
import java.util.List;

public class AllUserAdapter extends ArrayAdapter<NContact> {

    private List<NContact> originalList;
    private List<NContact> filteredList;
    private final Context context;

    public AllUserAdapter(Context context, List<NContact> utilisateurs) {
        super(context, 0, utilisateurs);
        this.context = context;
        this.originalList = new ArrayList<>(utilisateurs);
        this.filteredList = utilisateurs;
    }

    public void setOriginalList(List<NContact> originalList) {
        this.originalList = originalList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);

        NContact utilisateur = getItem(position);
        if (utilisateur != null) {
            TextView textView = convertView.findViewById(android.R.id.text1);
            textView.setText(utilisateur.getName());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<NContact> resultList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    resultList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (NContact user : originalList) {
                        if (user.getName().toLowerCase().contains(filterPattern)) {
                            resultList.add(user);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = resultList;
                results.count = resultList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                //noinspection unchecked
                addAll((List<NContact>) results.values);
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((NContact) resultValue).getName();
            }
        };
    }
}
