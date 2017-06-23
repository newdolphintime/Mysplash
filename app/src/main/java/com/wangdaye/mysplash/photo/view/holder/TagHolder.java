package com.wangdaye.mysplash.photo.view.holder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.Tag;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoInfoAdapter;
import com.wangdaye.mysplash._common.ui.adapter.TagAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag holder.
 * */

public class TagHolder extends PhotoInfoAdapter.ViewHolder {
    // widget
    private RecyclerView recyclerView;

    // data
    public static final int TYPE_TAG = 6;

    /** <br> life cycle. */

    public TagHolder(View itemView) {
        super(itemView);

        this.recyclerView = (RecyclerView) itemView.findViewById(R.id.item_photo_tag);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        Mysplash.getInstance().getTopActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
    }

    /** <br> life cycle. */

    @Override
    protected void onBindView(MysplashActivity a, Photo photo) {
        List<Tag> tagList = new ArrayList<>();
        if (photo.categories != null) {
            for (int i = 0; i < photo.categories.size(); i ++) {
                tagList.add(photo.categories.get(i));
            }
        }
        if (photo.tags != null) {
            for (int i = 0; i < photo.tags.size(); i ++) {
                tagList.add(photo.tags.get(i));
            }
        }
        recyclerView.setAdapter(new TagAdapter(a, tagList));
    }

    public void setScrollListener(RecyclerView.OnScrollListener listener) {
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(listener);
    }

    public void scrollTo(int x, int y) {
        recyclerView.scrollTo(x, y);
    }
}
