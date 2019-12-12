package dps.ethicaldigt.ucroptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;

class ImgPickUtil {

    static Context context = App.context;
    static ImageView imageView;
    static AppCompatActivity mActivity;
    static String currentPhotoPath = "";

    public ImageView getImage(AppCompatActivity activity, ImageView view){
        mActivity = activity;
        imageView = view;
        return imageView;
    }

    public void getImageFromCamera() throws IOException{
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();
        Uri uri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(App.context, RequestCode.PACKAGE_NAME, file);
            Log.i("fileUri@@", uri.toString());
        }else {
            uri = Uri.fromFile(file);
            Log.i("fileUri@#", uri.toString());
        }
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mActivity.startActivityForResult(pictureIntent, RequestCode.CAMERA_REQUEST_CODE);
    }

    public void getImageFromGallery() {
        mActivity.startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), RequestCode.GALLERY_REQUEST_CODE);
    }

     static void activityResult(int requestCode, int resultCode, Intent data){
        Uri desUri = Uri.fromFile(new File(context.getCacheDir(), RequestCode.SAMPLE_CROP_IMAGE_NAME + ".jpg"));

        // For Camera
        if (requestCode == RequestCode.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = Uri.parse(currentPhotoPath);
            startCrop(uri, uri);
        }
        // For Gellery
        if (requestCode == RequestCode.GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            startCrop(uri, desUri);
        }
        // For uCrop
        if (requestCode == UCrop.REQUEST_CROP){
            Uri imageResultCrop = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageResultCrop);
                Glide.with(context)
                        .load(bitmap)
                        .circleCrop()
                        .into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // For uCrop Error
        if (requestCode == UCrop.RESULT_ERROR){
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }


    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    static void startCrop(@NonNull Uri sourceUri, Uri destinationUri){
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1,1)
                .withMaxResultSize(450, 450)
                .withOptions(getOptioins())
                .start(mActivity);
    }

    static UCrop.Options getOptioins() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
//      // CompressType
//        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        // UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        //Color
        options.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Crop Image");
        return options;
    }
}
