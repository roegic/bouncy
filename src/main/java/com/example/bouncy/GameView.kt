package com.example.bouncy
import android.content.Context
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.SurfaceHolder


class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback{

    init {
        holder.addCallback(this)
    }

    var drawThread: DrawThread? = null

    override fun surfaceCreated(p0: SurfaceHolder) {

        drawThread = DrawThread(context, holder, this.height, this.width)
        drawThread?.start()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        drawThread?.requestStop()

        var retry = true
        while (retry) {
            try {
                drawThread?.join()
                retry = false
            } catch (e: InterruptedException) {

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            if (event.action == MotionEvent.ACTION_DOWN ) {
                if (drawThread!= null) {
                    synchronized(drawThread!!){

                        if (drawThread!!.game_started == false) {
                            drawThread!!.game_started = true
                        } else {
                            drawThread!!.jumped = true
                        }
                    }
                }
            }
        }
        return false
    }
}























