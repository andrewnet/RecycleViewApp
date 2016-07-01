package com.example.andreww.recycleviewapp;


import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> mDataset;
    private String mCurString;
    private GestureDetectorCompat mDetector;
    private MyViewOnTouchListener myViewOnTouchListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textViewInList);
            mImageView = (ImageView) v.findViewById(R.id.imageView);
            mImageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<String> myDataset, Context context) {
        mDataset = myDataset;
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
        myViewOnTouchListener = new MyViewOnTouchListener();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mytextlayout, parent, false);

        v.setOnTouchListener(myViewOnTouchListener);

        // set the view's size, margins, paddings and layout parameters

//        TextView tv = (TextView) v.findViewById(R.id.textViewInList);
//        TextView tv = new TextView(parent.getContext());

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return false;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if (event1.getX() < event2.getX()) {
                //from left to right
                if (mCurString != null) {
                    for (int i = 0; i < mDataset.size(); i++) {
                        if (mCurString.equals(mDataset.get(i))){
                            mDataset.remove(i);
                            notifyDataSetChanged();
                            break;
                        }
                    }
                }
            } else {
                //from right to left
            }

            return false;
        }
    }

    class MyViewOnTouchListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView tv = (TextView) v.findViewById(R.id.textViewInList);
            mCurString = tv.getText().toString();

            mDetector.onTouchEvent(event);

            //MUST return true in order for detector to detect fling!
            return true;
        }
    }
}