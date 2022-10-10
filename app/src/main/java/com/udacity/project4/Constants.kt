package com.udacity.project4

import com.google.android.gms.location.Geofence

object Constants {
    const val ACTION_GEOFENCE_EVENT = "SaveReminder.reminder.action.ACTION_GEOFENCE_EVENT"
    const val GEOFENCE_RADIUS_IN_METERS = 100f
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
    const val NEVER_EXPIRES = Geofence.NEVER_EXPIRE
    const val JOB_ID = 573
    const val TAG = "GeofenceIntentSer"
    const val PERMISSION_CODE_LOCATION_REQUEST = 1
    const val DEFAULT_ZOOM_LEVEL = 15f
     const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
     const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
     const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
     const val TAG_save = "SaveReminderFragment"
     const val LOCATION_PERMISSION_INDEX = 0
     const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1

}
