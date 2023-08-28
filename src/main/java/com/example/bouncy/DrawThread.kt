package com.example.bouncy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import java.util.Random
import kotlin.math.min


class DrawThread(context: Context, holder: SurfaceHolder, H:Int, W: Int) : Thread() {

    var context: Context
    var holder: SurfaceHolder
    var sprite = Sprite(context, context.resources)
    var candy = Candy(context, context.resources, H, W)
    var spikes_right = ArrayList<Spike>()
    var spikes_left = ArrayList<Spike>()
    val H:Int
    val W:Int
    val random = Random()
    var not_on_right_wall:Boolean = true
    var not_on_left_wall:Boolean = true
    var canvas: Canvas?
    var lastTime = System.currentTimeMillis()
    var frames = 0
    var delta = 0L
    var cachedBitmap: Bitmap
    var cachedCanvas: Canvas
    var color = Color.parseColor("#BBD6B5")
    var candy_cnt = 40
    var game_started = false
    var matrix = Matrix()
    var max_spikes_left = 4
    var max_spikes_right = 4

    init {
        holder.also { this.holder = it }
        context.also { this.context = it }

        this.H = H
        this.W = W

        generateSpikes()
        generateSpikes(false)

        canvas = null
        this.cachedBitmap = Bitmap.createBitmap(W, H,
            Bitmap.Config.ARGB_8888);
        cachedCanvas = Canvas(this.cachedBitmap);
        cachedCanvas.drawColor(color)
        matrix.postRotate(180F)

        generateBounds()
        render()
    }

    override fun run() {
        while(running) {

//            canvas = null;
//            update(1000)
//            render()
//            sleep(29)
            val now = System.currentTimeMillis()
            var t  = now - lastTime
            lastTime = min(lastTime,now-50)
            delta = (now - lastTime)

            if (game_started == false) {
                lastTime = System.currentTimeMillis()
                continue
            }

            update(delta)

            canvas = null
            frames++
            render()
            lastTime = System.currentTimeMillis()

        }

    }

    fun requestStop() {
        running = false
    }

    fun generateSpikes(is_right: Boolean = true){
        var cnt = H/100-1 // кол-во возможных мест для шипов
        if (cnt*100>=H-100) cnt--

        var place = random.nextInt(cnt)

        synchronized(spikes_right) {

            if (is_right) {

                if (spikes_left.size < max_spikes_right) {
                    for (i in spikes_right.size .. max_spikes_right) {
                        place = random.nextInt(cnt) + 1
                        spikes_right.add(Spike(context, context.resources, W - 101, place * 100))
                    }
                } else {
                    for (i in 0 until max_spikes_right) {
                        place = random.nextInt(cnt) + 1
                        spikes_right[i].y = 100 * place
                        spikes_right[i].generateBound()
                    }
                }
                if (points>0 && points%20 == 0 && max_spikes_right-4<cnt) max_spikes_right++
            } else {

                if (spikes_left.size < max_spikes_left) {
                    for (i in spikes_left.size .. max_spikes_left) {
                        place = random.nextInt(cnt) + 1
                        spikes_left.add(Spike(context, context.resources, 0, place * 100, false))
                    }
                } else {
                    for (i in 0 until max_spikes_left) {
                        place = random.nextInt(cnt) + 1
                        spikes_left[i].y = 100 * place
                        spikes_left[i].generateBound()
                    }
                }
                if (points>0 && points%20 == 0 && max_spikes_left-4<cnt ) max_spikes_left++
            }
        }
    }

    fun drawSpikes(canvas: Canvas) {
        for(i in 0 until spikes_right.size){
            spikes_right[i].draw(canvas)
            spikes_left[i].draw(canvas)
        }
    }

