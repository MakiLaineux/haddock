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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Vector;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {


    private static final String TAG = StatementAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private Vector<Statement> mStatementVector;


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

    StatementAdapter() {
        loadData();
        mInflater = LayoutInflater.from(App.context);
    }

    public void loadData() {
        mStatementVector = App.mBaseLocale.getStatementVector();
    }

    @Override
    public StatementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.card, parent, false);
        return new StatementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatementViewHolder holder, int position) {

        // fill from the vector
        holder.vhStatementText.setText(mStatementVector.elementAt(position).getText());
        holder.vhStatementProfile.setText(mStatementVector.elementAt(position).getTextProfile());

        // Color of the mark bar
        if (mStatementVector.elementAt(position).getStatus() == App.STATUS_MARKED)
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.colorMarked);
        else
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.colorCard);
    }

    public Statement getStatementFromPosition (int pos){
        if (mStatementVector == null)
            return null;
        else
            return mStatementVector.elementAt(pos);
    }

    public boolean toggleFavori (Statement statement){
        if (App.mBaseLocale.toggleStatutFavori(statement) == 1) return true;
        else return false;
    }

    @Override
    public int getItemCount() {
        return (mStatementVector == null) ? 0 : mStatementVector.size();
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


