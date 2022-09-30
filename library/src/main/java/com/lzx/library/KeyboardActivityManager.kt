package com.lzx.library


import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import java.lang.ref.WeakReference
import java.util.LinkedList

enum class KeyboardActivityManager {
    INSTANCE;

    private var mActivity: Activity? = null
    val actList = LinkedList<WeakReference<Activity?>>()

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActivity = activity
                addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) { //
            }

            override fun onActivityDestroyed(activity: Activity) {
                removeActivity(activity)
                if (isCurrentActivity(activity)) {
                    currentActivity = null
                }
            }
        })
    }

    private fun isCurrentActivity(activity: Activity): Boolean {
        return activity === currentActivity
    }

    var currentActivity: Activity?
        get() = mActivity
        private set(activity) {
            if (activity == null) {
                mActivity = null
                if (actList.size != 0) {
                    mActivity = actList[actList.size - 1].get()
                }
            } else if (mActivity == null || mActivity !== activity) {
                mActivity = activity
            }
        }

    private fun addActivity(activity: Activity?) {
        if (activity != null) {
            actList.add(WeakReference(activity))
        }
    }

    private fun removeActivity(activity: Activity?) {
        if (activity != null) {
            val iterator: MutableIterator<WeakReference<Activity?>> = actList.iterator()
            while (iterator.hasNext()) {
                val obj: WeakReference<Activity?> = iterator.next()
                if (obj.get() === activity) {
                    iterator.remove()
                    break
                }
            }
        }
    }
}