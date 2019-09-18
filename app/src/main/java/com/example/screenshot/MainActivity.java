package com.example.screenshot;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.screenshot.zxing.Utils;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String imagePath;
    private Button btn_jp;
    private Button btn_share;
    private Button btn_read;
    private ImageView qrcode;
    private WebView webView;
    Bitmap qrBitmap;

    Bitmap bigBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_main);
        btn_jp = findViewById(R.id.btn_jp);
        btn_share = findViewById(R.id.btn_share);
        btn_read = findViewById(R.id.btn_read);
        qrcode = findViewById(R.id.qrcode);
        webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/index.html");

        qrBitmap = QRCodeUtil.createQRCodeBitmap("https://www.baidu.com?title=1", DensityUtil.dp2px(this,296), DensityUtil.dp2px(this,296));
        qrcode.setImageBitmap(qrBitmap);
        final String result = QRCodeUtil.recogQRcode(qrcode);
        Log.d("ruanyandong", "onCreate: "+result);
        btn_jp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else {
                    screenshot();
                }
            }
        });

        //分享
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readInfo();
            }
        });

    }

    private void readInfo(){
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        String dir = sdCardPath+File.separator+"tencent"+File.separator+"MicroMsg"+File.separator+"WeiXin";
        File file = new File(dir);
        String[] fileList = file.list();
        if (fileList.length == 0){
            return;
        }
        String imagePath = dir+File.separator+fileList[fileList.length-1];

        Bitmap b = BitmapFactory.decodeFile(imagePath);
        String info = QRCodeUtil.recogQRcode(b);
        //String string = Utils.recogQRcode(b);
        Toast.makeText(this, "读取-》"+info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0){
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                screenshot();
        }
    }

    /**
     * 系统分享
     */
    private void share(){
        if (imagePath != null){
            Intent intent  = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
            File file = new File(imagePath);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                Uri photoUri = FileProvider.getUriForFile(MainActivity.this,"com.example.screenshot.fileprovider",file);
                intent.putExtra(Intent.EXTRA_STREAM, photoUri);// 分享的内容
            }else {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));// 分享的内容
            }
            intent.setType("image/*");// 分享发送的数据类型
            Intent chooser = Intent.createChooser(intent, "Share screen shot");
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(chooser);
            }
        } else {
            Toast.makeText(MainActivity.this, "先截屏，再分享", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享图片到 微信
     */
    private void shareWeChat(){
        if (imagePath != null){
            File file = new File(imagePath);
            Uri uriToImage = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                uriToImage = FileProvider.getUriForFile(MainActivity.this,"com.example.screenshot.fileprovider",file);
            }else {
                uriToImage = Uri.fromFile(file);
            }
            Intent shareIntent = new Intent();
            //发送图片到朋友圈
            //ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            //发送图片给好友。
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            shareIntent.setComponent(comp);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "分享图片"));
        }else {
            Toast.makeText(MainActivity.this, "先截屏，再分享", Toast.LENGTH_SHORT).show();
        }

    }

    //截取屏幕的方法
    private void screenshot() {
        // 获取屏幕
//        View dView = getWindow().getDecorView();
//        dView.setDrawingCacheEnabled(true);
//        dView.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache(), 0, 0, dView.getMeasuredWidth(),
//                dView.getMeasuredHeight());
//         //获取状态栏高度
//        Rect rect = new Rect();
//        dView.getWindowVisibleDisplayFrame(rect);
//        int statusBarHeights = rect.top;
//        Display display = getWindowManager().getDefaultDisplay();
//        int widths = display.getWidth();
//        int heights = display.getHeight();
//        // 去掉状态栏
//        saveBitmap = Bitmap.createBitmap(dView.getDrawingCache(), 0, statusBarHeights,
//                widths, heights - statusBarHeights);
//
//        Bitmap bmp = dView.getDrawingCache().copy(Bitmap.Config.ARGB_8888,false);
        Bitmap bmp = captureWebView(webView);
        bigBitmap = addBitmap(bmp,qrBitmap);
        String info = QRCodeUtil.recogQRcode(bigBitmap);
        Toast.makeText(this, "合成之后-》"+info, Toast.LENGTH_SHORT).show();
        popShotSrceenDialog(bigBitmap);
        if (bigBitmap != null)
        {
            try {
                // 获取内置SD卡路径
                //String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                String sdCardPath = this.getFilesDir().getAbsolutePath()+"/qk";
                // 图片文件路径
                imagePath = sdCardPath + File.separator + "screenshot.png";
                File file = new File(imagePath);
                FileOutputStream fos = new FileOutputStream(file);
                bigBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
            }
        }
//        dView.destroyDrawingCache();
//        dView.setDrawingCacheEnabled(false);
//        if (imagePath != null){
//            Log.d("ruanyandong", "screenshot: imagePath != null->"+imagePath);
//            writeInfo2Image(imagePath);
//        }

    }
