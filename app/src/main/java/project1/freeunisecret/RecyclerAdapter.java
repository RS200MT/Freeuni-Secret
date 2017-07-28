package project1.freeunisecret;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tornike on 27-Jul-17.
 */

public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.PostViewHolder> {

    private MainActivity mainActivity;
    private ArrayList<Post> posts;

    public RecyclerAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mainActivity = (MainActivity) parent.getContext();
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,null);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.postText.setText(posts.get(position).getText());
        holder.postTime.setText(posts.get(position).getCreateTime());
        holder.numComments.setText(posts.get(position).getNumComments());
        holder.numHearts.setText(posts.get(position).getNumHearts());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        private TextView postText;
        private TextView postTime;
        private TextView numComments;
        private TextView numHearts;

        public PostViewHolder(View itemView) {
            super(itemView);
            postText = (TextView) itemView.findViewById(R.id.post_text_id);
            postTime = (TextView) itemView.findViewById(R.id.post_time_id);
            numComments = (TextView) itemView.findViewById(R.id.num_comments_id);
            numHearts = (TextView) itemView.findViewById(R.id.num_loves);

        }
    }
}
