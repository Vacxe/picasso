package com.squareup.picasso3

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_8888
import android.net.Uri
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.common.truth.Truth
import com.squareup.picasso3.MediaStoreRequestHandler.PicassoKind.FULL
import com.squareup.picasso3.MediaStoreRequestHandler.PicassoKind.MICRO
import com.squareup.picasso3.MediaStoreRequestHandler.PicassoKind.MINI
import com.squareup.picasso3.RequestHandler.Callback
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaStoreRequestHandlerTest2 {
  private lateinit var context: Context
  private lateinit var picasso: Picasso
  private lateinit var contentResolver: ContentResolver
  private lateinit var externalImages: Uri

//  @get:Rule
//  val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(READ_EXTERNAL_STORAGE)

  @Before fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    picasso = Picasso.Builder(context)
      .callFactory { throw AssertionError() }
      .withCacheSize(0)
      .build()

    contentResolver = context.contentResolver
    externalImages = MediaStore.Images.Thumbnails.getContentUri("volumeName")
  }

  @Test fun decodesVideoThumbnailWithVideoMimeType() {
    val bitmap = makeBitmap()
    val request = Request.Builder(uri = MEDIA_STORE_CONTENT_1_URL)
      .config(ARGB_8888)
      .stableKey(MEDIA_STORE_CONTENT_KEY_1)
      .resize(100, 100)
      .build()
    val requestHandler = MediaStoreRequestHandler(context)
    requestHandler.load(
      picasso = picasso,
      request = request,
      callback = object : Callback {
        override fun onSuccess(result: RequestHandler.Result?) {
          val bitmapResult = result as RequestHandler.Result.Bitmap
          Truth.assertThat(bitmapResult.bitmap).isEqualTo(bitmap)
        }

        override fun onError(t: Throwable) = Assert.fail(t.message)
      }
    )
  }

  @Test fun decodesImageThumbnailWithImageMimeType() {
    val bitmap = makeBitmap(20, 20)
    val request = Request.Builder(uri = MEDIA_STORE_CONTENT_1_URL)
      .config(ARGB_8888)
      .stableKey(MEDIA_STORE_CONTENT_KEY_1)
      .resize(100, 100)
      .build()
    val requestHandler = MediaStoreRequestHandler(context)
    requestHandler.load(
      picasso = picasso,
      request = request,
      callback = object : Callback {
        override fun onSuccess(result: RequestHandler.Result?) {
          val bitmapResult = result as RequestHandler.Result.Bitmap
          Truth.assertThat(bitmapResult.bitmap).isEqualTo(bitmap)
        }

        override fun onError(t: Throwable) = Assert.fail(t.message)
      }
    )
  }

  @Test fun getPicassoKindMicro() {
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(96, 96)).isEqualTo(MICRO)
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(95, 95)).isEqualTo(MICRO)
  }

  @Test fun getPicassoKindMini() {
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(512, 384)).isEqualTo(MINI)
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(100, 100)).isEqualTo(MINI)
  }

  @Test fun getPicassoKindFull() {
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(513, 385)).isEqualTo(FULL)
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(1000, 1000)).isEqualTo(FULL)
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(1000, 384)).isEqualTo(FULL)
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(1000, 96)).isEqualTo(FULL)
    Truth.assertThat(MediaStoreRequestHandler.getPicassoKind(96, 1000)).isEqualTo(FULL)
  }

  companion object {
    val MEDIA_STORE_CONTENT_1_URL: Uri = Uri.parse("content://media/external/images/media/1")
    val MEDIA_STORE_CONTENT_KEY_1: String = Request.Builder(MEDIA_STORE_CONTENT_1_URL).build().key

    fun makeBitmap(
      width: Int = 10,
      height: Int = 10
    ): Bitmap = Bitmap.createBitmap(width, height, ALPHA_8)
  }
}
