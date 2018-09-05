package com.dl.common.uitils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;

import com.dl.common.R;
import com.dl.common.widget.dialog.DialogNormal;
import com.dl.common.widget.dialog.DialogSingle;
import com.dl.common.widget.dialog.DialogVertical;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * 弹窗工具类
 * created by dalang at 2018/8/30
 */
public class DialogUtil {
    private static Dialog dialog;

    //-------------------------------------------加载框-----------------------------------------------------------------------------//
    public static void buildLoading(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View root = View.inflate(activity.getApplicationContext(), R.layout.dialog_loading, null);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);
        if (!(activity.isFinishing() && dialog.isShowing())) {
            dialog.show();
        }
    }

    public static void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //-------------------------------------------通用弹窗-------------------------------------------------------------------------//
    public static DialogNormal buildDialogNormal(Context context, String title, String content) {
        DialogNormal dialog = new DialogNormal(context);
        dialog.setTitle(title);
        dialog.setContent(content);
        return dialog;
    }

    public static DialogNormal buildDialogNormal(Context context, float alpha, int gravity, String title, String content) {
        DialogNormal dialog = new DialogNormal(context, alpha, gravity);
        dialog.setTitle(title);
        dialog.setContent(content);
        return dialog;
    }


//-------------------------------------------强制弹窗-------------------------------------------------------------------------//

    public static DialogSingle buildDialogSingle(Context context, String title, String content) {
        DialogSingle dialog = new DialogSingle(context);
        dialog.setTitle(title);
        dialog.setContent(content);
        return dialog;
    }

    public static DialogSingle buildDialogSingle(Context context, float alpha, int gravity, String title, String content) {
        DialogSingle dialog = new DialogSingle(context, alpha, gravity);
        dialog.setTitle(title);
        dialog.setContent(content);
        return dialog;
    }

//-------------------------------------------竖排弹窗-------------------------------------------------------------------------//

    public static DialogVertical buildDialogVertical(Context context, String title, String text1, String text2) {
        DialogVertical dialog = new DialogVertical(context, false);
        dialog.setTitle(title);
        dialog.setButton1(text1);
        dialog.setButton2(text2);
        return dialog;
    }

    public static DialogVertical buildDialogVerticalThree(Context context, String title, String text1, String text2, String text3) {
        DialogVertical dialog = new DialogVertical(context, true);
        dialog.setTitle(title);
        dialog.setButton1(text1);
        dialog.setButton2(text2);
        dialog.setButton3(text3);
        return dialog;
    }

    //-------------------------------------------选择图片弹窗-------------------------------------------------------------------------//

    /**
     * 上传单张图片
     *
     * @param context
     * @param takePhoto
     * @param isCrop    是否裁剪
     */
    public static void uploadPhoto(Context context, final TakePhoto takePhoto, final boolean isCrop) {

        final DialogVertical cameraDialog = buildDialogVertical(context, "选择照片", "拍照", "从手机相册选择");


        final File file = new File(FileUtil.IMG_FILE_PATH + "/" + System.currentTimeMillis() +FileUtil.JPG_SUFFIX);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final Uri imageUri = Uri.fromFile(file);


        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        cameraDialog.setOnBtn1Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCrop) {
                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromCapture(imageUri);
                }

                cameraDialog.dismiss();
            }
        });
        cameraDialog.setOnBtn2Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCrop) {
                    takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromGallery();
                }

                cameraDialog.dismiss();
            }
        });
        cameraDialog.show();
    }



    public static void uploadMultiplePhoto(Context context, final TakePhoto takePhoto, final int limit,final boolean isCrop) {

        final File file = new File(FileUtil.IMG_FILE_PATH + "/" + System.currentTimeMillis() + FileUtil.JPG_SUFFIX);
        final DialogVertical cameraDialog = buildDialogVertical(context, "选择照片", "拍照", "从手机相册选择");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final Uri imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        cameraDialog.setOnBtn1Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCrop) {
                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromCapture(imageUri);
                }
                cameraDialog.dismiss();
            }
        });
        cameraDialog.setOnBtn2Listener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto.onPickMultiple(limit);
                cameraDialog.dismiss();
            }
        });
        cameraDialog.show();
    }


    //压缩图片
    private static void configCompress(TakePhoto takePhoto) {
        CompressConfig config;
        //压缩时是否显示进度条
        boolean showProgressBar = false;
        //压缩拍照后是否保存原图
        boolean enableRawFile = true;

        config = new CompressConfig.Builder()
                .setMaxSize(2 * 1024 * 1024)
                .enableReserveRaw(enableRawFile)
                .create();
        takePhoto.onEnableCompress(config, showProgressBar);

    }

    //选择图片配置
    private static void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //使用takephoto自带相册
        builder.setWithOwnGallery(true);
        //纠正拍照时旋转角度
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    //裁剪
    private static CropOptions getCropOptions() {
        //可以裁剪
        //使用第三方的工具，如果使用自带的话设为true
        boolean withWonCrop = false;

        CropOptions.Builder builder = new CropOptions.Builder();
        //宽/高
//        builder.setAspectX(width).setAspectY(height);
        //宽x高
//        builder.setOutputX(width).setOutputY(height);

        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

}
