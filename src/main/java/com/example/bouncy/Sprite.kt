package com.example.bouncy

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect

class Sprite(context: Context, resources: Resources) {
    var rects: ArrayList<Rect>

    var width: Int
    var height: Int
    var count = 0
    var x = 100
    var y = 300
    var xVelocity = 10F
    var yVelocity = 10F
    var minY = -1
    var startTime = System.currentTimeMillis()

    var bitmap: Bitmap //our image
    var bitmap_right: Bitmap
    var bitmap_left: Bitmap
    //var bitmap_reversed: Bitmap

    val ACCELERATION = 1.1F
    val CONSTANT_Y_VELOCITY = -30F

    init {

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.birdy_sprite)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            700,
            150,
            false
        )
        width = bitmap.width / 4
        height = bitmap.height
        rects = ArrayList<Rect>()

        for (j in 0..3) {
            rects.add(Rect(j * width, 0, (j + 1) * width, height))
            rects.add(Rect(j * width, 0, (j + 1) * width, height))
        }

        var matrix = Matrix()
        matrix.preScale(-1f, 1f)
        bitmap_right = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        bitmap_left = bitmap
    }

    fun update_coords(delta: Long) {
        this.x += (15F*delta*xVelocity/1000F).toInt()//xVelocity.toInt()//(10F/1000F*delta*xVelocity).toInt()
        this.y += (15F*delta*yVelocity/1000F).toInt()//yVelocity.toInt()//10F/1000F*delta*yVelocity).toInt()
    }


    fun setXY(H: Int, W: Int, jumped: Boolean, delta: Long) {

        this.yVelocity = ACCELERATION * 1 + this.yVelocity


        if (jumped) {
            minY = this.y - 300
            this.yVelocity = CONSTANT_Y_VELOCITY
        }

        if (minY == -1) this.minY = 0
        val maxX = W - (this.width / 2)
        val minX = -(this.width / 2)
        var maxY = H + (this.height / 2)

        if (this.y < minY) {
            this.y = minY
            this.yVelocity = -this.yVelocity
        } else if (this.y > maxY) {
            this.y = maxY
            this.yVelocity = -this.yVelocity
        }

        if (this.x > maxX) {
            this.x = (maxX)
            this.xVelocity = -this.xVelocity
        } else if (this.x < minX) {
            this.x = (minX)
            this.xVelocity = -this.xVelocity
        }
        update_coords(delta)
        startTime = System.currentTimeMillis()

    }

    fun draw(canvas: Canvas?) {
        val rect_dist: Rect = Rect(x, y, x + width, y + height)
        canvas!!.drawBitmap(bitmap, rects[count], rect_dist, Paint())
        next()
        var p = Paint()
        p.color = Color.RED
    }

    fun next() {
        ++count
        count = (count) % rects.size
    }
}

























