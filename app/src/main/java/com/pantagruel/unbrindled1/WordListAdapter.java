/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pantagruel.unbrindled1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Implements a simple Adapter for a RecyclerView.
 * Demonstrates how to add a click handler for each item in the ViewHolder.
 */
public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {


    private static final String TAG = WordListAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    Context mContext;
    WordListOpenHelper mDB;
    public WordListAdapter(Context context, WordListOpenHelper db) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDB=db;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Statement current = mDB.query(position);
        holder.textItemView.setText(current.getText());
        holder.profileItemView.setText(current.getTextProfile());
        // Keep a reference to the view holder for the click listener
        final WordViewHolder h = holder; // needs to be final for use in callback

        // Attach a click listener to the DELETE button.
        holder.delete_button.setOnClickListener(new MyButtonOnClickListener(
                current.getId(), null, null)  {

            @Override
            public void onClick(View v ) {
                int deleted = mDB.delete(id);
                if (deleted >= 0)
                    notifyItemRemoved(h.getAdapterPosition());
            }
        });

        // Attach a click listener to the EDIT button.
        holder.edit_button.setOnClickListener(new MyButtonOnClickListener(
                current.getId(), current.getText(), current.getTextProfile()) {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditStatementActivity.class);

                intent.putExtra(Globals.EXTRA_ID, id);
                intent.putExtra(Globals.EXTRA_POSITION, h.getAdapterPosition());
                intent.putExtra(Globals.EXTRA_TEXT, text);
                intent.putExtra(Globals.EXTRA_PROFILE, profile);

                // Start an empty edit activity.
                ((Activity) mContext).startActivityForResult(
                        intent, MainActivity.STATEMENT_EDIT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (int) mDB.count();
    }

    /**
     *  Custom view holder with a text view and two buttons.
     */
    class WordViewHolder extends RecyclerView.ViewHolder {
        public final TextView textItemView;
        public final TextView profileItemView;
        Button delete_button;
        Button edit_button;

        public WordViewHolder(View itemView) {
            super(itemView);
            textItemView = (TextView) itemView.findViewById(R.id.text);
            profileItemView = (TextView) itemView.findViewById(R.id.profile);
            delete_button = (Button)itemView.findViewById(R.id.delete_button);
            edit_button = (Button)itemView.findViewById(R.id.edit_button);
        }
    }
}


