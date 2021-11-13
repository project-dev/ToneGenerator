package jp.krohigewagma.tonegenerator.v1

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import jp.krohigewagma.tonegenerator.R

class MainActivity2 : AppCompatActivity(){

    /**
     * 音源
     */
    private val toneGenerator = ToneController(22050, 1, 8)

    /**
     * UIのボタンととOSCObjectのマッピング
     */
    private var keyMap = mutableMapOf<Int, Int>()

    /**
     * ボタンと音のマッピング
     */
    private var toneMap = mapOf(
            Pair(R.id.keyF0,  Tone.F2),
            Pair(R.id.keyFs0, Tone.F2s),
            Pair(R.id.keyG0,  Tone.G2),
            Pair(R.id.keyGs0, Tone.G2s),
            Pair(R.id.keyA0,  Tone.A2),
            Pair(R.id.keyAs0, Tone.A2s),
            Pair(R.id.keyB0,  Tone.B2),
            Pair(R.id.keyC1,  Tone.C3),
            Pair(R.id.keyCs1, Tone.C3s),
            Pair(R.id.keyD1,  Tone.D3),
            Pair(R.id.keyDs1, Tone.D3s),
            Pair(R.id.keyE1,  Tone.E3),
            Pair(R.id.keyF1,  Tone.F3),
            Pair(R.id.keyFs1, Tone.F3s),
            Pair(R.id.keyG1,  Tone.G3),
            Pair(R.id.keyGs1, Tone.G3s),
            Pair(R.id.keyA1,  Tone.A3),
            Pair(R.id.keyAs1, Tone.A3s),
            Pair(R.id.keyB1,  Tone.B3),
            Pair(R.id.keyC2,  Tone.C4),
            Pair(R.id.keyCs2, Tone.C4s),
            Pair(R.id.keyD2,  Tone.D4),
            Pair(R.id.keyDs2, Tone.D4s),
            Pair(R.id.keyE2,  Tone.E4),
    )

    // ボタンのIDの配列
    var ids = arrayListOf(
            R.id.keyF0,
            R.id.keyFs0,
            R.id.keyG0,
            R.id.keyGs0,
            R.id.keyA0,
            R.id.keyAs0,
            R.id.keyB0,
            R.id.keyC1,
            R.id.keyCs1,
            R.id.keyD1,
            R.id.keyDs1,
            R.id.keyE1,
            R.id.keyF1,
            R.id.keyFs1,
            R.id.keyG1,
            R.id.keyGs1,
            R.id.keyA1,
            R.id.keyAs1,
            R.id.keyB1,
            R.id.keyC2,
            R.id.keyCs2,
            R.id.keyD2,
            R.id.keyDs2,
            R.id.keyE2,
    )

    /**
     * ボタンのタップイベント
     * あまりよくない実装
     */
    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { v, event ->
        val osc = findViewById<Spinner>(R.id.oscSpinner).selectedItemPosition
        val level = findViewById<SeekBar>(R.id.oscLevel).progress
        var id = v.id

        if( v is ImageView){
            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    if(toneMap.containsKey(v.id)){
                        var tone = toneMap[v.id]
                        keyMap[v.id] = toneGenerator.toneOn(tone!!, level, osc)
                        if(-1 != tone.name.indexOf('s')){
                            v.setImageResource(R.drawable.ic_button01_02)
                        }else{
                            v.setImageResource(R.drawable.ic_button02_02)
                        }
                    }
                }

                MotionEvent.ACTION_MOVE->{
                    //Log.i(ToneController.APP_NAME, "move $id" )
                }

                MotionEvent.ACTION_UP->{
                    toneGenerator.toneOff(keyMap[v.id]!!)
                    var tone = toneMap[v.id]
                    keyMap.remove(v.id)
                    if(-1 != tone?.name?.indexOf('s')){
                        v.setImageResource(R.drawable.ic_button01_01)
                    }else{
                        v.setImageResource(R.drawable.ic_button02_01)
                    }
                }
                MotionEvent.ACTION_CANCEL->{
                    toneGenerator.toneOff(keyMap[v.id]!!)
                    var tone = toneMap[v.id]
                    keyMap.remove(v.id)
                    if(-1 != tone?.name?.indexOf('s')){
                        v.setImageResource(R.drawable.ic_button01_01)
                    }else{
                        v.setImageResource(R.drawable.ic_button02_01)
                    }
                }
                MotionEvent.ACTION_OUTSIDE->{
                    //Log.i(ToneController.APP_NAME, "outside $id" )
                    toneGenerator.toneOff(keyMap[v.id]!!)
                    var tone = toneMap[v.id]
                    keyMap.remove(v.id)
                    if(-1 != tone?.name?.indexOf('s')){
                        v.setImageResource(R.drawable.ic_button01_01)
                    }else{
                        v.setImageResource(R.drawable.ic_button02_01)
                    }
                }
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ボリューム調整のストリームの設定
        this.volumeControlStream = AudioManager.STREAM_MUSIC

        //TODO:全画面に対応する

        setContentView(R.layout.activity_keyborad1)

        //オシレータの選択用のアダプタ
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        adapter.add("sin")
        adapter.add("Square 50%")
        adapter.add("Square 25%")
        adapter.add("Noise")
        findViewById<Spinner>(R.id.oscSpinner).adapter = adapter

        // ボタンのIDの配列
        var ids = arrayListOf(
                R.id.keyF0,
                R.id.keyFs0,
                R.id.keyG0,
                R.id.keyGs0,
                R.id.keyA0,
                R.id.keyAs0,
                R.id.keyB0,
                R.id.keyC1,
                R.id.keyCs1,
                R.id.keyD1,
                R.id.keyDs1,
                R.id.keyE1,
                R.id.keyF1,
                R.id.keyFs1,
                R.id.keyG1,
                R.id.keyGs1,
                R.id.keyA1,
                R.id.keyAs1,
                R.id.keyB1,
                R.id.keyC2,
                R.id.keyCs2,
                R.id.keyD2,
                R.id.keyDs2,
                R.id.keyE2,
        )

        // 各ボタンにイベント関連付け
        ids.forEach {
            var key = findViewById<ImageView>(it)
            key.setOnTouchListener(touchListener)
        }
    }
}