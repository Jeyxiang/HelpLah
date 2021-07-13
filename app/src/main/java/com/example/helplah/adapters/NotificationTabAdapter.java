package com.example.helplah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.Notification;
import com.example.helplah.models.ProfilePictureHandler;
import com.example.helplah.models.Review;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A recycler view adapter that fills the notification tab in the account page with the user's
 * notification.
 */
public class NotificationTabAdapter extends FirestorePagingAdapter<Notification,
        NotificationTabAdapter.NotificationViewHolder> {

    /**
     * A listener interface that listens on when a particular notification is clicked.
     */
    public interface NotificationClickedListener {
        void notificationClicked(Notification notification, View v);
    }

    private final NotificationClickedListener listener;

    /**
     * Construct a new adapter.
     * @param options A firestore paging options that contains the query and paging configuration.
     * @param listener A listener to listen for notification clicks.
     */
    public NotificationTabAdapter(@NonNull FirestorePagingOptions<Notification> options, NotificationClickedListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull Notification model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NotificationViewHolder(inflater.inflate(R.layout.notification_tab_list_item, parent, false),
                parent.getContext());
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView text;
        private final TextView date;
        private final CircleImageView image;
        private final Context context;

        public NotificationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.text = itemView.findViewById(R.id.notificationText);
            this.title = itemView.findViewById(R.id.notificationTitle);
            this.date = itemView.findViewById(R.id.notificationDate);
            this.image = itemView.findViewById(R.id.notificationPicture);
            this.context = context;
        }

        public void bind(Notification notification) {
            this.title.setText(notification.getTitle());
            this.text.setText(notification.getText());
            this.date.setText(Review.getTimeAgo(notification.getDate()));
            ProfilePictureHandler.setProfilePicture(this.image, notification.getSenderId(), this.context);

            itemView.setOnClickListener(v -> listener.notificationClicked(notification, v));
        }

    }
}
