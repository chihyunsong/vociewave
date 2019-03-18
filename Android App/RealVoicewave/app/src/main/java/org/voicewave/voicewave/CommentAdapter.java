package org.voicewave.voicewave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chihyun on 15. 8. 5..
 */
public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;
    private ArrayList<Comment> comments;
    private static LayoutInflater inflater =null;
    private String imagePath;


    public CommentAdapter(Context context, ArrayList<Comment> comments){
        super(context, R.layout.row_comment, comments);
        this.context = context;
        this.comments = comments;
        inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(vi == null) {
            vi = inflater.inflate(R.layout.row_comment, null);
        }
        TextView nickname = (TextView) vi.findViewById(R.id.txt_comment_nick);
        TextView description = (TextView) vi.findViewById(R.id.txt_comment_description);
        nickname.setText(comments.get(position).getNickname());
        description.setText(comments.get(position).getDescription());
        return vi;
    }
}
