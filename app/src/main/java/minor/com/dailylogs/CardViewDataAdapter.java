package minor.com.dailylogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vicky on 9/18/2016.
 */
public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {

    private static ArrayList<LogsProperties> logsData;

    public CardViewDataAdapter(ArrayList<LogsProperties> data) {
        logsData = data;
    }


    @Override
    public CardViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CardViewDataAdapter.ViewHolder holder, int position) {
        LogsProperties logsProperties = logsData.get(position);
        holder.textView.setText(logsProperties.getLogs());
        holder.label.setText(logsProperties.getTitle());
        holder.lp = logsProperties;
    }

    @Override
    public int getItemCount() {
        return logsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        TextView textView;
        TextView label;
        LogsProperties lp;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            textView = (TextView) itemView.findViewById(R.id.logs);
            label = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((MyApplication) context.getApplicationContext()).getLongPressStatus()) {
                        Intent intent = new Intent(context, DetailLogs.class);
                        intent.putExtra("data", lp.getLogs());
                        intent.putExtra("id", lp.getId());
                        intent.putExtra("title", lp.getTitle());
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        if (!((MyApplication) context.getApplicationContext()).idToBeDeleted.contains(lp.getId())) {
                            v.setBackgroundResource(R.color.colorAccent);
                            ((MyApplication) context.getApplicationContext()).setLongPressId(lp.getId());
                        } else {
                            v.setBackgroundResource(android.R.color.white);
                            ((MyApplication) context.getApplicationContext()).idToBeDeleted.remove(lp.getId());
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    v.setBackgroundResource(R.color.colorAccent);
                    ((MyApplication) context.getApplicationContext()).setLongPressStatus(false);
                    ((MyApplication) context.getApplicationContext()).setLongPressId(lp.getId());
                    ((MainActivity) context).invalidateOptionsMenu();
                    return true;
                }
            });
        }
    }
}
