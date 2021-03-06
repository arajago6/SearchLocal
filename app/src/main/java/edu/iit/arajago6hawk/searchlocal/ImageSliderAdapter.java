package edu.iit.arajago6hawk.searchlocal;

/**
 * Created by rasuishere on 8/3/16.
 */

import android.content.Context;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageSliderAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    /**
     * This class defines the image slider that is seen on MainActivity. 
     * TODO Have to investigate why ImageViews in MainActivity flicker when
     * slider page changes. Also to investigate why hiding TextViews on page
     * change is not smooth sometimes. Hardware acceleration did not help.
     * Maybe some out-of-memory issue. 
     */
    Context mContext;

    ImageSliderAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return sliderImagesId.length;
    }

    private int[] sliderImagesId = new int[]{
            R.drawable.ban_bg, R.drawable.shop1,R.drawable.shop2,R.drawable.ban_bg
    };

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageResource(sliderImagesId[i]);
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int current, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int current) {
	/**
	* When page changes, check if the page index is 0 or 3. If yes,
	* make the "Hello <user>" text and entice text visible. If not, hide them.
	*/ 
        Log.d("Slider",Integer.toString(current));
        if (current==0||current==3){
            MainActivity.salutation.setVisibility(View.VISIBLE);
            MainActivity.entice.setVisibility(View.VISIBLE);
        }
        else{
            MainActivity.salutation.setVisibility(View.GONE);
            MainActivity.entice.setVisibility(View.GONE);
        }
    }
}
