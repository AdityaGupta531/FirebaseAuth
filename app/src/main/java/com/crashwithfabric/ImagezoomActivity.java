package com.crashwithfabric;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class ImagezoomActivity extends AppCompatActivity
{
    CircularImageView mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_image);

        mIcon = findViewById(R.id.ivIcon);

        Glide.with(ImagezoomActivity.this)
                .load("https://homepages.cae.wisc.edu/~ece533/images/airplane.png")

                .into(mIcon);
       /* Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.download);
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        mIcon.setImageDrawable(mDrawable);*/



        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* AlertDialog.Builder mBuilder = new AlertDialog.Builder(ImagezoomActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);





                Glide.with(ImagezoomActivity.this)
                        .load("https://homepages.cae.wisc.edu/~ece533/images/airplane.png")
                        .into(photoView);

                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();*/

               getInfoDialog();
            }
        });
    }

    public void getInfoDialog( )
    {

        final Dialog dialog1 = new Dialog(ImagezoomActivity.this,R.style.DialogCustomTheme);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        dialog1.setContentView(R.layout.dialog_custom_layout);

        int width = getWindowManager().getDefaultDisplay()
                .getWidth();
        dialog1.getWindow().setLayout(width, ActionBar.LayoutParams.MATCH_PARENT);


        PhotoView photoView = (PhotoView) dialog1.findViewById(R.id.imageView);

        ImageView iv_cross = (ImageView)dialog1.findViewById(R.id.iv_cross);


        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        Glide.with(ImagezoomActivity.this)
                .load("https://homepages.cae.wisc.edu/~ece533/images/airplane.png")
                .into(photoView);

        dialog1.show();

    }


}
