package project1.freeunisecret;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tornike on 27-Jul-17.
 */

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public RecyclerAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_recycler_item,null);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.commentText.setText(comments.get(position).getCommentText());
        holder.commentTime.setText(comments.get(position).getWriteDate());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView commentText;
        private TextView commentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentText = (TextView) itemView.findViewById(R.id.comment_text);
            commentTime = (TextView) itemView.findViewById(R.id.comment_write_time);
        }
    }
}
