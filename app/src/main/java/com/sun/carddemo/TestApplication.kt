package com.sun.carddemo

//import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.Manifest
import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.danikula.videocache.file.Md5FileNameGenerator
import com.sun.carddemo.cache.ICache
import com.sun.carddemo.intercept.InterceptCallback
import com.sun.carddemo.intercept.StarrySkyInterceptor
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener
import com.sun.carddemo.util.StarrySkyConstant
import com.sun.carddemo.util.showToast
import com.sun.carddemo.util.toSdcardPath
import com.sun.carddemo.viewmodel.MusicViewModel
import java.io.File


open class TestApplication : Application() {

    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        //bugly

    }

    /**
     * 权限申请拦截器
     */
    class PermissionInterceptor internal constructor(private val mContext: Context) : StarrySkyInterceptor() {
        override fun process(songInfo: SongInfo?, callback: InterceptCallback) {
            if (songInfo == null) {
                callback.onInterrupt("SongInfo is null")
                return
            }
            val hasPermission = SpConstant.HAS_PERMISSION
            if (hasPermission) {
                callback.onNext(songInfo)
                return
            }
            SoulPermission.getInstance().checkAndRequestPermissions(
                Permissions.build(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
                object : CheckRequestPermissionsListener {
                    override fun onAllPermissionOk(allPermissions: Array<Permission>) {
                        SpConstant.HAS_PERMISSION = true
                        callback.onNext(songInfo)
                    }

                    override fun onPermissionDenied(refusedPermissions: Array<Permission>) {
                        SpConstant.HAS_PERMISSION = false
                        callback.onInterrupt("没有权限，播放失败")
                        mContext.showToast("没有权限，播放失败")
                    }
                })
        }

        override fun getTag(): String = "PermissionInterceptor"
    }

    /**
     * 模拟请求封面，url拦截器
     */
    class RequestSongInfoInterceptor : StarrySkyInterceptor() {

        val z = "https://n.sinaimg.cn/sinacn10115/439/w641h598/20200214/4a9b-ipmxpvz8164848.jpg"
        val a = "https://www.yxwoo.com/uploads/images/xiaz/2020/0629/1593419103514.jpg"
        val b = "https://tupian.qqw21.com/article/UploadPic/2020-6/202061122583635797.jpg"
        val c = "https://img.xintp.com/2020/06/05/41xmurd45ei.jpg"
        val d = "https://www.yxwoo.com/uploads/images/xiaz/2020/0629/1593419103842.jpg"
        val e = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRWIl7bedDSfIb9VLSrQWKIS85S0p-Be-qhLw&usqp=CAU"
        val f = "https://i.pinimg.com/736x/05/f9/a2/05f9a2605cd47a742bad0466f632fa72.jpg"
        val g = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFkIzh8fgjP8MCnV8ziC6nkEkUfMEcbjSssA&usqp=CAU"

        val map = hashMapOf<String, String>()

        init {
            map["z"] = z
            map["a"] = a
            map["b"] = b
            map["c"] = c
            map["d"] = d
            map["e"] = e
            map["f"] = f
            map["g"] = g
        }

        override fun process(songInfo: SongInfo?, callback: InterceptCallback) {
            if (songInfo?.songCover.isNullOrEmpty()) {
                songInfo?.songCover = map[songInfo?.songName].orEmpty()
            }
            if (songInfo?.songUrl.isNullOrEmpty()) {
                val name = songInfo?.songName + "(" + songInfo?.songId + ").m4a"
                songInfo?.songUrl = MusicViewModel.baseUrl + name
            }
            callback.onNext(songInfo)
        }

        override fun getTag(): String = "RequestSongCoverInterceptor"
    }

    /**
     * 使用 AndroidVideoCache 这个第三方库做缓存的例子
     */
    class AndroidVideoCache(private val context: Context) : ICache {

        private var proxy: HttpProxyCacheServer? = null
        private var cacheFile: File? = null

        private fun getProxy(songInfo: SongInfo?): HttpProxyCacheServer? {
            return if (proxy == null) newProxy(songInfo).also { proxy = it } else proxy
        }

        private fun newProxy(songInfo: SongInfo?): HttpProxyCacheServer? {
            val builder = HttpProxyCacheServer.Builder(context)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .cacheDirectory(getCacheDirectory(context, ""))
            if (songInfo == null) {
                builder.fileNameGenerator(Md5FileNameGenerator())
            } else {
                builder.fileNameGenerator { url ->
                    val extension = getExtension(url)
                    val name = songInfo.songId
                    if (extension.isEmpty()) name else "$name.$extension"
                }
            }
            return builder.build()
        }

        private fun getExtension(url: String?): String {
            if (url.isNullOrEmpty()) return ""
            val dotIndex = url.lastIndexOf('.')
            val slashIndex = url.lastIndexOf('/')
            return if (dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + 4 > url.length) url.substring(
                dotIndex + 1,
                url.length
            ) else ""
        }

        override fun getProxyUrl(url: String, songInfo: SongInfo): String? {
            return if (isOpenCache()) getProxy(songInfo)?.getProxyUrl(url) else url
        }

        override fun isOpenCache(): Boolean {
            return StarrySkyConstant.KEY_CACHE_SWITCH
        }

        override fun getCacheDirectory(context: Context, destFileDir: String?): File? {
            var fileDir = destFileDir
            if (fileDir.isNullOrEmpty()) {
                fileDir = "00StarrySkyCache/".toSdcardPath()
            }
            if (cacheFile == null && fileDir.isNotEmpty()) {
                cacheFile = File(fileDir)
                if (cacheFile?.exists() == false) {
                    cacheFile?.mkdirs()
                }
            }
            if (cacheFile == null) {
                cacheFile = context.getExternalFilesDir(null)
                if (cacheFile == null) {
                    cacheFile = context.filesDir
                }
            }
            return cacheFile
        }

        override fun isCache(url: String): Boolean {
            return true
            // return getProxy(null)?.isCached(url) ?: false
        }
    }
}