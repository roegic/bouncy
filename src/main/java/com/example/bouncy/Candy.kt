package com.example.bouncy

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import java.util.Random

class Candy(context: Context, resources: Resources, H:Int, W: Int) {
    var rect: Rect
    var width: Int
    var height: Int
    var x: Int = 0
    var y: Int = 0
    var prev_x = 0
    var prev_y = 0
    val H: Int
    val W: Int
    val random = Random()


    var bitmap: Bitmap //our image

    init {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.candy)
        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        width = bitmap.width
        height = bitmap.height

        rect = Rect(x, y, x+width - 1, y+height - 1)
        this.H = H
        this.W = W
        respawnCandy()
    }


    fun draw(canvas: Canvas?) {
        val rect_dist: Rect = Rect(x, y, x + width, y + height)
        val rc = Rect(0,0,0+width,0+height)
        canvas!!.drawBitmap(bitmap, rc, rect_dist, Paint())
    }

    fun checkCollision(sprite: Sprite): Boolean {
        var constOffset = 17
        val sprite_rect = Rect(
            sprite.x+constOffset,
            sprite.y+constOffset,
            sprite.x + sprite.width -constOffset,
            sprite.y + sprite.height -constOffset
        )
        return rect.intersect(sprite_rect)
    }

    fun respawnCandy() {
        prev_x = x
        prev_y = y

        this.x = random.nextInt(W-220 - width)+110
        this.y = random.nextInt(H-220 - height)+110
        rect.left = x
        rect.right = x+width
        rect.top = y
        rect.bottom = y +height
    }

}
