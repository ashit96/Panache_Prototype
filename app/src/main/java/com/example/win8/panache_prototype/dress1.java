package com.example.win8.panache_prototype;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.view.GestureDetector;
import android.widget.ImageView;


public class dress1 extends ActionBarActivity {
    private GridView gv;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private int j=0;

    private  GestureDetector detector;
    private static final int SWIPE_MIN_DIST=120;
    private static final int SWIPE_THRESHOLD_VELOCITY=200;
    private int thumb[] = {R.drawable.dress1,R.drawable.dress1_1,R.drawable.dress1_2,R.drawable.dress1_3,R.drawable.dress1_4 };
    private ImageView expandedImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress1);
        detector= new GestureDetector(this,new SwipeGestureDetector());

        gv = (GridView) findViewById(R.id.grid_view);
        gv.setAdapter(new ImageAdapter(null));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               j=position;
               zoomImageFromThumb(view,thumb[position]);

            }
        });
        mShortAnimationDuration=getResources().getInteger(android.R.integer.config_shortAnimTime);
    }
 class ImageAdapter extends BaseAdapter{
     private LayoutInflater layoutInflater;

     public ImageAdapter(MainActivity activity){
         layoutInflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }
     @Override
     public int getCount() {
         return thumb.length;
     }

     @Override
     public Object getItem(int position) {
         return position;
     }

     @Override
     public long getItemId(int position) {
         return position;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         View listitem = convertView;
         int p = position;
         if(listitem==null){
             listitem=layoutInflater.inflate(R.layout.single_grid_item,null);
         }
         ImageView iv= (ImageView) listitem.findViewById(R.id.thumb);
         iv.setBackgroundResource(thumb[position]);

         return listitem;
     }
 }
private void zoomImageFromThumb(final View thumView,int imageResId){

    if(mCurrentAnimator!=null){
        mCurrentAnimator.cancel();
    }

    expandedImageView =(ImageView)findViewById(R.id.expanded_image);
    expandedImageView.setOnTouchListener(new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (detector.onTouchEvent(event)){
                return true;
            }else{
                return false;
            }

        }
    });
    expandedImageView.setImageResource(imageResId);
    final Rect startbounds = new Rect();
    final Rect finalbounds = new Rect();
    final Point globalOffset = new Point();

    thumView.getGlobalVisibleRect(startbounds);
    findViewById(R.id.container).getGlobalVisibleRect(finalbounds,globalOffset);
    startbounds.offset(-globalOffset.x,-globalOffset.y);
    finalbounds.offset(-globalOffset.x,-globalOffset.y);
    float startscale;
    if((float) finalbounds.width() / (float) finalbounds.height() > (float) startbounds.width() / (float) startbounds.height()){
        startscale=(float) startbounds.height()/finalbounds.height();
        float startwidth = startscale*finalbounds.width();
        float deltawidth = (startwidth-startbounds.width())/2;
        startbounds.left -= deltawidth;
        startbounds.right += deltawidth;
    }else{
        startscale =(float)startbounds.width()/finalbounds.width();
        float startheight= startscale * finalbounds.height();
        float deltaheight=(startheight = startbounds.height())/2;
        startbounds.top -=deltaheight;
        startbounds.bottom -=deltaheight;
    }
    thumView.setAlpha(0f);
    expandedImageView.setVisibility(View.VISIBLE);
    expandedImageView.setPivotX(0f);
    expandedImageView.setPivotY(0f);

    AnimatorSet set = new AnimatorSet();
    set.play(ObjectAnimator.ofFloat(expandedImageView,View.X,startbounds.left,finalbounds.left))
            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startbounds.top, finalbounds.top))
            .with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_X,startscale,1f))
            .with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_Y,startscale,1f));

    set.setDuration(mShortAnimationDuration);
    set.setInterpolator(new DecelerateInterpolator());
    set.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            mCurrentAnimator=null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCurrentAnimator=null;
        }
    });
    set.start();
    mCurrentAnimator=set;
    final float startscalefinal=startscale;
    expandedImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }
            AnimatorSet set = new AnimatorSet();

            set.play(ObjectAnimator.ofFloat(expandedImageView,View.X,startbounds.left))
                    .with(ObjectAnimator.ofFloat(expandedImageView,View.Y,startbounds.top))
                    .with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_X ,startscalefinal))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startscalefinal));
            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    thumView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    thumView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;
        }

    });
}
    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try{
                if(e1.getX()-e2.getX()> SWIPE_MIN_DIST && Math.abs(velocityX)> SWIPE_THRESHOLD_VELOCITY){
                       if(thumb.length > j){
                           j++;

                           if(j < thumb.length){
                               expandedImageView.setImageResource(thumb[j]);
                               return true;
                           }

                       }

                }else if(e2.getX()-e1.getX()>SWIPE_MIN_DIST &&  Math.abs(velocityX)> SWIPE_THRESHOLD_VELOCITY ){
                            if(j>0){
                                  j--;
                                expandedImageView.setImageResource(thumb[j]);
                                return true;
                            }else {
                                j=thumb.length-1;
                                expandedImageView.setImageResource(thumb[j]);
                                return true;

                            }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    }

}
