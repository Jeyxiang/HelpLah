package com.example.helplah.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.Notification;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationTabAdapter extends FirestorePagingAdapter<Notification,
        NotificationTabAdapter.NotificationViewHolder> {

    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public NotificationTabAdapter(@NonNull FirestorePagingOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull Notification model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NotificationViewHolder(inflater.inflate(R.layout.notification_tab_list_item, parent, false));
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView text;
        private final TextView date;
        private final CircleImageView image;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.notificationText);
            this.title = itemView.findViewById(R.id.notificationTitle);
            this.date = itemView.findViewById(R.id.notificationDate);
            this.image = itemView.findViewById(R.id.notificationPicture);
        }

        public void bind(Notification notification) {
            this.title.setText(notification.getTitle());
            this.text.setText(notification.getText());
            this.date.setText(Review.getTimeAgo(notification.getDate()));
        }

    }
}
