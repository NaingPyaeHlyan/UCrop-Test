package dps.ethicaldigt.ucroptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnCrop;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        PopupMenu popup = new PopupMenu(App.context, btnCrop);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ImgPickUtil imgPickUtil = new ImgPickUtil();
                imgPickUtil.getImage(MainActivity.this, imageView);

                switch (item.getItemId()){
                    case R.id.item_Camera:
                            try {
                                imgPickUtil.getImageFromCamera();
                            } catch (IOException e) {e.printStackTrace();}
                        break;
                    case R.id.item_Gallery:
                        imgPickUtil.getImageFromGallery();
                        //startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), RequestCode.GALLERY_REQUEST_CODE);
                        break;

                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImgPickUtil.activityResult(requestCode, resultCode, data);

    }
}
