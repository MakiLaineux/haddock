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

package com.pantagruel.megaoutrage.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pantagruel.megaoutrage.App;
import com.pantagruel.megaoutrage.R;
import com.pantagruel.megaoutrage.data.Statement;

import java.util.ArrayList;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private final LayoutInflater mInflater;
    private ArrayList<Statement> mStatementList;


    /**
     *  Custom view holder with a text view and two buttons.
     */
    class StatementViewHolder extends RecyclerView.ViewHolder {
        final TextView vhStatementText;
        final TextView vhStatementProfile;
        final View vhStatementBarreFavori;
        StatementViewHolder(View itemView) {
            super(itemView);
            vhStatementText = itemView.findViewById(R.id.text);
            vhStatementProfile = itemView.findViewById(R.id.profile);
            vhStatementBarreFavori = itemView.findViewById(R.id.barrefavori);
        }
    }

    public StatementAdapter() {
        loadData();
        mInflater = LayoutInflater.from(App.context);
    }

    public void loadData() {
        mStatementList = App.mBaseLocale.getStatementList();
    }

    @Override
    public StatementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_item_statement, parent, false);
        return new StatementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatementViewHolder holder, int position) {

        // fill from the vector
        holder.vhStatementText.setText(mStatementList.get(position).getText());
        holder.vhStatementProfile.setText(mStatementList.get(position).getTextProfile());

        // Color of the mark bar
        if (mStatementList.get(position).getStatus() == App.STATUS_MARKED)
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.colorMarked);
        else
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.colorCard);
    }

    public Statement getStatementFromPosition (int pos){
        if (mStatementList == null)
            return null;
        else
            return mStatementList.get(pos);
    }

    public boolean toggleFavori (Statement statement){
        if (App.mBaseLocale.toggleStatutFavori(statement) == 1) return true;
        else return false;
    }

    @Override
    public int getItemCount() {
        return (mStatementList == null) ? 0 : mStatementList.size();
    }

    public void insertStatement(Statement statement) {
        App.mBaseLocale.insertOneStatement(statement);
        notifyDataSetChanged();
    }

    public void updateStatement(Statement statement) {
        App.mBaseLocale.updateOneStatement(statement);
        notifyDataSetChanged();
    }

    public void removeStatement(Statement statement) {
        App.mBaseLocale.removeOneStatement(statement);
        notifyDataSetChanged();
    }
}


