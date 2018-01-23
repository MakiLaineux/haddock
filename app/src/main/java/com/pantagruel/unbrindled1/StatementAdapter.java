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
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.StatementViewHolder> {


    private static final String TAG = StatementAdapter.class.getSimpleName();
    private final LayoutInflater mInflater;
    private BddLocale mBaseLocale = null;
    private Cursor mCursorStatement;    // statements in the RV
    private int mCount;             // nombre d'items
    private int mScope;             // display scope (all, marked only...)


    /**
     *  Custom view holder with a text view and two buttons.
     */
    class StatementViewHolder extends RecyclerView.ViewHolder {
        View vhBarreFavori;
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

    StatementAdapter(int scope) {
        if (mBaseLocale == null) {
            mBaseLocale = new BddLocale(Globals.context);
            mBaseLocale.open();
        }
        mScope = scope;
        loadData(mScope);
        mInflater = LayoutInflater.from(Globals.context);
    }

    public void loadData(int typeAffichage) {
        if (mCursorStatement != null) mCursorStatement.close();
        mCursorStatement = mBaseLocale.getAllStatement(typeAffichage, null);
        if (mCursorStatement == null) {
            mCount = 0;
            return;
        }
        mCount = mCursorStatement.getCount();
    }
    @Override
    public StatementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.card, parent, false);
        return new StatementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatementViewHolder holder, int position) {

        mCursorStatement.moveToPosition(position);
        Statement statement = new Statement(
                mCursorStatement.getInt(Globals.STATEMENT_NUM_COL_ID),
                mCursorStatement.getString(Globals.STATEMENT_NUM_COL_TEXT),
                mCursorStatement.getString(Globals.STATEMENT_NUM_COL_PROFILE),
                mCursorStatement.getInt(Globals.STATEMENT_NUM_COL_STATUS)
                );

        holder.vhStatementText.setText(statement.getText());
        holder.vhStatementProfile.setText(statement.getTextProfile());

        // Color of the mark bar
        if (statement.getStatus() == Globals.STATUS_MARKED)
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.colorMarked);
        else
            holder.vhStatementBarreFavori.setBackgroundResource(R.color.colorCard);
    }

    public Statement getStatementFromPosition (int pos){
        if ((mCursorStatement == null) || !mCursorStatement.moveToPosition(pos)) return null;
        Statement statement = new Statement(
                mCursorStatement.getInt(Globals.STATEMENT_NUM_COL_ID),
                mCursorStatement.getString(Globals.STATEMENT_NUM_COL_TEXT),
                mCursorStatement.getString(Globals.STATEMENT_NUM_COL_PROFILE),
                mCursorStatement.getInt(Globals.STATEMENT_NUM_COL_STATUS)
        );
        return statement;
    }

    public boolean toggleFavori (Statement statement){
        if (mBaseLocale.toggleStatutFavori(statement) == 1) return true;
        else return false;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void insertStatement(Statement statement) {
        mBaseLocale.insertOneStatement(statement);
        notifyDataSetChanged();
    }

    public void updateStatement(Statement statement) {
        mBaseLocale.updateOneStatement(statement);
        notifyDataSetChanged();
    }

    public void removeStatement(Statement statement) {
        mBaseLocale.removeOneStatement(statement);
        notifyDataSetChanged();
    }
}


