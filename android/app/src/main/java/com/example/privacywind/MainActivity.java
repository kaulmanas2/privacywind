package com.example.privacywind;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import org.json.JSONObject;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "com.example.test_permissions_app/permissions";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(getFlutterEngine());


        new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

                PackageManager packageManager = getPackageManager();
                HashMap<String, List<String>> permissionsForApp = new HashMap<>();

                String executeFunctionType = call.method;

                switch (executeFunctionType) {
                    case "getAppPermission": {
                        final String packageName = call.arguments();
                        try {
                            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

                            if (packageInfo.requestedPermissions != null) {
                                String[] permissionList = packageInfo.requestedPermissions;

                                int[] permissionCodeInt = packageInfo.requestedPermissionsFlags;
                                String[] permissionCode = new String[permissionCodeInt.length];
                                for (int i = 0; i < permissionCodeInt.length; i++) {
                                    permissionCode[i] = String.valueOf(permissionCodeInt[i]);
                                }

                                permissionsForApp.put("permission_list", Arrays.asList(permissionList));
                                permissionsForApp.put("permission_code", Arrays.asList(permissionCode));

                                result.success(permissionsForApp);
                            }
                        } catch (Exception e) {
                            Log.i("ERROR ==>", e.getMessage());
                            result.error("-1", "Error", "Error in fetching permissions for the particular package");
                        }
                        break;
                    }
                    case "openAppInfo": {
                        final String packageName = call.arguments();
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + packageName));
                            startActivity(intent);
                            result.success("SUCCESS");
                        } catch (Exception e) {
                            Log.i("ERROR", e.getMessage());
                            result.error("-1", "Error", "Error in opening app info settings for the particular package");
                        }
                        break;
                    }
                }
            }
        });
    }
}
