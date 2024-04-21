package com.display.photos.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.display.photos.R
import com.display.photos.data.model.PhotosResponse
import com.display.photos.databinding.ItemPhotoBinding
import com.display.photos.util.Utils
import com.display.photos.util.cache.CachedDataImpl
import java.net.URL

class PhotosAdapter(
    private val photosResponseMutableList: MutableList<PhotosResponse>,
    private val cachedDataImpl: CachedDataImpl
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val downloaderThread: HandlerThread = HandlerThread("photoDownloaderThread")
    private val bgHandler: ImageDownloadHandler
    private val uiHandler: ImageShowHandler

    init {
        downloaderThread.start()
        uiHandler = ImageShowHandler(Looper.getMainLooper(), cachedDataImpl)
        bgHandler = ImageDownloadHandler(downloaderThread.looper, uiHandler, cachedDataImpl)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = photosResponseMutableList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PhotoViewHolder)
            holder.bind(photosResponseMutableList[position])
    }

    fun addData(photosResponseList: List<PhotosResponse>) {
        val size = this.photosResponseMutableList.size
        this.photosResponseMutableList.addAll(photosResponseList)
        val newSize = this.photosResponseMutableList.size
        notifyItemRangeChanged(size, newSize)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        photosResponseMutableList.clear()
        notifyDataSetChanged()
    }

    inner class PhotoViewHolder(binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {

        private val rootLayout: ConstraintLayout = binding.rootLayout
        val photoImgView: ImageView = binding.image

        fun bind(item: PhotosResponse) {
            val imageUrl = item.urls.imageUrl
            val cacheBitmap = cachedDataImpl.getImage(imageUrl)
            if (cacheBitmap != null) {
                photoImgView.setImageBitmap(cacheBitmap)
            } else {
                //showing the app icon as initial drawable
                photoImgView.setImageResource(R.drawable.ic_launcher_foreground)
                val context = photoImgView.context
                val hasNetworkConnection = Utils.hasInternetConnection(context)
                if (hasNetworkConnection) {
                    val message = Message()
                    message.obj = photoImgView
                    message.what = KEY_HANDLER_MESSAGE_CODE
                    message.data = Bundle()
                    message.data.putString(KEY_IMAGE_URL, imageUrl)

                    bgHandler.sendMessage(message)
                } else {
                    Utils.displayToast(
                        context,
                        context.resources.getString(R.string.no_network_text)
                    )
                }
            }

            val set = ConstraintSet()
            set.clone(rootLayout)
            set.setDimensionRatio(photoImgView.id, "${item.width}:${item.height}")
            set.applyTo(rootLayout)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is PhotoViewHolder) {
            bgHandler.removeMessages(KEY_HANDLER_MESSAGE_CODE, holder.photoImgView)
            uiHandler.removeMessages(KEY_HANDLER_MESSAGE_CODE, holder.photoImgView)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        downloaderThread.quit()
        cachedDataImpl.clearCache()
    }

    companion object {
        const val KEY_IMAGE_URL = "imageUrl"
        const val KEY_HANDLER_MESSAGE_CODE = 1
    }
}

/**
 * To download the image and cache the downloaded image.
 */
class ImageDownloadHandler(
    looper: Looper,
    private val uiHandler: ImageShowHandler,
    private val cachedDataImpl: CachedDataImpl
) : Handler(looper) {

    override fun handleMessage(inMessage: Message) {
        val imageUrl = inMessage.data.getString(PhotosAdapter.KEY_IMAGE_URL)
        val photoImageView = inMessage.obj as ImageView
        imageUrl?.let { url ->
            downloadImage(url)?.let {

                cachedDataImpl.saveImage(url, it)

                val outMessage = Message()
                outMessage.obj = photoImageView
                outMessage.what = PhotosAdapter.KEY_HANDLER_MESSAGE_CODE
                outMessage.data = Bundle()
                outMessage.data.putString(PhotosAdapter.KEY_IMAGE_URL, url)

                val newMessagesEnqueuedForThisImageView =
                    hasMessages(PhotosAdapter.KEY_HANDLER_MESSAGE_CODE, photoImageView)
                if (!newMessagesEnqueuedForThisImageView) {
                    uiHandler.sendMessage(outMessage)
                }
            } ?: run {
                loadRemoteUrl(photoImageView, imageUrl)
            }
        } ?: run {
            //we already showing the placeHolder image. So, updating the user
            Utils.displayToast(
                photoImageView.context,
                photoImageView.context.resources.getString(R.string.url_can_not_empty)
            )
        }
    }

    /**
     * To load the url into the imageview.
     * usage: If we get any exception while caching the image(downloading the file or converting
     * the bitmap to file or vice-versa) we can show load the url directly and can cache next time.
     */
    private fun loadRemoteUrl(photoImageView: ImageView, imageUrl: String) {
        val image = BitmapFactory.decodeStream(URL(imageUrl).openConnection().getInputStream())
        Handler(Looper.getMainLooper()).post {
            photoImageView.setImageBitmap(image)
        }
    }

    /**
     * To download the image from the given url
     *
     * @param url the url of the image
     */
    private fun downloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        URL(url).openStream()?.use { bitmap = BitmapFactory.decodeStream(it) }
        return bitmap
    }
}

/**
 * To update the UI - handle and show the bitmap to the imageview.
 */
class ImageShowHandler(
    looper: Looper,
    private val cachedDataImpl: CachedDataImpl
) : Handler(looper) {

    override fun handleMessage(inMessage: Message) {
        val imageUrl = inMessage.data.getString(PhotosAdapter.KEY_IMAGE_URL)
        imageUrl?.let { url ->
            val ivImage = inMessage.obj as ImageView
            ivImage.setImageBitmap(cachedDataImpl.getImage(url))
        }
    }
}