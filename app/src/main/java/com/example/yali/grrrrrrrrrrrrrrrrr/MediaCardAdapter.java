package com.example.yali.grrrrrrrrrrrrrrrrr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class MediaCardAdapter extends ArrayAdapter <Media>
{
    public MediaCardAdapter (Context context)
    {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView==null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_media_card, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        Media media = getItem(position);

        holder.title.setText(media.getTitle());
        holder.rating.setText(""+media.getRating());
        holder.language.setText(media.getLanguage());
        holder.poster.setImageBitmap(media.getPoster());

        return convertView;
    }


    private static class ViewHolder
    {
        public TextView title;
        public TextView rating;
        public TextView language;
        public ImageView poster;

        public ViewHolder (View view)
        {
            this.title = (TextView)view.findViewById(R.id.item_media_title);
            this.rating = (TextView)view.findViewById(R.id.item_media_rating);
            this.language = (TextView)view.findViewById(R.id.item_media_language);
            this.poster = (ImageView)view.findViewById(R.id.item_media_poster);
        }
    }
}
