/*
 * Copyright (C) 2018 Shobhit Agarwal
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.odk.collect.android.R;
import org.odk.collect.android.adapters.model.Discussion;
import org.odk.collect.android.utilities.DownloadImageTask;
import org.odk.collect.android.utilities.TimeAgo;
import org.odk.collect.android.views.ForumViewHolder;

import java.util.List;

public class DiscussionListAdapter extends RecyclerView.Adapter<DiscussionListAdapter.ViewHolder> {

    private final Context context;
    private List<Discussion> discussions;
    private final DiscussionItemClickListener listener;

    public DiscussionListAdapter(List<Discussion> discussions, Context context, DiscussionItemClickListener listener) {
        this.context = context;
        this.discussions = discussions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscussionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(context)
                .inflate(R.layout.discussion_list_item_layout, parent, false);
        return new ViewHolder(itemLayoutView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionListAdapter.ViewHolder holder, int position) {
        Discussion discussion = discussions.get(position);
        DownloadImageTask task = new DownloadImageTask(holder);
        task.execute(discussion.getIcon());
        holder.title.setText(discussion.getTitle());
        holder.description.setText(discussion.getDescription());
        holder.lastCommentTimestamp.setText(this.context.getString(R.string.discussion_last_comment_time, TimeAgo.getTimeAgo(discussion.getLastCommentTimestamp())));
        holder.likes.setText(String.valueOf(discussion.getLikes()));
        holder.views.setText(String.valueOf(discussion.getViews()));
        holder.commentCount.setText(String.valueOf(discussion.getCommentCount()));
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public void setDiscussions(List<Discussion> discussions) {
        this.discussions = discussions;
        notifyDataSetChanged();
    }


    public interface DiscussionItemClickListener {
        void onClick(int position);
    }

    class ViewHolder extends ForumViewHolder implements View.OnClickListener {
        private final DiscussionItemClickListener listener;
        private final TextView title;
        private final TextView description;
        private final TextView likes;
        private final TextView views;
        private final TextView commentCount;
        private final TextView lastCommentTimestamp;

        ViewHolder(View view, DiscussionItemClickListener listener) {
            super(view, R.id.discussion_image);
            this.listener = listener;
            title = view.findViewById(R.id.discussion_title);
            description = view.findViewById(R.id.discussion_description);
            likes = view.findViewById(R.id.discussion_like_count);
            views = view.findViewById(R.id.discussion_view_count);
            commentCount = view.findViewById(R.id.comment_count);
            lastCommentTimestamp = view.findViewById(R.id.last_comment_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getAdapterPosition());
        }
    }
}