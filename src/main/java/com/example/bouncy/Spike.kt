package com.example.bouncy

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

class Spike(context: Context, resources: Resources, x: Int, y: Int, is_right: Boolean = true) {
    var rect: Rect
    var rects: ArrayList<Rect>
    var width: Int
    var height: Int
    var count = 0
    var x: Int
    var y: Int
    val is_right: Boolean


    var bitmap: Bitmap //our image

    init {
        if (is_right) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.rightspike)
        } else {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.leftspike)
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        width = bitmap.width
        height = bitmap.height
        this.is_right = is_right

        rect = Rect(0, 0, width - 1, height - 1)

        this.x = x
        this.y = y

        rects = ArrayList<Rect>()
        generateBound()
    }


    fun draw(canvas: Canvas?) {
        val rect_dist: Rect = Rect(x, y, x + width, y + height)
        canvas!!.drawBitmap(bitmap, rect, rect_dist, Paint())
    }

    fun checkCollision(sprite: Sprite): Boolean {
        var constOffset = 17
        val sprite_rect = Rect(
            sprite.x+constOffset,
            sprite.y+constOffset,
            sprite.x + sprite.width -constOffset,
            sprite.y + sprite.height -constOffset
        )
        for (rct in this.rects) {
            if (rct.intersect(sprite_rect)) return true
        }
        return false
    }

    fun generateBound() {

        rects.clear()
        if (is_right) {
            rects.add(Rect(x, y + 40, x+50 , y + height - 40))
            rects.add(Rect(x+50, y + 25, x+100 , y + height - 25))
            return
        }
        val sw = 50
        var sh = 15
        var j = 25
        var i = 0
        while (i <= this.width - sw && j < (y + height)) {
            rects.add(Rect(i, y + j, i + sw, y + height - j))
            i += sw
            j += sh
        }

    }
}
