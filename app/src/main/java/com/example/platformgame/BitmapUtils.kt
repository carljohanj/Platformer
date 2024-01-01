package com.example.platformgame

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point


object BitmapUtils
{
    private const val TAG = "BitmapUtils"
    private const val USE_BILINEAR_FILTERING = true

    fun scaleBitmap(bmp: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap
    {
        if (targetWidth == bmp.width && targetHeight == bmp.height)
        {
            return bmp
        }
        val newDimensions = scaleToTargetDimensions(
            targetWidth.toFloat(),
            targetHeight.toFloat(),
            bmp.width.toFloat(),
            bmp.height.toFloat()
        )
        return Bitmap.createScaledBitmap(bmp, newDimensions.x, newDimensions.y, USE_BILINEAR_FILTERING)
    }

    //Set either of the dimensions for aspect-correct scaling, or both to force the aspect.
    @Throws(OutOfMemoryError::class)
    fun loadScaledBitmap(context: Context, bitmapName: String,
                         targetWidth: Int, targetHeight: Int): Bitmap
    {
        val res = context.resources
        val resID = res.getIdentifier(bitmapName, "drawable", context.packageName)
        return loadScaledBitmap(res, resID, targetWidth, targetHeight)
    }

    //Set either of the dimensions for aspect-correct scaling, or both to force the aspect.
    @Throws(OutOfMemoryError::class)
    fun loadScaledBitmap(res: Resources, resID: Int, targetWidth: Int, targetHeight: Int): Bitmap
    {
        val options = readBitmapMetaData(res, resID) //parse raw file info into _options
        val newDimensions = scaleToTargetDimensions(
            targetWidth.toFloat(),
            targetHeight.toFloat(),
            options.outWidth.toFloat(),
            options.outHeight.toFloat()
        )
        var bitmap: Bitmap = loadSubSampledBitmap(res, resID, newDimensions.x, newDimensions.y, options)
        if (bitmap.height != newDimensions.y || bitmap.width != newDimensions.x)
        {
            //scale to pixel-perfect dimensions in case we have non-uniform density on x / y
            bitmap = Bitmap.createScaledBitmap(bitmap, newDimensions.x, newDimensions.y, USE_BILINEAR_FILTERING)
        }
        return bitmap
    }

    private fun loadSubSampledBitmap(res: Resources, resId: Int,targetWidth: Int,
                                     targetHeight: Int, opts: BitmapFactory.Options): Bitmap
    {
        //calculates closest POT sample factor
        opts.inSampleSize = calculateInSampleSize(opts, targetWidth, targetHeight)
        opts.inJustDecodeBounds = false
        opts.inScaled = true //scale after subsampling, to reach exact target density.
        if (targetHeight > 0)
        {
            opts.inDensity = opts.outHeight
            opts.inTargetDensity = targetHeight * opts.inSampleSize
        }
        else
        {
            opts.inDensity = opts.outWidth
            opts.inTargetDensity = targetWidth * opts.inSampleSize
        }
        return BitmapFactory.decodeResource(res, resId, opts)
    }

    private fun calculateInSampleSize(opts: BitmapFactory.Options, reqWidth: Int,
                                      reqHeight: Int): Int
    {
        val height = opts.outHeight // original height and width of image
        val width = opts.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth)
        {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth)
            {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    //loads metadata about a Bitmap into the Options object. Does *not* load the Bitmap.
    private fun readBitmapMetaData(res: Resources, resID: Int): BitmapFactory.Options
    {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //only read metadata right now
        BitmapFactory.decodeResource(res, resID, options)
        options.inJustDecodeBounds = false //next time we use this option-object, read the actual file.
        return options
    }

    //provide both or either of targetWidth / targetHeight. If one is left at 0, the other is calculated
    //based on source-dimensions. Ergo; should scale and keep aspect ratio.
    private fun scaleToTargetDimensions(targetWidth: Float, targetHeight: Float, srcWidth: Float,
                                        srcHeight: Float): Point
    {
        val actualTargetHeight = if(targetWidth > 0) targetWidth else srcWidth
        val actualTargetWidth = if(targetHeight > 0) targetHeight else srcHeight
        val newDimensions = Point(actualTargetHeight.toInt(), actualTargetWidth.toInt())

        if (actualTargetHeight == 0f || actualTargetWidth == 0f)
        {
            //formula: new height = (original height / original width) x new width
            if (actualTargetWidth > 0)
            { //if Y is configured, calculate X
                newDimensions.x = (srcWidth / srcHeight * actualTargetWidth).toInt()
            }
            else
            { //else keep X and calculate Y
                newDimensions.y = (srcHeight / srcWidth * actualTargetHeight).toInt()
            }
        }
        return newDimensions
    }

    fun rotate(source: Bitmap, angle: Float): Bitmap
    {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun flip(source: Bitmap, horizontally: Boolean): Bitmap
    {
        val matrix = Matrix()
        val centerX = source.width * 0.5f
        val centerY = source.height * 0.5f
        if (horizontally)
        {
            matrix.postScale(1.0f, -1.0f, centerX, centerY)
        }
        else
        {
            matrix.postScale(-1.0f, 1.0f, centerX, centerY)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun scaleToHeight(source: Bitmap, height: Int): Bitmap
    {
        val ratio = height / source.height.toFloat()
        val newHeight = (source.height * ratio).toInt()
        val newWidth = (source.width * ratio).toInt()
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }
}