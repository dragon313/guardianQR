package com.vdovenkov.alexander.guardianqr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;
import com.vdovenkov.alexander.guardianqr.model.MagicItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOfStudiedActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MagicItemAdapter adapter;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_studied);
        initDB();
        adapter = new MagicItemAdapter(getItems(), this);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<MagicItem> getItems() {
        List<MagicItem> items = new ArrayList<>();
        String querry = "SELECT * FROM magic_items WHERE magic_items.is_read = 1";
        Cursor cursor = database.rawQuery(querry, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MagicItem item = new MagicItem();
            item.setId(cursor.getInt(0));
            item.setTitle(cursor.getString(1));
            item.setDescription(cursor.getString(2));
            if (dataBaseHelper.checkRules(item.getId(), this)) {
                items.add(item);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }

    private void initDB() {
        dataBaseHelper = new DataBaseHelper(this);
        try {
            dataBaseHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("UnableToUpdateDatabase");
        }
        database = dataBaseHelper.getWritableDatabase();
    }

    private class MagicItemAdapter extends RecyclerView.Adapter<MagicItemsViewHolder> {
        private List<MagicItem> items;
        private Context context;

        public MagicItemAdapter(List<MagicItem> items, Context context) {
            this.items = items;
            this.context = context;
        }

        @Override
        public MagicItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.magic_item_view, parent, false);
            return new MagicItemsViewHolder(root);
        }

        @Override
        public void onBindViewHolder(MagicItemsViewHolder holder, int position) {
            final MagicItem item = items.get(holder.getAdapterPosition());
            holder.title.setText(item.getTitle());
            holder.description.setText(item.getDescription());
            holder.itemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ScanDetailActivity.showScanResult(context, String.valueOf(item.getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            if (items.size() > 0) {
                return items.size();
            }
            return 0;
        }
    }


    private static class MagicItemsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        CardView itemCardView;
        public MagicItemsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.magic_item_title_text_view);
            description = itemView.findViewById(R.id.magic_item_description_text_view);
            itemCardView = itemView.findViewById(R.id.item_card_view);
        }
    }
}
