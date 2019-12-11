package dps.ethicaldigt.ucroptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnCrop;
    private ImageView imageView;
    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int CODE_IMAGE_GALLERY = 100;
    private String SAMPLE_CROPED_IMAGE_NAME = "SimpleCropIMG";
    private Uri desUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCrop = findViewById(R.id.btnOpenCamera);
        imageView = findViewById(R.id.imageView);

        btnCrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpMenu();
            }
        });
    }

    private void popUpMenu(){
        PopupMenu popup = new PopupMenu(this, btnCrop);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_Camera:
                        try {
                            openCamera();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.item_Gallery:
                        startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMAGE_GALLERY);
                        break;

                }
                return true;
            }
        });
        popup.show();
    }

    private void openCamera() throws IOException {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

            uri = FileProvider.getUriForFile(this, getPackageName(), file);
            Log.i("File URI:##",uri.toString());
        } else {
            uri = Uri.fromFile(file);
            Log.i("File Uri:", uri.toString());
        }
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    String currentPhotoPath = "";
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
        @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        desUri = Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPED_IMAGE_NAME + ".jpg"));

        // This is for camera
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK){
            Uri uri = Uri.parse(currentPhotoPath);
//            File tempFile = new File(getExternalCacheDir(), SAMPLE_CROPED_IMAGE_NAME + "1.jpg");
//            Uri desUri1;
//            desUri1 = Uri.fromFile(tempFile);
            startCrop(uri, uri);

//
//            startCrop(uri, uri);

//
//            Bundle extra = data.getExtras();
//            Bitmap bitmap = null;
//            //   bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageResultCrop);
//
//            assert extra != null;
//            bitmap = (Bitmap)extra.get("data");
//            Glide.with(this)
//                    .load(bitmap)
//                    .circleCrop()
//                    .into(imageView);

            //      startCrop(imageUri, desUri);
        }


//
//        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
//            final Uri resultUri = UCrop.getOutput(data);
//            Glide.with(this)
//                    .load(resultUri)
//                    .circleCrop()
//                    .into(imageView);
//
//        }else if (resultCode == UCrop.RESULT_ERROR){
//            final Throwable cropError = UCrop.getError(data);
//            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//

        if (requestCode == UCrop.RESULT_ERROR){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode == CODE_IMAGE_GALLERY && resultCode == RESULT_OK){
            // DESDE GALLERY
            Uri imgUri = data.getData();
            startCrop(imgUri, desUri);
        }


        if (requestCode == UCrop.REQUEST_CROP){
            Uri imageResultCrop = UCrop.getOutput(data);
     //       Toast.makeText(this, imageResultCrop.getPath(), Toast.LENGTH_SHORT).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageResultCrop);
                Glide.with(this)
                        .load(bitmap)
                        .circleCrop()
                        .into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCrop(@NonNull Uri sourceUri, Uri destinationUri){

      //  File tempFile = new File(getCacheDir(), SAMPLE_CROPED_IMAGE_NAME + ".jpg");
      //  Uri destinationUri = Uri.fromFile(tempFile);

        UCrop.of(sourceUri, destinationUri)
        .withAspectRatio(1,1)
        .withMaxResultSize(450, 450)
        .withOptions(getOptioins()).start(this);

//        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), SAMPLE_CROPED_IMAGE_NAME + ".jpg")));
//        uCrop.withAspectRatio(1,1);
//        uCrop.withMaxResultSize(450, 450);
//        uCrop.withOptions(getOptioins());
//        uCrop.start(this);
    }

    private UCrop.Options getOptioins() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);

        // CompressType
//        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        // UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);

        //Color
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Crop Image");

        return options;
    }
}
