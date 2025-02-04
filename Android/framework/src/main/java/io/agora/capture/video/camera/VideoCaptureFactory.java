// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.agora.capture.video.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Build;


/**
 * This class implements a factory of Android Video Capture objects for Chrome.
 * Cameras are identified by |id|. Video Capture objects allocated via
 * createVideoCapture() are explicitly owned by the caller. ChromiumCameraInfo
 * is an internal class with some static methods needed from the rest of the
 * class to manipulate the |id|s of devices.
 **/
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VideoCaptureFactory {
    private static boolean isLReleaseOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private static boolean isLegacyDevice(Context context) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);
                Integer level = characteristics.get(
                        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                return (level != null && level ==
                        CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY);
            }

            return false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static VideoCapture createVideoCapture(Context context, boolean useCamera2) {
        if(useCamera2 && isLReleaseOrLater() && !isLegacyDevice(context)){
            return new VideoCaptureCamera2(context);
        }
        return new VideoCaptureCamera(context);
    }
}