//
//    private void writeInfo2Image(String imagePath){
//        try {
//            ExifInterface exifInterface = new ExifInterface(imagePath);
//            exifInterface.setAttribute(ExifInterface.TAG_MAKE,"666");
//            exifInterface.saveAttributes();
//            Log.d("ruanyandong", "writeInfo2Image: "+exifInterface.getAttribute(ExifInterface.TAG_MAKE));
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("ruanyandong",e.getMessage());
//        }
//    }
//    //sdCardPath/tencent/MicroMsg/WeiXin
//    private void readInfoFromImage(String imagePath){
//        try {
//            String sdCardPath = Environment.getExternalStorageDirectory().getPath();
////            String dir = sdCardPath+File.separator+"tencent"+File.separator+"MicroMsg"+File.separator+"WeiXin";
////            Log.d("ruanyandong", "readInfoFromImage: "+dir);
////            File file = new File(dir);
////            String[] fileList = file.list();
////            for (int i = 0; i < fileList.length; i++) {
////                Log.d("ruanyandong", "readInfoFromImage: "+fileList[i]);
////            }
////            String path = dir+File.separator+fileList[0];
////            File file1 = new File(path);
////            if (file1.exists()){
////                Log.d("ruanyandong", "readInfoFromImage: path="+file1.getPath());
////            }
//
////            ExifInterface exifInterface = new ExifInterface(path);
////            if (exifInterface != null){
////                Log.d("ruanyandong", "readInfoFromImage: exifInterface != null");
////            }
////            String info = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
////            Log.d("ruanyandong", "readInfoFromImage: ==="+info);
////            Toast.makeText(this,info,Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    /**
//     * 截取WebView的屏幕，
//     *
//     * webView.capturePicture()方法在android 4.4（19）版本被废弃，
//     *
//     * 官方建议通过onDraw(Canvas)截屏
//     *
//     * @param webView
//     * @return
//     */
    private Bitmap captureWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();
        Bitmap bitmap = Bitmap.createBitmap(snapShot.getWidth(),
                snapShot.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        snapShot.draw(canvas);
        return bitmap;
    }
//
//    /**
//     * 截取WebView的屏幕，
//     *
//     * webView.getScale()方法在android 4.4.2（17）版本被废弃，
//     *
//     * 官方建议通过 onScaleChanged(WebView, float, float)回调方法获取缩放率
//     *
//     * @param webView
//     * @return
//     */
//    private Bitmap getWebViewBitmap(WebView webView) {
//        //获取webview缩放率
//        float scale = webView.getScale();
//        //得到缩放后webview内容的高度
//        int webViewHeight = (int) (webView.getContentHeight() * scale);
//        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webViewHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        //绘制
//        webView.draw(canvas);
//        return bitmap;
//    }
//
//    //截屏功能
    private void popShotSrceenDialog(Bitmap bitmap){
        final AlertDialog cutDialog = new AlertDialog.Builder(this).create();
        View dialogView = View.inflate(this, R.layout.show_cut_screen_layout, null);
        ImageView showImg = (ImageView) dialogView.findViewById(R.id.show_cut_screen_img);
        dialogView.findViewById(R.id.share_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享
                ImgUtils.saveImageToGalleryV2(MainActivity.this,bigBitmap);
                //share();
                shareWeChat();
            }
        });

        showImg.setImageBitmap(bitmap);
        cutDialog.setView(dialogView);
        final Window window = cutDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager m = window.getWindowManager();
        final Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        final WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        p.gravity = Gravity.CENTER;//设置弹出框位置
        window.setAttributes(p);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        cutDialog.show();

//        showImg.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                p.height = (int) (d.getHeight() * 0.3);
//                p.width = (int) (d.getWidth() * 0.3);
//                p.gravity = Gravity.RIGHT;
//                window.setAttributes(p);
//            }
//        },1500);

    }
//
//    /**
//     * 纵向拼接二维码
//     * @param first
//     * @param second
//     * @return
//     */
    public static Bitmap addBitmap(Bitmap first,Bitmap second){
        int width = first.getWidth();
        int height = first.getHeight()+second.getHeight();
        Bitmap result = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        result.eraseColor(Color.parseColor("#ffffff"));
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first,0,0,null);
        canvas.drawBitmap(second,width/2-second.getWidth()/2,first.getHeight(),null);
        return result;
    }
}