    fun generateBounds(){
        var cnt = W/100
        var matrixUp = Matrix()
        matrixUp.postRotate(270F)

        var matrixDown = Matrix()
        matrixDown.postRotate(90F)

        var bitmapUp = BitmapFactory.decodeResource(context.resources, R.drawable.rightspike)
        bitmapUp = Bitmap.createScaledBitmap(bitmapUp, 100, 100, false)

        var bitmapDown = Bitmap.createBitmap(bitmapUp, 0, 0, bitmapUp.width, bitmapUp.height, matrixDown, true)
        bitmapUp = Bitmap.createBitmap(bitmapUp, 0, 0, bitmapUp.width, bitmapUp.height, matrixUp, true)

        var rect = Rect(0, 0, bitmapUp.width - 1, bitmapUp.height - 1)
        var rect_dist: Rect = Rect(0, 0, 0 + bitmapUp.width, 0 + bitmapUp.height)

        for (x in 0 .. cnt){
            rect_dist.top = 0
            rect_dist.bottom = 0 + bitmapUp.height

            cachedCanvas.drawBitmap(bitmapUp, rect, rect_dist, Paint())
            rect_dist.top = H-bitmapUp.height
            rect_dist.bottom = H-100+ bitmapUp.height

            cachedCanvas!!.drawBitmap(bitmapDown, rect, rect_dist, Paint())
            rect_dist.left=rect_dist.left+100
            rect_dist.right=rect_dist.right+100

        }
    }

    fun SpriteCollide(): Boolean{
        if (sprite.x > W - 200) {
            for (spike in spikes_right) {
                if (spike.checkCollision(sprite)) {
                    return true
                }
            }
        } else {
            for (spike in spikes_left) {
                if (spike.checkCollision(sprite)) {
                    return true
                }
            }
        }
        return false
    }

    fun EndGame(){
        //dead :(
        val intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("points",points)
        context.startActivity(intent)
        requestStop()
        (context as? Activity)?.finish()
    }

    fun update(delta: Long){
        if (sprite.y<=100 || sprite.y+sprite.height >=H-100 || ((sprite.x <=100 || sprite.x+sprite.width>=W-100) && SpriteCollide())) {
            EndGame()
        }

        if (candy.checkCollision(sprite)) {
            points+=3
            candy.respawnCandy()
            candy_cnt = 0
        }

        sprite.setXY(H, W, this.jumped, delta)

        if (not_on_right_wall && sprite.x+sprite.width>=W) {
            not_on_right_wall = false
            points++
            sprite.bitmap = sprite.bitmap_right
        }

        if (!not_on_right_wall && sprite.x+sprite.width<W-150) {
            generateSpikes()
            not_on_right_wall = true
        }

        if (not_on_left_wall && sprite.x<=20) {
            not_on_left_wall = false
            points++
            sprite.bitmap = sprite.bitmap_left
        }
        if (!not_on_left_wall && sprite.x>=150) {
            generateSpikes(false)
            not_on_left_wall = true
        }
        this.jumped = false
    }

    fun render(){
        var p = Paint()

        p.textSize = 250F
        p.textAlign = Paint.Align.CENTER
        p.color = Color.WHITE
        p.typeface = Typeface.DEFAULT_BOLD
        val fontMetrics: Paint.FontMetrics = p.fontMetrics
        val textHeight = fontMetrics.descent - fontMetrics.ascent

        canvas = holder?.lockCanvas()

        if (canvas != null) {
            try {

                canvas!!.drawBitmap(this.cachedBitmap, 0F, 0F, p)

                p.color = Color.WHITE
                canvas!!.drawCircle(W/2.toFloat(),H/2.toFloat(),270F,p)
                p.color = color
                canvas!!.drawText(points.toString(),W.toFloat()/2, (H).toFloat()/2 - textHeight/2 - fontMetrics.ascent,p)

                drawSpikes(canvas!!)
                if(candy_cnt <40) {
                    p.color = Color.parseColor("#FFE200")
                    p.textSize = 85F
                    p.typeface = Typeface.DEFAULT
                    canvas!!.drawText("+3",candy.prev_x.toFloat()+10, candy.prev_y.toFloat()+10,p)
                    candy_cnt++
                }
                candy.draw(canvas)
                sprite.draw(canvas)
                if (game_started == false) {
                    p.color = Color.WHITE
                    canvas!!.drawCircle(W/2.toFloat(),H/2.toFloat(),270F,p)
                    p.textSize = 100F
                    p.typeface = Typeface.DEFAULT
                    p.color = Color.parseColor("#656565")
                    canvas!!.drawText("TAP TO START",W.toFloat()/2, (H).toFloat()/2,p)
                }
            } catch (e: Exception) {
            } finally {
                holder?.unlockCanvasAndPost(canvas)
            }
        }
    }


    @Volatile
    var running: Boolean = true
    @Volatile
    var jumped: Boolean = false;
    @Volatile
    var points: Int = 0
}