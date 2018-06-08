package com.alpha.torchapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;

public class MainActivity extends AppCompatActivity {

    private LabeledSwitch turnOnOff;
    private ImageView torch;
    private ConstraintLayout layout;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = (ConstraintLayout) findViewById(R.id.layout);
        torch = (ImageView) findViewById(R.id.torch);
        turnOnOff = (LabeledSwitch) findViewById(R.id.turnOnOff);
        /*
         * First check if device is supporting flashlight or not
         */
        checkFeature();
        turnOnOff.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if (isOn) {

                    turnOnFlash();
                } else {

                    turnOffFlash();
                }
            }
        });
    }

    // getting camera parameters
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                //Camera Error
                Log.e("Failed to Open", e.getMessage());
            }
        }
    }

    /*
     * Turning On flash
     */
    private void turnOnFlash() {
        layout.setBackgroundColor(getResources().getColor(R.color.colorTorchOn));
        torch.setImageDrawable(getResources().getDrawable(R.drawable.on));
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }
    }

    /*
     * Turning Off flash
     */
    private void turnOffFlash() {
        layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        torch.setImageDrawable(getResources().getDrawable(R.drawable.off));
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;


        }
    }

    private void checkFeature() {
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            // device doesn't support flash
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Can't Start");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // get the camera
        getCamera();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // get the camera
        getCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // on resume turn on the flash
        if (hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }

    }
}
