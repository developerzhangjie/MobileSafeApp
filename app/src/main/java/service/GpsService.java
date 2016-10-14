package service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import utils.Constants;
import utils.SharedPreferencesUtils;

/**
 * Created by Sin on 2016/9/21.
 * Description:
 */

public class GpsService extends Service {
    private boolean isSent = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //1.获取位置管理者
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.1获取定位方式
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            System.out.println(provider);
        }
        //2.2获取最佳定位方式
        Criteria criteria = new Criteria();
        //确定定位方式是GPS，只有GPS能获取海拔
        criteria.setAltitudeRequired(true);
        String bestProvider = locationManager.getBestProvider(criteria, true);
        //3.定位操作
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        isSent = false;
        locationManager.requestLocationUpdates(bestProvider, 0, 0, new MyLoctionListener());

    }

    private class MyLoctionListener implements LocationListener {
        //位置改变时调用
        @Override
        public void onLocationChanged(Location location) {
            if (!isSent) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                Double altitude = location.getAltitude();
                System.out.println("经度：" + longitude + "  纬度：" + latitude + "海拔" + altitude);

                Geocoder geocoder = new Geocoder(getApplicationContext());
                Locale.getDefault();
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
                    String address = addresses.get(0).getAddressLine(0);
                    String safeNumber = SharedPreferencesUtils.getString(GpsService.this, Constants.SAFENUMBER, "15376771229");
                    if (!TextUtils.isEmpty(safeNumber) && !TextUtils.isEmpty(address)) {
                        SmsManager.getDefault().sendTextMessage(safeNumber, null, address, null, null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isSent = true;
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
